<template>
  <div class="flex flex-col gap-10px">
    <el-radio-group v-model="radioGroup" style="margin-bottom: 30px">
      <el-radio-button value="property">属性列表</el-radio-button>
      <el-radio-button value="service">服务列表</el-radio-button>
    </el-radio-group>
    <property v-if="radioGroup==='property'" :type-id="typeId" />
  </div>
</template>
<script setup>
import { ref } from 'vue'
import Property from '@/views/tslModel/widget/property.vue'

const props = defineProps(['typeId'])
const radioGroup = ref('property')
console.log(props.typeId)
</script>

<style scoped lang="scss">

</style>
