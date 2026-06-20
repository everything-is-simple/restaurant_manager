package com.ruoyi.web.controller.restaurant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.TInventory;
import com.ruoyi.system.domain.vo.RestaurantDashboardVO;
import com.ruoyi.system.service.ITStatsService;

/**
 * 餐厅数据看板Controller
 *
 * @author ruoyi
 * @date 2026-06-20
 */
@RestController
@RequestMapping("/restaurant/stats")
public class TStatsController extends BaseController
{
    @Autowired
    private ITStatsService tStatsService;

    /**
     * 看板聚合接口（一次返回所有卡片/图表数据）
     *
     * @param trendDays 营收趋势天数（默认 7，含当天）
     * @param topLimit  销量 Top 条数（默认 10）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stats:query')")
    @GetMapping("/dashboard")
    public AjaxResult dashboard(@RequestParam(value = "trendDays", required = false, defaultValue = "7") int trendDays,
                                @RequestParam(value = "topLimit", required = false, defaultValue = "10") int topLimit)
    {
        RestaurantDashboardVO vo = tStatsService.getDashboard(trendDays, topLimit);
        return success(vo);
    }

    /**
     * 今日营收（指定日期默认今天，yyyy-MM-dd）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stats:query')")
    @GetMapping("/today")
    public AjaxResult today(@RequestParam(value = "day", required = false) String day)
    {
        Date target = parseDayOrToday(day);
        return success(tStatsService.sumRevenueByDay(target));
    }

    /**
     * 日期区间内每日营收（按天 GROUP BY）
     * 默认近 7 天
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stats:query')")
    @GetMapping("/revenueTrend")
    public AjaxResult revenueTrend(@RequestParam(value = "beginDate", required = false) String beginDate,
                                   @RequestParam(value = "endDate", required = false) String endDate)
    {
        Date end = parseDayOrToday(endDate);
        Date begin = (beginDate == null || beginDate.isEmpty())
                ? offsetDay(end, -6)
                : parseDay(beginDate);
        List<Map<String, Object>> list = tStatsService.sumRevenueGroupByDay(begin, end);
        return success(list);
    }

    /**
     * 销量 TopN（默认 10）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stats:query')")
    @GetMapping("/topDishes")
    public AjaxResult topDishes(@RequestParam(value = "limit", required = false, defaultValue = "10") int limit)
    {
        return success(tStatsService.topDishes(limit));
    }

    /**
     * 库存预警列表（带分页以便前端表格使用）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:stats:query')")
    @GetMapping("/warning")
    public TableDataInfo warning()
    {
        startPage();
        List<TInventory> list = tStatsService.listWarning();
        return getDataTable(list);
    }

    private Date parseDayOrToday(String day)
    {
        if (day == null || day.isEmpty())
        {
            return DateUtils.getNowDate();
        }
        return parseDay(day);
    }

    private Date parseDay(String day)
    {
        try
        {
            return new SimpleDateFormat("yyyy-MM-dd").parse(day);
        }
        catch (Exception e)
        {
            return DateUtils.getNowDate();
        }
    }

    private Date offsetDay(Date base, int amount)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(base);
        c.add(Calendar.DAY_OF_MONTH, amount);
        return c.getTime();
    }
}
