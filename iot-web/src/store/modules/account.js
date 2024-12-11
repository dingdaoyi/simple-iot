import { getItem, removeItem, setItem } from '@/utils/storage' // getItem和setItem是封装的操作localStorage的方法
import { defineStore } from 'pinia'

export const TOKEN = 'VEA-TOKEN'

export const useAccountStore = defineStore({
  id: 'account',
  state: () => ({
    authorization: getItem(TOKEN),
    userinfo: null,
    menuData: [], // 树列表
  }),
  getters: {},
  actions: {
    setToken(data) {
      this.authorization = data
      // 保存到localStorage
      setItem(TOKEN, data)
    },
    clearToken() {
      this.authorization = {}
      this.userinfo = null
      removeItem(TOKEN)
    },
  },
})
