package com.ruoyi.weather.extreme.mapper;

import java.util.List;
import com.ruoyi.weather.extreme.domain.ExtremeWeatherAlert;

/**
 * 极端天气预警记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
public interface ExtremeWeatherAlertMapper 
{
    /**
     * 查询极端天气预警记录
     * 
     * @param id 极端天气预警记录主键
     * @return 极端天气预警记录
     */
    public ExtremeWeatherAlert selectExtremeWeatherAlertById(Long id);

    /**
     * 查询极端天气预警记录列表
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 极端天气预警记录集合
     */
    public List<ExtremeWeatherAlert> selectExtremeWeatherAlertList(ExtremeWeatherAlert extremeWeatherAlert);

    /**
     * 新增极端天气预警记录
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 结果
     */
    public int insertExtremeWeatherAlert(ExtremeWeatherAlert extremeWeatherAlert);

    /**
     * 修改极端天气预警记录
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 结果
     */
    public int updateExtremeWeatherAlert(ExtremeWeatherAlert extremeWeatherAlert);

    /**
     * 删除极端天气预警记录
     * 
     * @param id 极端天气预警记录主键
     * @return 结果
     */
    public int deleteExtremeWeatherAlertById(Long id);

    /**
     * 批量删除极端天气预警记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteExtremeWeatherAlertByIds(Long[] ids);
}
