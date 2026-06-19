package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 菜品配方对象 t_dish_ingredient
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public class TDishIngredient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配方ID */
    private Long dishIngredientId;

    /** 菜品ID */
    @Excel(name = "菜品ID")
    private Long dishId;

    /** 食材ID */
    @Excel(name = "食材ID")
    private Long ingredientId;

    /** 单份消耗量 */
    @Excel(name = "单份消耗量")
    private BigDecimal quantity;

    /** 食材名称（非持久化，查询时 join 回填） */
    private String ingredientName;

    public void setDishIngredientId(Long dishIngredientId) 
    {
        this.dishIngredientId = dishIngredientId;
    }

    public Long getDishIngredientId() 
    {
        return dishIngredientId;
    }

    public void setDishId(Long dishId) 
    {
        this.dishId = dishId;
    }

    public Long getDishId() 
    {
        return dishId;
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

    public String getIngredientName()
    {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName)
    {
        this.ingredientName = ingredientName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dishIngredientId", getDishIngredientId())
            .append("dishId", getDishId())
            .append("ingredientId", getIngredientId())
            .append("quantity", getQuantity())
            .append("ingredientName", getIngredientName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
