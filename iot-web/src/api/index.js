import request from '@/utils/request'

// 登录接口
export const Index = data => {
  return request({
    headers: {
      noToken: 1
    },
    url: '/user/login',
    method: 'post',
    data
  })
}

export const productTypeListApi = params => {
  return request({
    url: '/product/type',
    method: 'get',
    params
  })
}

/**
 * 修改产品,暂时不需要
 * @param data
 * @returns {*}
 */
export const productTypeEditeApi = data => {
  return request({
    url: '/product/type',
    method: 'put',
    data
  })
}

export const productTypeAddApi = data => {
  console.log('添加参数:', data)
  return request({
    url: '/product/type',
    method: 'post',
    data
  })
}
export const productTypeDelApi = id => {
  return request({
    url: `/product/type/${id}`,
    method: 'delete'
  })
}
export const productTypeDetailApi = id => {
  return request({
    url: `/product/type/${id}`,
    method: 'get'
  })
}

/**
 * 属性列表
 * @param params
 * @returns {*}
 */
export const propertyListApi = params => {
  return request({
    url: '/model/property/product/type',
    method: 'get',
    params
  })
}
/**
 * 属性列表
 * @param id
 * @returns {*}
 */
export const propertyDeleteApi = id => {
  return request({
    url: `/model/property/${id}`,
    method: 'DELETE'
  })
}
/**
 * 保存标准属性
 * @param data
 * @returns {*}
 */
export const standardPropertyAddApi = data => {
  return request({
    url: '/model/property/product/type',
    method: 'post',
    data
  })
}

/**
 * 保存自定义属性
 * @param data
 * @returns {*}
 */
export const customPropertyAddApi = data => {
  return request({
    url: '/model/property/product',
    method: 'post',
    data
  })
}

/**
 * 修改标准属性
 * @param data
 * @returns {*}
 */
export const standardPropertyEditApi = data => {
  return request({
    url: '/model/property/product',
    method: 'put',
    data
  })
}
export const standardServiceListApi = params => {
  return request({
    url: '/model/service/standard',
    method: 'get',
    params
  })
}
export const customServiceListApi = params => {
  return request({
    url: '/model/service/custom',
    method: 'get',
    params
  })
}

export const serviceAddApi = data => {
  return request({
    url: '/model/service',
    method: 'post',
    data
  })
}
export const serviceDeleteApi = id => {
  return request({
    url: '/model/service',
    method: 'delete',
    id
  })
}

export const serviceEditeApi = data => {
  return request({
    url: '/model/service',
    method: 'put',
    data
  })
}

/**
 * 产品分页
 * @param data
 * @returns {*}
 */
export const productPageApi = data => {
  return request({
    url: '/product/page',
    method: 'post',
    data
  })
}

/**
 * 产品列表
 * @param data
 * @returns {*}
 */
export const manufacturerListApi = ({ productTypeId }) => {
  return request({
    url: '/manufacturer',
    method: 'get',
    params: {
      productTypeId
    }
  })
}
/**
 * 产品列表
 * @param data
 * @returns {*}
 */
export const productListApi = ({ productTypeId, manufacturer }) => {
  return request({
    url: '/product',
    method: 'get',
    params: {
      productTypeId,
      manufacturer
    }
  })
}

/**
 * 产品添加
 * @param data
 * @returns {*}
 */
export const productAddApi = data => {
  return request({
    url: '/product',
    method: 'post',
    data
  })
}
/**
 * 产品编辑
 * @param data
 * @returns {*}
 */
export const productEditeApi = data => {
  return request({
    url: '/product',
    method: 'put',
    data
  })
}

/**
 * 产品删除
 * @param id
 * @returns {*}
 */
export const productDeleteApi = id => {
  return request({
    url: `/product/${id}`,
    method: 'DELETE',
    id
  })
}

/**
 * 产品详情
 * @param id
 * @returns {*}
 */
export const productDetailApi = id => {
  return request({
    url: `/product/${id}`,
    method: 'GET'
  })
}

/**
 * 协议列表
 * @param params
 * @returns {*}
 */
export const protocolListApi = params => {
  return request({
    url: '/protocol',
    method: 'get',
    params
  })
}

/**
 * 协议添加
 * @param data
 * @returns {*}
 */
export const protocolAddApi = data => {
  return request({
    url: '/protocol',
    method: 'POST',
    data
  })
}
/**
 * 协议添加
 * @param data
 * @returns {*}
 */
export const protocolEditApi = data => {
  return request({
    url: '/protocol',
    method: 'put',
    data
  })
}

/**
 * 协议添加
 * @param id
 * @returns {*}
 */
export const protocolDeleteApi = id => {
  return request({
    url: `/protocol/${id}`,
    method: 'delete'
  })
}

/**
 * 图标列表
 * @param params
 * @returns {*}
 */
export const iconListApi = params => {
  return request({
    url: '/icon',
    method: 'get',
    params
  })
}
/**
 * 图标添加
 * @param data
 * @returns {*}
 */
export const iconAddApi = data => {
  return request({
    url: '/icon',
    method: 'post',
    data
  })
}

/**
 * 图标修改
 * @param data
 * @returns {*}
 */
export const iconEditeApi = data => {
  return request({
    url: '/icon',
    method: 'put',
    data
  })
}

/**
 * 图标删除
 * @param id
 * @returns {*}
 */
export const iconDeleteApi = id => {
  return request({
    url: `/icon/${id}`,
    method: 'delete'
  })
}

export const uploadMoreApi = data => {
  return request({
    url: '/file/upload',
    method: 'post',
    data
  })
}

export const loadTslData = productId => {
  return request({
    url: `/product/tsl/${productId}`,
    method: 'get'
  })
}

export const deviceDeleteApi = id => {
  return request({
    url: `/device/${id}`,
    method: 'DELETE'
  })
}

export const devicePageApi = data => {
  return request({
    url: '/device/page',
    method: 'post',
    data
  })
}
