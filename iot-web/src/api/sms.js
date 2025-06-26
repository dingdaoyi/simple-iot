import request from '@/utils/request'

export function getSmsConfigList(params) {
  return request({
    url: '/sms/config',
    method: 'get',
    params,
  })
}

export function addSmsConfig(data) {
  return request({
    url: '/sms/config',
    method: 'post',
    data,
  })
}

export function updateSmsConfig(data) {
  return request({
    url: '/sms/config',
    method: 'put',
    data,
  })
}

export function deleteSmsConfig(id) {
  return request({
    url: `/sms/config/${id}`,
    method: 'delete',
  })
}

export function setDefaultConfig(id) {
  return request({
    url: `/sms/config/${id}/default`,
    method: 'put',
  })
}

export function updateConfigStatus(id, status) {
  return request({
    url: `/sms/config/${id}/status`,
    method: 'put',
    params: { status },
  })
}

export function sendSms(data) {
  return request({
    url: '/sms/send',
    method: 'post',
    data,
  })
}
