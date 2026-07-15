import request from '@/utils/request'

export function firmwareListApi(params) {
  return request({ url: '/ota/firmware', method: 'get', params })
}

export function firmwareUploadApi(formData) {
  return request({ url: '/ota/firmware/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}

export function firmwarePublishApi(id) {
  return request({ url: `/ota/firmware/${id}/publish`, method: 'put' })
}

export function firmwareDeleteApi(id) {
  return request({ url: `/ota/firmware/${id}`, method: 'delete' })
}

export function otaTaskListApi(params) {
  return request({ url: '/ota/task', method: 'get', params })
}

export function otaTaskGetApi(id) {
  return request({ url: `/ota/task/${id}`, method: 'get' })
}

export function otaTaskCreateApi(params) {
  return request({ url: '/ota/task', method: 'post', params })
}
