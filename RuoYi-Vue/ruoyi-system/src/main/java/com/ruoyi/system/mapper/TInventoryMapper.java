package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TInventory;

/**
 * 食材库存Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface TInventoryMapper 
{
    /**
     * 查询食材库存
     * 
     * @param inventoryId 食材库存主键
     * @return 食材库存
     */
    public TInventory selectTInventoryByInventoryId(Long inventoryId);

    /**
     * 查询食材库存列表
     * 
     * @param tInventory 食材库存
     * @return 食材库存集合
     */
    public List<TInventory> selectTInventoryList(TInventory tInventory);

    /**
     * 新增食材库存
     * 
     * @param tInventory 食材库存
     * @return 结果
     */
    public int insertTInventory(TInventory tInventory);

    /**
     * 修改食材库存
     * 
     * @param tInventory 食材库存
     * @return 结果
     */
    public int updateTInventory(TInventory tInventory);

    /**
     * 删除食材库存
     * 
     * @param inventoryId 食材库存主键
     * @return 结果
     */
    public int deleteTInventoryByInventoryId(Long inventoryId);

    /**
     * 批量删除食材库存
     * 
     * @param inventoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTInventoryByInventoryIds(Long[] inventoryIds);
}
