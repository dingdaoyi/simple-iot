<template>
  <div class="flex flex-col wh-full">
    <headers />
    <div class="flex-1 flex overflow-hidden">
      <slider />
      <div class="flex-1 m-20px  overflow-hidden" :class="bgClass">
        <router-view #default="{ Component, route }">
          <transition name="fade-slide" mode="out-in">
            <keep-alive :include="include">
              <component :is="Component" :key="route.path" class="flex flex-col h-full" />
            </keep-alive>
          </transition>
        </router-view>
      </div>
    </div>
  </div>
  <div class="flex-center w-42px h-42px border-rounded dwmain-fixed" @click="handleBtn">
    <el-icon :size="26" color="#fff"><Setting /></el-icon>
  </div>
  <ThemeSet v-model="showThemeSet" />
</template>
<script lang="jsx" setup>
import { ref, watch } from 'vue'
import headers from './widget/header.vue'
import slider from './widget/slider'
import ThemeSet from '@/components/ThemeSet'
import { useRoute } from 'vue-router'

const route = useRoute()
const showThemeSet = ref(false)
const noBgPage = ref(['home'])
const defClass = 'bg-dwboxbg p-20px'
const bgClass = ref(defClass)

const include = ref('404')
const handleBtn = () => {
  showThemeSet.value = true
}

watch(() => route.name, name => {
  if (name === 'deviceDetail' || name === 'equipmentList') { // 入口页面
    include.value = 'equipmentList' // 缓存页面
  } else {
    include.value ? include.value = '404' : ''
  }
  if (noBgPage.value.includes(name)) { // 取消白色背景
    bgClass.value = ''
  } else {
    bgClass.value = defClass
  }
}, { immediate: true })
</script>

<style lang="scss" scoped>
.wrapper {
  display: flex;
  margin: 0 auto;
  width: 1440px;
  height: 100%;
  &.fluid {
    width: 100%;
  }

  .right {
    flex: 1;
    overflow: auto;
    &.flex {
      overflow: hidden;
      display: flex;
      flex-direction: column;
    }
    .top {
      background: #fff;
    }
    .main {
      flex: 1;
      background: #f5f5f5;
      padding: 16px;
      overflow: auto;
      &.pt0 {
        padding-top: 0;
      }
    }
  }
}
</style>
