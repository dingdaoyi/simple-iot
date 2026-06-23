import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

// Element Plus 语言包
import zhCnElementPlus from 'element-plus/es/locale/lang/zh-cn'
import enElementPlus from 'element-plus/es/locale/lang/en'

// 获取浏览器语言
function getBrowserLocale() {
  const navigatorLocale = navigator.language || navigator.userLanguage
  if (navigatorLocale.startsWith('zh')) {
    return 'zh-CN'
  }
  return 'en-US'
}

// 从 localStorage 获取语言设置，如果没有则使用浏览器语言
const savedLocale = localStorage.getItem('locale') || getBrowserLocale()

const i18n = createI18n({
  legacy: false, // 使用 Composition API 模式
  locale: savedLocale,
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': {
      ...zhCN,
      el: zhCnElementPlus.el,
    },
    'en-US': {
      ...enUS,
      el: enElementPlus.el,
    },
  },
})

export default i18n

// 导出 Element Plus 语言包配置
export const elementPlusLocales = {
  'zh-CN': zhCnElementPlus,
  'en-US': enElementPlus,
}

// 切换语言的辅助函数
export function setLocale(locale) {
  i18n.global.locale.value = locale
  localStorage.setItem('locale', locale)
  // 更新 HTML lang 属性
  document.querySelector('html').setAttribute('lang', locale)
}

// 获取当前语言
export function getLocale() {
  return i18n.global.locale.value
}
