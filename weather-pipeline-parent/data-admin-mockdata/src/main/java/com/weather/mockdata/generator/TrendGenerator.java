package com.weather.mockdata.generator;

import java.util.*;

/**
 * 趋势数据生成器
 * 生成 24 小时温度变化趋势
 */
public class TrendGenerator {

    private static final Random RANDOM = new Random();

    public static List<Map<String, Object>> generate24Hours(String city, double baseTemp) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            Map<String, Object> point = new HashMap<>();
            point.put("hour", hour + ":00");

            // 温度变化：凌晨低、午后高
            double factor = Math.sin((hour - 6) * Math.PI / 12);
            double temp = baseTemp + factor * 5 + (RANDOM.nextDouble() - 0.5) * 2;
            point.put("temperature", Math.round(temp * 10) / 10.0);

            result.add(point);
        }
        return result;
    }

    public static List<Map<String, Object>> generate7Days(String city, double baseTemp) {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] days = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        for (int i = 0; i < 7; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("day", days[i]);
            double temp = baseTemp + (RANDOM.nextDouble() - 0.5) * 6;
            point.put("high", Math.round((temp + 3 + RANDOM.nextDouble() * 2) * 10) / 10.0);
            point.put("low", Math.round((temp - 3 - RANDOM.nextDouble() * 2) * 10) / 10.0);
            result.add(point);
        }
        return result;
    }
}
