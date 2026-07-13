import request from '@/utils/request'

// 查询极端天气预警记录列表
export function listExtreme(query) {
  return request({
    url: '/extreme/extreme/list',
    method: 'get',
    params: query
  })
}

// 查询极端天气预警记录详细
export function getExtreme(id) {
  return request({
    url: '/extreme/extreme/' + id,
    method: 'get'
  })
}

// 新增极端天气预警记录
export function addExtreme(data) {
  return request({
    url: '/extreme/extreme',
    method: 'post',
    data: data
  })
}

// 修改极端天气预警记录
export function updateExtreme(data) {
  return request({
    url: '/extreme/extreme',
    method: 'put',
    data: data
  })
}

// 删除极端天气预警记录
export function delExtreme(id) {
  return request({
    url: '/extreme/extreme/' + id,
    method: 'delete'
  })
}
