package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TIngredient;

/**
 * 食材档案Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITIngredientService 
{
    /**
     * 查询食材档案
     * 
     * @param ingredientId 食材档案主键
     * @return 食材档案
     */
    public TIngredient selectTIngredientByIngredientId(Long ingredientId);

    /**
     * 查询食材档案列表
     * 
     * @param tIngredient 食材档案
     * @return 食材档案集合
     */
    public List<TIngredient> selectTIngredientList(TIngredient tIngredient);

    /**
     * 新增食材档案
     * 
     * @param tIngredient 食材档案
     * @return 结果
     */
    public int insertTIngredient(TIngredient tIngredient);

    /**
     * 修改食材档案
     * 
     * @param tIngredient 食材档案
     * @return 结果
     */
    public int updateTIngredient(TIngredient tIngredient);

    /**
     * 批量删除食材档案
     * 
     * @param ingredientIds 需要删除的食材档案主键集合
     * @return 结果
     */
    public int deleteTIngredientByIngredientIds(Long[] ingredientIds);

    /**
     * 删除食材档案信息
     * 
     * @param ingredientId 食材档案主键
     * @return 结果
     */
    public int deleteTIngredientByIngredientId(Long ingredientId);
}
