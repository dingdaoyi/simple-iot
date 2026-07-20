import request from '@/utils/request'

export function webhookListApi(params) {
  return request({ url: '/webhook/config', method: 'get', params })
}

export function webhookCreateApi(data) {
  return request({ url: '/webhook/config', method: 'post', data })
}

export function webhookUpdateApi(data) {
  return request({ url: '/webhook/config', method: 'put', data })
}

export function webhookDeleteApi(id) {
  return request({ url: `/webhook/config/${id}`, method: 'delete' })
}

export function webhookRegenerateSecretApi(id) {
  return request({ url: `/webhook/config/${id}/regenerate-secret`, method: 'post' })
}
