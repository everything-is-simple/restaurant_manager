import request from '@/utils/request'

// 查询订单列表
export function listOrder(query) {
  return request({
    url: '/restaurant/order/list',
    method: 'get',
    params: query
  })
}

// 查询订单详细
export function getOrder(orderId) {
  return request({
    url: '/restaurant/order/' + orderId,
    method: 'get'
  })
}

// 新增订单
export function addOrder(data) {
  return request({
    url: '/restaurant/order',
    method: 'post',
    data: data
  })
}

// 修改订单
export function updateOrder(data) {
  return request({
    url: '/restaurant/order',
    method: 'put',
    data: data
  })
}

// 删除订单
export function delOrder(orderId) {
  return request({
    url: '/restaurant/order/' + orderId,
    method: 'delete'
  })
}

// 模拟下单
export function mockOrder(data) {
  return request({
    url: '/restaurant/order/mock',
    method: 'post',
    data: data
  })
}

// 完成订单
export function completeOrder(orderId) {
  return request({
    url: '/restaurant/order/complete/' + orderId,
    method: 'put'
  })
}

// 退单
export function cancelOrder(orderId) {
  return request({
    url: '/restaurant/order/cancel/' + orderId,
    method: 'put'
  })
}
