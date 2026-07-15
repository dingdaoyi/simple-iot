import request from '@/utils/request'

// 首页统计 (原有 API)
export function getDashboardStatistics() {
  return request({
    url: '/api/dashboard/statistics',
    method: 'get',
  })
}

// 自定义仪表盘 CRUD
export function dashboardList() {
  return request({ url: '/dashboard/list', method: 'get' })
}

export function dashboardGet(id) {
  return request({ url: `/dashboard/${id}`, method: 'get' })
}

export function dashboardSave(data) {
  return request({ url: '/dashboard', method: 'post', data })
}

export function dashboardUpdate(data) {
  return request({ url: '/dashboard', method: 'put', data })
}

export function dashboardDelete(id) {
  return request({ url: `/dashboard/${id}`, method: 'delete' })
}

// 复用现有遥测数据 API
export function fetchLatestData(deviceKey) {
  return request({ url: `/device/data/property/last/${deviceKey}`, method: 'get' })
}

export function fetchMetric(data) {
  return request({ url: '/device/data/property/metric', method: 'post', data })
}
