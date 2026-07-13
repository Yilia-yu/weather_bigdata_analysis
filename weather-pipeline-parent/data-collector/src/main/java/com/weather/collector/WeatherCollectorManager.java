package com.weather.collector;

import com.weather.kafka.WeatherProducer;
import com.weather.model.WeatherData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherCollectorManager {

    private static final String KAFKA_TOPIC = "weather-raw";
    private static final String KAFKA_BOOTSTRAP = "172.18.0.2:9092";

    private final List<WeatherDataSource> sources = new ArrayList<>();
    private final WeatherProducer producer;
    private final ScheduledExecutorService scheduler;

    private int totalCollected = 0;
    private int totalSuccess = 0;
    private int totalFailed = 0;

    public WeatherCollectorManager() {
        this.producer = new WeatherProducer(KAFKA_BOOTSTRAP, KAFKA_TOPIC);
        initDataSources();
        this.scheduler = Executors.newScheduledThreadPool(2);
        printStatus();
    }

    private void initDataSources() {
        // 主数据源：OpenWeatherMap（容器无外网，暂时跳过）
        // try {
        //     OpenWeatherSource source = new OpenWeatherSource();
        //     if (source.isAvailable()) {
        //         sources.add(source);
        //         System.out.println("✅ OpenWeatherMap 数据源已加载");
        //     } else {
        //         System.out.println("⚠️ OpenWeatherMap 数据源不可用");
        //     }
        // } catch (Exception e) {
        //     System.out.println("❌ OpenWeatherMap 加载失败: " + e.getMessage());
        // }

        // 数据源：模拟传感器
        try {
            SimulateSensorSource simulator = new SimulateSensorSource();
            sources.add(simulator);
            System.out.println("✅ 模拟传感器数据源已加载");
        } catch (Exception e) {
            System.out.println("❌ 模拟传感器加载失败: " + e.getMessage());
        }

        System.out.println("\n📊 共加载 " + sources.size() + " 个数据源");
    }

    private void printStatus() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  🌤️  天气数据采集管理器已启动");
        System.out.println("  📡 数据源: " + sources.size() + " 个");
        System.out.println("  📤 Kafka: " + KAFKA_TOPIC);
        System.out.println("  ⏱️  采集间隔: 5 分钟");
        System.out.println("═══════════════════════════════════════════════════");
    }

    public void start() {
        collectAll();
        scheduler.scheduleAtFixedRate(this::collectAll, 5, 5, TimeUnit.MINUTES);
    }

    public void stop() {
        scheduler.shutdown();
        producer.close();
        System.out.println("🛑 数据采集已停止");
    }

    private void collectAll() {
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("📡 开始采集 - " + new java.util.Date());
        System.out.println("─────────────────────────────────────────────────");

        int success = 0;

        for (WeatherDataSource source : sources) {
            try {
                String sourceName = source.getName();
                System.out.print("   [" + sourceName + "] 采集 ");
                long start = System.currentTimeMillis();

                List<WeatherData> dataList = source.fetchAllCities();

                for (WeatherData data : dataList) {
                    String json = data.toJson();
                    producer.send(data.getCity(), json);
                    success++;
                }

                producer.flush();

                long elapsed = System.currentTimeMillis() - start;
                System.out.println("✅ " + dataList.size() + " 条, " + elapsed + "ms");

            } catch (Exception e) {
                System.err.println("    ❌ 采集失败: " + e.getMessage());
                totalFailed++;
            }
        }

        totalCollected += success;
        totalSuccess += success;

        System.out.println("\n📊 本轮: 成功 " + success + " 条");
        System.out.println("📈 总计: 采集 " + totalCollected + " 条, 成功 " + totalSuccess + " 条, 失败 " + totalFailed + " 条");
        System.out.println("─────────────────────────────────────────────────");
    }

    public static void main(String[] args) {
        WeatherCollectorManager manager = new WeatherCollectorManager();
        Runtime.getRuntime().addShutdownHook(new Thread(manager::stop));
        manager.start();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            manager.stop();
        }
    }
}
