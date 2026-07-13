import request from '@/utils/request'

// 查询天气数据记录列表
export function listRecord(query) {
  return request({
    url: '/record/record/list',
    method: 'get',
    params: query
  })
}

// 查询天气数据记录详细
export function getRecord(id) {
  return request({
    url: '/record/record/' + id,
    method: 'get'
  })
}

// 新增天气数据记录
export function addRecord(data) {
  return request({
    url: '/record/record',
    method: 'post',
    data: data
  })
}

// 修改天气数据记录
export function updateRecord(data) {
  return request({
    url: '/record/record',
    method: 'put',
    data: data
  })
}

// 删除天气数据记录
export function delRecord(id) {
  return request({
    url: '/record/record/' + id,
    method: 'delete'
  })
}
