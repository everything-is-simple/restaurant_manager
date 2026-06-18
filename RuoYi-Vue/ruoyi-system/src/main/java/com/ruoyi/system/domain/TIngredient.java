package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 食材档案对象 t_ingredient
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public class TIngredient extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 食材ID */
    private Long ingredientId;

    /** 食材名称 */
    @Excel(name = "食材名称")
    private String name;

    /** 单位 */
    @Excel(name = "单位")
    private String unit;

    /** 安全库存量 */
    @Excel(name = "安全库存量")
    private BigDecimal safetyStock;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0正常 2删除） */
    private String delFlag;

    public void setIngredientId(Long ingredientId) 
    {
        this.ingredientId = ingredientId;
    }

    public Long getIngredientId() 
    {
        return ingredientId;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setUnit(String unit) 
    {
        this.unit = unit;
    }

    public String getUnit() 
    {
        return unit;
    }

    public void setSafetyStock(BigDecimal safetyStock) 
    {
        this.safetyStock = safetyStock;
    }

    public BigDecimal getSafetyStock() 
    {
        return safetyStock;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
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
            .append("ingredientId", getIngredientId())
            .append("name", getName())
            .append("unit", getUnit())
            .append("safetyStock", getSafetyStock())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
