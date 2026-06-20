package com.ruoyi.system.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.TInventory;
import com.ruoyi.system.domain.vo.RestaurantDashboardVO;

/**
 * 餐厅数据统计Service接口
 *
 * @author ruoyi
 * @date 2026-06-20
 */
public interface ITStatsService
{
    /**
     * 聚合看板数据：今日营收/订单数/菜品数/预警数 + 近 N 天趋势 + 销量 TopN + 预警列表
     *
     * @param trendDays 营收趋势天数（含当天），如 7 表示返回 7 条
     * @param topLimit 销量 Top 条数
     * @return 看板聚合数据
     */
    public RestaurantDashboardVO getDashboard(int trendDays, int topLimit);

    /**
     * 指定日期的已完成订单营收
     *
     * @param day 统计日期
     * @return 营收
     */
    public Map<String, Object> sumRevenueByDay(Date day);

    /**
     * 指定日期的已完成订单数
     *
     * @param day 统计日期
     * @return 订单数
     */
    public int countCompletedOrdersByDay(Date day);

    /**
     * 日期区间内每日营收（按天 GROUP BY）
     *
     * @param beginDate 起始日期
     * @param endDate 结束日期
     * @return 每日营收
     */
    public List<Map<String, Object>> sumRevenueGroupByDay(Date beginDate, Date endDate);

    /**
     * 销量 TopN
     *
     * @param limit Top 条数
     * @return 销量排行
     */
    public List<Map<String, Object>> topDishes(int limit);

    /**
     * 库存预警列表
     *
     * @return 预警列表
     */
    public List<TInventory> listWarning();

    /**
     * 库存预警条数
     *
     * @return 预警条数
     */
    public int countWarning();

    /**
     * 在售菜品数
     *
     * @return 在售菜品数
     */
    public int countOnSaleDishes();
}
