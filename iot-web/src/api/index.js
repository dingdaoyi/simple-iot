import request from '@/utils/request'

// 登录接口
export const Index = data => {
  return request({
    headers: {
      noToken: 1
    },
    url:   '/user/login',
    method: 'post',
    data
  })
}

