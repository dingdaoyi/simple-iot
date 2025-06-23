import { useAccountStore } from '@/store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/iot',
  timeout: 100000,
  withCredentials: true,
})

// 拦截请求
service.interceptors.request.use(
  (config) => {
    if (config.headers.noToken !== 1) {
      const store = useAccountStore()
      if (!store.authorization) {
        // TODO 没有登录
        return config
      }
      const { tokenName, tokenValue } = store.authorization
      if (tokenValue) {
        config.headers[tokenName] = tokenValue
      }
    }
    return config
  },
  (error) => {
    // console.log(error);
    return Promise.reject(error)
  },
)

// 拦截响应
service.interceptors.response.use(
  // 响应成功进入第1个函数，该函数的参数是响应对象
  (response) => {
    const res = response.data
    if (res instanceof Blob && response.config.responseType === 'blob') {
      let filename = response.headers['content-disposition']
      filename = filename.split(';')[1].split('filename=')[1]
      return {
        blob: res,
        fileName: decodeURIComponent(filename),
      }
    }
    if (res.code === 1 || res.code === 200 || response.config.backOtherCode)
      return res
    ElMessage.error(res?.msg || '请求出错')
    return Promise.reject(res)
  },
  // 响应失败进入第2个函数，该函数的参数是错误对象
  async (error) => {
    const user = useAccountStore()
    const { data, status } = error.response
    if (status === 401) {
      user.clearToken()
      // 使用路由跳转，保持当前页面作为重定向参数
      const currentPath = window.location.hash.replace('#', '')
      window.location.href = `#/login?redirect=${encodeURIComponent(currentPath)}`
      return
    }
    ElMessage.error(data?.msg || error?.message || '请求出错')
    return Promise.reject(data)
  },
)

export default service
