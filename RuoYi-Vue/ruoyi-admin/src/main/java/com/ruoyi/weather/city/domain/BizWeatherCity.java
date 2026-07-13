package com.ruoyi.weather.city.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 天气监测城市对象 biz_weather_city
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public class BizWeatherCity extends BaseEntity
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

    /** 所属区域(华北/东北/华东/华南/华中/西南/西北) */
    @Excel(name = "所属区域(华北/东北/华东/华南/华中/西南/西北)")
    private String region;

    /** 状态(1=监控中 0=已停用) */
    @Excel(name = "状态(1=监控中 0=已停用)")
    private Long status;

    /** 排序 */
    @Excel(name = "排序")
    private Long sortOrder;

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

    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    public void setSortOrder(Long sortOrder) 
    {
        this.sortOrder = sortOrder;
    }

    public Long getSortOrder() 
    {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("cityName", getCityName())
            .append("province", getProvince())
            .append("region", getRegion())
            .append("status", getStatus())
            .append("sortOrder", getSortOrder())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
