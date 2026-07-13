package com.weather.spark.runner;

import com.weather.spark.job.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.util.*;

public class SparkRunner {

    private static final String KAFKA_BOOTSTRAP = "192.168.139.38:9092";
    private static final String KAFKA_TOPIC = "weather-raw";
    private static final String KAFKA_GROUP = "spark-streaming-group";

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("hadoop.home.dir", "/Users/liumeiyu/projects/hadoop-2.7.6");

        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  ⚡ Spark Streaming 实时计算启动");
        System.out.println("  📡 Kafka: " + KAFKA_BOOTSTRAP);
        System.out.println("  📤 Topic: " + KAFKA_TOPIC);
        System.out.println("  ⏱️  批次间隔: 10 秒");
        System.out.println("═══════════════════════════════════════════════════");

        SparkConf conf = new SparkConf()
                .setAppName("WeatherStreaming")
                .setMaster("local[2]")
                .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");

        // 批次间隔 10 秒
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(10));

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, KAFKA_GROUP);
        kafkaParams.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

        JavaInputDStream<org.apache.kafka.clients.consumer.ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.Subscribe(Collections.singletonList(KAFKA_TOPIC), kafkaParams)
                );

        JavaDStream<String> messages = stream.map(record -> record.value());

        System.out.println("\n📋 启动 5 个实时 Job...");
        System.out.println("  🔹 KpiStreamingJob     → 实时KPI");
        System.out.println("  🔹 CityRankStreamingJob → 城市排行榜");
        System.out.println("  🔹 WeatherDistStreamingJob → 天气分布");
        System.out.println("  🔹 AlertStreamingJob   → 实时告警");
        System.out.println("  🔹 MonitorStreamingJob → 健康监控");
        System.out.println();

        KpiStreamingJob.process(jssc, messages);
        CityRankStreamingJob.process(jssc, messages);
        WeatherDistStreamingJob.process(jssc, messages);
        AlertStreamingJob.process(jssc, messages);
        MonitorStreamingJob.process(jssc, messages);

        System.out.println("🚀 Spark Streaming 启动中...");
        jssc.start();
        jssc.awaitTermination();
    }
}
