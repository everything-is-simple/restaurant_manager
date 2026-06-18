import request from '@/utils/request'

// 查询菜品列表
export function listDish(query) {
  return request({
    url: '/restaurant/dish/list',
    method: 'get',
    params: query
  })
}

// 查询菜品详细
export function getDish(dishId) {
  return request({
    url: '/restaurant/dish/' + dishId,
    method: 'get'
  })
}

// 新增菜品
export function addDish(data) {
  return request({
    url: '/restaurant/dish',
    method: 'post',
    data: data
  })
}

// 修改菜品
export function updateDish(data) {
  return request({
    url: '/restaurant/dish',
    method: 'put',
    data: data
  })
}

// 删除菜品
export function delDish(dishId) {
  return request({
    url: '/restaurant/dish/' + dishId,
    method: 'delete'
  })
}
