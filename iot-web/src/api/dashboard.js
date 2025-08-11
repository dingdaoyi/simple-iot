import request from '@/utils/request'

export function getDashboardStatistics() {
  return request({
    url: '/api/dashboard/statistics',
    method: 'get',
  })
}
