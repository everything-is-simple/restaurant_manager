package com.ruoyi.web.controller.restaurant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.TDishIngredient;
import com.ruoyi.system.service.ITDishIngredientService;

/**
 * 配方管理Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/recipe")
public class TRecipeController extends BaseController
{
    @Autowired
    private ITDishIngredientService tDishIngredientService;

    /**
     * 查询某菜品配方列表（含食材名称）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:recipe:query')")
    @GetMapping("/list/{dishId}")
    public AjaxResult list(@PathVariable("dishId") Long dishId)
    {
        if (dishId == null)
        {
            return error("菜品ID不能为空");
        }
        List<TDishIngredient> list = tDishIngredientService.selectTDishIngredientByDishId(dishId);
        return success(list);
    }

    /**
     * 覆盖保存配方（先删后插，事务保证）
     * 入参：{ dishId: Long, ingredients: [{ ingredientId, quantity }] }
     */
    @PreAuthorize("@ss.hasPermi('restaurant:recipe:save')")
    @PutMapping("/save")
    public AjaxResult save(@RequestBody Map<String, Object> params)
    {
        Object dishIdObj = params.get("dishId");
        if (dishIdObj == null || dishIdObj.toString().trim().isEmpty())
        {
            return error("菜品ID不能为空");
        }
        Long dishId = Long.valueOf(dishIdObj.toString());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> ingredientMaps = (List<Map<String, Object>>) params.get("ingredients");
        List<TDishIngredient> list = new ArrayList<>();
        if (ingredientMaps != null)
        {
            for (Map<String, Object> map : ingredientMaps)
            {
                Object ingredientIdObj = map.get("ingredientId");
                if (ingredientIdObj == null || ingredientIdObj.toString().trim().isEmpty())
                {
                    return error("食材不能为空");
                }
                Object quantityObj = map.get("quantity");
                if (quantityObj == null || quantityObj.toString().trim().isEmpty())
                {
                    return error("配方用量不能为空");
                }
                BigDecimal quantity = new BigDecimal(quantityObj.toString());
                if (quantity.compareTo(BigDecimal.ZERO) <= 0)
                {
                    return error("配方用量必须大于0");
                }
                TDishIngredient item = new TDishIngredient();
                item.setIngredientId(Long.valueOf(ingredientIdObj.toString()));
                item.setQuantity(quantity);
                list.add(item);
            }
        }
        return toAjax(tDishIngredientService.saveRecipe(dishId, list));
    }
}
