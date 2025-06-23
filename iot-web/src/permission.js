import router from '@/router'
import { useAccountStore } from '@/store'

export const TOKEN = 'VEA-TOKEN'

router.beforeEach(async (to) => {
  const store = useAccountStore()
  const isLoggedIn = !!store.authorization?.tokenValue
  // 如果访问登录页面且已登录，跳转到首页
  if (to.path === '/login' && isLoggedIn) {
    return '/home'
  }

  // 如果访问需要登录的页面但未登录，跳转到登录页
  if (to.path !== '/login' && !isLoggedIn) {
    return `/login?redirect=${encodeURIComponent(to.fullPath)}`
  }

  return true
})
