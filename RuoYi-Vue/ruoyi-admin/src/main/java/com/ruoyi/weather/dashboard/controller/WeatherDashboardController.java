package com.ruoyi.weather.dashboard.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.weather.record.domain.BizWeatherRecord;
import com.ruoyi.weather.record.mapper.BizWeatherRecordMapper;

/**
 * 天气数据大屏Controller
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@RestController
@RequestMapping("/weather")
public class WeatherDashboardController
{
    @Autowired
    private BizWeatherRecordMapper bizWeatherRecordMapper;

    /**
     * 获取各城市最新天气数据
     */
    @GetMapping("/current")
    public AjaxResult current()
    {
        List<BizWeatherRecord> list = bizWeatherRecordMapper.selectWeatherCurrent();
        return AjaxResult.success(list);
    }

    /**
     * 获取天气统计数据
     */
    @GetMapping("/stats")
    public AjaxResult stats()
    {
        Map<String, Object> raw = bizWeatherRecordMapper.selectWeatherStats();
        Map<String, Object> result = new HashMap<>();
        
        if (raw != null) {
            Object cityCount = raw.get("city_count");
            Object avgTemp = raw.get("avg_temp");
            Object hottestCity = raw.get("hottest_city");
            Object hottestTemp = raw.get("hottest_temp");
            
            result.put("cityCount", cityCount != null ? cityCount : 0);
            result.put("avgTemp", avgTemp != null ? avgTemp : 0);
            result.put("hottestCity", hottestCity != null ? hottestCity : "--");
            result.put("hottestTemp", hottestTemp != null ? hottestTemp : 0);
        } else {
            result.put("cityCount", 0);
            result.put("avgTemp", 0);
            result.put("hottestCity", "--");
            result.put("hottestTemp", 0);
        }
        
        return AjaxResult.success(result);
    }
}
