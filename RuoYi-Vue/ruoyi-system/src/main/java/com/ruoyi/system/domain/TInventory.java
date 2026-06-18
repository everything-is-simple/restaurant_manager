package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 食材库存对象 t_inventory
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public class TInventory extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 库存ID */
    private Long inventoryId;

    /** 食材ID */
    @Excel(name = "食材ID")
    private Long ingredientId;

    /** 当前库存量 */
    @Excel(name = "当前库存量")
    private BigDecimal stock;

    /** 删除标志（0正常 2删除） */
    private String delFlag;

    public void setInventoryId(Long inventoryId) 
    {
        this.inventoryId = inventoryId;
    }

    public Long getInventoryId() 
    {
        return inventoryId;
    }

    public void setIngredientId(Long ingredientId) 
    {
        this.ingredientId = ingredientId;
    }

    public Long getIngredientId() 
    {
        return ingredientId;
    }

    public void setStock(BigDecimal stock) 
    {
        this.stock = stock;
    }

    public BigDecimal getStock() 
    {
        return stock;
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
            .append("inventoryId", getInventoryId())
            .append("ingredientId", getIngredientId())
            .append("stock", getStock())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
