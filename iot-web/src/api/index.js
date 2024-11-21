import request from '@/utils/request'

export const ucenter = '/ucenter'

// 登录接口
export const Index = data => {
  return request({
    headers: {
      noToken: 1
    },
    url: ucenter + '/sso/login',
    method: 'post',
    data
  })
}

