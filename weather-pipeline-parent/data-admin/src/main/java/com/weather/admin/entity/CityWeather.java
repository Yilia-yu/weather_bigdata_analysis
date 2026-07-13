package com.weather.admin.entity;

import java.time.LocalDateTime;

public class CityWeather {
    private Long id;
    private String city;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String weather;
    private Integer aqi;
    private String aqiLevel;
    private String updateTime;
    private LocalDateTime createTime;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public Integer getAqi() { return aqi; }
    public void setAqi(Integer aqi) { this.aqi = aqi; }

    public String getAqiLevel() { return aqiLevel; }
    public void setAqiLevel(String aqiLevel) { this.aqiLevel = aqiLevel; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
