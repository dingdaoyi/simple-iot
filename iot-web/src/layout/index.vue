<script lang="jsx" setup>
import headers from './widget/header.vue'
import slider from './widget/slider'
import { useSidebar } from '@/composables/useSidebar.js'

const { isMobileOpen, closeMobile } = useSidebar()
</script>

<template>
  <div class="layout-container">
    <headers />
    <div class="layout-content">
      <slider />
      <!-- 移动端遮罩 -->
      <div v-if="isMobileOpen" class="mobile-overlay" @click="closeMobile" />
      <div class="layout-main">
        <router-view v-slot="{ Component, route }">
          <div class="view-container">
            <component :is="Component" :key="route.path" />
          </div>
        </router-view>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: 100%;
  background: var(--iot-color-background);
  transition: background var(--transition-fast);
}

/* ponytail: 桌面端保留最小宽度, 移动端去掉 */
@media (min-width: 769px) {
  .layout-container {
    min-width: 1024px;
  }
}

.layout-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.layout-main {
  flex: 1;
  padding: 0;
  overflow: hidden;
  background: var(--iot-color-background);
  transition: background var(--transition-fast);
}

.view-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: auto;
}

/* 移动端遮罩 */
.mobile-overlay {
  display: none;
}

@media (max-width: 768px) {
  .mobile-overlay {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    z-index: 2000;
  }
}
</style>
