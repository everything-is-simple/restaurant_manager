package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.TDishIngredientMapper;
import com.ruoyi.system.domain.TDishIngredient;
import com.ruoyi.system.service.ITDishIngredientService;

/**
 * 菜品配方Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TDishIngredientServiceImpl implements ITDishIngredientService 
{
    @Autowired
    private TDishIngredientMapper tDishIngredientMapper;

    /**
     * 查询菜品配方列表
     * 
     * @param tDishIngredient 菜品配方
     * @return 菜品配方集合
     */
    @Override
    public List<TDishIngredient> selectTDishIngredientList(TDishIngredient tDishIngredient)
    {
        return tDishIngredientMapper.selectTDishIngredientList(tDishIngredient);
    }

    /**
     * 按菜品ID查询配方列表（含食材名称）
     * 
     * @param dishId 菜品ID
     * @return 菜品配方集合
     */
    @Override
    public List<TDishIngredient> selectTDishIngredientByDishId(Long dishId)
    {
        return tDishIngredientMapper.selectTDishIngredientByDishId(dishId);
    }

    /**
     * 新增菜品配方
     * 
     * @param tDishIngredient 菜品配方
     * @return 结果
     */
    @Override
    public int insertTDishIngredient(TDishIngredient tDishIngredient)
    {
        tDishIngredient.setCreateTime(DateUtils.getNowDate());
        return tDishIngredientMapper.insertTDishIngredient(tDishIngredient);
    }

    /**
     * 批量插入菜品配方
     * 
     * @param list 菜品配方集合
     * @return 结果
     */
    @Override
    public int batchInsertTDishIngredient(List<TDishIngredient> list)
    {
        return tDishIngredientMapper.batchInsertTDishIngredient(list);
    }

    /**
     * 按菜品ID删除配方
     * 
     * @param dishId 菜品ID
     * @return 结果
     */
    @Override
    public int deleteTDishIngredientByDishId(Long dishId)
    {
        return tDishIngredientMapper.deleteTDishIngredientByDishId(dishId);
    }

    /**
     * 覆盖保存配方（先删后插，事务保证）
     * 
     * @param dishId 菜品ID
     * @param list 新的配方列表
     * @return 结果
     */
    @Override
    @Transactional
    public int saveRecipe(Long dishId, List<TDishIngredient> list)
    {
        // 先删除该菜品所有配方
        tDishIngredientMapper.deleteTDishIngredientByDishId(dishId);
        // 再批量插入新配方
        if (list != null && !list.isEmpty())
        {
            for (TDishIngredient item : list)
            {
                item.setDishId(dishId);
                item.setCreateTime(DateUtils.getNowDate());
            }
            return tDishIngredientMapper.batchInsertTDishIngredient(list);
        }
        return 1;
    }
}
