import { ref } from 'vue'

// 侧边栏折叠状态（桌面端）
const isCollapsed = ref(false)
// 移动端抽屉开关
const isMobileOpen = ref(false)

export function useSidebar() {
  function toggle() {
    isCollapsed.value = !isCollapsed.value
  }

  function collapse() {
    isCollapsed.value = true
  }

  function expand() {
    isCollapsed.value = false
  }

  function openMobile() {
    isMobileOpen.value = true
  }

  function closeMobile() {
    isMobileOpen.value = false
  }

  function toggleMobile() {
    isMobileOpen.value = !isMobileOpen.value
  }

  return {
    isCollapsed,
    isMobileOpen,
    toggle,
    collapse,
    expand,
    openMobile,
    closeMobile,
    toggleMobile,
  }
}
