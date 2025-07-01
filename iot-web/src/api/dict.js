import request from '@/utils/request'

// 字典接口
export function getSmsSupplier() {
  return request({
    url: '/dict/sms-supplier',
    method: 'get',
  })
}

export function getSmsTemplateType() {
  return request({
    url: '/dict/sms-template-type',
    method: 'get',
  })
}

export function getNotifyType() {
  return request({
    url: '/dict/notify-type',
    method: 'get',
  })
}

export function getStatus() {
  return request({
    url: '/dict/status',
    method: 'get',
  })
}

export function getServiceType() {
  return request({
    url: '/dict/service-type',
    method: 'get',
  })
}

export function getDataType() {
  return request({
    url: '/dict/data-type',
    method: 'get',
  })
}

export function getParamType() {
  return request({
    url: '/dict/param-type',
    method: 'get',
  })
}

export function getEventType() {
  return request({
    url: '/dict/event-type',
    method: 'get',
  })
}
