import request from '@/utils/request'

export function getDriverList() {
  return request({
    url: '/driver',
    method: 'get',
  })
}

export function addDriver(data) {
  return request({
    url: '/driver',
    method: 'post',
    data,
  })
}

export function updateDriver(data) {
  return request({
    url: '/driver',
    method: 'put',
    data,
  })
}

export function deleteDriver(driverId) {
  return request({
    url: `/driver/${driverId}`,
    method: 'delete',
  })
} 