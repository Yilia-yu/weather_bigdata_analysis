package com.ruoyi.weather.extreme.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.extreme.mapper.ExtremeWeatherAlertMapper;
import com.ruoyi.weather.extreme.domain.ExtremeWeatherAlert;
import com.ruoyi.weather.extreme.service.IExtremeWeatherAlertService;

/**
 * 极端天气预警记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
@Service
public class ExtremeWeatherAlertServiceImpl implements IExtremeWeatherAlertService 
{
    @Autowired
    private ExtremeWeatherAlertMapper extremeWeatherAlertMapper;

    /**
     * 查询极端天气预警记录
     * 
     * @param id 极端天气预警记录主键
     * @return 极端天气预警记录
     */
    @Override
    public ExtremeWeatherAlert selectExtremeWeatherAlertById(Long id)
    {
        return extremeWeatherAlertMapper.selectExtremeWeatherAlertById(id);
    }

    /**
     * 查询极端天气预警记录列表
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 极端天气预警记录
     */
    @Override
    public List<ExtremeWeatherAlert> selectExtremeWeatherAlertList(ExtremeWeatherAlert extremeWeatherAlert)
    {
        return extremeWeatherAlertMapper.selectExtremeWeatherAlertList(extremeWeatherAlert);
    }

    /**
     * 新增极端天气预警记录
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 结果
     */
    @Override
    public int insertExtremeWeatherAlert(ExtremeWeatherAlert extremeWeatherAlert)
    {
        extremeWeatherAlert.setCreateTime(DateUtils.getNowDate());
        return extremeWeatherAlertMapper.insertExtremeWeatherAlert(extremeWeatherAlert);
    }

    /**
     * 修改极端天气预警记录
     * 
     * @param extremeWeatherAlert 极端天气预警记录
     * @return 结果
     */
    @Override
    public int updateExtremeWeatherAlert(ExtremeWeatherAlert extremeWeatherAlert)
    {
        return extremeWeatherAlertMapper.updateExtremeWeatherAlert(extremeWeatherAlert);
    }

    /**
     * 批量删除极端天气预警记录
     * 
     * @param ids 需要删除的极端天气预警记录主键
     * @return 结果
     */
    @Override
    public int deleteExtremeWeatherAlertByIds(Long[] ids)
    {
        return extremeWeatherAlertMapper.deleteExtremeWeatherAlertByIds(ids);
    }

    /**
     * 删除极端天气预警记录信息
     * 
     * @param id 极端天气预警记录主键
     * @return 结果
     */
    @Override
    public int deleteExtremeWeatherAlertById(Long id)
    {
        return extremeWeatherAlertMapper.deleteExtremeWeatherAlertById(id);
    }
}
