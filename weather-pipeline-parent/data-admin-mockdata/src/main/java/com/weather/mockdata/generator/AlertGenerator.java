package com.weather.mockdata.generator;

import java.util.*;

/**
 * 告警数据生成器
 * 生成模拟告警记录
 */
public class AlertGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] CITIES = {"北京", "上海", "广州", "深圳", "成都", "武汉", "杭州", "重庆", "南京", "西安", "长沙", "郑州"};
    private static final String[] ALERT_TYPES = {"高温", "低温", "大风", "暴雨", "大雾"};
    private static final String[] ALERT_LEVELS = {"提示", "预警", "严重预警", "紧急预警"};

    public static Map<String, Object> generateRandom() {
        Map<String, Object> alert = new HashMap<>();
        alert.put("city", CITIES[RANDOM.nextInt(CITIES.length)]);
        alert.put("type", ALERT_TYPES[RANDOM.nextInt(ALERT_TYPES.length)]);
        alert.put("level", ALERT_LEVELS[RANDOM.nextInt(ALERT_LEVELS.length)]);
        alert.put("value", Math.round((20 + RANDOM.nextDouble() * 30) * 10) / 10.0);
        alert.put("threshold", Math.round((25 + RANDOM.nextDouble() * 20) * 10) / 10.0);
        alert.put("timestamp", System.currentTimeMillis());
        alert.put("status", "active");
        return alert;
    }

    public static List<Map<String, Object>> generateBatch(int count) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            alerts.add(generateRandom());
        }
        return alerts;
    }
}
