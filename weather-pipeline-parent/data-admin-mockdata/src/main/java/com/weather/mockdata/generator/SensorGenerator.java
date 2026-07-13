package com.weather.mockdata.generator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

/**
 * 传感器数据生成器（UDP 模拟）
 * 对标水务项目的 UDP 传感器模拟
 */
public class SensorGenerator {

    private static final Random RANDOM = new Random();
    private static final String[] SENSOR_TYPES = {"temperature", "humidity", "wind", "pressure"};

    public static String generateSensorData(String sensorId) {
        String type = SENSOR_TYPES[RANDOM.nextInt(SENSOR_TYPES.length)];
        double value = 20 + RANDOM.nextDouble() * 40;

        return String.format(
            "{\"sensor_id\":\"%s\",\"type\":\"%s\",\"value\":%.2f,\"unit\":\"%s\",\"timestamp\":%d}",
            sensorId, type, value, getUnit(type), System.currentTimeMillis()
        );
    }

    private static String getUnit(String type) {
        switch (type) {
            case "temperature": return "°C";
            case "humidity": return "%";
            case "wind": return "km/h";
            case "pressure": return "hPa";
            default: return "";
        }
    }

    /**
     * 通过 UDP 发送传感器数据
     * 对标水务项目的 UDP 协议
     */
    public static void sendUDPSensorData(String host, int port, String sensorId) throws Exception {
        String data = generateSensorData(sensorId);
        DatagramSocket socket = new DatagramSocket();
        byte[] buffer = data.getBytes();
        DatagramPacket packet = new DatagramPacket(
            buffer,
            buffer.length,
            InetAddress.getByName(host),
            port
        );
        socket.send(packet);
        socket.close();
        System.out.println("  📡 UDP 发送: " + data);
    }
}
