package com.ruoyi.weather.record.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.weather.record.mapper.BizWeatherRecordMapper;
import com.ruoyi.weather.record.domain.BizWeatherRecord;
import com.ruoyi.weather.record.service.IBizWeatherRecordService;

/**
 * 天气数据记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@Service
public class BizWeatherRecordServiceImpl implements IBizWeatherRecordService 
{
    @Autowired
    private BizWeatherRecordMapper bizWeatherRecordMapper;

    /**
     * 查询天气数据记录
     * 
     * @param id 天气数据记录主键
     * @return 天气数据记录
     */
    @Override
    public BizWeatherRecord selectBizWeatherRecordById(Long id)
    {
        return bizWeatherRecordMapper.selectBizWeatherRecordById(id);
    }

    /**
     * 查询天气数据记录列表
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 天气数据记录
     */
    @Override
    public List<BizWeatherRecord> selectBizWeatherRecordList(BizWeatherRecord bizWeatherRecord)
    {
        return bizWeatherRecordMapper.selectBizWeatherRecordList(bizWeatherRecord);
    }

    /**
     * 新增天气数据记录
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 结果
     */
    @Override
    public int insertBizWeatherRecord(BizWeatherRecord bizWeatherRecord)
    {
        bizWeatherRecord.setCreateTime(DateUtils.getNowDate());
        return bizWeatherRecordMapper.insertBizWeatherRecord(bizWeatherRecord);
    }

    /**
     * 修改天气数据记录
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 结果
     */
    @Override
    public int updateBizWeatherRecord(BizWeatherRecord bizWeatherRecord)
    {
        return bizWeatherRecordMapper.updateBizWeatherRecord(bizWeatherRecord);
    }

    /**
     * 批量删除天气数据记录
     * 
     * @param ids 需要删除的天气数据记录主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherRecordByIds(Long[] ids)
    {
        return bizWeatherRecordMapper.deleteBizWeatherRecordByIds(ids);
    }

    /**
     * 删除天气数据记录信息
     * 
     * @param id 天气数据记录主键
     * @return 结果
     */
    @Override
    public int deleteBizWeatherRecordById(Long id)
    {
        return bizWeatherRecordMapper.deleteBizWeatherRecordById(id);
    }
}
