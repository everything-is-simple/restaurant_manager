package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.TOrderMapper;
import com.ruoyi.system.mapper.TOrderItemMapper;
import com.ruoyi.system.mapper.TDishIngredientMapper;
import com.ruoyi.system.mapper.TInventoryMapper;
import com.ruoyi.system.domain.TOrder;
import com.ruoyi.system.domain.TOrderItem;
import com.ruoyi.system.domain.TDishIngredient;
import com.ruoyi.system.service.ITOrderService;

/**
 * 订单Service业务层处理
 *
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TOrderServiceImpl implements ITOrderService
{
    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private TOrderItemMapper tOrderItemMapper;

    @Autowired
    private TDishIngredientMapper tDishIngredientMapper;

    @Autowired
    private TInventoryMapper tInventoryMapper;

    /**
     * 查询订单
     *
     * @param orderId 订单主键
     * @return 订单
     */
    @Override
    public TOrder selectTOrderByOrderId(Long orderId)
    {
        return tOrderMapper.selectTOrderByOrderId(orderId);
    }

    /**
     * 查询订单列表
     *
     * @param tOrder 订单
     * @return 订单
     */
    @Override
    public List<TOrder> selectTOrderList(TOrder tOrder)
    {
        return tOrderMapper.selectTOrderList(tOrder);
    }

    /**
     * 新增订单
     *
     * @param tOrder 订单
     * @return 结果
     */
    @Override
    public int insertTOrder(TOrder tOrder)
    {
        tOrder.setCreateTime(DateUtils.getNowDate());
        return tOrderMapper.insertTOrder(tOrder);
    }

    /**
     * 修改订单
     *
     * @param tOrder 订单
     * @return 结果
     */
    @Override
    public int updateTOrder(TOrder tOrder)
    {
        tOrder.setUpdateTime(DateUtils.getNowDate());
        return tOrderMapper.updateTOrder(tOrder);
    }

    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteTOrderByOrderIds(Long[] orderIds)
    {
        return tOrderMapper.deleteTOrderByOrderIds(orderIds);
    }

    /**
     * 删除订单信息
     *
     * @param orderId 订单主键
     * @return 结果
     */
    @Override
    public int deleteTOrderByOrderId(Long orderId)
    {
        return tOrderMapper.deleteTOrderByOrderId(orderId);
    }

    /**
     * 模拟下单（生成订单号 + 批量插入明细 + 状态=1已下单）
     *
     * @param tOrder 订单（含 orderItems 明细列表）
     * @return 新生成的订单ID
     */
    @Override
    @Transactional
    public Long mockOrder(TOrder tOrder)
    {
        List<TOrderItem> items = tOrder.getOrderItems();
        if (items == null || items.isEmpty())
        {
            throw new ServiceException("订单明细不能为空");
        }

        // 生成订单号：yyMMddHHmmss + 3位毫秒
        String orderNo = new SimpleDateFormat("yyMMddHHmmssSSS").format(DateUtils.getNowDate());
        tOrder.setOrderNo(orderNo);
        tOrder.setStatus(1L);
        tOrder.setOrderTime(DateUtils.getNowDate());
        tOrder.setCreateTime(DateUtils.getNowDate());
        // 计算总额
        BigDecimal total = BigDecimal.ZERO;
        for (TOrderItem item : items)
        {
            BigDecimal lineTotal = item.getPrice().multiply(new BigDecimal(item.getQuantity()));
            total = total.add(lineTotal);
        }
        tOrder.setTotalAmount(total);

        // 插入订单主表（回填 orderId）
        tOrderMapper.insertTOrder(tOrder);

        // 填充 orderId 后批量插入明细
        for (TOrderItem item : items)
        {
            item.setOrderId(tOrder.getOrderId());
            item.setCreateTime(DateUtils.getNowDate());
        }
        tOrderItemMapper.batchInsertTOrderItem(items);

        return tOrder.getOrderId();
    }

    /**
     * 完成订单（事务 + 按配方扣库存 + 状态=2已完成 + 写 complete_time）
     * 库存不足时抛出 ServiceException 触发事务回滚
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int completeOrder(Long orderId)
    {
        TOrder order = tOrderMapper.selectTOrderByOrderId(orderId);
        if (order == null)
        {
            throw new ServiceException("订单不存在");
        }
        if (order.getStatus() != 1L)
        {
            throw new ServiceException("仅已下单状态的订单可完成，当前状态：" + order.getStatus());
        }

        // 查订单明细
        List<TOrderItem> items = tOrderItemMapper.selectTOrderItemByOrderId(orderId);
        if (items == null || items.isEmpty())
        {
            throw new ServiceException("订单明细为空，无法完成");
        }

        // 汇总每个食材的总消耗量：ingredientId -> 总消耗量
        Map<Long, BigDecimal> ingredientConsumeMap = new HashMap<>();
        for (TOrderItem item : items)
        {
            // 查该菜品的配方
            List<TDishIngredient> recipe = tDishIngredientMapper.selectTDishIngredientByDishId(item.getDishId());
            if (recipe == null || recipe.isEmpty())
            {
                throw new ServiceException("菜品[" + item.getDishName() + "]未配置配方，无法完成订单");
            }
            // 按份数累加每个食材消耗
            for (TDishIngredient di : recipe)
            {
                BigDecimal consume = di.getQuantity().multiply(new BigDecimal(item.getQuantity()));
                ingredientConsumeMap.merge(di.getIngredientId(), consume, BigDecimal::add);
            }
        }

        // 逐个扣库存（UPDATE ... WHERE stock >= deductQty 防并发超扣）
        for (Map.Entry<Long, BigDecimal> entry : ingredientConsumeMap.entrySet())
        {
            int rows = tInventoryMapper.deductStock(entry.getKey(), entry.getValue());
            if (rows == 0)
            {
                // 库存不足，抛异常触发事务回滚
                throw new ServiceException("食材ID[" + entry.getKey() + "]库存不足，需要 "
                        + entry.getValue() + "，扣减失败，订单已回滚");
            }
        }

        // 扣库存成功，更新订单状态为已完成
        TOrder update = new TOrder();
        update.setOrderId(orderId);
        update.setStatus(2L);
        update.setCompleteTime(DateUtils.getNowDate());
        update.setUpdateTime(DateUtils.getNowDate());
        return tOrderMapper.updateTOrder(update);
    }

    /**
     * 退单（事务 + 回滚库存 + 状态=3已退单）
     * 仅已完成订单可退单
     *
     * @param orderId 订单ID
     * @return 结果
     */
    @Override
    @Transactional
    public int cancelOrder(Long orderId)
    {
        TOrder order = tOrderMapper.selectTOrderByOrderId(orderId);
        if (order == null)
        {
            throw new ServiceException("订单不存在");
        }
        if (order.getStatus() != 2L)
        {
            throw new ServiceException("仅已完成状态的订单可退单，当前状态：" + order.getStatus());
        }

        // 查订单明细
        List<TOrderItem> items = tOrderItemMapper.selectTOrderItemByOrderId(orderId);
        if (items == null || items.isEmpty())
        {
            throw new ServiceException("订单明细为空，无法退单");
        }

        // 汇总每个食材的总消耗量（与完成订单逻辑对称）
        Map<Long, BigDecimal> ingredientReturnMap = new HashMap<>();
        for (TOrderItem item : items)
        {
            List<TDishIngredient> recipe = tDishIngredientMapper.selectTDishIngredientByDishId(item.getDishId());
            if (recipe == null || recipe.isEmpty())
            {
                throw new ServiceException("菜品[" + item.getDishName() + "]未配置配方，无法回滚库存");
            }
            for (TDishIngredient di : recipe)
            {
                BigDecimal consume = di.getQuantity().multiply(new BigDecimal(item.getQuantity()));
                ingredientReturnMap.merge(di.getIngredientId(), consume, BigDecimal::add);
            }
        }

        // 回滚库存（加回去）
        for (Map.Entry<Long, BigDecimal> entry : ingredientReturnMap.entrySet())
        {
            tInventoryMapper.addStock(entry.getKey(), entry.getValue());
        }

        // 更新订单状态为已退单
        TOrder update = new TOrder();
        update.setOrderId(orderId);
        update.setStatus(3L);
        update.setUpdateTime(DateUtils.getNowDate());
        return tOrderMapper.updateTOrder(update);
    }
}
