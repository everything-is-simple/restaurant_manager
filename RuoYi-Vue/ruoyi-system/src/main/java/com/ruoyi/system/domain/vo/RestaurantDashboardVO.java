package com.ruoyi.system.domain.vo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.ruoyi.system.domain.TInventory;

/**
 * 餐厅数据看板聚合响应
 *
 * @author ruoyi
 * @date 2026-06-20
 */
public class RestaurantDashboardVO
{
    /** 今日已完成订单营收（status=2） */
    private BigDecimal todayRevenue;

    /** 今日已完成订单数（status=2） */
    private Integer todayOrderCount;

    /** 在售菜品数（status=0 且未删除） */
    private Integer dishCount;

    /** 库存预警条数（stock < safety_stock） */
    private Integer warningCount;

    /** 近 7 天每日营收：[{date: 'yyyy-MM-dd', amount: 0.00}, ...] */
    private List<Map<String, Object>> revenueTrend;

    /** 销量 TopN：[{dishId, dishName, totalQuantity, totalAmount}, ...] */
    private List<Map<String, Object>> topDishes;

    /** 库存预警列表 */
    private List<TInventory> warningList;

    public BigDecimal getTodayRevenue()
    {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue)
    {
        this.todayRevenue = todayRevenue;
    }

    public Integer getTodayOrderCount()
    {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount)
    {
        this.todayOrderCount = todayOrderCount;
    }

    public Integer getDishCount()
    {
        return dishCount;
    }

    public void setDishCount(Integer dishCount)
    {
        this.dishCount = dishCount;
    }

    public Integer getWarningCount()
    {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount)
    {
        this.warningCount = warningCount;
    }

    public List<Map<String, Object>> getRevenueTrend()
    {
        return revenueTrend;
    }

    public void setRevenueTrend(List<Map<String, Object>> revenueTrend)
    {
        this.revenueTrend = revenueTrend;
    }

    public List<Map<String, Object>> getTopDishes()
    {
        return topDishes;
    }

    public void setTopDishes(List<Map<String, Object>> topDishes)
    {
        this.topDishes = topDishes;
    }

    public List<TInventory> getWarningList()
    {
        return warningList;
    }

    public void setWarningList(List<TInventory> warningList)
    {
        this.warningList = warningList;
    }
}
