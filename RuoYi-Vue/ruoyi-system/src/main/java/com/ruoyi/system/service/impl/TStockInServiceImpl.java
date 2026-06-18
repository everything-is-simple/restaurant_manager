package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public int insertTStockIn(TStockIn tStockIn)
    {
        tStockIn.setCreateTime(DateUtils.getNowDate());
        return tStockInMapper.insertTStockIn(tStockIn);
    }

    /**
     * 修改入库记录
     * 
     * @param tStockIn 入库记录
     * @return 结果
     */
    @Override
    public int updateTStockIn(TStockIn tStockIn)
    {
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
    public int deleteTStockInByStockInIds(Long[] stockInIds)
    {
        return tStockInMapper.deleteTStockInByStockInIds(stockInIds);
    }

    /**
     * 删除入库记录信息
     * 
     * @param stockInId 入库记录主键
     * @return 结果
     */
    @Override
    public int deleteTStockInByStockInId(Long stockInId)
    {
        return tStockInMapper.deleteTStockInByStockInId(stockInId);
    }
}
