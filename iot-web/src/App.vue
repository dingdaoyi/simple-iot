<template>
  <DwProjectConfig :config="projectConfig">
    <router-view />
  </DwProjectConfig>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { uploadMoreApi } from '@/api/index.js'
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
    uploadMoreApi
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
