import request from '@/utils/request'

// 查询天气监测城市列表
export function listCity(query) {
  return request({
    url: '/city/city/list',
    method: 'get',
    params: query
  })
}

// 查询天气监测城市详细
export function getCity(id) {
  return request({
    url: '/city/city/' + id,
    method: 'get'
  })
}

// 新增天气监测城市
export function addCity(data) {
  return request({
    url: '/city/city',
    method: 'post',
    data: data
  })
}

// 修改天气监测城市
export function updateCity(data) {
  return request({
    url: '/city/city',
    method: 'put',
    data: data
  })
}

// 删除天气监测城市
export function delCity(id) {
  return request({
    url: '/city/city/' + id,
    method: 'delete'
  })
}
