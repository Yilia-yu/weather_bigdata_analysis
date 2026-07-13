package com.ruoyi.weather.record.mapper;

import java.util.List;
import com.ruoyi.weather.record.domain.BizWeatherRecord;

/**
 * 天气数据记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
public interface BizWeatherRecordMapper 
{
    /**
     * 查询天气数据记录
     * 
     * @param id 天气数据记录主键
     * @return 天气数据记录
     */
    public BizWeatherRecord selectBizWeatherRecordById(Long id);

    /**
     * 查询天气数据记录列表
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 天气数据记录集合
     */
    public List<BizWeatherRecord> selectBizWeatherRecordList(BizWeatherRecord bizWeatherRecord);

    /**
     * 新增天气数据记录
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 结果
     */
    public int insertBizWeatherRecord(BizWeatherRecord bizWeatherRecord);

    /**
     * 修改天气数据记录
     * 
     * @param bizWeatherRecord 天气数据记录
     * @return 结果
     */
    public int updateBizWeatherRecord(BizWeatherRecord bizWeatherRecord);

    /**
     * 删除天气数据记录
     * 
     * @param id 天气数据记录主键
     * @return 结果
     */
    public int deleteBizWeatherRecordById(Long id);

    /**
     * 批量删除天气数据记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizWeatherRecordByIds(Long[] ids);

    /**
     * 查询各城市最新天气记录（大屏用）
     * 
     * @return 天气数据记录集合
     */
    public List<BizWeatherRecord> selectWeatherCurrent();

    /**
     * 查询天气统计数据（大屏用）
     * 
     * @return 统计结果Map
     */
    public java.util.Map<String, Object> selectWeatherStats();
}
