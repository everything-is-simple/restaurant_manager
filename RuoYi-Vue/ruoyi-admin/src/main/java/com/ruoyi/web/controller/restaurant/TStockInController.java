package com.ruoyi.web.controller.restaurant;

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
import com.ruoyi.system.domain.TStockIn;
import com.ruoyi.system.service.ITStockInService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 入库记录Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/stockIn")
public class TStockInController extends BaseController
{
    @Autowired
    private ITStockInService tStockInService;

    /**
     * 查询入库记录列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:list')")
    @GetMapping("/list")
    public TableDataInfo list(TStockIn tStockIn)
    {
        startPage();
        List<TStockIn> list = tStockInService.selectTStockInList(tStockIn);
        return getDataTable(list);
    }

    /**
     * 导出入库记录列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:export')")
    @Log(title = "入库记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TStockIn tStockIn)
    {
        List<TStockIn> list = tStockInService.selectTStockInList(tStockIn);
        ExcelUtil<TStockIn> util = new ExcelUtil<TStockIn>(TStockIn.class);
        util.exportExcel(response, list, "入库记录数据");
    }

    /**
     * 获取入库记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:query')")
    @GetMapping(value = "/{stockInId}")
    public AjaxResult getInfo(@PathVariable("stockInId") Long stockInId)
    {
        return success(tStockInService.selectTStockInByStockInId(stockInId));
    }

    /**
     * 新增入库记录
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:add')")
    @Log(title = "入库记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TStockIn tStockIn)
    {
        return toAjax(tStockInService.insertTStockIn(tStockIn));
    }

    /**
     * 修改入库记录
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:edit')")
    @Log(title = "入库记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TStockIn tStockIn)
    {
        return toAjax(tStockInService.updateTStockIn(tStockIn));
    }

    /**
     * 删除入库记录
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stockIn:remove')")
    @Log(title = "入库记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{stockInIds}")
    public AjaxResult remove(@PathVariable Long[] stockInIds)
    {
        return toAjax(tStockInService.deleteTStockInByStockInIds(stockInIds));
    }
}

