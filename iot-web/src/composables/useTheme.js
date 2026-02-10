import { computed, onMounted, ref, watch } from 'vue'

// 当前主题
const theme = ref('auto')

// 实际应用的主题（考虑 auto 时的系统偏好）
const resolvedTheme = ref('light')

// 获取系统主题偏好
function getSystemTheme() {
  if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
    return 'dark'
  }
  return 'light'
}

// 更新实际主题
function updateResolvedTheme() {
  if (theme.value === 'auto') {
    resolvedTheme.value = getSystemTheme()
  }
  else {
    resolvedTheme.value = theme.value
  }
  // 应用到 DOM
  applyTheme(resolvedTheme.value)
}

// 应用主题到 DOM
function applyTheme(mode) {
  if (mode === 'dark') {
    document.documentElement.classList.add('dark')
    document.documentElement.setAttribute('data-theme', 'dark')
  }
  else {
    document.documentElement.classList.remove('dark')
    document.documentElement.setAttribute('data-theme', 'light')
  }
}

// 切换主题
function setTheme(newTheme) {
  theme.value = newTheme
  updateResolvedTheme()
  // 保存到 localStorage
  localStorage.setItem('theme', newTheme)
}

// 从 localStorage 加载主题
function loadTheme() {
  const saved = localStorage.getItem('theme')
  if (saved && ['light', 'dark', 'auto'].includes(saved)) {
    theme.value = saved
  }
  updateResolvedTheme()
}

// 监听系统主题变化
function watchSystemTheme() {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')
  mediaQuery.addEventListener('change', updateResolvedTheme)
  return () => mediaQuery.removeEventListener('change', updateResolvedTheme)
}

// 初始化
export function useTheme() {
  onMounted(() => {
    loadTheme()
    // 清理监听器
    return watchSystemTheme()
  })

  // 监听主题变化
  watch(theme, updateResolvedTheme)

  return {
    theme,
    resolvedTheme,
    setTheme,
    isDark: computed(() => resolvedTheme.value === 'dark'),
    isLight: computed(() => resolvedTheme.value === 'light'),
  }
}
