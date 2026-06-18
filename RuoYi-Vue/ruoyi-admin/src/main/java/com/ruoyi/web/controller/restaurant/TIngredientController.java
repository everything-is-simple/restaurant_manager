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
import com.ruoyi.system.domain.TIngredient;
import com.ruoyi.system.service.ITIngredientService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 食材档案Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/ingredient")
public class TIngredientController extends BaseController
{
    @Autowired
    private ITIngredientService tIngredientService;

    /**
     * 查询食材档案列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:list')")
    @GetMapping("/list")
    public TableDataInfo list(TIngredient tIngredient)
    {
        startPage();
        List<TIngredient> list = tIngredientService.selectTIngredientList(tIngredient);
        return getDataTable(list);
    }

    /**
     * 导出食材档案列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:export')")
    @Log(title = "食材档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TIngredient tIngredient)
    {
        List<TIngredient> list = tIngredientService.selectTIngredientList(tIngredient);
        ExcelUtil<TIngredient> util = new ExcelUtil<TIngredient>(TIngredient.class);
        util.exportExcel(response, list, "食材档案数据");
    }

    /**
     * 获取食材档案详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:query')")
    @GetMapping(value = "/{ingredientId}")
    public AjaxResult getInfo(@PathVariable("ingredientId") Long ingredientId)
    {
        return success(tIngredientService.selectTIngredientByIngredientId(ingredientId));
    }

    /**
     * 新增食材档案
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:add')")
    @Log(title = "食材档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TIngredient tIngredient)
    {
        return toAjax(tIngredientService.insertTIngredient(tIngredient));
    }

    /**
     * 修改食材档案
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:edit')")
    @Log(title = "食材档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TIngredient tIngredient)
    {
        return toAjax(tIngredientService.updateTIngredient(tIngredient));
    }

    /**
     * 删除食材档案
     */
    @PreAuthorize("@ss.hasPermi('restaurant:ingredient:remove')")
    @Log(title = "食材档案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ingredientIds}")
    public AjaxResult remove(@PathVariable Long[] ingredientIds)
    {
        return toAjax(tIngredientService.deleteTIngredientByIngredientIds(ingredientIds));
    }
}

