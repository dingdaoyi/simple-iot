<template>
  <DwProjectConfig :config="projectConfig">
    <router-view />
  </DwProjectConfig>
</template>

<script setup>
// import { uploadMoreApi } from '@/api/newApi'
// import { useThemeStore } from '@/store'
import { useRouter, useRoute } from 'vue-router'
/* 主题默认加载配置 */
// const themeStore = useThemeStore()
// themeStore.setDarkModel(themeStore.isDark)
// themeStore.changeTheme(themeStore.themeJson.elColorPrimary)
// themeStore.changeSliderTheme(themeStore.themeJson.dwSliderActiveBg)
/* 主题默认加载配置 */

/* dwyl-ui默认传递参数配置 */
const projectConfig = {
  table: {
    pageField: 'page',
    limitField: 'size',
    totalField: 'total',
    dataField: 'data',
    defaultLimit: 20 // 默认展示多少条
  },
  vueRouter: {
    useRouter,
    useRoute
  },
  upload: {
    // uploadMoreApi
  }
}
/* dwyl-ui默认传递参数配置 */
</script>

<style lang="scss">
html,
body,
#app {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
}
#app {
  position: relative;
}
.dwmain-fixed {
  position: fixed;
  background-color: var(--el-color-primary);
  z-index: 10;
  top: 360px;
  right: 14px;
  cursor: pointer;
}
</style>
