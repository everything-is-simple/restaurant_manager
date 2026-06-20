package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.system.domain.TInventory;
import com.ruoyi.system.domain.vo.RestaurantDashboardVO;
import com.ruoyi.system.mapper.TDishMapper;
import com.ruoyi.system.mapper.TInventoryMapper;
import com.ruoyi.system.mapper.TOrderItemMapper;
import com.ruoyi.system.mapper.TOrderMapper;
import com.ruoyi.system.service.ITStatsService;

/**
 * 餐厅数据统计Service业务层
 *
 * @author ruoyi
 * @date 2026-06-20
 */
@Service
public class TStatsServiceImpl implements ITStatsService
{
    private static final Logger logger = LoggerFactory.getLogger(TStatsServiceImpl.class);

    @Autowired
    private TOrderMapper tOrderMapper;

    @Autowired
    private TOrderItemMapper tOrderItemMapper;

    @Autowired
    private TInventoryMapper tInventoryMapper;

    @Autowired
    private TDishMapper tDishMapper;

    /**
     * 聚合看板数据
     */
    @Override
    @Transactional(readOnly = true)
    public RestaurantDashboardVO getDashboard(int trendDays, int topLimit)
    {
        RestaurantDashboardVO vo = new RestaurantDashboardVO();

        // 1) 今日营收 + 今日订单数（以"今天"为基准）
        Date today = new Date();
        BigDecimal todayRevenue = tOrderMapper.sumRevenueByDay(today);
        vo.setTodayRevenue(todayRevenue == null ? BigDecimal.ZERO : todayRevenue);
        vo.setTodayOrderCount(tOrderMapper.countCompletedOrdersByDay(today));

        // 2) 在售菜品数
        vo.setDishCount(tDishMapper.countOnSaleDishes());

        // 3) 库存预警条数
        vo.setWarningCount(tInventoryMapper.countWarning());

        // 4) 近 N 天营收趋势：把缺失日期补 0，保证前端图表横轴连续
        int days = trendDays <= 0 ? 7 : trendDays;
        Date beginDate = offsetDay(today, -(days - 1));
        List<Map<String, Object>> rawTrend = tOrderMapper.sumRevenueGroupByDay(beginDate, today);
        vo.setRevenueTrend(fillMissingDates(beginDate, today, rawTrend));

        // 5) 销量 TopN
        int limit = topLimit <= 0 ? 10 : topLimit;
        vo.setTopDishes(tOrderItemMapper.sumQuantityGroupByDish(limit));

        // 6) 库存预警列表
        vo.setWarningList(tInventoryMapper.selectWarningList());

        return vo;
    }

    @Override
    public Map<String, Object> sumRevenueByDay(Date day)
    {
        BigDecimal amount = tOrderMapper.sumRevenueByDay(day);
        Map<String, Object> map = new HashMap<>();
        map.put("date", new SimpleDateFormat("yyyy-MM-dd").format(day));
        map.put("amount", amount == null ? BigDecimal.ZERO : amount);
        return map;
    }

    @Override
    public int countCompletedOrdersByDay(Date day)
    {
        return tOrderMapper.countCompletedOrdersByDay(day);
    }

    @Override
    public List<Map<String, Object>> sumRevenueGroupByDay(Date beginDate, Date endDate)
    {
        return fillMissingDates(beginDate, endDate, tOrderMapper.sumRevenueGroupByDay(beginDate, endDate));
    }

    @Override
    public List<Map<String, Object>> topDishes(int limit)
    {
        return tOrderItemMapper.sumQuantityGroupByDish(limit);
    }

    @Override
    public List<TInventory> listWarning()
    {
        return tInventoryMapper.selectWarningList();
    }

    @Override
    public int countWarning()
    {
        return tInventoryMapper.countWarning();
    }

    @Override
    public int countOnSaleDishes()
    {
        return tDishMapper.countOnSaleDishes();
    }

    /**
     * 把日期区间内无数据的日期补 0，保证横轴连续
     */
    private List<Map<String, Object>> fillMissingDates(Date beginDate, Date endDate, List<Map<String, Object>> raw)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 把数据库返回的 map（key 为 date）放到以日期字符串为 key 的字典里
        Map<String, BigDecimal> byDate = new HashMap<>();
        if (raw != null)
        {
            for (Map<String, Object> row : raw)
            {
                Object d = row.get("date");
                if (d == null)
                {
                    continue;
                }
                String key = d.toString();
                Object amt = row.get("amount");
                BigDecimal amount = amt == null ? BigDecimal.ZERO : new BigDecimal(amt.toString());
                byDate.put(key, amount);
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        Date cursor = beginDate;
        while (!cursor.after(endDate))
        {
            String key = sdf.format(cursor);
            Map<String, Object> row = new HashMap<>();
            row.put("date", key);
            row.put("amount", byDate.getOrDefault(key, BigDecimal.ZERO));
            result.add(row);
            cursor = offsetDay(cursor, 1);
        }
        return result;
    }

    /**
     * 日期偏移：amount > 0 向后、amount < 0 向前，单位：天
     */
    private Date offsetDay(Date base, int amount)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }
}
