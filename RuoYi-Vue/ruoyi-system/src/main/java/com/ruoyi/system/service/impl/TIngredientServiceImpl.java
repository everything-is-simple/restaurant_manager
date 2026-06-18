package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TIngredientMapper;
import com.ruoyi.system.domain.TIngredient;
import com.ruoyi.system.service.ITIngredientService;

/**
 * 食材档案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TIngredientServiceImpl implements ITIngredientService 
{
    @Autowired
    private TIngredientMapper tIngredientMapper;

    /**
     * 查询食材档案
     * 
     * @param ingredientId 食材档案主键
     * @return 食材档案
     */
    @Override
    public TIngredient selectTIngredientByIngredientId(Long ingredientId)
    {
        return tIngredientMapper.selectTIngredientByIngredientId(ingredientId);
    }

    /**
     * 查询食材档案列表
     * 
     * @param tIngredient 食材档案
     * @return 食材档案
     */
    @Override
    public List<TIngredient> selectTIngredientList(TIngredient tIngredient)
    {
        return tIngredientMapper.selectTIngredientList(tIngredient);
    }

    /**
     * 新增食材档案
     * 
     * @param tIngredient 食材档案
     * @return 结果
     */
    @Override
    public int insertTIngredient(TIngredient tIngredient)
    {
        tIngredient.setCreateTime(DateUtils.getNowDate());
        return tIngredientMapper.insertTIngredient(tIngredient);
    }

    /**
     * 修改食材档案
     * 
     * @param tIngredient 食材档案
     * @return 结果
     */
    @Override
    public int updateTIngredient(TIngredient tIngredient)
    {
        tIngredient.setUpdateTime(DateUtils.getNowDate());
        return tIngredientMapper.updateTIngredient(tIngredient);
    }

    /**
     * 批量删除食材档案
     * 
     * @param ingredientIds 需要删除的食材档案主键
     * @return 结果
     */
    @Override
    public int deleteTIngredientByIngredientIds(Long[] ingredientIds)
    {
        return tIngredientMapper.deleteTIngredientByIngredientIds(ingredientIds);
    }

    /**
     * 删除食材档案信息
     * 
     * @param ingredientId 食材档案主键
     * @return 结果
     */
    @Override
    public int deleteTIngredientByIngredientId(Long ingredientId)
    {
        return tIngredientMapper.deleteTIngredientByIngredientId(ingredientId);
    }
}
