package com.ruoyi.weather.record.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 天气数据记录对象 biz_weather_record
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public class BizWeatherRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 城市名称 */
    @Excel(name = "城市名称")
    private String cityName;

    /** 所属省份 */
    @Excel(name = "所属省份")
    private String province;

    /** 所属区域 */
    @Excel(name = "所属区域")
    private String region;

    /** 温度(°C) */
    @Excel(name = "温度(°C)")
    private BigDecimal temperature;

    /** 湿度(%) */
    @Excel(name = "湿度(%)")
    private BigDecimal humidity;

    /** 风速(km/h) */
    @Excel(name = "风速(km/h)")
    private BigDecimal windSpeed;

    /** 天气(晴/多云/雨/雪等) */
    @Excel(name = "天气(晴/多云/雨/雪等)")
    private String weather;

    /** 空气质量指数 */
    @Excel(name = "空气质量指数")
    private Long aqi;

    /** 空气质量等级 */
    @Excel(name = "空气质量等级")
    private String aqiLevel;

    /** 数据来源 */
    @Excel(name = "数据来源")
    private String dataSource;

    /** 记录时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "记录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

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

    public void setRegion(String region) 
    {
        this.region = region;
    }

    public String getRegion() 
    {
        return region;
    }

    public void setTemperature(BigDecimal temperature) 
    {
        this.temperature = temperature;
    }

    public BigDecimal getTemperature() 
    {
        return temperature;
    }

    public void setHumidity(BigDecimal humidity) 
    {
        this.humidity = humidity;
    }

    public BigDecimal getHumidity() 
    {
        return humidity;
    }

    public void setWindSpeed(BigDecimal windSpeed) 
    {
        this.windSpeed = windSpeed;
    }

    public BigDecimal getWindSpeed() 
    {
        return windSpeed;
    }

    public void setWeather(String weather) 
    {
        this.weather = weather;
    }

    public String getWeather() 
    {
        return weather;
    }

    public void setAqi(Long aqi) 
    {
        this.aqi = aqi;
    }

    public Long getAqi() 
    {
        return aqi;
    }

    public void setAqiLevel(String aqiLevel) 
    {
        this.aqiLevel = aqiLevel;
    }

    public String getAqiLevel() 
    {
        return aqiLevel;
    }

    public void setDataSource(String dataSource) 
    {
        this.dataSource = dataSource;
    }

    public String getDataSource() 
    {
        return dataSource;
    }

    public void setRecordTime(Date recordTime) 
    {
        this.recordTime = recordTime;
    }

    public Date getRecordTime() 
    {
        return recordTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cityName", getCityName())
            .append("province", getProvince())
            .append("region", getRegion())
            .append("temperature", getTemperature())
            .append("humidity", getHumidity())
            .append("windSpeed", getWindSpeed())
            .append("weather", getWeather())
            .append("aqi", getAqi())
            .append("aqiLevel", getAqiLevel())
            .append("dataSource", getDataSource())
            .append("recordTime", getRecordTime())
            .append("createTime", getCreateTime())
            .toString();
    }
}
