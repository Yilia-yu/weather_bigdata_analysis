package com.weather.model;

public class WeatherData {
    private String city;
    private String updateTime;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String weatherText;
    private String windDir;
    private int aqi;
    private String aqiLevel;

    public WeatherData() {}

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getWeatherText() { return weatherText; }
    public void setWeatherText(String weatherText) { this.weatherText = weatherText; }

    public String getWindDir() { return windDir; }
    public void setWindDir(String windDir) { this.windDir = windDir; }

    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    public String getAqiLevel() { return aqiLevel; }
    public void setAqiLevel(String aqiLevel) { this.aqiLevel = aqiLevel; }

    @Override
    public String toString() {
        return String.format("WeatherData{city='%s', temp=%.1f°C, humidity=%.1f%%, weather='%s', aqi=%d}",
                city, temperature, humidity, weatherText, aqi);
    }

    public String toJson() {
        return String.format(
            "{\"city\":\"%s\",\"temp\":%.1f,\"humidity\":%.1f,\"windSpeed\":%.1f,\"weather\":\"%s\",\"windDir\":\"%s\",\"updateTime\":\"%s\",\"aqi\":%d,\"aqiLevel\":\"%s\"}",
            city, temperature, humidity, windSpeed, weatherText, windDir, updateTime, aqi, aqiLevel
        );
    }
}
