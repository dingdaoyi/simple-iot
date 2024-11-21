<template>
  <div class="absolute z-1 wh-full overflow-hidden">
    <div class="absolute -right-300px -top-900px <sm:(-right-100px -top-1170px)">
<!--      <corner-top :start-color="lightColor" :end-color="darkColor" />-->
    </div>
    <div class="absolute -left-200px -bottom-400px <sm:(-left-100px -bottom-760px)">
<!--      <corner-bottom :start-color="darkColor" :end-color="lightColor" />-->
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import CornerTop from './CornerTop'
import CornerBottom from './CornerBottom'

const props = defineProps({
  themeColor: String
})
</script>
<style scoped></style>
