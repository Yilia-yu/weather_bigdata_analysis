package com.weather.mockdata.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.weather.mockdata.entity.MockWeatherData;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.List;
import java.util.Properties;

public class NginxUploader {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public NginxUploader(String host, int port) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.139.38:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        this.producer = new KafkaProducer<>(props);
        this.topic = "weather-raw";
        System.out.println("📤 直接发送到 Kafka: " + topic);
    }

    public NginxUploader() {
        this("192.168.139.38", 8888);
    }

    public boolean upload(String table, List<?> rows) {
        if (rows == null || rows.isEmpty()) {
            return true;
        }

        int success = 0;
        int fail = 0;

        for (Object row : rows) {
            try {
                String json;
                if (row instanceof MockWeatherData) {
                    json = ((MockWeatherData) row).toJson();
                } else {
                    json = JSON.toJSONString(row);
                }
                String city = extractCity(row);
                producer.send(new ProducerRecord<>(topic, city, json));
                success++;
            } catch (Exception e) {
                fail++;
            }
        }

        producer.flush();
        return fail == 0;
    }

    private String extractCity(Object row) {
        try {
            if (row instanceof MockWeatherData) {
                return ((MockWeatherData) row).getCity();
            }
            String json = JSON.toJSONString(row);
            JSONObject obj = JSON.parseObject(json);
            String city = obj.getString("city");
            return city != null ? city : "unknown";
        } catch (Exception e) {
            return "unknown";
        }
    }

    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
