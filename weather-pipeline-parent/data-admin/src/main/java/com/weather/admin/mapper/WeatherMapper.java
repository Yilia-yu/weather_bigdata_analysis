package com.weather.admin.mapper;

import com.weather.admin.entity.CityWeather;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WeatherMapper {

    @Select("SELECT wh.* FROM weather_history wh " +
            "INNER JOIN (SELECT city, MAX(id) AS max_id FROM weather_history GROUP BY city) latest " +
            "ON wh.city = latest.city AND wh.id = latest.max_id " +
            "ORDER BY wh.city")
    List<CityWeather> findLatest();

    @Select("SELECT * FROM weather_history WHERE city = #{city} ORDER BY id DESC LIMIT 1")
    CityWeather findByCity(@Param("city") String city);

    @Select("SELECT DISTINCT city FROM weather_history")
    List<String> findAllCities();

    @Select("SELECT COUNT(DISTINCT city) FROM weather_history")
    int countCities();

    @Select("SELECT ROUND(AVG(temperature), 1) FROM weather_history")
    Double getAvgTemperature();

    @Select("SELECT city, MAX(temperature) as maxTemp FROM weather_history GROUP BY city ORDER BY maxTemp DESC LIMIT 1")
    CityWeather getHottestCity();
}
