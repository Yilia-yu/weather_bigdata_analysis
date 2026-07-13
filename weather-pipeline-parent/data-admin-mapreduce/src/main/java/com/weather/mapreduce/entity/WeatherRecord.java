package com.weather.mapreduce.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Writable;

public class WeatherRecord implements Writable {
    private String city;
    private double temperature;
    private double humidity;
    private double windSpeed;
    private String weather;
    private String date;

    public WeatherRecord() {}

    public WeatherRecord(String city, double temperature, double humidity, 
                         double windSpeed, String weather, String date) {
        this.city = city;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.weather = weather;
        this.date = date;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeUTF(city);
        out.writeDouble(temperature);
        out.writeDouble(humidity);
        out.writeDouble(windSpeed);
        out.writeUTF(weather);
        out.writeUTF(date);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        city = in.readUTF();
        temperature = in.readDouble();
        humidity = in.readDouble();
        windSpeed = in.readDouble();
        weather = in.readUTF();
        date = in.readUTF();
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

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return city + "\t" + temperature + "\t" + humidity + "\t" + windSpeed + "\t" + weather + "\t" + date;
    }
}
