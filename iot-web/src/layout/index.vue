<script lang="jsx" setup>
import headers from './widget/header.vue'
import slider from './widget/slider'
</script>

<template>
  <div class="layout-container">
    <headers />
    <div class="layout-content">
      <slider />
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
</style>
