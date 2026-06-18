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
import com.ruoyi.system.domain.TCategory;
import com.ruoyi.system.service.ITCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 菜品分类Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/category")
public class TCategoryController extends BaseController
{
    @Autowired
    private ITCategoryService tCategoryService;

    /**
     * 查询菜品分类列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:list')")
    @GetMapping("/list")
    public TableDataInfo list(TCategory tCategory)
    {
        startPage();
        List<TCategory> list = tCategoryService.selectTCategoryList(tCategory);
        return getDataTable(list);
    }

    /**
     * 导出菜品分类列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:export')")
    @Log(title = "菜品分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TCategory tCategory)
    {
        List<TCategory> list = tCategoryService.selectTCategoryList(tCategory);
        ExcelUtil<TCategory> util = new ExcelUtil<TCategory>(TCategory.class);
        util.exportExcel(response, list, "菜品分类数据");
    }

    /**
     * 获取菜品分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(tCategoryService.selectTCategoryByCategoryId(categoryId));
    }

    /**
     * 新增菜品分类
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:add')")
    @Log(title = "菜品分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TCategory tCategory)
    {
        return toAjax(tCategoryService.insertTCategory(tCategory));
    }

    /**
     * 修改菜品分类
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:edit')")
    @Log(title = "菜品分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TCategory tCategory)
    {
        return toAjax(tCategoryService.updateTCategory(tCategory));
    }

    /**
     * 删除菜品分类
     */
    @PreAuthorize("@ss.hasPermi('restaurant:category:remove')")
    @Log(title = "菜品分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(tCategoryService.deleteTCategoryByCategoryIds(categoryIds));
    }
}

