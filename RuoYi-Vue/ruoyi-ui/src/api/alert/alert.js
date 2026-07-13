import request from '@/utils/request'

// 查询天气告警规则配置列表
export function listAlert(query) {
  return request({
    url: '/alert/alert/list',
    method: 'get',
    params: query
  })
}

// 查询天气告警规则配置详细
export function getAlert(id) {
  return request({
    url: '/alert/alert/' + id,
    method: 'get'
  })
}

// 新增天气告警规则配置
export function addAlert(data) {
  return request({
    url: '/alert/alert',
    method: 'post',
    data: data
  })
}

// 修改天气告警规则配置
export function updateAlert(data) {
  return request({
    url: '/alert/alert',
    method: 'put',
    data: data
  })
}

// 删除天气告警规则配置
export function delAlert(id) {
  return request({
    url: '/alert/alert/' + id,
    method: 'delete'
  })
}
