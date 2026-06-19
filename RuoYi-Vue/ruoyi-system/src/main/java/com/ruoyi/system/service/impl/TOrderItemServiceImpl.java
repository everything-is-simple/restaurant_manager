package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.TOrderItemMapper;
import com.ruoyi.system.domain.TOrderItem;
import com.ruoyi.system.service.ITOrderItemService;

/**
 * 订单明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TOrderItemServiceImpl implements ITOrderItemService 
{
    @Autowired
    private TOrderItemMapper tOrderItemMapper;

    /**
     * 查询订单明细列表
     * 
     * @param tOrderItem 订单明细
     * @return 订单明细集合
     */
    @Override
    public List<TOrderItem> selectTOrderItemList(TOrderItem tOrderItem)
    {
        return tOrderItemMapper.selectTOrderItemList(tOrderItem);
    }

    /**
     * 按订单ID查询明细列表
     * 
     * @param orderId 订单ID
     * @return 订单明细集合
     */
    @Override
    public List<TOrderItem> selectTOrderItemByOrderId(Long orderId)
    {
        return tOrderItemMapper.selectTOrderItemByOrderId(orderId);
    }

    /**
     * 新增订单明细
     * 
     * @param tOrderItem 订单明细
     * @return 结果
     */
    @Override
    public int insertTOrderItem(TOrderItem tOrderItem)
    {
        tOrderItem.setCreateTime(DateUtils.getNowDate());
        return tOrderItemMapper.insertTOrderItem(tOrderItem);
    }

    /**
     * 批量插入订单明细
     * 
     * @param list 订单明细集合
     * @return 结果
     */
    @Override
    public int batchInsertTOrderItem(List<TOrderItem> list)
    {
        return tOrderItemMapper.batchInsertTOrderItem(list);
    }

    /**
     * 按订单ID删除明细
     * 
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    public int deleteTOrderItemByOrderId(Long orderId)
    {
        return tOrderItemMapper.deleteTOrderItemByOrderId(orderId);
    }
}
