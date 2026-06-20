import request from '@/utils/request'

// 查询数据看板聚合数据（今日指标 + 趋势 + TopN + 预警列表）
export function getDashboard(query) {
  return request({
    url: '/restaurant/stats/dashboard',
    method: 'get',
    params: query
  })
}

// 查询库存预警列表（带分页）
export function listWarning(query) {
  return request({
    url: '/restaurant/stats/warning',
    method: 'get',
    params: query
  })
}
