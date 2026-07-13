package com.weather.mapreduce.entity;

public class CityStat {
    private String city;
    private double avgTemp;
    private double maxTemp;
    private double minTemp;
    private double avgHumidity;
    private int recordCount;

    public CityStat() {}

    public CityStat(String city, double avgTemp, double maxTemp, double minTemp, 
                    double avgHumidity, int recordCount) {
        this.city = city;
        this.avgTemp = avgTemp;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.avgHumidity = avgHumidity;
        this.recordCount = recordCount;
    }

    // Getter & Setter
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public double getAvgTemp() { return avgTemp; }
    public void setAvgTemp(double avgTemp) { this.avgTemp = avgTemp; }

    public double getMaxTemp() { return maxTemp; }
    public void setMaxTemp(double maxTemp) { this.maxTemp = maxTemp; }

    public double getMinTemp() { return minTemp; }
    public void setMinTemp(double minTemp) { this.minTemp = minTemp; }

    public double getAvgHumidity() { return avgHumidity; }
    public void setAvgHumidity(double avgHumidity) { this.avgHumidity = avgHumidity; }

    public int getRecordCount() { return recordCount; }
    public void setRecordCount(int recordCount) { this.recordCount = recordCount; }

    public String toInsertSQL() {
        return String.format(
            "INSERT INTO weather_daily (city, avg_temp, max_temp, min_temp, avg_humidity, record_count, update_date) " +
            "VALUES ('%s', %.1f, %.1f, %.1f, %.1f, %d, CURDATE()) " +
            "ON DUPLICATE KEY UPDATE avg_temp=%.1f, max_temp=%.1f, min_temp=%.1f, avg_humidity=%.1f, record_count=%d",
            city, avgTemp, maxTemp, minTemp, avgHumidity, recordCount,
            avgTemp, maxTemp, minTemp, avgHumidity, recordCount
        );
    }
}
