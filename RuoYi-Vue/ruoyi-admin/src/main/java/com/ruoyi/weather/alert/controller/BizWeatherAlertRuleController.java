package com.ruoyi.weather.alert.controller;

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
import com.ruoyi.weather.alert.domain.BizWeatherAlertRule;
import com.ruoyi.weather.alert.service.IBizWeatherAlertRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 天气告警规则配置Controller
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@RestController
@RequestMapping("/alert/alert")
public class BizWeatherAlertRuleController extends BaseController
{
    @Autowired
    private IBizWeatherAlertRuleService bizWeatherAlertRuleService;

    /**
     * 查询天气告警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizWeatherAlertRule bizWeatherAlertRule)
    {
        startPage();
        List<BizWeatherAlertRule> list = bizWeatherAlertRuleService.selectBizWeatherAlertRuleList(bizWeatherAlertRule);
        return getDataTable(list);
    }

    /**
     * 导出天气告警规则配置列表
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:export')")
    @Log(title = "天气告警规则配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizWeatherAlertRule bizWeatherAlertRule)
    {
        List<BizWeatherAlertRule> list = bizWeatherAlertRuleService.selectBizWeatherAlertRuleList(bizWeatherAlertRule);
        ExcelUtil<BizWeatherAlertRule> util = new ExcelUtil<BizWeatherAlertRule>(BizWeatherAlertRule.class);
        util.exportExcel(response, list, "天气告警规则配置数据");
    }

    /**
     * 获取天气告警规则配置详细信息
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizWeatherAlertRuleService.selectBizWeatherAlertRuleById(id));
    }

    /**
     * 新增天气告警规则配置
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:add')")
    @Log(title = "天气告警规则配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizWeatherAlertRule bizWeatherAlertRule)
    {
        return toAjax(bizWeatherAlertRuleService.insertBizWeatherAlertRule(bizWeatherAlertRule));
    }

    /**
     * 修改天气告警规则配置
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:edit')")
    @Log(title = "天气告警规则配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizWeatherAlertRule bizWeatherAlertRule)
    {
        return toAjax(bizWeatherAlertRuleService.updateBizWeatherAlertRule(bizWeatherAlertRule));
    }

    /**
     * 删除天气告警规则配置
     */
    @PreAuthorize("@ss.hasPermi('alert:alert:remove')")
    @Log(title = "天气告警规则配置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizWeatherAlertRuleService.deleteBizWeatherAlertRuleByIds(ids));
    }
}
