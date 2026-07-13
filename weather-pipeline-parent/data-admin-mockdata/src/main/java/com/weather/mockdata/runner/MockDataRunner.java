package com.weather.mockdata.runner;

import com.weather.mockdata.entity.MockWeatherData;
import com.weather.mockdata.generator.*;
import com.weather.mockdata.http.NginxUploader;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 模拟数据生成器主程序
 * 定时生成数据，通过 HTTP 发送到 Nginx → Kafka
 * 
 * 对标水务项目的 MockDataRunner
 */
public class MockDataRunner {

    private static final String DEFAULT_HOST = "192.168.139.38";
    private static final int DEFAULT_PORT = 8888;
    private static final int DEFAULT_INTERVAL = 5; // 秒

    private final NginxUploader uploader;
    private final ScheduledExecutorService scheduler;
    private int round = 0;

    public MockDataRunner(String host, int port) {
        this.uploader = new NginxUploader(host, port);
        this.scheduler = Executors.newScheduledThreadPool(2);
        printBanner();
    }

    private void printBanner() {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  🎲  模拟数据生成器启动");
        System.out.println("  📡 目标: " + uploader.getClass().getSimpleName());
        System.out.println("  ⏱️  间隔: " + DEFAULT_INTERVAL + " 秒");
        System.out.println("  🌆 城市数: " + WeatherGenerator.getCityCount());
        System.out.println("═══════════════════════════════════════════════════");
    }

    public void start() {
        // 立即执行一次
        generateAndUpload();

        // 定时执行
        scheduler.scheduleAtFixedRate(this::generateAndUpload, DEFAULT_INTERVAL, DEFAULT_INTERVAL, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdown();
        System.out.println("🛑 模拟数据生成已停止");
    }

    private void generateAndUpload() {
        round++;
        System.out.println("\n─────────────────────────────────────────────────");
        System.out.println("🎲 第 " + round + " 轮生成 - " + new java.util.Date());
        System.out.println("─────────────────────────────────────────────────");

        int total = 0;

        // 1. 生成天气数据（全部 75 个城市）
        List<MockWeatherData> weatherData = WeatherGenerator.generateAll();
        boolean success = uploader.upload("weather", weatherData);
        System.out.println("   ☀️ 天气数据: " + weatherData.size() + " 条 " + (success ? "✅" : "❌"));
        total += weatherData.size();

        // 2. 生成告警数据（5-10 条）
        List<java.util.Map<String, Object>> alerts = AlertGenerator.generateBatch(5 + (int)(Math.random() * 5));
        success = uploader.upload("alerts", alerts);
        System.out.println("   🔔 告警数据: " + alerts.size() + " 条 " + (success ? "✅" : "❌"));
        total += alerts.size();

        // 3. 生成事件流（10-20 条）
        List<java.util.Map<String, Object>> events = EventGenerator.generateEvents(10 + (int)(Math.random() * 10));
        success = uploader.upload("events", events);
        System.out.println("   📨 事件流: " + events.size() + " 条 " + (success ? "✅" : "❌"));
        total += events.size();

        // 4. 模拟 UDP 传感器数据（2-5 条）
        try {
            int sensorCount = 2 + (int)(Math.random() * 3);
            for (int i = 0; i < sensorCount; i++) {
                SensorGenerator.sendUDPSensorData("127.0.0.1", 9999, "SENSOR_" + (100 + i));
            }
            System.out.println("   📡 传感器数据: " + sensorCount + " 条 ✅");
            total += sensorCount;
        } catch (Exception e) {
            System.out.println("   📡 传感器数据: 发送失败 ❌");
        }

        System.out.println("\n📊 本轮总计: " + total + " 条");
        System.out.println("📈 累计: " + (round * total) + " 条");
    }

    public static void main(String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            port = Integer.parseInt(args[1]);
        }

        System.out.println("使用配置: host=" + host + ", port=" + port);

        MockDataRunner runner = new MockDataRunner(host, port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🛑 收到关闭信号...");
            runner.stop();
        }));

        runner.start();

        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            runner.stop();
        }
    }
}
