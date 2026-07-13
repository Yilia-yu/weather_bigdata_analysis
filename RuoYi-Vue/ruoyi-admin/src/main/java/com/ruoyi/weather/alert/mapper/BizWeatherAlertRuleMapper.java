package com.ruoyi.weather.alert.mapper;

import java.util.List;
import com.ruoyi.weather.alert.domain.BizWeatherAlertRule;

/**
 * 天气告警规则配置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public interface BizWeatherAlertRuleMapper 
{
    /**
     * 查询天气告警规则配置
     * 
     * @param id 天气告警规则配置主键
     * @return 天气告警规则配置
     */
    public BizWeatherAlertRule selectBizWeatherAlertRuleById(Long id);

    /**
     * 查询天气告警规则配置列表
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 天气告警规则配置集合
     */
    public List<BizWeatherAlertRule> selectBizWeatherAlertRuleList(BizWeatherAlertRule bizWeatherAlertRule);

    /**
     * 新增天气告警规则配置
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 结果
     */
    public int insertBizWeatherAlertRule(BizWeatherAlertRule bizWeatherAlertRule);

    /**
     * 修改天气告警规则配置
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 结果
     */
    public int updateBizWeatherAlertRule(BizWeatherAlertRule bizWeatherAlertRule);

    /**
     * 删除天气告警规则配置
     * 
     * @param id 天气告警规则配置主键
     * @return 结果
     */
    public int deleteBizWeatherAlertRuleById(Long id);

    /**
     * 批量删除天气告警规则配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizWeatherAlertRuleByIds(Long[] ids);
}
