package com.ruoyi.weather.city.mapper;

import java.util.List;
import com.ruoyi.weather.city.domain.BizWeatherCity;

/**
 * 天气监测城市Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public interface BizWeatherCityMapper 
{
    /**
     * 查询天气监测城市
     * 
     * @param id 天气监测城市主键
     * @return 天气监测城市
     */
    public BizWeatherCity selectBizWeatherCityById(Long id);

    /**
     * 查询天气监测城市列表
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 天气监测城市集合
     */
    public List<BizWeatherCity> selectBizWeatherCityList(BizWeatherCity bizWeatherCity);

    /**
     * 新增天气监测城市
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 结果
     */
    public int insertBizWeatherCity(BizWeatherCity bizWeatherCity);

    /**
     * 修改天气监测城市
     * 
     * @param bizWeatherCity 天气监测城市
     * @return 结果
     */
    public int updateBizWeatherCity(BizWeatherCity bizWeatherCity);

    /**
     * 删除天气监测城市
     * 
     * @param id 天气监测城市主键
     * @return 结果
     */
    public int deleteBizWeatherCityById(Long id);

    /**
     * 批量删除天气监测城市
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizWeatherCityByIds(Long[] ids);
}
