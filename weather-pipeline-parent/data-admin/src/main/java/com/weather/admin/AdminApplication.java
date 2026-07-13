package com.weather.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("  🌤️  天气监控系统 Admin 启动成功");
        System.out.println("  📊 大屏地址: http://localhost:8888/screen");
        System.out.println("  📡 API地址:  http://localhost:8888/api/weather/current");
        System.out.println("========================================");
    }
}
