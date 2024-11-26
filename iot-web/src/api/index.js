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
