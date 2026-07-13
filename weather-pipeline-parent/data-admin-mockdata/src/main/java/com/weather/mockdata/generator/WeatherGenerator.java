package com.weather.mockdata.generator;

import com.weather.mockdata.entity.MockWeatherData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 天气数据生成器
 * 生成温度、湿度、风速、天气状况
 */
public class WeatherGenerator {

    private static final Random RANDOM = new Random();

    // 75 个城市（与 data-collector 保持一致）
    private static final String[] CITIES = {
        "北京", "上海", "天津", "重庆", "石家庄", "唐山", "保定",
        "太原", "大同", "呼和浩特", "包头", "沈阳", "大连",
        "长春", "吉林", "哈尔滨", "大庆", "南京", "苏州", "无锡",
        "杭州", "宁波", "温州", "合肥", "芜湖", "福州", "厦门",
        "泉州", "南昌", "九江", "赣州", "济南", "青岛", "烟台",
        "郑州", "洛阳", "南阳", "武汉", "宜昌", "襄阳",
        "长沙", "岳阳", "张家界", "广州", "深圳", "珠海",
        "南宁", "桂林", "北海", "海口", "三亚", "成都",
        "绵阳", "乐山", "贵阳", "遵义", "昆明", "丽江",
        "大理", "拉萨", "日喀则", "西安", "咸阳", "延安",
        "兰州", "天水", "敦煌", "西宁", "海东", "银川",
        "石嘴山", "乌鲁木齐", "克拉玛依", "喀什", "伊犁"
    };

    // 各城市基础温度（模拟真实季节温度）
    private static final int[] BASE_TEMPS = {
        28,26,27,29,26,25,24,
        27,25,24,25,27,26,
        26,25,26,24,28,27,28,
        27,26,25,26,25,28,29,
        26,28,27,26,28,27,26,
        27,26,25,29,27,26,
        28,27,26,32,30,29,
        29,28,27,30,28,28,
        27,26,27,25,26,24,
        23,22,20,27,26,25,
        26,25,24,24,23,26,
        25,28,27,26,25
    };

    private static final String[] WEATHERS = {"晴", "多云", "阴", "小雨", "中雨", "阵雨", "雷阵雨", "雾", "晴转多云", "多云转阴"};
    private static final String[] AQI_LEVELS = {"优", "良", "轻度污染", "中度污染", "重度污染"};

    public static List<MockWeatherData> generate(int count) {
        List<MockWeatherData> result = new ArrayList<>();
        int cityCount = CITIES.length;

        for (int i = 0; i < count && i < cityCount; i++) {
            result.add(generateSingle(i));
        }
        return result;
    }

    public static MockWeatherData generateSingle(int index) {
        int cityIdx = index % CITIES.length;
        String city = CITIES[cityIdx];
        int baseTemp = BASE_TEMPS[cityIdx % BASE_TEMPS.length];

        // 温度浮动 ±4°C
        double temp = baseTemp + (RANDOM.nextDouble() - 0.5) * 8;
        double humidity = 30 + RANDOM.nextDouble() * 50;
        double windSpeed = 1 + RANDOM.nextDouble() * 12;
        String weather = WEATHERS[RANDOM.nextInt(WEATHERS.length)];
        int aqi = 20 + RANDOM.nextInt(180);
        String aqiLevel = getAqiLevel(aqi);

        MockWeatherData data = new MockWeatherData();
        data.setCity(city);
        data.setTemperature(Math.round(temp * 10) / 10.0);
        data.setHumidity(Math.round(humidity * 10) / 10.0);
        data.setWindSpeed(Math.round(windSpeed * 10) / 10.0);
        data.setWeather(weather);
        data.setAqi(aqi);
        data.setAqiLevel(aqiLevel);
        data.setTimestamp(System.currentTimeMillis());
        return data;
    }

    public static List<MockWeatherData> generateAll() {
        List<MockWeatherData> result = new ArrayList<>();
        for (int i = 0; i < CITIES.length; i++) {
            result.add(generateSingle(i));
        }
        return result;
    }

    private static String getAqiLevel(int aqi) {
        if (aqi <= 50) return "优";
        if (aqi <= 100) return "良";
        if (aqi <= 150) return "轻度污染";
        if (aqi <= 200) return "中度污染";
        return "重度污染";
    }

    public static int getCityCount() {
        return CITIES.length;
    }
}
