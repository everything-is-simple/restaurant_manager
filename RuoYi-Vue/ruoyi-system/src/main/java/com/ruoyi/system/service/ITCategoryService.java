package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TCategory;

/**
 * 菜品分类Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITCategoryService 
{
    /**
     * 查询菜品分类
     * 
     * @param categoryId 菜品分类主键
     * @return 菜品分类
     */
    public TCategory selectTCategoryByCategoryId(Long categoryId);

    /**
     * 查询菜品分类列表
     * 
     * @param tCategory 菜品分类
     * @return 菜品分类集合
     */
    public List<TCategory> selectTCategoryList(TCategory tCategory);

    /**
     * 新增菜品分类
     * 
     * @param tCategory 菜品分类
     * @return 结果
     */
    public int insertTCategory(TCategory tCategory);

    /**
     * 修改菜品分类
     * 
     * @param tCategory 菜品分类
     * @return 结果
     */
    public int updateTCategory(TCategory tCategory);

    /**
     * 批量删除菜品分类
     * 
     * @param categoryIds 需要删除的菜品分类主键集合
     * @return 结果
     */
    public int deleteTCategoryByCategoryIds(Long[] categoryIds);

    /**
     * 删除菜品分类信息
     * 
     * @param categoryId 菜品分类主键
     * @return 结果
     */
    public int deleteTCategoryByCategoryId(Long categoryId);
}
