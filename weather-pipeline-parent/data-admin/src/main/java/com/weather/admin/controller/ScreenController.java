package com.weather.admin.controller;

import com.weather.admin.entity.CityWeather;
import com.weather.admin.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class ScreenController {

    @Autowired
    private ScreenService screenService;

    // 获取所有城市最新天气
    @GetMapping("/current")
    public List<CityWeather> getCurrent() {
        return screenService.getCurrentWeather();
    }

    // 获取统计数据
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        return screenService.getStats();
    }

    // 获取城市列表
    @GetMapping("/cities")
    public List<String> getCities() {
        return screenService.getCities();
    }

    // 获取单个城市天气
    @GetMapping("/city/{city}")
    public CityWeather getCity(@PathVariable String city) {
        return screenService.getCityWeather(city);
    }

    // 健康检查
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "weather-admin");
        result.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return result;
    }

    // 数据链路监控指标
    @GetMapping("/monitor")
    public Map<String, Object> monitor() {
        return screenService.getMonitor();
    }
}
