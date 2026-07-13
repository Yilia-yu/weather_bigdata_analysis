package com.weather.spark.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weather.spark.entity.WeatherEvent;
import com.weather.spark.redis.RedisWriter;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.List;

public class AlertStreamingJob {

    private static final double HIGH_TEMP_THRESHOLD = 35.0;

    public static void process(JavaStreamingContext jssc, JavaDStream<String> stream) {
        System.out.println("🔔 AlertStreamingJob 已启动...");

        JavaDStream<WeatherEvent> events = stream
                .filter(json -> json != null && !json.isEmpty())
                .map(json -> parseWeatherEvent(json))
                .filter(e -> e != null);

        JavaDStream<String> alerts = events
                .filter(e -> e.getTemperature() > HIGH_TEMP_THRESHOLD)
                .map(e -> String.format(
                    "{\"city\":\"%s\",\"temp\":%.1f,\"level\":\"高温预警\",\"time\":%d}",
                    e.getCity(), e.getTemperature(), System.currentTimeMillis()
                ));

        alerts.foreachRDD(rdd -> {
            List<String> alertList = rdd.collect();
            if (alertList.isEmpty()) return;

            RedisWriter.del("screen:alerts");
            for (String alert : alertList) {
                RedisWriter.set("screen:alerts", alert);
            }
            System.out.println("  🔔 告警: " + alertList.size() + " 条");
        });
    }

    private static WeatherEvent parseWeatherEvent(String json) {
        try {
            JSONObject obj = JSON.parseObject(json);
            WeatherEvent event = new WeatherEvent();
            event.setCity(obj.getString("city"));
            event.setTemperature(obj.getDoubleValue("temp"));
            event.setHumidity(obj.getDoubleValue("humidity"));
            event.setWindSpeed(obj.getDoubleValue("windSpeed"));
            event.setWeather(obj.getString("weather"));
            event.setTimestamp(System.currentTimeMillis());
            return event;
        } catch (Exception e) {
            return null;
        }
    }
}
