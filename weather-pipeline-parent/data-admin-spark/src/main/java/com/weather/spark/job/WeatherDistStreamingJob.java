package com.weather.spark.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weather.spark.redis.RedisWriter;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.HashMap;
import java.util.Map;

public class WeatherDistStreamingJob {

    public static void process(JavaStreamingContext jssc, JavaDStream<String> stream) {
        System.out.println("🌤️ WeatherDistStreamingJob 已启动...");

        JavaDStream<String> weathers = stream
                .filter(json -> json != null && !json.isEmpty())
                .map(json -> extractWeather(json))
                .filter(w -> w != null);

        JavaPairDStream<String, Integer> weatherCounts = weathers
                .mapToPair(w -> new Tuple2<>(w, 1))
                .reduceByKey((a, b) -> a + b);

        weatherCounts.foreachRDD(rdd -> {
            if (rdd.isEmpty()) return;

            RedisWriter.del("screen:weather_dist");

            Map<String, String> dist = new HashMap<>();
            rdd.collect().forEach(t -> dist.put(t._1(), String.valueOf(t._2())));

            RedisWriter.hsetAll("screen:weather_dist", dist);
            System.out.println("  ✅ 天气分布更新: " + dist);
        });
    }

    private static String extractWeather(String json) {
        try {
            JSONObject obj = JSON.parseObject(json);
            return obj.getString("weather");
        } catch (Exception e) {
            return null;
        }
    }
}
