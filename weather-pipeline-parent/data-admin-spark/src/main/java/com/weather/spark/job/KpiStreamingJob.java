package com.weather.spark.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weather.spark.entity.WeatherEvent;
import com.weather.spark.redis.RedisWriter;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KpiStreamingJob {

    public static void process(JavaStreamingContext jssc, JavaDStream<String> stream) {
        System.out.println("📊 KpiStreamingJob 已启动...");

        JavaDStream<WeatherEvent> events = stream
                .filter(json -> json != null && !json.isEmpty())
                .map(json -> parseWeatherEvent(json))
                .filter(e -> e != null);

        JavaPairDStream<String, Double> tempPairs = events
                .mapToPair(e -> new Tuple2<>(e.getCity(), e.getTemperature()));

        // 窗口大小 20秒，滑动间隔 10秒（与批次间隔一致）
        JavaPairDStream<String, Double> windowed = tempPairs
                .window(Durations.seconds(20), Durations.seconds(10));

        windowed.foreachRDD(rdd -> {
            if (rdd.isEmpty()) return;

            List<Tuple2<String, Double>> list = rdd.collect();
            double sum = 0;
            double max = -100;
            double min = 100;
            int count = 0;

            for (Tuple2<String, Double> t : list) {
                double temp = t._2();
                sum += temp;
                max = Math.max(max, temp);
                min = Math.min(min, temp);
                count++;
            }

            Map<String, String> kpi = new HashMap<>();
            kpi.put("cityCount", String.valueOf(list.size()));
            kpi.put("avgTemp", String.format("%.1f", sum / count));
            kpi.put("maxTemp", String.format("%.1f", max));
            kpi.put("minTemp", String.format("%.1f", min));
            kpi.put("updateTime", String.valueOf(System.currentTimeMillis()));

            RedisWriter.hsetAll("screen:kpi", kpi);
            System.out.println("  ✅ KPI 更新: " + kpi);
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
