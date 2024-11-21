<template>
    <router-view />
</template>

<script setup>
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
