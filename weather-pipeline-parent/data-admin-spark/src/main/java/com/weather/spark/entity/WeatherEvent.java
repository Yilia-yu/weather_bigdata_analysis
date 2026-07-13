package com.weather.spark.entity;

import java.io.Serializable;

public class WeatherEvent implements Serializable {
    private String city;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String weather;
    private long timestamp;

    public WeatherEvent() {}

    public WeatherEvent(String city, double temperature, double humidity, 
                        double windSpeed, String weather, long timestamp) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weather = weather;
        this.timestamp = timestamp;
    }

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

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format("WeatherEvent{city='%s', temp=%.1f, humidity=%.1f, weather='%s'}",
                city, temperature, humidity, weather);
    }
}
