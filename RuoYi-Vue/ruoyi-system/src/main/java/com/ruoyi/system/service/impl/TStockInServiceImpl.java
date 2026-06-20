package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.mapper.TInventoryMapper;
import com.ruoyi.system.mapper.TStockInMapper;
import com.ruoyi.system.domain.TStockIn;
import com.ruoyi.system.service.ITStockInService;

/**
 * 入库记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
@Service
public class TStockInServiceImpl implements ITStockInService 
{
    @Autowired
    private TStockInMapper tStockInMapper;

    @Autowired
    private TInventoryMapper tInventoryMapper;

    /**
     * 查询入库记录
     * 
     * @param stockInId 入库记录主键
     * @return 入库记录
     */
    @Override
    public TStockIn selectTStockInByStockInId(Long stockInId)
    {
        return tStockInMapper.selectTStockInByStockInId(stockInId);
    }

    /**
     * 查询入库记录列表
     * 
     * @param tStockIn 入库记录
     * @return 入库记录
     */
    @Override
    public List<TStockIn> selectTStockInList(TStockIn tStockIn)
    {
        return tStockInMapper.selectTStockInList(tStockIn);
    }

    /**
     * 新增入库记录
     * 
     * @param tStockIn 入库记录
     * @return 结果
     */
    @Override
    @Transactional
    public int insertTStockIn(TStockIn tStockIn)
    {
        validateStockIn(tStockIn);
        if (tStockIn.getInTime() == null)
        {
            tStockIn.setInTime(DateUtils.getNowDate());
        }
        tStockIn.setCreateTime(DateUtils.getNowDate());
        int rows = tStockInMapper.insertTStockIn(tStockIn);
        addInventoryStock(tStockIn.getIngredientId(), tStockIn.getQuantity());
        return rows;
    }

    /**
     * 修改入库记录
     * 
     * @param tStockIn 入库记录
     * @return 结果
     */
    @Override
    @Transactional
    public int updateTStockIn(TStockIn tStockIn)
    {
        validateStockIn(tStockIn);
        TStockIn old = tStockInMapper.selectTStockInByStockInId(tStockIn.getStockInId());
        if (old == null)
        {
            throw new ServiceException("入库记录不存在");
        }

        adjustInventoryOnUpdate(old, tStockIn);
        tStockIn.setUpdateTime(DateUtils.getNowDate());
        return tStockInMapper.updateTStockIn(tStockIn);
    }

    /**
     * 批量删除入库记录
     * 
     * @param stockInIds 需要删除的入库记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTStockInByStockInIds(Long[] stockInIds)
    {
        for (Long stockInId : stockInIds)
        {
            subtractInventoryForStockIn(stockInId);
        }
        return tStockInMapper.deleteTStockInByStockInIds(stockInIds);
    }

    /**
     * 删除入库记录信息
     * 
     * @param stockInId 入库记录主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteTStockInByStockInId(Long stockInId)
    {
        subtractInventoryForStockIn(stockInId);
        return tStockInMapper.deleteTStockInByStockInId(stockInId);
    }

    private void validateStockIn(TStockIn tStockIn)
    {
        if (tStockIn.getIngredientId() == null)
        {
            throw new ServiceException("食材不能为空");
        }
        if (tStockIn.getQuantity() == null || tStockIn.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("入库数量必须大于0");
        }
        if (tStockIn.getUnitPrice() != null && tStockIn.getUnitPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("采购单价不能小于0");
        }
    }

    private void adjustInventoryOnUpdate(TStockIn old, TStockIn current)
    {
        if (old.getIngredientId().equals(current.getIngredientId()))
        {
            BigDecimal diff = current.getQuantity().subtract(old.getQuantity());
            if (diff.compareTo(BigDecimal.ZERO) > 0)
            {
                addInventoryStock(current.getIngredientId(), diff);
            }
            else if (diff.compareTo(BigDecimal.ZERO) < 0)
            {
                deductInventoryStock(current.getIngredientId(), diff.abs());
            }
            return;
        }

        deductInventoryStock(old.getIngredientId(), old.getQuantity());
        addInventoryStock(current.getIngredientId(), current.getQuantity());
    }

    private void subtractInventoryForStockIn(Long stockInId)
    {
        TStockIn old = tStockInMapper.selectTStockInByStockInId(stockInId);
        if (old == null)
        {
            throw new ServiceException("入库记录不存在");
        }
        deductInventoryStock(old.getIngredientId(), old.getQuantity());
    }

    private void addInventoryStock(Long ingredientId, BigDecimal quantity)
    {
        int rows = tInventoryMapper.addStock(ingredientId, quantity);
        if (rows == 0)
        {
            throw new ServiceException("食材ID[" + ingredientId + "]未初始化库存，入库失败");
        }
    }

    private void deductInventoryStock(Long ingredientId, BigDecimal quantity)
    {
        int rows = tInventoryMapper.deductStock(ingredientId, quantity);
        if (rows == 0)
        {
            throw new ServiceException("食材ID[" + ingredientId + "]库存不足，无法调整入库记录");
        }
    }
}
