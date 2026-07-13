package com.weather.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class WeatherProducer {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public WeatherProducer(String bootstrapServers, String topic) {
        this.topic = topic;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        this.producer = new KafkaProducer<>(props);
    }

    public void send(String key, String value) {
        producer.send(new ProducerRecord<>(topic, key, value),
            (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("发送失败: " + key + " - " + exception.getMessage());
                }
            }
        );
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.flush();
        producer.close();
    }
}
