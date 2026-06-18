import request from '@/utils/request'

// 查询菜品分类列表
export function listCategory(query) {
  return request({
    url: '/restaurant/category/list',
    method: 'get',
    params: query
  })
}

// 查询菜品分类详细
export function getCategory(categoryId) {
  return request({
    url: '/restaurant/category/' + categoryId,
    method: 'get'
  })
}

// 新增菜品分类
export function addCategory(data) {
  return request({
    url: '/restaurant/category',
    method: 'post',
    data: data
  })
}

// 修改菜品分类
export function updateCategory(data) {
  return request({
    url: '/restaurant/category',
    method: 'put',
    data: data
  })
}

// 删除菜品分类
export function delCategory(categoryId) {
  return request({
    url: '/restaurant/category/' + categoryId,
    method: 'delete'
  })
}
