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
import com.ruoyi.system.domain.TDish;
import com.ruoyi.system.service.ITDishService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 菜品Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/dish")
public class TDishController extends BaseController
{
    @Autowired
    private ITDishService tDishService;

    /**
     * 查询菜品列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:list')")
    @GetMapping("/list")
    public TableDataInfo list(TDish tDish)
    {
        startPage();
        List<TDish> list = tDishService.selectTDishList(tDish);
        return getDataTable(list);
    }

    /**
     * 导出菜品列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:export')")
    @Log(title = "菜品", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TDish tDish)
    {
        List<TDish> list = tDishService.selectTDishList(tDish);
        ExcelUtil<TDish> util = new ExcelUtil<TDish>(TDish.class);
        util.exportExcel(response, list, "菜品数据");
    }

    /**
     * 获取菜品详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:query')")
    @GetMapping(value = "/{dishId}")
    public AjaxResult getInfo(@PathVariable("dishId") Long dishId)
    {
        return success(tDishService.selectTDishByDishId(dishId));
    }

    /**
     * 新增菜品
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:add')")
    @Log(title = "菜品", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TDish tDish)
    {
        return toAjax(tDishService.insertTDish(tDish));
    }

    /**
     * 修改菜品
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:edit')")
    @Log(title = "菜品", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TDish tDish)
    {
        return toAjax(tDishService.updateTDish(tDish));
    }

    /**
     * 删除菜品
     */
    @PreAuthorize("@ss.hasPermi('restaurant:dish:remove')")
    @Log(title = "菜品", businessType = BusinessType.DELETE)
	@DeleteMapping("/{dishIds}")
    public AjaxResult remove(@PathVariable Long[] dishIds)
    {
        return toAjax(tDishService.deleteTDishByDishIds(dishIds));
    }
}

