package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TOrderItem;

/**
 * 订单明细Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITOrderItemService 
{
    /**
     * 查询订单明细列表
     * 
     * @param tOrderItem 订单明细
     * @return 订单明细集合
     */
    public List<TOrderItem> selectTOrderItemList(TOrderItem tOrderItem);

    /**
     * 按订单ID查询明细列表
     * 
     * @param orderId 订单ID
     * @return 订单明细集合
     */
    public List<TOrderItem> selectTOrderItemByOrderId(Long orderId);

    /**
     * 新增订单明细
     * 
     * @param tOrderItem 订单明细
     * @return 结果
     */
    public int insertTOrderItem(TOrderItem tOrderItem);

    /**
     * 批量插入订单明细
     * 
     * @param list 订单明细集合
     * @return 结果
     */
    public int batchInsertTOrderItem(List<TOrderItem> list);

    /**
     * 按订单ID删除明细
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    public int deleteTOrderItemByOrderId(Long orderId);
}
