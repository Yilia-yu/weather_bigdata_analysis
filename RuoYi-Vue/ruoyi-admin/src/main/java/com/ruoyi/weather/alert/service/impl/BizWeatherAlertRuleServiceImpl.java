package com.ruoyi.weather.alert.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.alert.mapper.BizWeatherAlertRuleMapper;
import com.ruoyi.weather.alert.domain.BizWeatherAlertRule;
import com.ruoyi.weather.alert.service.IBizWeatherAlertRuleService;

/**
 * 天气告警规则配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@Service
public class BizWeatherAlertRuleServiceImpl implements IBizWeatherAlertRuleService 
{
    @Autowired
    private BizWeatherAlertRuleMapper bizWeatherAlertRuleMapper;

    /**
     * 查询天气告警规则配置
     * 
     * @param id 天气告警规则配置主键
     * @return 天气告警规则配置
     */
    @Override
    public BizWeatherAlertRule selectBizWeatherAlertRuleById(Long id)
    {
        return bizWeatherAlertRuleMapper.selectBizWeatherAlertRuleById(id);
    }

    /**
     * 查询天气告警规则配置列表
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 天气告警规则配置
     */
    @Override
    public List<BizWeatherAlertRule> selectBizWeatherAlertRuleList(BizWeatherAlertRule bizWeatherAlertRule)
    {
        return bizWeatherAlertRuleMapper.selectBizWeatherAlertRuleList(bizWeatherAlertRule);
    }

    /**
     * 新增天气告警规则配置
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 结果
     */
    @Override
    public int insertBizWeatherAlertRule(BizWeatherAlertRule bizWeatherAlertRule)
    {
        bizWeatherAlertRule.setCreateTime(DateUtils.getNowDate());
        return bizWeatherAlertRuleMapper.insertBizWeatherAlertRule(bizWeatherAlertRule);
    }

    /**
     * 修改天气告警规则配置
     * 
     * @param bizWeatherAlertRule 天气告警规则配置
     * @return 结果
     */
    @Override
    public int updateBizWeatherAlertRule(BizWeatherAlertRule bizWeatherAlertRule)
    {
        bizWeatherAlertRule.setUpdateTime(DateUtils.getNowDate());
        return bizWeatherAlertRuleMapper.updateBizWeatherAlertRule(bizWeatherAlertRule);
    }

    /**
     * 批量删除天气告警规则配置
     * 
     * @param ids 需要删除的天气告警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherAlertRuleByIds(Long[] ids)
    {
        return bizWeatherAlertRuleMapper.deleteBizWeatherAlertRuleByIds(ids);
    }

    /**
     * 删除天气告警规则配置信息
     * 
     * @param id 天气告警规则配置主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherAlertRuleById(Long id)
    {
        return bizWeatherAlertRuleMapper.deleteBizWeatherAlertRuleById(id);
    }
}
