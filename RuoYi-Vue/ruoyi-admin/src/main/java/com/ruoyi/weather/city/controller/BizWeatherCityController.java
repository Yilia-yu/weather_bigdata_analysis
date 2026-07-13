package com.ruoyi.weather.city.controller;

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
import com.ruoyi.weather.city.domain.BizWeatherCity;
import com.ruoyi.weather.city.service.IBizWeatherCityService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 天气监测城市Controller
 * 
 * @author ruoyi
 * @date 2026-07-03
 */
@RestController
@RequestMapping("/city/city")
public class BizWeatherCityController extends BaseController
{
    @Autowired
    private IBizWeatherCityService bizWeatherCityService;

    /**
     * 查询天气监测城市列表
     */
    @PreAuthorize("@ss.hasPermi('city:city:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizWeatherCity bizWeatherCity)
    {
        startPage();
        List<BizWeatherCity> list = bizWeatherCityService.selectBizWeatherCityList(bizWeatherCity);
        return getDataTable(list);
    }

    /**
     * 导出天气监测城市列表
     */
    @PreAuthorize("@ss.hasPermi('city:city:export')")
    @Log(title = "天气监测城市", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizWeatherCity bizWeatherCity)
    {
        List<BizWeatherCity> list = bizWeatherCityService.selectBizWeatherCityList(bizWeatherCity);
        ExcelUtil<BizWeatherCity> util = new ExcelUtil<BizWeatherCity>(BizWeatherCity.class);
        util.exportExcel(response, list, "天气监测城市数据");
    }

    /**
     * 获取天气监测城市详细信息
     */
    @PreAuthorize("@ss.hasPermi('city:city:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizWeatherCityService.selectBizWeatherCityById(id));
    }

    /**
     * 新增天气监测城市
     */
    @PreAuthorize("@ss.hasPermi('city:city:add')")
    @Log(title = "天气监测城市", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizWeatherCity bizWeatherCity)
    {
        return toAjax(bizWeatherCityService.insertBizWeatherCity(bizWeatherCity));
    }

    /**
     * 修改天气监测城市
     */
    @PreAuthorize("@ss.hasPermi('city:city:edit')")
    @Log(title = "天气监测城市", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizWeatherCity bizWeatherCity)
    {
        return toAjax(bizWeatherCityService.updateBizWeatherCity(bizWeatherCity));
    }

    /**
     * 删除天气监测城市
     */
    @PreAuthorize("@ss.hasPermi('city:city:remove')")
    @Log(title = "天气监测城市", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizWeatherCityService.deleteBizWeatherCityByIds(ids));
    }
}
