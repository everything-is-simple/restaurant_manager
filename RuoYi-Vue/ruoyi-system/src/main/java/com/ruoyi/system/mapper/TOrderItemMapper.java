package com.ruoyi.system.mapper;

import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.TOrderItem;
import org.apache.ibatis.annotations.Param;

/**
 * 订单明细Mapper接口
 *
 * @author ruoyi
 * @date 2026-06-19
 */
public interface TOrderItemMapper
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

    /**
     * 销量 TopN 统计：按菜品分组，关联 t_order 仅取已完成订单（status=2）
     * 返回字段：dish_id, dish_name, total_quantity(份数合计), total_amount(销售金额合计)
     * 按 total_quantity 降序
     *
     * @param limit 取前 N 条
     * @return 销量排行列表
     */
    public List<Map<String, Object>> sumQuantityGroupByDish(@Param("limit") int limit);
}
