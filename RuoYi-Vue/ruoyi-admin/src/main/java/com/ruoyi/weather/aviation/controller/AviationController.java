package com.ruoyi.weather.aviation.controller;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.weather.aviation.mapper.AviationMapper;

/**
 * 航空天气风险分析 Controller
 */
@RestController
@RequestMapping("/aviation")
public class AviationController {

    @Autowired
    private AviationMapper aviationMapper;

    /** Tab1: 机场实时风险看板 */
    @GetMapping("/dashboard")
    public AjaxResult dashboard() {
        Map<String, Object> result = new HashMap<>();

        // 获取最新天气数据
        List<Map<String, Object>> weatherList = aviationMapper.selectLatestWeather();

        // 统计风险
        int safeCount = 0, delayCount = 0, highRiskCount = 0;
        List<Map<String, Object>> riskAirports = new ArrayList<>();

        for (Map<String, Object> w : weatherList) {
            String city = (String) w.get("city_name");
            Object tempObj = w.get("temperature");
            Object windObj = w.get("wind_speed");
            Object humObj = w.get("humidity");

            double wind = windObj != null ? ((Number) windObj).doubleValue() : 0;
            double hum = humObj != null ? ((Number) humObj).doubleValue() : 0;

            // 判定风险等级
            String windLevel = getWindLevel(wind);
            String rainLevel = getRainLevel(hum);

            String worstLevel = getWorstLevel(windLevel, rainLevel);
            String riskType = null;
            double riskValue = 0;
            String riskUnit = null;

            if (!"正常".equals(windLevel) && !"正常".equals(rainLevel)) {
                riskType = "综合";
                riskValue = Math.max(wind, hum);
                riskUnit = wind >= hum ? "km/h" : "%";
            } else if (!"正常".equals(windLevel)) {
                riskType = "大风";
                riskValue = wind;
                riskUnit = "km/h";
            } else if (!"正常".equals(rainLevel)) {
                riskType = "暴雨";
                riskValue = hum;
                riskUnit = "%";
            }

            if ("蓝色".equals(worstLevel) || "正常".equals(worstLevel)) {
                safeCount++;
            } else if ("黄色".equals(worstLevel)) {
                delayCount++;
                Map<String, Object> risk = new HashMap<>();
                risk.put("city", city);
                risk.put("type", riskType);
                risk.put("level", worstLevel);
                risk.put("value", riskValue);
                risk.put("unit", riskUnit);
                risk.put("wind", wind);
                risk.put("humidity", hum);
                risk.put("weather", w.get("weather"));
                risk.put("aqi", w.get("aqi"));
                riskAirports.add(risk);
            } else {
                highRiskCount++;
                Map<String, Object> risk = new HashMap<>();
                risk.put("city", city);
                risk.put("type", riskType);
                risk.put("level", worstLevel);
                risk.put("value", riskValue);
                risk.put("unit", riskUnit);
                risk.put("wind", wind);
                risk.put("humidity", hum);
                risk.put("weather", w.get("weather"));
                risk.put("aqi", w.get("aqi"));
                riskAirports.add(risk);
            }
        }

        result.put("safeAirports", safeCount);
        result.put("delayWarning", delayCount);
        result.put("highRisk", highRiskCount);

        // 受影响航线：10条预设航线中，只要出发或到达城市有风险（黄色及以上）就算受影响
        Set<String> riskCityNames = new HashSet<>();
        for (Map<String, Object> r : riskAirports) {
            riskCityNames.add((String) r.get("city"));
        }
        String[][] routeDefs2 = {
            {"北京","上海"},{"北京","广州"},{"北京","成都"},{"上海","广州"},{"上海","成都"},
            {"深圳","北京"},{"昆明","成都"},{"西安","乌鲁木齐"},{"武汉","广州"},{"哈尔滨","北京"}
        };
        int affectedRoutes = 0;
        for (String[] pair : routeDefs2) {
            if (riskCityNames.contains(pair[0]) || riskCityNames.contains(pair[1])) {
                affectedRoutes++;
            }
        }
        result.put("affectedRoutes", affectedRoutes);
        result.put("riskAirports", riskAirports);

        // 预警事件流
        result.put("recentAlerts", aviationMapper.selectRecentAlerts());

        // 所有城市天气（地图用）
        result.put("allWeather", weatherList);

        return AjaxResult.success(result);
    }

    /** Tab2: 航线天气分析 */
    @GetMapping("/routes")
    public AjaxResult routes() {
        List<Map<String, Object>> weatherList = aviationMapper.selectLatestWeather();
        
        // 构建城市->天气映射
        Map<String, Map<String, Object>> weatherMap = new HashMap<>();
        for (Map<String, Object> w : weatherList) {
            weatherMap.put((String) w.get("city_name"), w);
        }

        // 10条预设航线
        String[][] routeDefs = {
            {"北京", "上海", "PEK", "PVG", "京沪线", "1088", "98"},
            {"北京", "广州", "PEK", "CAN", "京广线", "1889", "76"},
            {"北京", "成都", "PEK", "CTU", "京蓉线", "1541", "62"},
            {"上海", "广州", "PVG", "CAN", "沪穗线", "1206", "82"},
            {"上海", "成都", "PVG", "CTU", "沪蓉线", "1651", "55"},
            {"深圳", "北京", "SZX", "PEK", "深京线", "1951", "70"},
            {"昆明", "成都", "KMG", "CTU", "昆蓉线", "614", "48"},
            {"西安", "乌鲁木齐", "XIY", "URC", "西乌线", "2095", "22"},
            {"武汉", "广州", "WUH", "CAN", "武广线", "836", "45"},
            {"哈尔滨", "北京", "HRB", "PEK", "哈京线", "1010", "52"},
        };

        List<Map<String, Object>> routes = new ArrayList<>();
        for (String[] r : routeDefs) {
            Map<String, Object> dep = weatherMap.get(r[0]);
            Map<String, Object> arr = weatherMap.get(r[1]);

            if (dep == null || arr == null) continue; // 跳过没有天气数据的航线

            double depWind = getDouble(dep, "wind_speed");
            double depHum = getDouble(dep, "humidity");
            double depTemp = getDouble(dep, "temperature");
            double arrWind = getDouble(arr, "wind_speed");
            double arrHum = getDouble(arr, "humidity");
            double arrTemp = getDouble(arr, "temperature");

            int riskScore = calcRouteRisk(depWind, depHum, depTemp, dep.get("aqi"),
                                          arrWind, arrHum, arrTemp, arr.get("aqi"));

            String status, tagType, color, advice;
            if (riskScore < 30) {
                status = "适航"; tagType = "success"; color = "#67c23a";
                advice = "天气良好，适合飞行";
            } else if (riskScore < 50) {
                status = "关注"; tagType = "warning"; color = "#e6a23c";
                advice = "建议关注天气变化";
            } else if (riskScore < 70) {
                status = "谨慎"; tagType = "danger"; color = "#ff8800";
                advice = "注意风险，备选航线";
            } else {
                status = "建议备降"; tagType = "danger"; color = "#f56c6c";
                advice = "高风险，建议备降";
            }

            Map<String, Object> route = new HashMap<>();
            route.put("name", r[0] + " → " + r[1]);
            route.put("routeName", r[4]);
            route.put("depCity", r[0]); route.put("depCode", r[2]);
            route.put("arrCity", r[1]); route.put("arrCode", r[3]);
            route.put("distance", Integer.parseInt(r[5]));
            route.put("flights", Integer.parseInt(r[6]));
            route.put("depTemp", dep != null ? dep.get("temperature") : null);
            route.put("depWeather", dep != null ? dep.get("weather") : null);
            route.put("depWind", depWind);
            route.put("depHumidity", depHum);
            route.put("arrTemp", arr != null ? arr.get("temperature") : null);
            route.put("arrWeather", arr != null ? arr.get("weather") : null);
            route.put("arrWind", arrWind);
            route.put("arrHumidity", arrHum);
            route.put("riskScore", riskScore);
            route.put("status", status);
            route.put("tagType", tagType);
            route.put("color", color);
            route.put("advice", advice);
            routes.add(route);
        }

        // 热力图数据
        List<Object[]> heatmapData = new ArrayList<>();
        String[] riskTypes = {"大风", "暴雨", "综合"};
        for (int i = 0; i < routes.size(); i++) {
            Map<String, Object> rt = routes.get(i);
            double depW = getDoubleVal(rt, "depWind");
            double arrW = getDoubleVal(rt, "arrWind");
            double depH = getDoubleVal(rt, "depHumidity");
            double arrH = getDoubleVal(rt, "arrHumidity");
            
            double windRisk = Math.min(100, (depW - 15) / 20 * 100 + (arrW - 15) / 20 * 100);
            double rainRisk = Math.min(100, (depH - 70) / 30 * 100 + (arrH - 70) / 30 * 100);
            double overall = getDoubleVal(rt, "riskScore");

            heatmapData.add(new Object[]{i, 0, Math.max(0, Math.round(windRisk))});
            heatmapData.add(new Object[]{i, 1, Math.max(0, Math.round(rainRisk))});
            heatmapData.add(new Object[]{i, 2, Math.max(0, Math.round(overall))});
        }

        Map<String, Object> result = new HashMap<>();
        result.put("routes", routes);
        result.put("heatmapData", heatmapData);
        result.put("riskTypes", riskTypes);
        return AjaxResult.success(result);
    }

    /** Tab3: 趋势统计 */
    @GetMapping("/trend")
    public AjaxResult trend() {
        Map<String, Object> result = new HashMap<>();

        // 补齐7天数据
        List<Map<String, Object>> rawTrend = aviationMapper.selectAirportCountTrend();
        List<Map<String, Object>> rawDist = aviationMapper.selectTypeDistribution();

        result.put("airportCountTrend", fill7Days(rawTrend, "count"));
        result.put("typeDistribution", fill7DaysType(rawDist));
        result.put("reliabilityRank", aviationMapper.selectReliabilityRank());
        result.put("calendar", aviationMapper.selectCalendarData());

        return AjaxResult.success(result);
    }

    // ====== 工具方法 ======

    private String getWindLevel(double wind) {
        if (wind >= 35) return "红色";
        if (wind >= 30) return "橙色";
        if (wind >= 25) return "黄色";
        if (wind >= 20) return "蓝色";
        return "正常";
    }

    private String getRainLevel(double hum) {
        if (hum >= 98) return "红色";
        if (hum >= 95) return "橙色";
        if (hum >= 90) return "黄色";
        if (hum >= 85) return "蓝色";
        return "正常";
    }

    private String getWorstLevel(String w, String r) {
        String[] order = {"正常", "蓝色", "黄色", "橙色", "红色"};
        int wi = 0, ri = 0;
        for (int i = 0; i < order.length; i++) {
            if (order[i].equals(w)) wi = i;
            if (order[i].equals(r)) ri = i;
        }
        return order[Math.max(wi, ri)];
    }

    private double getDouble(Map<String, Object> map, String key) {
        if (map == null) return 0;
        Object v = map.get(key);
        return v != null ? ((Number) v).doubleValue() : 0;
    }

    private double getDoubleVal(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? ((Number) v).doubleValue() : 0;
    }

    private int calcRouteRisk(double depWind, double depHum, double depTemp, Object depAqi,
                               double arrWind, double arrHum, double arrTemp, Object arrAqi) {
        double score = 0;
        score += Math.max(0, (depWind - 15) / 20.0 * 40);
        score += Math.max(0, (arrWind - 15) / 20.0 * 40);
        score += Math.max(0, (depHum - 70) / 30.0 * 30);
        score += Math.max(0, (arrHum - 70) / 30.0 * 30);
        // AQI
        double dAqi = depAqi != null ? ((Number) depAqi).doubleValue() : 0;
        double aAqi = arrAqi != null ? ((Number) arrAqi).doubleValue() : 0;
        if (dAqi > 150) score += 20; else if (dAqi > 100) score += 10;
        if (aAqi > 150) score += 20; else if (aAqi > 100) score += 10;
        if (depWind >= 30 || depHum >= 95) score += 15;
        if (arrWind >= 30 || arrHum >= 95) score += 15;
        return Math.min(100, (int) Math.round(score));
    }

    private List<Map<String, Object>> fill7Days(List<Map<String, Object>> raw, String valKey) {
        List<Map<String, Object>> result = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -6);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");
        java.text.SimpleDateFormat dbFmt = new java.text.SimpleDateFormat("yyyy-MM-dd");

        // 构建日期->值的映射
        Map<String, Integer> map = new HashMap<>();
        for (Map<String, Object> r : raw) {
            Object d = r.get("date");
            Object c = r.get(valKey);
            if (d != null) {
                String key = d instanceof java.sql.Date ? dbFmt.format((java.sql.Date) d) : d.toString().substring(0, 10);
                map.put(key, c != null ? ((Number) c).intValue() : 0);
            }
        }

        for (int i = 0; i < 7; i++) {
            Map<String, Object> item = new HashMap<>();
            String dateKey = dbFmt.format(cal.getTime());
            item.put("date", sdf.format(cal.getTime()));
            item.put(valKey, map.getOrDefault(dateKey, 0));
            result.add(item);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }

    private List<Map<String, Object>> fill7DaysType(List<Map<String, Object>> raw) {
        List<Map<String, Object>> result = new ArrayList<>();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -6);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MM-dd");
        java.text.SimpleDateFormat dbFmt = new java.text.SimpleDateFormat("yyyy-MM-dd");

        Map<String, int[]> map = new HashMap<>();
        for (Map<String, Object> r : raw) {
            Object d = r.get("date");
            if (d != null) {
                String key = d instanceof java.sql.Date ? dbFmt.format((java.sql.Date) d) : d.toString().substring(0, 10);
                int w = r.get("wind") != null ? ((Number) r.get("wind")).intValue() : 0;
                int rain = r.get("rain") != null ? ((Number) r.get("rain")).intValue() : 0;
                map.put(key, new int[]{w, rain});
            }
        }

        for (int i = 0; i < 7; i++) {
            Map<String, Object> item = new HashMap<>();
            String dateKey = dbFmt.format(cal.getTime());
            int[] vals = map.getOrDefault(dateKey, new int[]{0, 0});
            item.put("date", sdf.format(cal.getTime()));
            item.put("wind", vals[0]);
            item.put("rain", vals[1]);
            result.add(item);
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }
        return result;
    }
}
