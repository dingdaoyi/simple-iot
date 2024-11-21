import { defineStore } from 'pinia'
import { getItem, setItem, removeItem } from '@/utils/storage' // getItem和setItem是封装的操作localStorage的方法
import { kebabCase } from 'lodash'
import { nextTick } from 'vue'

const themeKey = 'theme'
const key = 'primary'

const defaultConfig = {
  elColorPrimary: '#167EFF',
  dwSliderActiveBg: '#465D83'
}

/** 添加css vars至html */
function addThemeCssVarsToHtml (themeVars) {
  const keys = Object.keys(themeVars)
  const style = []
  keys.forEach(key => {
    style.push(`--${kebabCase(key)}: ${themeVars[key]}`)
  })
  const styleStr = style.join(';')
  document.documentElement.style.cssText += styleStr
}

export const useThemeStore = defineStore({
  id: 'theme',
  state: () => ({
    isDark: getItem('vueuse-color-scheme') === true || false,
    themeJson: getItem(themeKey) || defaultConfig
  }),
  getters: {},
  actions: {
    /* 设置主题模式 */
    setDarkModel (model) {
      nextTick(() => {
        this.isDark = model
        if (this.isDark) {
          document.documentElement.classList.add('dark')
        } else {
          document.documentElement.classList.remove('dark')
        }
        setItem('vueuse-color-scheme', this.isDark)
      })
    },
    /* 切换主题模式 */
    toogleDarkModel () {
      if (this.isDark) {
        this.setDarkModel(false)
      } else {
        this.setDarkModel(true)
      }
    },
    /* 设置系统主题 */
    changeTheme (colorVal) {
      const el = document.documentElement
      el.style.setProperty(`--el-color-${key}`, colorVal)

      const resData = {
        elColorPrimary: colorVal
      }

      for (let i = 1; i <= 9; i++) {
        el.style.setProperty(`--el-color-${key}-light-${i}`, lighten(colorVal, i / 10))
        el.style.setProperty(`--el-color-${key}-dark-${i}`, darken(colorVal, i / 10))

        const variable1 = `elColorPrimaryLight${i}`
        const variable2 = `elColorPrimaryDark${i}`
        resData[variable1] = lighten(colorVal, i / 10)
        resData[variable2] = darken(colorVal, i / 10)
      }
      addThemeCssVarsToHtml(resData)
      this.themeJson.elColorPrimary = colorVal

      setItem(themeKey, this.themeJson)
    },
    /* 设置侧边栏主题 */
    changeSliderTheme (val) {
      document.documentElement.style.cssText += `--dw-slider-active-bg:${val}`
      this.themeJson.dwSliderActiveBg = val
      setItem(themeKey, this.themeJson)
    }
  }
})
