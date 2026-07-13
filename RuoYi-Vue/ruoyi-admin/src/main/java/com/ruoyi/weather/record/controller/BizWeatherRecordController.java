package com.ruoyi.weather.record.controller;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.weather.record.domain.BizWeatherRecord;
import com.ruoyi.weather.record.service.IBizWeatherRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 天气数据记录Controller
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@RestController
@RequestMapping("/record/record")
public class BizWeatherRecordController extends BaseController
{
    @Autowired
    private IBizWeatherRecordService bizWeatherRecordService;

    /**
     * 查询天气数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizWeatherRecord bizWeatherRecord)
    {
        startPage();
        List<BizWeatherRecord> list = bizWeatherRecordService.selectBizWeatherRecordList(bizWeatherRecord);
        return getDataTable(list);
    }

    /**
     * 导出天气数据记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:record:export')")
    @Log(title = "天气数据记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizWeatherRecord bizWeatherRecord)
    {
        List<BizWeatherRecord> list = bizWeatherRecordService.selectBizWeatherRecordList(bizWeatherRecord);
        ExcelUtil<BizWeatherRecord> util = new ExcelUtil<BizWeatherRecord>(BizWeatherRecord.class);
        util.exportExcel(response, list, "天气数据记录数据");
    }

    /**
     * 获取天气数据记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('record:record:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizWeatherRecordService.selectBizWeatherRecordById(id));
    }

    /**
     * 新增天气数据记录
     */
    @PreAuthorize("@ss.hasPermi('record:record:add')")
    @Log(title = "天气数据记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizWeatherRecord bizWeatherRecord)
    {
        return toAjax(bizWeatherRecordService.insertBizWeatherRecord(bizWeatherRecord));
    }

    /**
     * 修改天气数据记录
     */
    @PreAuthorize("@ss.hasPermi('record:record:edit')")
    @Log(title = "天气数据记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizWeatherRecord bizWeatherRecord)
    {
        return toAjax(bizWeatherRecordService.updateBizWeatherRecord(bizWeatherRecord));
    }

    /**
     * 删除天气数据记录
     */
    @PreAuthorize("@ss.hasPermi('record:record:remove')")
    @Log(title = "天气数据记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizWeatherRecordService.deleteBizWeatherRecordByIds(ids));
    }
}
