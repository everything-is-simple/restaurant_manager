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
import com.ruoyi.system.domain.TInventory;
import com.ruoyi.system.service.ITInventoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 食材库存Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/inventory")
public class TInventoryController extends BaseController
{
    @Autowired
    private ITInventoryService tInventoryService;

    /**
     * 查询食材库存列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:list')")
    @GetMapping("/list")
    public TableDataInfo list(TInventory tInventory)
    {
        startPage();
        List<TInventory> list = tInventoryService.selectTInventoryList(tInventory);
        return getDataTable(list);
    }

    /**
     * 导出食材库存列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:export')")
    @Log(title = "食材库存", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TInventory tInventory)
    {
        List<TInventory> list = tInventoryService.selectTInventoryList(tInventory);
        ExcelUtil<TInventory> util = new ExcelUtil<TInventory>(TInventory.class);
        util.exportExcel(response, list, "食材库存数据");
    }

    /**
     * 获取食材库存详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:query')")
    @GetMapping(value = "/{inventoryId}")
    public AjaxResult getInfo(@PathVariable("inventoryId") Long inventoryId)
    {
        return success(tInventoryService.selectTInventoryByInventoryId(inventoryId));
    }

    /**
     * 新增食材库存
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:add')")
    @Log(title = "食材库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TInventory tInventory)
    {
        return toAjax(tInventoryService.insertTInventory(tInventory));
    }

    /**
     * 修改食材库存
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:edit')")
    @Log(title = "食材库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TInventory tInventory)
    {
        return toAjax(tInventoryService.updateTInventory(tInventory));
    }

    /**
     * 删除食材库存
     */
    @PreAuthorize("@ss.hasPermi('restaurant:inventory:remove')")
    @Log(title = "食材库存", businessType = BusinessType.DELETE)
	@DeleteMapping("/{inventoryIds}")
    public AjaxResult remove(@PathVariable Long[] inventoryIds)
    {
        return toAjax(tInventoryService.deleteTInventoryByInventoryIds(inventoryIds));
    }
}

