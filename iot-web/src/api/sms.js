import request from '@/utils/request'

// 短信配置相关
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

// 短信发送相关
export function sendSms(data) {
  return request({
    url: '/sms/send',
    method: 'post',
    data,
  })
}

export function sendSmsWithTemplate(data) {
  return request({
    url: '/sms/send/template',
    method: 'post',
    data,
  })
}

// 短信模板相关
export function getSmsTemplateList(configId) {
  return request({
    url: '/sms/template',
    method: 'get',
    params: { configId },
  })
}

export function addSmsTemplate(data) {
  return request({
    url: '/sms/template',
    method: 'post',
    data,
  })
}

export function updateSmsTemplate(data) {
  return request({
    url: '/sms/template',
    method: 'put',
    data,
  })
}

export function deleteSmsTemplate(id) {
  return request({
    url: `/sms/template/${id}`,
    method: 'delete',
  })
}

// 枚举相关
export function getSuppliers() {
  return request({
    url: '/sms/suppliers',
    method: 'get',
  })
}

export function getTemplateTypes() {
  return request({
    url: '/sms/template/types',
    method: 'get',
  })
}
