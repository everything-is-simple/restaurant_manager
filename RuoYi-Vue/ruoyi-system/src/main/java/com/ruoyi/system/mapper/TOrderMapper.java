package com.ruoyi.system.mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.ruoyi.system.domain.TOrder;
import org.apache.ibatis.annotations.Param;

/**
 * 订单Mapper接口
 *
 * @author ruoyi
 * @date 2026-06-19
 */
public interface TOrderMapper
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
     * 删除订单
     *
     * @param orderId 订单主键
     * @return 结果
     */
    public int deleteTOrderByOrderId(Long orderId);

    /**
     * 批量删除订单
     *
     * @param orderIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTOrderByOrderIds(Long[] orderIds);

    /**
     * 统计指定日期的已完成订单总额（按 complete_time 的 DATE 部分匹配）
     * 仅统计 status=2（已完成）的订单
     *
     * @param day 统计日期（按 DATE(complete_time) = #{day}）
     * @return 营收总额（无数据返回 0）
     */
    public BigDecimal sumRevenueByDay(@Param("day") Date day);

    /**
     * 统计指定日期的已完成订单数（按 complete_time 的 DATE 部分匹配）
     * 仅统计 status=2（已完成）的订单
     *
     * @param day 统计日期
     * @return 订单数
     */
    public int countCompletedOrdersByDay(@Param("day") Date day);

    /**
     * 统计日期区间内每日已完成订单的营收（按 complete_time 天维度 GROUP BY）
     * 返回结果按天升序，key=date(yyyy-MM-dd), value=amount
     * 仅统计 status=2 的订单
     *
     * @param beginDate 起始日期（含）
     * @param endDate 结束日期（含）
     * @return 每日营收列表
     */
    public List<Map<String, Object>> sumRevenueGroupByDay(@Param("beginDate") Date beginDate, @Param("endDate") Date endDate);
}
