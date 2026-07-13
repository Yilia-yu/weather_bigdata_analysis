package com.weather.mockdata.generator;

import java.util.*;

/**
 * 事件流数据生成器
 * 生成实时事件流（用于大屏滚动）
 */
public class EventGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] CITIES = {"北京", "上海", "广州", "深圳", "成都", "武汉", "杭州", "重庆", "南京", "西安"};
    private static final String[] EVENT_TYPES = {"温度更新", "湿度更新", "风速更新", "告警触发", "告警解除", "数据同步"};

    public static Map<String, Object> generateEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("id", UUID.randomUUID().toString().substring(0, 8));
        event.put("city", CITIES[RANDOM.nextInt(CITIES.length)]);
        event.put("type", EVENT_TYPES[RANDOM.nextInt(EVENT_TYPES.length)]);
        event.put("value", Math.round((10 + RANDOM.nextDouble() * 40) * 10) / 10.0);
        event.put("timestamp", System.currentTimeMillis());
        event.put("status", RANDOM.nextBoolean() ? "success" : "warning");

        // 额外信息
        Map<String, String> extra = new HashMap<>();
        extra.put("source", RANDOM.nextBoolean() ? "api" : "sensor");
        event.put("extra", extra);

        return event;
    }

    public static List<Map<String, Object>> generateEvents(int count) {
        List<Map<String, Object>> events = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            events.add(generateEvent());
        }
        return events;
    }
}
