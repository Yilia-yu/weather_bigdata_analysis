package com.weather.collector;

import com.weather.model.WeatherData;
import java.util.List;

public interface WeatherDataSource {
    String getName();
    WeatherData fetchCity(String city);
    List<WeatherData> fetchAllCities();
    boolean isAvailable();
}
