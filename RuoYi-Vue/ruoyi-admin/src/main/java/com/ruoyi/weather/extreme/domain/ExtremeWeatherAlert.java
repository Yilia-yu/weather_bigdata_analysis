package com.ruoyi.weather.extreme.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 极端天气预警记录对象 extreme_weather_alert
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
public class ExtremeWeatherAlert extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String cityName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String province;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String alertType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String alertLevel;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String metricField;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private BigDecimal alertValue;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String weatherDesc;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date alertTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCityName(String cityName) 
    {
        this.cityName = cityName;
    }

    public String getCityName() 
    {
        return cityName;
    }

    public void setProvince(String province) 
    {
        this.province = province;
    }

    public String getProvince() 
    {
        return province;
    }

    public void setAlertType(String alertType) 
    {
        this.alertType = alertType;
    }

    public String getAlertType() 
    {
        return alertType;
    }

    public void setAlertLevel(String alertLevel) 
    {
        this.alertLevel = alertLevel;
    }

    public String getAlertLevel() 
    {
        return alertLevel;
    }

    public void setMetricField(String metricField) 
    {
        this.metricField = metricField;
    }

    public String getMetricField() 
    {
        return metricField;
    }

    public void setAlertValue(BigDecimal alertValue) 
    {
        this.alertValue = alertValue;
    }

    public BigDecimal getAlertValue() 
    {
        return alertValue;
    }

    public void setWeatherDesc(String weatherDesc) 
    {
        this.weatherDesc = weatherDesc;
    }

    public String getWeatherDesc() 
    {
        return weatherDesc;
    }

    public void setAlertTime(Date alertTime) 
    {
        this.alertTime = alertTime;
    }

    public Date getAlertTime() 
    {
        return alertTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cityName", getCityName())
            .append("province", getProvince())
            .append("alertType", getAlertType())
            .append("alertLevel", getAlertLevel())
            .append("metricField", getMetricField())
            .append("alertValue", getAlertValue())
            .append("weatherDesc", getWeatherDesc())
            .append("alertTime", getAlertTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
