package com.weather.cleaner;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.Duration;
import java.util.*;
import java.util.Collections;
import java.util.Properties;

public class WeatherCleaner {

    private static final String KAFKA_BOOTSTRAP = "172.18.0.2:9092";
    private static final String KAFKA_TOPIC = "weather-raw";
    private static final String KAFKA_GROUP = "weather-cleaner-group";
    private static final String HDFS_JMX_URL = "http://master:50070/jmx?qry=Hadoop:service=NameNode,name=FSNamesystem";

    private static final String MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/weather_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String RUOYI_MYSQL_URL = "jdbc:mysql://127.0.0.1:3306/ry?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "123456";

    private static final String REDIS_HOST = "172.18.0.2";
    private static final int REDIS_PORT = 6379;

    // 监控计数器
    private static long totalConsumed = 0;
    private static long totalCleaned = 0;
    private static long mysqlOk = 0;
    private static long redisOk = 0;
    private static long ruoyiOk = 0;
    private static long extremeAlerts = 0;
    private static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("🌤️ 天气数据清洗程序启动");
        System.out.println("Kafka Topic: " + KAFKA_TOPIC);
        System.out.println("========================================");

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(KAFKA_TOPIC));

        // 启动独立的监控心跳线程：每 5 秒推一次监控指标到 Redis
        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    updateMonitor(0);  // batchSize=0 表示心跳推送，不改变计数
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        int totalReceived = 0;

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            int batchSize = 0;

            for (ConsumerRecord<String, String> record : records) {
                totalReceived++;
                String json = record.value();
                System.out.println("\n📥 [" + totalReceived + "] 收到: " + record.key());

                CleanWeatherData data = parseAndClean(json);
                if (data == null) {
                    System.out.println("  ⚠️ 数据异常，已丢弃");
                    totalConsumed++;
                    continue;
                }

                System.out.println("  ✅ 清洗后: " + data);
                totalConsumed++;
                totalCleaned++;
                batchSize++;

                saveToMySQL(data);
                saveToRuoyi(data);
                saveToRedis(data);
                checkAndAlertExtreme(data);
            }
            if (batchSize > 0) {
                updateMonitor(batchSize);
            }
        }
    }

    static class CleanWeatherData {
        String city;
        double temperature;
        double humidity;
        double windSpeed;
        String weather;
        String updateTime;

        @Override
        public String toString() {
            return String.format("%s | %.1f°C | %.1f%% | %.1fkm/h | %s",
                    city, temperature, humidity, windSpeed, weather);
        }
    }

    private static CleanWeatherData parseAndClean(String json) {
        try {
            JSONObject obj = JSONObject.parseObject(json);

            double temp = obj.getDoubleValue("temp");
            double humidity = obj.getDoubleValue("humidity");
            double windSpeed = obj.getDoubleValue("windSpeed");
            String city = obj.getString("city");
            String weather = obj.getString("weather");
            String updateTime = obj.getString("updateTime");

            if (temp < -50 || temp > 60) {
                System.err.println("  ⚠️ 温度异常: " + city + " " + temp + "°C");
                return null;
            }
            if (humidity < 0 || humidity > 100) {
                System.err.println("  ⚠️ 湿度异常: " + city + " " + humidity + "%");
                return null;
            }
            if (city == null || city.trim().isEmpty()) {
                System.err.println("  ⚠️ 城市名为空");
                return null;
            }

            CleanWeatherData data = new CleanWeatherData();
            data.city = city.trim();
            data.temperature = Math.round(temp * 10) / 10.0;
            data.humidity = Math.round(humidity * 10) / 10.0;
            data.windSpeed = Math.round(windSpeed * 10) / 10.0;
            data.weather = weather != null ? weather : "unknown";
            data.updateTime = updateTime != null ? updateTime : String.valueOf(System.currentTimeMillis() / 1000);
            return data;

        } catch (Exception e) {
            System.err.println("  ❌ 解析失败: " + e.getMessage());
            return null;
        }
    }

    private static void saveToMySQL(CleanWeatherData data) {
        String sql = "INSERT INTO weather_history (city, temperature, humidity, wind_speed, weather, update_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.city);
            pstmt.setDouble(2, data.temperature);
            pstmt.setDouble(3, data.humidity);
            pstmt.setDouble(4, data.windSpeed);
            pstmt.setString(5, data.weather);
            pstmt.setString(6, data.updateTime);
            pstmt.executeUpdate();
            System.out.println("  💾 MySQL: " + data.city + " ✅");
            mysqlOk++;

        } catch (SQLException e) {
            System.err.println("  ❌ MySQL 写入失败: " + e.getMessage());
        }
    }

    private static void saveToRedis(CleanWeatherData data) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            String key = "weather:current:" + data.city;
            String value = String.format(
                "{\"city\":\"%s\",\"temp\":%.1f,\"humidity\":%.1f,\"windSpeed\":%.1f,\"weather\":\"%s\",\"updateTime\":\"%s\"}",
                data.city, data.temperature, data.humidity, data.windSpeed, data.weather, data.updateTime
            );
            jedis.set(key, value);
            jedis.expire(key, 3600);
            System.out.println("  💾 Redis: " + key + " ✅");
            redisOk++;

        } catch (Exception e) {
            System.err.println("  ❌ Redis 写入失败: " + e.getMessage());
        }
    }

    // 同步写入若依数据库
    private static void saveToRuoyi(CleanWeatherData data) {
        String sql = "INSERT INTO biz_weather_record (city_name, temperature, humidity, wind_speed, weather, data_source, record_time) " +
                     "VALUES (?, ?, ?, ?, ?, '模拟传感器', NOW())";

        try (Connection conn = DriverManager.getConnection(RUOYI_MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, data.city);
            pstmt.setDouble(2, data.temperature);
            pstmt.setDouble(3, data.humidity);
            pstmt.setDouble(4, data.windSpeed);
            pstmt.setString(5, data.weather);
            pstmt.executeUpdate();
            System.out.println("  💾 若依DB: " + data.city + " ✅");
            ruoyiOk++;

        } catch (SQLException e) {
            System.err.println("  ❌ 若依DB写入失败: " + e.getMessage());
        }
    }

    // ---- 极端天气大风/暴雨预警 ----
    private static void checkAndAlertExtreme(CleanWeatherData data) {
        // 判定逻辑：风速 >= 20 = 蓝色，>= 25 = 黄色，>= 30 = 橙色，>= 35 = 红色
        //          湿度 >= 85 = 蓝色，>= 90 = 黄色，>= 95 = 橙色，>= 98 = 红色
        String alertType = null;
        String alertLevel = null;
        String metricField = null;
        double alertValue = 0;

        if (data.windSpeed >= 20) {
            alertType = "大风";
            metricField = "wind_speed";
            alertValue = data.windSpeed;
            if (data.windSpeed >= 35) alertLevel = "红色";
            else if (data.windSpeed >= 30) alertLevel = "橙色";
            else if (data.windSpeed >= 25) alertLevel = "黄色";
            else alertLevel = "蓝色";
        } else if (data.humidity >= 85) {
            alertType = "暴雨";
            metricField = "humidity";
            alertValue = data.humidity;
            if (data.humidity >= 98) alertLevel = "红色";
            else if (data.humidity >= 95) alertLevel = "橙色";
            else if (data.humidity >= 90) alertLevel = "黄色";
            else alertLevel = "蓝色";
        }

        if (alertType == null) return;

        // 去重：同一城市今天同类型预警只记一次
        final String finalType = alertType;
        final String finalLevel = alertLevel;
        final String finalField = metricField;
        final double finalValue = alertValue;

        // 先用独立线程做去重查询，避免阻塞数据流
        new Thread(() -> {
            try (Connection conn = DriverManager.getConnection(RUOYI_MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD)) {
                // 检查今天是否已记录
                String checkSql = "SELECT COUNT(*) FROM extreme_weather_alert " +
                                  "WHERE city_name = ? AND alert_type = ? AND DATE(alert_time) = CURDATE()";
                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                    checkPs.setString(1, data.city);
                    checkPs.setString(2, finalType);
                    ResultSet rs = checkPs.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("  ⚠️ 今日已预警 " + data.city + " " + finalType + ", 跳过");
                        return;
                    }
                }

                // 写入预警
                String insertSql = "INSERT INTO extreme_weather_alert (city_name, alert_type, alert_level, metric_field, alert_value, weather_desc) " +
                                   "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                    ps.setString(1, data.city);
                    ps.setString(2, finalType);
                    ps.setString(3, finalLevel);
                    ps.setString(4, finalField);
                    ps.setDouble(5, finalValue);
                    ps.setString(6, data.weather + ", " + finalValue +
                                   (finalField.equals("wind_speed") ? "km/h" : "%") +
                                   ", " + finalLevel + "色" + finalType + "预警");
                    ps.executeUpdate();
                    System.out.println("  🚨 极端天气: " + data.city + " " + finalLevel + "色" + finalType + "预警 (" + finalValue + ")");
                    extremeAlerts++;
                }
            } catch (SQLException e) {
                System.err.println("  ❌ 极端天气写入失败: " + e.getMessage());
            }
        }).start();
    }

    // ---- 更新监控指标到 Redis ----
    private static void updateMonitor(int batchSize) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            long uptime = (System.currentTimeMillis() - startTime) / 1000;

            // 真实采集四项指标
            long kafkaLag = collectKafkaLag();
            double hdfsUsage = collectHdfsUsage();
            double redisMemory = collectRedisMemory();
            int cleanRate = totalConsumed > 0 ? (int)(totalCleaned * 100.0 / totalConsumed) : 0;

            // Kafka 堆积量转健康分：堆积 < 100 = 100分，每多 100 条扣 1 分，最低 60
            int kafkaScore = (int) Math.max(60, 100 - kafkaLag / 100);
            // HDFS 使用率转健康分：使用率越低分越高
            int hdfsScore = (int) Math.min(100, Math.max(60, 100 - hdfsUsage));
            // Redis 内存使用率转健康分
            int redisScore = (int) Math.min(100, Math.max(60, 100 - redisMemory));

            jedis.hset("screen:monitor", "kafka_lag", String.valueOf(kafkaLag));
            jedis.hset("screen:monitor", "hdfs_usage", String.valueOf(hdfsUsage));
            jedis.hset("screen:monitor", "redis_memory", String.valueOf(redisMemory));
            jedis.hset("screen:monitor", "cleaner", String.valueOf(cleanRate));
            jedis.hset("screen:monitor", "kafka_score", String.valueOf(kafkaScore));
            jedis.hset("screen:monitor", "hdfs_score", String.valueOf(hdfsScore));
            jedis.hset("screen:monitor", "redis_score", String.valueOf(redisScore));
            jedis.hset("screen:monitor", "total_consumed", String.valueOf(totalConsumed));
            jedis.hset("screen:monitor", "total_cleaned", String.valueOf(totalCleaned));
            jedis.hset("screen:monitor", "mysql_ok", String.valueOf(mysqlOk));
            jedis.hset("screen:monitor", "redis_ok", String.valueOf(redisOk));
            jedis.hset("screen:monitor", "extreme_alerts", String.valueOf(extremeAlerts));
            jedis.hset("screen:monitor", "last_batch", String.valueOf(batchSize));
            jedis.hset("screen:monitor", "uptime", String.valueOf(uptime));
            jedis.hset("screen:monitor", "last_update", String.valueOf(System.currentTimeMillis()));
            jedis.expire("screen:monitor", 3600);
        } catch (Exception e) {
            System.err.println("  ❌ 监控指标写入 Redis 失败: " + e.getMessage());
        }
    }

    // ---- 采集方法1：Kafka 消息堆积量 ----
    private static long collectKafkaLag() {
        Properties props = new Properties();
        props.put("bootstrap.servers", KAFKA_BOOTSTRAP);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("enable.auto.commit", "true");

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            List<PartitionInfo> parts = consumer.partitionsFor(KAFKA_TOPIC);
            if (parts == null || parts.isEmpty()) return 0;

            List<TopicPartition> tps = new ArrayList<>();
            for (PartitionInfo p : parts) {
                tps.add(new TopicPartition(KAFKA_TOPIC, p.partition()));
            }
            Map<TopicPartition, Long> endOffsets = consumer.endOffsets(tps);
            long totalLag = 0;
            for (TopicPartition tp : tps) {
                long end = endOffsets.getOrDefault(tp, 0L);
                OffsetAndMetadata meta = consumer.committed(tp);
                long committed = (meta != null) ? meta.offset() : 0;
                if (end > committed) totalLag += (end - committed);
            }
            return totalLag;
        } catch (Exception e) {
            return 0;
        }
    }

    // ---- 采集方法2：HDFS 磁盘使用率 ----
    private static double collectHdfsUsage() {
        HttpURLConnection conn = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(HDFS_JMX_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("GET");
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            String json = sb.toString();
            // 简单解析：找 CapacityUsed 和 CapacityTotal
            double used = extractJsonNumber(json, "CapacityUsed");
            double total = extractJsonNumber(json, "CapacityTotal");
            if (total > 0) return Math.round(used / total * 10000.0) / 100.0;
            return 0;
        } catch (Exception e) {
            return 0;
        } finally {
            try { if (reader != null) reader.close(); } catch (Exception ignored) {}
            if (conn != null) conn.disconnect();
        }
    }

    // ---- 采集方法3：Redis 内存使用率 ----
    private static double collectRedisMemory() {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            String info = jedis.info("memory");
            if (info == null || info.isEmpty()) return 0;
            double used = extractRedisValue(info, "used_memory:");
            double max = extractRedisValue(info, "maxmemory:");
            if (max <= 0) max = 268435456.0;  // 默认 256MB
            if (max > 0) return Math.round(used / max * 10000.0) / 100.0;
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    // 解析 Redis INFO 输出中的 key:value
    private static double extractRedisValue(String info, String key) {
        for (String line : info.split("\\r?\\n")) {
            if (line.startsWith(key)) {
                try { return Double.parseDouble(line.substring(key.length()).trim()); }
                catch (NumberFormatException ignored) {}
            }
        }
        return 0;
    }

    // 从 JSON 中提取指定 key 的数值
    private static double extractJsonNumber(String json, String key) {
        int idx = json.indexOf("\"" + key + "\"");
        if (idx < 0) return 0;
        idx = json.indexOf(":", idx);
        if (idx < 0) return 0;
        int end = idx + 1;
        while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '.' || json.charAt(end) == '-')) {
            end++;
        }
        try { return Double.parseDouble(json.substring(idx + 1, end).trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}
