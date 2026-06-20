package com.ruoyi.web.controller.restaurant;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TOrder;
import com.ruoyi.system.service.ITOrderService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 订单Controller
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@RestController
@RequestMapping("/restaurant/order")
public class TOrderController extends BaseController
{
    @Autowired
    private ITOrderService tOrderService;

    /**
     * 查询订单列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:list')")
    @GetMapping("/list")
    public TableDataInfo list(TOrder tOrder)
    {
        startPage();
        List<TOrder> list = tOrderService.selectTOrderList(tOrder);
        return getDataTable(list);
    }

    /**
     * 导出订单列表
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:export')")
    @Log(title = "订单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TOrder tOrder)
    {
        List<TOrder> list = tOrderService.selectTOrderList(tOrder);
        ExcelUtil<TOrder> util = new ExcelUtil<TOrder>(TOrder.class);
        util.exportExcel(response, list, "订单数据");
    }

    /**
     * 获取订单详细信息
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:query')")
    @GetMapping(value = "/{orderId}")
    public AjaxResult getInfo(@PathVariable("orderId") Long orderId)
    {
        return success(tOrderService.selectTOrderByOrderId(orderId));
    }

    /**
     * 新增订单
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:add')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TOrder tOrder)
    {
        return toAjax(tOrderService.insertTOrder(tOrder));
    }

    /**
     * 修改订单
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:edit')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TOrder tOrder)
    {
        return toAjax(tOrderService.updateTOrder(tOrder));
    }

    /**
     * 删除订单
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:remove')")
    @Log(title = "订单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(tOrderService.deleteTOrderByOrderIds(orderIds));
    }

    /**
     * 模拟下单（生成订单号 + 批量插入明细 + 状态=1已下单）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:mock')")
    @Log(title = "订单", businessType = BusinessType.INSERT)
    @PostMapping("/mock")
    public AjaxResult mock(@RequestBody TOrder tOrder)
    {
        Long orderId = tOrderService.mockOrder(tOrder);
        return AjaxResult.success(orderId);
    }

    /**
     * 完成订单（事务 + 按配方扣库存 + 状态=2已完成）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:complete')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping("/complete/{orderId}")
    public AjaxResult complete(@PathVariable Long orderId)
    {
        return toAjax(tOrderService.completeOrder(orderId));
    }

    /**
     * 退单（事务 + 回滚库存 + 状态=3已退单）
     */
    @PreAuthorize("@ss.hasPermi('restaurant:order:cancel')")
    @Log(title = "订单", businessType = BusinessType.UPDATE)
    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId)
    {
        return toAjax(tOrderService.cancelOrder(orderId));
    }
}

