package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TOrder;

/**
 * 订单Service接口
 *
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITOrderService
{
    /**
     * 查询订单
     *
     * @param orderId 订单主键
     * @return 订单
     */
    public TOrder selectTOrderByOrderId(Long orderId);

    /**
     * 查询订单列表
     *
     * @param tOrder 订单
     * @return 订单集合
     */
    public List<TOrder> selectTOrderList(TOrder tOrder);

    /**
     * 新增订单
     *
     * @param tOrder 订单
     * @return 结果
     */
    public int insertTOrder(TOrder tOrder);

    /**
     * 修改订单
     *
     * @param tOrder 订单
     * @return 结果
     */
    public int updateTOrder(TOrder tOrder);

    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的订单主键集合
     * @return 结果
     */
    public int deleteTOrderByOrderIds(Long[] orderIds);

    /**
     * 删除订单信息
     *
     * @param orderId 订单主键
     * @return 结果
     */
    public int deleteTOrderByOrderId(Long orderId);

    /**
     * 模拟下单（生成订单号 + 批量插入明细 + 状态=1已下单）
     *
     * @param tOrder 订单（含 orderItems 明细列表）
     * @return 新生成的订单ID
     */
    public Long mockOrder(TOrder tOrder);

    /**
     * 完成订单（事务 + 按配方扣库存 + 状态=2已完成 + 写 complete_time）
     * 库存不足时抛出 ServiceException 触发事务回滚
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int completeOrder(Long orderId);

    /**
     * 退单（事务 + 回滚库存 + 状态=3已退单）
     * 仅已完成订单可退单
     *
     * @param orderId 订单ID
     * @return 结果
     */
    public int cancelOrder(Long orderId);
}
