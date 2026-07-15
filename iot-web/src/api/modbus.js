import request from '@/utils/request'

export function modbusListApi(params) {
  return request({ url: '/modbus', method: 'get', params })
}

export function modbusGetApi(id) {
  return request({ url: `/modbus/${id}`, method: 'get' })
}

export function modbusAddApi(data) {
  return request({ url: '/modbus', method: 'post', data })
}

export function modbusUpdateApi(data) {
  return request({ url: '/modbus', method: 'put', data })
}

export function modbusDeleteApi(id) {
  return request({ url: `/modbus/${id}`, method: 'delete' })
}

export function modbusTestApi(id) {
  return request({ url: `/modbus/${id}/test`, method: 'post' })
}
