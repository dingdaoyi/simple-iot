import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import * as echarts from 'echarts'
// 引入element-plus
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { createApp } from 'vue'
import Echarts from 'vue-echarts'
import App from './App.vue'
// 引入路由
import router from './router'

// 引入store
import { setupStore } from './store'
// 引入中文语言包
import 'dayjs/locale/zh-cn'

import 'uno.css'

import '@/styles/global.scss'

// 权限控制
import './permission'

async function setupApp() {
  const app = createApp(App)

  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
  }

  app.use(ElementPlus, { locale: zhCn })
  setupStore(app)
  app.use(router)
  app.component('e-charts', Echarts)
  app.config.globalProperties.$echarts = echarts
  app.mount('#app')
}

setupApp()
