package com.weather.mockdata.entity;

public class MockWeatherData {
    private String city;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String weather;
    private int aqi;
    private String aqiLevel;
    private long timestamp;

    // Getter & Setter
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    public String getAqiLevel() { return aqiLevel; }
    public void setAqiLevel(String aqiLevel) { this.aqiLevel = aqiLevel; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String toJson() {
        return String.format(
            "{\"city\":\"%s\",\"temp\":%.1f,\"humidity\":%.1f,\"windSpeed\":%.1f,\"weather\":\"%s\",\"aqi\":%d,\"aqiLevel\":\"%s\",\"timestamp\":%d}",
            city, temperature, humidity, windSpeed, weather, aqi, aqiLevel, timestamp
        );
    }
}
