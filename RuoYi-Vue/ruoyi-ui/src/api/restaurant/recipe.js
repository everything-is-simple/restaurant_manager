import request from '@/utils/request'

// 查询某菜品配方列表
export function getRecipe(dishId) {
  return request({
    url: '/restaurant/recipe/list/' + dishId,
    method: 'get'
  })
}

// 覆盖保存配方
export function saveRecipe(data) {
  return request({
    url: '/restaurant/recipe/save',
    method: 'put',
    data: data
  })
}
