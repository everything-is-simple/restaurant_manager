package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.TInventory;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 按食材ID扣减库存（防并发超扣：WHERE stock >= deductQty）
     * 返回受影响行数，0 表示库存不足
     *
     * @param ingredientId 食材ID
     * @param deductQty 扣减数量
     * @return 受影响行数（1成功，0库存不足）
     */
    public int deductStock(@Param("ingredientId") Long ingredientId, @Param("deductQty") java.math.BigDecimal deductQty);

    /**
     * 按食材ID增加库存（退单回滚用）
     *
     * @param ingredientId 食材ID
     * @param addQty 增加数量
     * @return 受影响行数
     */
    public int addStock(@Param("ingredientId") Long ingredientId, @Param("addQty") java.math.BigDecimal addQty);
}
