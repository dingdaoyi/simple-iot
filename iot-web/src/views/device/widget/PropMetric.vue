<script setup>
defineProps(['tslProp', 'value'])
</script>

<template>
  <div class="flex cursor-pointer">
    <el-image style="width: 60px; height: 60px" :src="tslProp.icon" fit="cover" />
    <div class="flex flex-col pt-12px max-w-100px flex-1 gap-2px">
      <div class="color-#292A2C truncate">
        {{ value }}{{ tslProp.unit }}
      </div>
      <div class="color-#6E7279 truncate">
        {{ tslProp.name }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
