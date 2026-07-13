package com.weather.collector;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.weather.model.WeatherData;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OpenWeatherSource implements WeatherDataSource {

    private static final String API_KEY = "82d3d1ed3d8b49712ecf7c64126bf377";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    private final CloseableHttpClient httpClient;
    private final List<String> cities;

    public OpenWeatherSource() {
        this.httpClient = HttpClients.createDefault();
        this.cities = loadCities();
        System.out.println("   [OpenWeatherSource] 加载了 " + this.cities.size() + " 个城市");
    }

    private List<String> loadCities() {
        List<String> result = new ArrayList<>();
        try {
            // 使用类加载器读取资源文件
            InputStream is = OpenWeatherSource.class.getClassLoader().getResourceAsStream("cities.txt");
            if (is == null) {
                System.err.println("   [OpenWeatherSource] 找不到 cities.txt 文件！");
                return getDefaultCities();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",");
                    result.add(parts[0].trim());
                }
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("   [OpenWeatherSource] 加载城市列表失败: " + e.getMessage());
            return getDefaultCities();
        }
        return result;
    }

    private List<String> getDefaultCities() {
        List<String> list = new ArrayList<>();
        list.add("Beijing");
        list.add("Shanghai");
        list.add("Guangzhou");
        list.add("Shenzhen");
        list.add("Chengdu");
        list.add("Wuhan");
        list.add("Hangzhou");
        list.add("Chongqing");
        list.add("Nanjing");
        list.add("Xi'an");
        return list;
    }

    @Override
    public String getName() {
        return "OpenWeatherMap";
    }

    @Override
    public boolean isAvailable() {
        try {
            WeatherData data = fetchCity("Beijing");
            return data != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public WeatherData fetchCity(String city) {
        try {
            // 城市名转拼音（部分城市需要英文化）
            String englishCity = toEnglishCity(city);
            String encodedCity = URLEncoder.encode(englishCity, StandardCharsets.UTF_8.name());
            String url = BASE_URL + "?q=" + encodedCity + "&appid=" + API_KEY + "&units=metric";
            HttpGet request = new HttpGet(url);
            request.setHeader("User-Agent", "Mozilla/5.0");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    return null;
                }
                String json = EntityUtils.toString(response.getEntity());
                return parseWeatherData(city, json);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String toEnglishCity(String city) {
        // 常用城市名映射
        switch (city) {
            case "北京": return "Beijing";
            case "上海": return "Shanghai";
            case "天津": return "Tianjin";
            case "重庆": return "Chongqing";
            case "石家庄": return "Shijiazhuang";
            case "唐山": return "Tangshan";
            case "保定": return "Baoding";
            case "太原": return "Taiyuan";
            case "大同": return "Datong";
            case "呼和浩特": return "Hohhot";
            case "包头": return "Baotou";
            case "沈阳": return "Shenyang";
            case "大连": return "Dalian";
            case "长春": return "Changchun";
            case "吉林": return "Jilin";
            case "哈尔滨": return "Harbin";
            case "大庆": return "Daqing";
            case "南京": return "Nanjing";
            case "苏州": return "Suzhou";
            case "无锡": return "Wuxi";
            case "杭州": return "Hangzhou";
            case "宁波": return "Ningbo";
            case "温州": return "Wenzhou";
            case "合肥": return "Hefei";
            case "芜湖": return "Wuhu";
            case "福州": return "Fuzhou";
            case "厦门": return "Xiamen";
            case "泉州": return "Quanzhou";
            case "南昌": return "Nanchang";
            case "九江": return "Jiujiang";
            case "赣州": return "Ganzhou";
            case "济南": return "Jinan";
            case "青岛": return "Qingdao";
            case "烟台": return "Yantai";
            case "郑州": return "Zhengzhou";
            case "洛阳": return "Luoyang";
            case "南阳": return "Nanyang";
            case "武汉": return "Wuhan";
            case "宜昌": return "Yichang";
            case "襄阳": return "Xiangyang";
            case "长沙": return "Changsha";
            case "岳阳": return "Yueyang";
            case "张家界": return "Zhangjiajie";
            case "广州": return "Guangzhou";
            case "深圳": return "Shenzhen";
            case "珠海": return "Zhuhai";
            case "南宁": return "Nanning";
            case "桂林": return "Guilin";
            case "北海": return "Beihai";
            case "海口": return "Haikou";
            case "三亚": return "Sanya";
            case "成都": return "Chengdu";
            case "绵阳": return "Mianyang";
            case "乐山": return "Leshan";
            case "贵阳": return "Guiyang";
            case "遵义": return "Zunyi";
            case "昆明": return "Kunming";
            case "丽江": return "Lijiang";
            case "大理": return "Dali";
            case "拉萨": return "Lhasa";
            case "日喀则": return "Shigatse";
            case "西安": return "Xi'an";
            case "咸阳": return "Xianyang";
            case "延安": return "Yan'an";
            case "兰州": return "Lanzhou";
            case "天水": return "Tianshui";
            case "敦煌": return "Dunhuang";
            case "西宁": return "Xining";
            case "海东": return "Haidong";
            case "银川": return "Yinchuan";
            case "石嘴山": return "Shizuishan";
            case "乌鲁木齐": return "Urumqi";
            case "克拉玛依": return "Karamay";
            case "喀什": return "Kashgar";
            case "伊犁": return "Yili";
            default: return city;
        }
    }

    @Override
    public List<WeatherData> fetchAllCities() {
        List<WeatherData> result = new ArrayList<>();
        int success = 0;
        int fail = 0;
        System.out.println("   [OpenWeatherMap] 开始采集 " + cities.size() + " 个城市...");

        for (String city : cities) {
            WeatherData data = fetchCity(city);
            if (data != null) {
                result.add(data);
                success++;
            } else {
                fail++;
            }
            if ((success + fail) % 10 == 0) {
                System.out.print(".");
            }
            try { Thread.sleep(300); } catch (InterruptedException e) {}
        }
        System.out.println();
        System.out.println("   [OpenWeatherMap] 成功: " + success + ", 失败: " + fail);
        return result;
    }

    private WeatherData parseWeatherData(String city, String json) {
        try {
            JSONObject root = JSONObject.parseObject(json);
            JSONObject main = root.getJSONObject("main");
            JSONObject wind = root.getJSONObject("wind");
            JSONArray weatherArray = root.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);

            WeatherData data = new WeatherData();
            data.setCity(city);
            data.setTemperature(main.getDouble("temp"));
            data.setHumidity(main.getDouble("humidity"));
            data.setWindSpeed(wind.getDouble("speed"));
            data.setWeatherText(weather.getString("description"));
            data.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
            return data;
        } catch (Exception e) {
            return null;
        }
    }
}
