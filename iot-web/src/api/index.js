import request from '@/utils/request'

// 登录接口
export function Index(data) {
  return request({
    headers: {
      noToken: 1,
    },
    url: '/user/login',
    method: 'post',
    data,
  })
}

export function productTypeListApi(params) {
  return request({
    url: '/product/type',
    method: 'get',
    params,
  })
}

/**
 * 修改产品,暂时不需要
 * @param data
 * @returns {*}
 */
export function productTypeEditeApi(data) {
  return request({
    url: '/product/type',
    method: 'put',
    data,
  })
}

export function productTypeAddApi(data) {
  console.log('添加参数:', data)
  return request({
    url: '/product/type',
    method: 'post',
    data,
  })
}

export function productTypeDelApi(id) {
  return request({
    url: `/product/type/${id}`,
    method: 'delete',
  })
}

export function productTypeDetailApi(id) {
  return request({
    url: `/product/type/${id}`,
    method: 'get',
  })
}

/**
 * 属性列表
 * @param params
 * @returns {*}
 */
export function propertyListApi(params) {
  return request({
    url: '/model/property/product/type',
    method: 'get',
    params,
  })
}

/**
 * 属性列表
 * @param id
 * @returns {*}
 */
export function propertyDeleteApi(id) {
  return request({
    url: `/model/property/${id}`,
    method: 'DELETE',
  })
}

/**
 * 保存标准属性
 * @param data
 * @returns {*}
 */
export function standardPropertyAddApi(data) {
  return request({
    url: '/model/property/product/type',
    method: 'post',
    data,
  })
}

/**
 * 保存自定义属性
 * @param data
 * @returns {*}
 */
export function customPropertyAddApi(data) {
  return request({
    url: '/model/property/product',
    method: 'post',
    data,
  })
}

/**
 * 修改标准属性
 * @param data
 * @returns {*}
 */
export function standardPropertyEditApi(data) {
  return request({
    url: '/model/property/product',
    method: 'put',
    data,
  })
}

export function standardServiceListApi(params) {
  return request({
    url: '/model/service/standard',
    method: 'get',
    params,
  })
}

export function customServiceListApi(params) {
  return request({
    url: '/model/service/custom',
    method: 'get',
    params,
  })
}

export function serviceAddApi(data) {
  return request({
    url: '/model/service',
    method: 'post',
    data,
  })
}

export function serviceDeleteApi(id) {
  return request({
    url: '/model/service',
    method: 'delete',
    id,
  })
}

export function serviceEditeApi(data) {
  return request({
    url: '/model/service',
    method: 'put',
    data,
  })
}

/**
 * 产品分页
 * @param data
 * @returns {*}
 */
export function productPageApi(data) {
  return request({
    url: '/product/page',
    method: 'post',
    data,
  })
}

/**
 * 产品列表
 * @param data
 * @returns {*}
 */
export function manufacturerListApi({ productTypeId }) {
  return request({
    url: '/manufacturer',
    method: 'get',
    params: {
      productTypeId,
    },
  })
}

/**
 * 产品列表
 * @param data
 * @returns {*}
 */
export function productListApi({ productTypeId, manufacturer }) {
  return request({
    url: '/product',
    method: 'get',
    params: {
      productTypeId,
      manufacturer,
    },
  })
}

/**
 * 产品添加
 * @param data
 * @returns {*}
 */
export function productAddApi(data) {
  return request({
    url: '/product',
    method: 'post',
    data,
  })
}

/**
 * 产品编辑
 * @param data
 * @returns {*}
 */
export function productEditeApi(data) {
  return request({
    url: '/product',
    method: 'put',
    data,
  })
}

/**
 * 产品删除
 * @param id
 * @returns {*}
 */
export function productDeleteApi(id) {
  return request({
    url: `/product/${id}`,
    method: 'DELETE',
    id,
  })
}

/**
 * 产品详情
 * @param id
 * @returns {*}
 */
export function productDetailApi(id) {
  return request({
    url: `/product/${id}`,
    method: 'GET',
  })
}

/**
 * 协议列表
 * @param params
 * @returns {*}
 */
export function protocolListApi(params) {
  return request({
    url: '/protocol',
    method: 'get',
    params,
  })
}

/**
 * 协议添加
 * @param data
 * @returns {*}
 */
export function protocolAddApi(data) {
  return request({
    url: '/protocol',
    method: 'POST',
    data,
  })
}

/**
 * 协议添加
 * @param data
 * @returns {*}
 */
export function protocolEditApi(data) {
  return request({
    url: '/protocol',
    method: 'put',
    data,
  })
}

/**
 * 协议添加
 * @param id
 * @returns {*}
 */
export function protocolDeleteApi(id) {
  return request({
    url: `/protocol/${id}`,
    method: 'delete',
  })
}

/**
 * 图标列表
 * @param params
 * @returns {*}
 */
export function iconListApi(params) {
  return request({
    url: '/icon',
    method: 'get',
    params,
  })
}

/**
 * 图标添加
 * @param data
 * @returns {*}
 */
export function iconAddApi(data) {
  return request({
    url: '/icon',
    method: 'post',
    data,
  })
}

/**
 * 图标修改
 * @param data
 * @returns {*}
 */
export function iconEditeApi(data) {
  return request({
    url: '/icon',
    method: 'put',
    data,
  })
}

/**
 * 图标删除
 * @param id
 * @returns {*}
 */
export function iconDeleteApi(id) {
  return request({
    url: `/icon/${id}`,
    method: 'delete',
  })
}

export function uploadMoreApi(data) {
  return request({
    url: '/file/upload',
    method: 'post',
    data,
  })
}

export function loadTslData(productId) {
  return request({
    url: `/product/tsl/${productId}`,
    method: 'get',
  })
}

export function deviceDeleteApi(id) {
  return request({
    url: `/device/${id}`,
    method: 'DELETE',
  })
}

export function devicePageApi(data) {
  return request({
    url: '/device/page',
    method: 'post',
    data,
  })
}

export function deviceDetailApi(id) {
  return request({
    url: `/device/${id}`,
    method: 'get',
  })
}

/**
 * 设备编辑
 * @param data
 * @returns {*}
 */
export function deviceEditeApi(data) {
  return request({
    url: '/device',
    method: 'put',
    data,
  })
}

/**
 * 设备编辑
 * @param data
 * @returns {*}
 */
export function deviceAddApi(data) {
  return request({
    url: '/device',
    method: 'post',
    data,
  })
}

/**
 * 设备搜索
 * @param params
 * @returns {*}
 */
export function deviceListApi(params) {
  return request({
    url: '/device/list',
    method: 'get',
    params,
  })
}
/**
 * 字典查询
 * @param group 分组名称
 */
export function dictListApi(group) {
  return request({
    url: '/dict/list',
    method: 'get',
    params: {
      group,
    },
  })
}

/**
 * 设备日志最后数据
 * @param deviceKey 设备日志最后数据
 */
export function deviceDataLast(deviceKey) {
  return request({
    url: `/device/data/property/last/${deviceKey}`,
    method: 'get',
  })
}

/**
 * 设备指标
 * @param data 参数上
 */
export function deviceDataMetric(data) {
  return request({
    url: `/device/data/property/metric`,
    method: 'post',
    data,
  })
}

/**
 * 设备指标
 * @param data 参数上
 */
export function rulePageApi(data) {
  return request({
    url: `/rule/page`,
    method: 'post',
    data,
  })
}

/**
 * 添加消息接收者
 * @param data 参数上
 */
export function messageReceiveAddApi(data) {
  return request({
    url: '/message/receive',
    method: 'post',
    data,
  })
}

/**
 * 编辑消息接收者
 * @param data 参数上
 */
export function messageReceiveEditApi(data) {
  return request({
    url: '/message/receive',
    method: 'put',
    data,
  })
}

/**
 * 编辑消息接收者
 * @param id id
 */
export function messageReceiveDeleteApi(id) {
  return request({
    url: `/message/receive/${id}`,
    method: 'delete',
  })
}

/**
 * 编辑消息接收者
 * @param data data
 */
export function messageReceivePageApi(data) {
  return request({
    url: `/message/receive/page`,
    method: 'post',
    data,
  })
}

/**
 * 编辑消息接收者
 * @param params params
 */
export function messageReceiveListApi(params) {
  return request({
    url: `/message/receive`,
    method: 'GET',
    params,
  })
}
