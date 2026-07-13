import request from '@/utils/request'

// 航空天气风险分析 API

export function getDashboard() {
  return request({
    url: '/aviation/dashboard',
    method: 'get'
  })
}

export function getRoutes() {
  return request({
    url: '/aviation/routes',
    method: 'get'
  })
}

export function getTrend() {
  return request({
    url: '/aviation/trend',
    method: 'get'
  })
}
