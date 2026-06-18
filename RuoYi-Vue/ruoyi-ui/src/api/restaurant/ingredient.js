import request from '@/utils/request'

// 查询食材档案列表
export function listIngredient(query) {
  return request({
    url: '/restaurant/ingredient/list',
    method: 'get',
    params: query
  })
}

// 查询食材档案详细
export function getIngredient(ingredientId) {
  return request({
    url: '/restaurant/ingredient/' + ingredientId,
    method: 'get'
  })
}

// 新增食材档案
export function addIngredient(data) {
  return request({
    url: '/restaurant/ingredient',
    method: 'post',
    data: data
  })
}

// 修改食材档案
export function updateIngredient(data) {
  return request({
    url: '/restaurant/ingredient',
    method: 'put',
    data: data
  })
}

// 删除食材档案
export function delIngredient(ingredientId) {
  return request({
    url: '/restaurant/ingredient/' + ingredientId,
    method: 'delete'
  })
}
