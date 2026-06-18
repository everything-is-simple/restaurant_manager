package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 入库记录对象 t_stock_in
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public class TStockIn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 入库ID */
    private Long stockInId;

    /** 食材ID */
    @Excel(name = "食材ID")
    private Long ingredientId;

    /** 入库数量 */
    @Excel(name = "入库数量")
    private BigDecimal quantity;

    /** 采购单价 */
    @Excel(name = "采购单价")
    private BigDecimal unitPrice;

    /** 入库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入库时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date inTime;

    /** 删除标志（0正常 2删除） */
    private String delFlag;

    public void setStockInId(Long stockInId) 
    {
        this.stockInId = stockInId;
    }

    public Long getStockInId() 
    {
        return stockInId;
    }

    public void setIngredientId(Long ingredientId) 
    {
        this.ingredientId = ingredientId;
    }

    public Long getIngredientId() 
    {
        return ingredientId;
    }

    public void setQuantity(BigDecimal quantity) 
    {
        this.quantity = quantity;
    }

    public BigDecimal getQuantity() 
    {
        return quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) 
    {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getUnitPrice() 
    {
        return unitPrice;
    }

    public void setInTime(Date inTime) 
    {
        this.inTime = inTime;
    }

    public Date getInTime() 
    {
        return inTime;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("stockInId", getStockInId())
            .append("ingredientId", getIngredientId())
            .append("quantity", getQuantity())
            .append("unitPrice", getUnitPrice())
            .append("inTime", getInTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
