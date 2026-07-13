package com.weather.admin.entity;

import java.time.LocalDateTime;

public class AlertRecord {
    private Long id;
    private String city;
    private String alertType;
    private String alertLevel;
    private Double alertValue;
    private Double threshold;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime resolveTime;

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public Double getAlertValue() { return alertValue; }
    public void setAlertValue(Double alertValue) { this.alertValue = alertValue; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getResolveTime() { return resolveTime; }
    public void setResolveTime(LocalDateTime resolveTime) { this.resolveTime = resolveTime; }
}
