import request from '@/utils/request'

// === Device Group API ===

export function deviceGroupTreeApi() {
  return request({
    url: '/device-group/tree',
    method: 'get',
  })
}

export function deviceGroupAddApi(data) {
  return request({
    url: '/device-group',
    method: 'post',
    data,
  })
}

export function deviceGroupEditApi(data) {
  return request({
    url: '/device-group',
    method: 'put',
    data,
  })
}

export function deviceGroupDelApi(id) {
  return request({
    url: `/device-group/${id}`,
    method: 'delete',
  })
}

export function deviceGroupAssignApi(groupId, deviceIds) {
  return request({
    url: `/device-group/${groupId}/devices`,
    method: 'post',
    data: deviceIds,
  })
}

export function deviceGroupRemoveApi(groupId, deviceIds) {
  return request({
    url: `/device-group/${groupId}/devices`,
    method: 'delete',
    data: deviceIds,
  })
}

export function deviceGroupDevicesApi(groupId) {
  return request({
    url: `/device-group/${groupId}/devices`,
    method: 'get',
  })
}

// === Device Tag API ===

export function deviceTagAllApi() {
  return request({
    url: '/device-tag/all',
    method: 'get',
  })
}

export function deviceTagAddApi(data) {
  return request({
    url: '/device-tag',
    method: 'post',
    data,
  })
}

export function deviceTagEditApi(data) {
  return request({
    url: '/device-tag',
    method: 'put',
    data,
  })
}

export function deviceTagDelApi(id) {
  return request({
    url: `/device-tag/${id}`,
    method: 'delete',
  })
}

export function deviceTagAssignApi(tagId, deviceIds) {
  return request({
    url: `/device-tag/${tagId}/devices`,
    method: 'post',
    data: deviceIds,
  })
}

export function deviceTagRemoveApi(tagId, deviceIds) {
  return request({
    url: `/device-tag/${tagId}/devices`,
    method: 'delete',
    data: deviceIds,
  })
}

export function deviceTagDevicesApi(tagId) {
  return request({
    url: `/device-tag/${tagId}/devices`,
    method: 'get',
  })
}

export function deviceTagsByDeviceApi(deviceId) {
  return request({
    url: `/device-tag/device/${deviceId}`,
    method: 'get',
  })
}
