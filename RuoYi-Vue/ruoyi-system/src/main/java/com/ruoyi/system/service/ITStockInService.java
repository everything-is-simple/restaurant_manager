package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.TStockIn;

/**
 * 入库记录Service接口
 * 
 * @author ruoyi
 * @date 2026-06-19
 */
public interface ITStockInService 
{
    /**
     * 查询入库记录
     * 
     * @param stockInId 入库记录主键
     * @return 入库记录
     */
    public TStockIn selectTStockInByStockInId(Long stockInId);

    /**
     * 查询入库记录列表
     * 
     * @param tStockIn 入库记录
     * @return 入库记录集合
     */
    public List<TStockIn> selectTStockInList(TStockIn tStockIn);

    /**
     * 新增入库记录
     * 
     * @param tStockIn 入库记录
     * @return 结果
     */
    public int insertTStockIn(TStockIn tStockIn);

    /**
     * 修改入库记录
     * 
     * @param tStockIn 入库记录
     * @return 结果
     */
    public int updateTStockIn(TStockIn tStockIn);

    /**
     * 批量删除入库记录
     * 
     * @param stockInIds 需要删除的入库记录主键集合
     * @return 结果
     */
    public int deleteTStockInByStockInIds(Long[] stockInIds);

    /**
     * 删除入库记录信息
     * 
     * @param stockInId 入库记录主键
     * @return 结果
     */
    public int deleteTStockInByStockInId(Long stockInId);
}
