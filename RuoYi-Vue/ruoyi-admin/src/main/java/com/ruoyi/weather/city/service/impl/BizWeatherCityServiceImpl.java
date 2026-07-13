package com.ruoyi.weather.city.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.city.mapper.BizWeatherCityMapper;
import com.ruoyi.weather.city.domain.BizWeatherCity;
import com.ruoyi.weather.city.service.IBizWeatherCityService;

/**
 * 天气监测城市Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@Service
public class BizWeatherCityServiceImpl implements IBizWeatherCityService 
{
    @Autowired
    private BizWeatherCityMapper bizWeatherCityMapper;

    /**
     * 查询天气监测城市
     * 
     * @param id 天气监测城市主键
     * @return 天气监测城市
     */
    @Override
    public BizWeatherCity selectBizWeatherCityById(Long id)
    {
        return bizWeatherCityMapper.selectBizWeatherCityById(id);
    }

    /**
     * 查询天气监测城市列表
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 天气监测城市
     */
    @Override
    public List<BizWeatherCity> selectBizWeatherCityList(BizWeatherCity bizWeatherCity)
    {
        return bizWeatherCityMapper.selectBizWeatherCityList(bizWeatherCity);
    }

    /**
     * 新增天气监测城市
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 结果
     */
    @Override
    public int insertBizWeatherCity(BizWeatherCity bizWeatherCity)
    {
        bizWeatherCity.setCreateTime(DateUtils.getNowDate());
        return bizWeatherCityMapper.insertBizWeatherCity(bizWeatherCity);
    }

    /**
     * 修改天气监测城市
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 结果
     */
    @Override
    public int updateBizWeatherCity(BizWeatherCity bizWeatherCity)
    {
        bizWeatherCity.setUpdateTime(DateUtils.getNowDate());
        return bizWeatherCityMapper.updateBizWeatherCity(bizWeatherCity);
    }

    /**
     * 批量删除天气监测城市
     * 
     * @param ids 需要删除的天气监测城市主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherCityByIds(Long[] ids)
    {
        return bizWeatherCityMapper.deleteBizWeatherCityByIds(ids);
    }

    /**
     * 删除天气监测城市信息
     * 
     * @param id 天气监测城市主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherCityById(Long id)
    {
        return bizWeatherCityMapper.deleteBizWeatherCityById(id);
    }
}
