<template>
  <div class="flex flex-col wh-full">
    <!--    <headers />-->
    <div class="flex-1 flex overflow-hidden">
      <slider/>
      <div class="flex-1 m-20px  overflow-hidden" >
        <router-view #default="{ Component, route }">
<!--          <div class="flex flex-col h-full">-->
<!--            <component :is="Component" :key="route.path" />-->
<!--          </div>-->
          <component :is="Component" :key="route.path" classz="flex flex-col h-full"/>
        </router-view>
      </div>
    </div>
  </div>
</template>
<script lang="jsx" setup>
import {ref, watch} from 'vue'
// import headers from './widget/header.vue'
import slider from './widget/slider'

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
