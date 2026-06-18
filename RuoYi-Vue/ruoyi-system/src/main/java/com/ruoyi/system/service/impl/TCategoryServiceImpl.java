package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TCategoryMapper;
import com.ruoyi.system.domain.TCategory;
import com.ruoyi.system.service.ITCategoryService;

/**
 * 菜品分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TCategoryServiceImpl implements ITCategoryService 
{
    @Autowired
    private TCategoryMapper tCategoryMapper;

    /**
     * 查询菜品分类
     * 
     * @param categoryId 菜品分类主键
     * @return 菜品分类
     */
    @Override
    public TCategory selectTCategoryByCategoryId(Long categoryId)
    {
        return tCategoryMapper.selectTCategoryByCategoryId(categoryId);
    }

    /**
     * 查询菜品分类列表
     * 
     * @param tCategory 菜品分类
     * @return 菜品分类
     */
    @Override
    public List<TCategory> selectTCategoryList(TCategory tCategory)
    {
        return tCategoryMapper.selectTCategoryList(tCategory);
    }

    /**
     * 新增菜品分类
     * 
     * @param tCategory 菜品分类
     * @return 结果
     */
    @Override
    public int insertTCategory(TCategory tCategory)
    {
        tCategory.setCreateTime(DateUtils.getNowDate());
        return tCategoryMapper.insertTCategory(tCategory);
    }

    /**
     * 修改菜品分类
     * 
     * @param tCategory 菜品分类
     * @return 结果
     */
    @Override
    public int updateTCategory(TCategory tCategory)
    {
        tCategory.setUpdateTime(DateUtils.getNowDate());
        return tCategoryMapper.updateTCategory(tCategory);
    }

    /**
     * 批量删除菜品分类
     * 
     * @param categoryIds 需要删除的菜品分类主键
     * @return 结果
     */
    @Override
    public int deleteTCategoryByCategoryIds(Long[] categoryIds)
    {
        return tCategoryMapper.deleteTCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除菜品分类信息
     * 
     * @param categoryId 菜品分类主键
     * @return 结果
     */
    @Override
    public int deleteTCategoryByCategoryId(Long categoryId)
    {
        return tCategoryMapper.deleteTCategoryByCategoryId(categoryId);
    }
}
