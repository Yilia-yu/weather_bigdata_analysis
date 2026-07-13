package com.ruoyi.weather.extreme.controller;

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
import com.ruoyi.weather.extreme.domain.ExtremeWeatherAlert;
import com.ruoyi.weather.extreme.service.IExtremeWeatherAlertService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 极端天气预警记录Controller
 * 
 * @author ruoyi
 * @date 2026-07-07
 */
@RestController
@RequestMapping("/extreme/extreme")
public class ExtremeWeatherAlertController extends BaseController
{
    @Autowired
    private IExtremeWeatherAlertService extremeWeatherAlertService;

    /**
     * 查询极端天气预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:list')")
    @GetMapping("/list")
    public TableDataInfo list(ExtremeWeatherAlert extremeWeatherAlert)
    {
        startPage();
        List<ExtremeWeatherAlert> list = extremeWeatherAlertService.selectExtremeWeatherAlertList(extremeWeatherAlert);
        return getDataTable(list);
    }

    /**
     * 导出极端天气预警记录列表
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:export')")
    @Log(title = "极端天气预警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ExtremeWeatherAlert extremeWeatherAlert)
    {
        List<ExtremeWeatherAlert> list = extremeWeatherAlertService.selectExtremeWeatherAlertList(extremeWeatherAlert);
        ExcelUtil<ExtremeWeatherAlert> util = new ExcelUtil<ExtremeWeatherAlert>(ExtremeWeatherAlert.class);
        util.exportExcel(response, list, "极端天气预警记录数据");
    }

    /**
     * 获取极端天气预警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(extremeWeatherAlertService.selectExtremeWeatherAlertById(id));
    }

    /**
     * 新增极端天气预警记录
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:add')")
    @Log(title = "极端天气预警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ExtremeWeatherAlert extremeWeatherAlert)
    {
        return toAjax(extremeWeatherAlertService.insertExtremeWeatherAlert(extremeWeatherAlert));
    }

    /**
     * 修改极端天气预警记录
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:edit')")
    @Log(title = "极端天气预警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ExtremeWeatherAlert extremeWeatherAlert)
    {
        return toAjax(extremeWeatherAlertService.updateExtremeWeatherAlert(extremeWeatherAlert));
    }

    /**
     * 删除极端天气预警记录
     */
    @PreAuthorize("@ss.hasPermi('extreme:extreme:remove')")
    @Log(title = "极端天气预警记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(extremeWeatherAlertService.deleteExtremeWeatherAlertByIds(ids));
    }
}
