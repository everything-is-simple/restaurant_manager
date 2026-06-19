package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 订单明细对象 t_order_item
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public class TOrderItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 明细ID */
    private Long orderItemId;

    /** 订单ID */
    @Excel(name = "订单ID")
    private Long orderId;

    /** 菜品ID */
    @Excel(name = "菜品ID")
    private Long dishId;

    /** 菜品名快照 */
    @Excel(name = "菜品名快照")
    private String dishName;

    /** 成交单价快照 */
    @Excel(name = "成交单价快照")
    private BigDecimal price;

    /** 份数 */
    @Excel(name = "份数")
    private Integer quantity;

    public void setOrderItemId(Long orderItemId) 
    {
        this.orderItemId = orderItemId;
    }

    public Long getOrderItemId() 
    {
        return orderItemId;
    }

    public void setOrderId(Long orderId) 
    {
        this.orderId = orderId;
    }

    public Long getOrderId() 
    {
        return orderId;
    }

    public void setDishId(Long dishId) 
    {
        this.dishId = dishId;
    }

    public Long getDishId() 
    {
        return dishId;
    }

    public void setDishName(String dishName) 
    {
        this.dishName = dishName;
    }

    public String getDishName() 
    {
        return dishName;
    }

    public void setPrice(BigDecimal price) 
    {
        this.price = price;
    }

    public BigDecimal getPrice() 
    {
        return price;
    }

    public void setQuantity(Integer quantity) 
    {
        this.quantity = quantity;
    }

    public Integer getQuantity() 
    {
        return quantity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("orderItemId", getOrderItemId())
            .append("orderId", getOrderId())
            .append("dishId", getDishId())
            .append("dishName", getDishName())
            .append("price", getPrice())
            .append("quantity", getQuantity())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
