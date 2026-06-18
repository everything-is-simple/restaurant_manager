import request from '@/utils/request'

// 查询入库记录列表
export function listStockIn(query) {
  return request({
    url: '/restaurant/stockIn/list',
    method: 'get',
    params: query
  })
}

// 查询入库记录详细
export function getStockIn(stockInId) {
  return request({
    url: '/restaurant/stockIn/' + stockInId,
    method: 'get'
  })
}

// 新增入库记录
export function addStockIn(data) {
  return request({
    url: '/restaurant/stockIn',
    method: 'post',
    data: data
  })
}

// 修改入库记录
export function updateStockIn(data) {
  return request({
    url: '/restaurant/stockIn',
    method: 'put',
    data: data
  })
}

// 删除入库记录
export function delStockIn(stockInId) {
  return request({
    url: '/restaurant/stockIn/' + stockInId,
    method: 'delete'
  })
}
