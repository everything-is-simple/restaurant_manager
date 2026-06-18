package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TDish;

/**
 * 菜品Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITDishService 
{
    /**
     * 查询菜品
     * 
     * @param dishId 菜品主键
     * @return 菜品
     */
    public TDish selectTDishByDishId(Long dishId);

    /**
     * 查询菜品列表
     * 
     * @param tDish 菜品
     * @return 菜品集合
     */
    public List<TDish> selectTDishList(TDish tDish);

    /**
     * 新增菜品
     * 
     * @param tDish 菜品
     * @return 结果
     */
    public int insertTDish(TDish tDish);

    /**
     * 修改菜品
     * 
     * @param tDish 菜品
     * @return 结果
     */
    public int updateTDish(TDish tDish);

    /**
     * 批量删除菜品
     * 
     * @param dishIds 需要删除的菜品主键集合
     * @return 结果
     */
    public int deleteTDishByDishIds(Long[] dishIds);

    /**
     * 删除菜品信息
     * 
     * @param dishId 菜品主键
     * @return 结果
     */
    public int deleteTDishByDishId(Long dishId);
}
