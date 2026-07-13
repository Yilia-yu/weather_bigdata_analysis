package com.weather.mapreduce.runner;

import com.weather.mapreduce.job.*;

/**
 * MapReduce 任务统一调度器
 * 依次运行所有 5 个分析 Job
 */
public class JobRunner {

    private static final String INPUT_BASE = "/tmp/weather_data";
    private static final String OUTPUT_BASE = "/tmp/weather_analysis";

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════");
        System.out.println("  📊 MapReduce 离线分析启动");
        System.out.println("  输入路径: " + INPUT_BASE);
        System.out.println("  输出路径: " + OUTPUT_BASE);
        System.out.println("═══════════════════════════════════════════════════");

        try {
            // Job1: 每日城市统计
            System.out.println("\n🔹 运行 Job1: 每日城市统计...");
            DailyCityStatMR.run(INPUT_BASE, OUTPUT_BASE + "/daily_stat");

            // Job2: 天气分布统计
            System.out.println("\n🔹 运行 Job2: 天气分布统计...");
            WeatherDistMR.run(INPUT_BASE, OUTPUT_BASE + "/weather_dist");

            // Job3: 城市温度排名
            System.out.println("\n🔹 运行 Job3: 城市温度排名...");
            CityTempRankMR.run(INPUT_BASE, OUTPUT_BASE + "/city_rank");

            // Job4: 告警统计
            System.out.println("\n🔹 运行 Job4: 告警统计...");
            AlertStatMR.run(INPUT_BASE, OUTPUT_BASE + "/alert_stat");

            // Job5: 地域统计
            System.out.println("\n🔹 运行 Job5: 地域统计...");
            RegionStatMR.run(INPUT_BASE, OUTPUT_BASE + "/region_stat");

            System.out.println("\n═══════════════════════════════════════════════════");
            System.out.println("  ✅ 所有 MapReduce 任务执行完成！");
            System.out.println("═══════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("❌ 任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
