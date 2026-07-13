package com.weather.spark.job;

import com.weather.spark.redis.RedisWriter;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

import java.util.HashMap;
import java.util.Map;

public class MonitorStreamingJob {

    private static long totalReceived = 0;
    private static long startTime = System.currentTimeMillis();

    public static void process(JavaStreamingContext jssc, JavaDStream<String> stream) {
        System.out.println("📈 MonitorStreamingJob 已启动...");

        stream.foreachRDD(rdd -> {
            long count = rdd.count();
            totalReceived += count;

            Map<String, String> monitor = new HashMap<>();
            monitor.put("status", "RUNNING");
            monitor.put("totalReceived", String.valueOf(totalReceived));
            monitor.put("lastBatchCount", String.valueOf(count));
            monitor.put("uptime", String.valueOf((System.currentTimeMillis() - startTime) / 1000));
            monitor.put("updateTime", String.valueOf(System.currentTimeMillis()));

            RedisWriter.hsetAll("screen:monitor", monitor);
        });
    }
}
