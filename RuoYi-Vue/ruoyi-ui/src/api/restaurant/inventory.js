import request from '@/utils/request'

// 查询食材库存列表
export function listInventory(query) {
  return request({
    url: '/restaurant/inventory/list',
    method: 'get',
    params: query
  })
}

// 查询食材库存详细
export function getInventory(inventoryId) {
  return request({
    url: '/restaurant/inventory/' + inventoryId,
    method: 'get'
  })
}

// 新增食材库存
export function addInventory(data) {
  return request({
    url: '/restaurant/inventory',
    method: 'post',
    data: data
  })
}

// 修改食材库存
export function updateInventory(data) {
  return request({
    url: '/restaurant/inventory',
    method: 'put',
    data: data
  })
}

// 删除食材库存
export function delInventory(inventoryId) {
  return request({
    url: '/restaurant/inventory/' + inventoryId,
    method: 'delete'
  })
}
