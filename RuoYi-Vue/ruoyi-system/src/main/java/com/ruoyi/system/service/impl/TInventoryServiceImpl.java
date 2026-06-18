package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TInventoryMapper;
import com.ruoyi.system.domain.TInventory;
import com.ruoyi.system.service.ITInventoryService;

/**
 * 食材库存Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TInventoryServiceImpl implements ITInventoryService 
{
    @Autowired
    private TInventoryMapper tInventoryMapper;

    /**
     * 查询食材库存
     * 
     * @param inventoryId 食材库存主键
     * @return 食材库存
     */
    @Override
    public TInventory selectTInventoryByInventoryId(Long inventoryId)
    {
        return tInventoryMapper.selectTInventoryByInventoryId(inventoryId);
    }

    /**
     * 查询食材库存列表
     * 
     * @param tInventory 食材库存
     * @return 食材库存
     */
    @Override
    public List<TInventory> selectTInventoryList(TInventory tInventory)
    {
        return tInventoryMapper.selectTInventoryList(tInventory);
    }

    /**
     * 新增食材库存
     * 
     * @param tInventory 食材库存
     * @return 结果
     */
    @Override
    public int insertTInventory(TInventory tInventory)
    {
        tInventory.setCreateTime(DateUtils.getNowDate());
        return tInventoryMapper.insertTInventory(tInventory);
    }

    /**
     * 修改食材库存
     * 
     * @param tInventory 食材库存
     * @return 结果
     */
    @Override
    public int updateTInventory(TInventory tInventory)
    {
        tInventory.setUpdateTime(DateUtils.getNowDate());
        return tInventoryMapper.updateTInventory(tInventory);
    }

    /**
     * 批量删除食材库存
     * 
     * @param inventoryIds 需要删除的食材库存主键
     * @return 结果
     */
    @Override
    public int deleteTInventoryByInventoryIds(Long[] inventoryIds)
    {
        return tInventoryMapper.deleteTInventoryByInventoryIds(inventoryIds);
    }

    /**
     * 删除食材库存信息
     * 
     * @param inventoryId 食材库存主键
     * @return 结果
     */
    @Override
    public int deleteTInventoryByInventoryId(Long inventoryId)
    {
        return tInventoryMapper.deleteTInventoryByInventoryId(inventoryId);
    }
}
