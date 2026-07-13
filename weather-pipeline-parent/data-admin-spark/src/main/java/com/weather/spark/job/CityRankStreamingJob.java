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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityRankStreamingJob {

    public static void process(JavaStreamingContext jssc, JavaDStream<String> stream) {
        System.out.println("🏆 CityRankStreamingJob 已启动...");

        JavaDStream<WeatherEvent> events = stream
                .filter(json -> json != null && !json.isEmpty())
                .map(json -> parseWeatherEvent(json))
                .filter(e -> e != null);

        JavaPairDStream<String, Double> tempPairs = events
                .mapToPair(e -> new Tuple2<>(e.getCity(), e.getTemperature()));

        tempPairs.window(Durations.seconds(20), Durations.seconds(10))
                .foreachRDD(rdd -> {
                    if (rdd.isEmpty()) return;

                    // 收集数据并创建可修改的列表
                    List<Tuple2<String, Double>> list = new ArrayList<>(rdd.collect());
                    // 按温度降序排序
                    list.sort((a, b) -> Double.compare(b._2(), a._2()));

                    RedisWriter.del("screen:city_rank");

                    int rank = 1;
                    for (Tuple2<String, Double> t : list) {
                        if (rank > 10) break;
                        RedisWriter.zadd("screen:city_rank", t._2(), t._1());
                        rank++;
                    }

                    System.out.println("  ✅ 排行榜更新: " + Math.min(list.size(), 10) + " 个城市");
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
