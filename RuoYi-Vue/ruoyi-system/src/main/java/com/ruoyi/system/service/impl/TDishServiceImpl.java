package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TDishMapper;
import com.ruoyi.system.domain.TDish;
import com.ruoyi.system.service.ITDishService;

/**
 * 菜品Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TDishServiceImpl implements ITDishService 
{
    @Autowired
    private TDishMapper tDishMapper;

    /**
     * 查询菜品
     * 
     * @param dishId 菜品主键
     * @return 菜品
     */
    @Override
    public TDish selectTDishByDishId(Long dishId)
    {
        return tDishMapper.selectTDishByDishId(dishId);
    }

    /**
     * 查询菜品列表
     * 
     * @param tDish 菜品
     * @return 菜品
     */
    @Override
    public List<TDish> selectTDishList(TDish tDish)
    {
        return tDishMapper.selectTDishList(tDish);
    }

    /**
     * 新增菜品
     * 
     * @param tDish 菜品
     * @return 结果
     */
    @Override
    public int insertTDish(TDish tDish)
    {
        tDish.setCreateTime(DateUtils.getNowDate());
        return tDishMapper.insertTDish(tDish);
    }

    /**
     * 修改菜品
     * 
     * @param tDish 菜品
     * @return 结果
     */
    @Override
    public int updateTDish(TDish tDish)
    {
        tDish.setUpdateTime(DateUtils.getNowDate());
        return tDishMapper.updateTDish(tDish);
    }

    /**
     * 批量删除菜品
     * 
     * @param dishIds 需要删除的菜品主键
     * @return 结果
     */
    @Override
    public int deleteTDishByDishIds(Long[] dishIds)
    {
        return tDishMapper.deleteTDishByDishIds(dishIds);
    }

    /**
     * 删除菜品信息
     * 
     * @param dishId 菜品主键
     * @return 结果
     */
    @Override
    public int deleteTDishByDishId(Long dishId)
    {
        return tDishMapper.deleteTDishByDishId(dishId);
    }
}
