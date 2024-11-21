import { defineStore } from 'pinia'
import { getItem, setItem, removeItem } from '@/utils/storage' // getItem和setItem是封装的操作localStorage的方法
import { GetUserinfo } from '@/api'
import { flattenTree } from '@/utils/utils.js'
export const TOKEN = 'VEA-TOKEN'

export const useAccountStore = defineStore({
  id: 'account',
  state: () => ({
    authorization: getItem(TOKEN),
    userinfo: null,
    menuData: [], // 树列表
    flatMenuData: [] // 扁片树
  }),
  getters: {},
  actions: {
    setToken (data) {
      this.authorization = data
      // 保存到localStorage
      setItem(TOKEN, data)
    },
    clearToken (state) {
      this.authorization = {}
      this.userinfo = null
      removeItem(TOKEN)
    },
    // 获取用户信息
    async getUserinfo () {
      const { code, data } = await GetUserinfo()
      if (code === 1) {
        this.userinfo = data
        this.menuData = data.permissionVos
        this.flatMenuData = flattenTree(this.menuData)
        return Promise.resolve(data, data.permissionVos)
      }
    },
    /* 判断是否有菜单路由权限 */
    checkMenuPermission (data) {
      const flatMenuData = this.flatMenuData
      const isMenu = flatMenuData.find(o => o.permissionUrl === data)
      return !!isMenu
    }
  }
})
