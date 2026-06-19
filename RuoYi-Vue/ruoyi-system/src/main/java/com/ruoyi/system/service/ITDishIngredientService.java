package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TDishIngredient;

/**
 * 菜品配方Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITDishIngredientService 
{
    /**
     * 查询菜品配方列表
     * 
     * @param tDishIngredient 菜品配方
     * @return 菜品配方集合
     */
    public List<TDishIngredient> selectTDishIngredientList(TDishIngredient tDishIngredient);

    /**
     * 按菜品ID查询配方列表（含食材名称）
     * 
     * @param dishId 菜品ID
     * @return 菜品配方集合
     */
    public List<TDishIngredient> selectTDishIngredientByDishId(Long dishId);

    /**
     * 新增菜品配方
     * 
     * @param tDishIngredient 菜品配方
     * @return 结果
     */
    public int insertTDishIngredient(TDishIngredient tDishIngredient);

    /**
     * 批量插入菜品配方
     * 
     * @param list 菜品配方集合
     * @return 结果
     */
    public int batchInsertTDishIngredient(List<TDishIngredient> list);

    /**
     * 按菜品ID删除配方
     * 
     * @param dishId 菜品ID
     * @return 结果
     */
    public int deleteTDishIngredientByDishId(Long dishId);

    /**
     * 覆盖保存配方（先删后插）
     * 
     * @param dishId 菜品ID
     * @param list 新的配方列表
     * @return 结果
     */
    public int saveRecipe(Long dishId, List<TDishIngredient> list);
}
