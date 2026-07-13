package com.ruoyi.weather.alert.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 天气告警规则配置对象 biz_weather_alert_rule
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public class BizWeatherAlertRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 告警类型(高温/低温/大风/暴雨) */
    @Excel(name = "告警类型(高温/低温/大风/暴雨)")
    private String alertType;

    /** 监测字段(temperature/humidity/wind_speed) */
    @Excel(name = "监测字段(temperature/humidity/wind_speed)")
    private String metricField;

    /** 下限阈值 */
    @Excel(name = "下限阈值")
    private BigDecimal thresholdMin;

    /** 上限阈值 */
    @Excel(name = "上限阈值")
    private BigDecimal thresholdMax;

    /** 告警级别(红色/橙色/黄色/蓝色) */
    @Excel(name = "告警级别(红色/橙色/黄色/蓝色)")
    private String alertLevel;

    /** 状态(1=启用 0=停用) */
    @Excel(name = "状态(1=启用 0=停用)")
    private Long status;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setRuleName(String ruleName) 
    {
        this.ruleName = ruleName;
    }

    public String getRuleName() 
    {
        return ruleName;
    }

    public void setAlertType(String alertType) 
    {
        this.alertType = alertType;
    }

    public String getAlertType() 
    {
        return alertType;
    }

    public void setMetricField(String metricField) 
    {
        this.metricField = metricField;
    }

    public String getMetricField() 
    {
        return metricField;
    }

    public void setThresholdMin(BigDecimal thresholdMin) 
    {
        this.thresholdMin = thresholdMin;
    }

    public BigDecimal getThresholdMin() 
    {
        return thresholdMin;
    }

    public void setThresholdMax(BigDecimal thresholdMax) 
    {
        this.thresholdMax = thresholdMax;
    }

    public BigDecimal getThresholdMax() 
    {
        return thresholdMax;
    }

    public void setAlertLevel(String alertLevel) 
    {
        this.alertLevel = alertLevel;
    }

    public String getAlertLevel() 
    {
        return alertLevel;
    }

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("ruleName", getRuleName())
            .append("alertType", getAlertType())
            .append("metricField", getMetricField())
            .append("thresholdMin", getThresholdMin())
            .append("thresholdMax", getThresholdMax())
            .append("alertLevel", getAlertLevel())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
