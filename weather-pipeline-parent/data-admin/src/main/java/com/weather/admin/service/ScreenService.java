package com.weather.admin.service;

import com.weather.admin.entity.CityWeather;
import com.weather.admin.mapper.WeatherMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Service
public class ScreenService {

    @Autowired
    private WeatherMapper weatherMapper;

    @Autowired
    private JedisPool jedisPool;

    // 获取所有城市最新天气
    public List<CityWeather> getCurrentWeather() {
        // 优先从 Redis 读取
        List<CityWeather> redisData = getFromRedis();
        if (redisData != null && !redisData.isEmpty()) {
            return redisData;
        }
        // 回退到 MySQL
        return weatherMapper.findLatest();
    }

    // 从 Redis 读取缓存数据
    private List<CityWeather> getFromRedis() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("weather:current:*");
            if (keys == null || keys.isEmpty()) {
                return null;
            }
            List<CityWeather> result = new ArrayList<>();
            for (String key : keys) {
                String json = jedis.get(key);
                if (json != null) {
                    CityWeather weather = parseJson(json);
                    if (weather != null) {
                        result.add(weather);
                    }
                }
            }
            return result;
        } catch (Exception e) {
            System.err.println("Redis 读取失败: " + e.getMessage());
            return null;
        }
    }

    // 获取统计数据
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("cityCount", weatherMapper.countCities());
        stats.put("avgTemp", weatherMapper.getAvgTemperature());
        CityWeather hottest = weatherMapper.getHottestCity();
        if (hottest != null) {
            stats.put("hottestCity", hottest.getCity());
            stats.put("hottestTemp", hottest.getTemperature());
        }
        return stats;
    }

    // 获取所有城市列表
    public List<String> getCities() {
        return weatherMapper.findAllCities();
    }

    // 获取单个城市天气
    public CityWeather getCityWeather(String city) {
        // 优先从 Redis 读取
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get("weather:current:" + city);
            if (json != null) {
                return parseJson(json);
            }
        } catch (Exception e) {
            System.err.println("Redis 读取失败: " + e.getMessage());
        }
        return weatherMapper.findByCity(city);
    }

    // 获取监控指标
    public Map<String, Object> getMonitor() {
        Map<String, Object> monitor = new HashMap<>();
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> fields = Arrays.asList(
                "kafka_score","hdfs_score","redis_score","cleaner",
                "total_consumed","total_cleaned","mysql_ok","redis_ok","extreme_alerts",
                "kafka_lag","hdfs_usage","redis_memory","last_batch","uptime","last_update"
            );
            for (String f : fields) {
                String val = jedis.hget("screen:monitor", f);
                if (val != null) monitor.put(f, val);
            }
        } catch (Exception e) {
            monitor.put("error", e.getMessage());
        }
        return monitor;
    }
    private CityWeather parseJson(String json) {
        try {
            // 简单实现：提取关键字段
            CityWeather weather = new CityWeather();
            String[] parts = json.replace("{", "").replace("}", "").split(",");
            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length == 2) {
                    String key = kv[0].trim().replace("\"", "");
                    String value = kv[1].trim().replace("\"", "");
                    switch (key) {
                        case "city": weather.setCity(value); break;
                        case "temp": weather.setTemperature(Double.parseDouble(value)); break;
                        case "humidity": weather.setHumidity(Double.parseDouble(value)); break;
                        case "windSpeed": weather.setWindSpeed(Double.parseDouble(value)); break;
                        case "weather": weather.setWeather(value); break;
                        case "aqi": weather.setAqi(Integer.parseInt(value)); break;
                    }
                }
            }
            return weather;
        } catch (Exception e) {
            return null;
        }
    }
}
