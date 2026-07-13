package com.ruoyi.weather.aviation.mapper;

import java.util.List;
import java.util.Map;

/**
 * 航空天气分析Mapper接口
 */
public interface AviationMapper {

    /**
     * 获取各城市最新天气（用于判定风险）
     */
    List<Map<String, Object>> selectLatestWeather();

    /**
     * 获取最近30条极端天气预警事件
     */
    List<Map<String, Object>> selectRecentAlerts();

    /**
     * 获取近7天每天触发极端天气的机场数
     */
    List<Map<String, Object>> selectAirportCountTrend();

    /**
     * 近7天大风 vs 暴雨分别触发的机场数
     */
    List<Map<String, Object>> selectTypeDistribution();

    /**
     * 机场可靠性排行 TOP10
     */
    List<Map<String, Object>> selectReliabilityRank();

    /**
     * 日历热力图数据 — 7天 × 10个枢纽
     */
    List<Map<String, Object>> selectCalendarData();
}
