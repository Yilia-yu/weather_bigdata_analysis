package com.weather.collector;

import com.weather.model.WeatherData;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulateSensorSource implements WeatherDataSource {

    private final List<String> cities;
    private final Random random = new Random();

    public SimulateSensorSource() {
        this.cities = loadCities();
        System.out.println("   [SimulateSensor] 加载了 " + this.cities.size() + " 个城市");
    }

    private List<String> loadCities() {
        List<String> result = new ArrayList<>();
        try {
            InputStream is = SimulateSensorSource.class.getClassLoader().getResourceAsStream("cities.txt");
            if (is == null) {
                return getDefaultCities();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    result.add(parts[0].trim());
                }
            }
            reader.close();
        } catch (Exception e) {
            return getDefaultCities();
        }
        return result;
    }

    private List<String> getDefaultCities() {
        List<String> list = new ArrayList<>();
        list.add("北京");
        list.add("上海");
        list.add("广州");
        list.add("深圳");
        list.add("成都");
        list.add("武汉");
        list.add("杭州");
        list.add("重庆");
        list.add("南京");
        list.add("西安");
        return list;
    }

    @Override
    public String getName() {
        return "模拟传感器(UDP)";
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public WeatherData fetchCity(String city) {
        double baseTemp = 20 + random.nextInt(15);
        double variation = (random.nextDouble() - 0.5) * 4;
        double temp = baseTemp + variation;

        // 使用正态分布控制极端天气频率（展示用，适度提升概率）
        // 风速：均值 12km/h，标准差 9 → 约 13% 概率 > 24km/h，约 2% 概率 > 30km/h
        double windSpeed = 12 + random.nextGaussian() * 9;
        windSpeed = Math.max(0, Math.min(45, windSpeed));

        // 湿度：均值 65%，标准差 18 → 约 8% 概率 > 90%，约 3% 概率 > 98%（展示用）
        double humidity = 65 + random.nextGaussian() * 18;
        humidity = Math.max(10, Math.min(100, humidity));

        WeatherData data = new WeatherData();
        data.setCity(city);
        data.setTemperature(Math.round(temp * 10) / 10.0);
        data.setHumidity(Math.round(humidity * 10) / 10.0);
        data.setWindSpeed(Math.round(windSpeed * 10) / 10.0);
        data.setWeatherText(getRandomWeather(windSpeed, humidity));
        data.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
        return data;
    }

    @Override
    public List<WeatherData> fetchAllCities() {
        List<WeatherData> result = new ArrayList<>();
        for (String city : cities) {
            result.add(fetchCity(city));
        }
        return result;
    }

    private String getRandomWeather(double windSpeed, double humidity) {
        // 根据实际数据匹配天气描述
        if (humidity > 95) return "暴雨";
        if (humidity > 90) return "大雨";
        if (windSpeed > 30) return "狂风";
        if (windSpeed > 20) return "大风";
        if (humidity > 80) return "小雨";
        String[] weathers = {"晴", "多云", "阴", "阵雨", "雷阵雨", "雾"};
        return weathers[random.nextInt(weathers.length)];
    }
}
