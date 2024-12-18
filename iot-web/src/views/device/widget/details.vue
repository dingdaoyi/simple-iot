<script setup>
import { deviceDataLast, deviceDetailApi } from '@/api/index.js'
import Breadcrumb from '@/components/Breadcrumb.vue'
import { onlineOpts } from '@/utils/base.jsx'
import LabelItem from '@/views/device/widget/LabelItem.vue'
import PropMetric from '@/views/device/widget/PropMetric.vue'
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const activeName = ref('基础信息')
const route = useRoute()
const id = route.query.id
const deviceDetail = ref({})
const devicePropData = ref(null)
async function loadData() {
  const { data } = await deviceDetailApi(id)
  deviceDetail.value = data
  const deviceDataRes = await deviceDataLast(data.deviceKey)
  devicePropData.value = deviceDataRes.data
}
function dictTsl(identifier) {
  const properties = deviceDetail.value.tslModel?.properties
  return properties?.find(item => item.identifier === identifier)
}
const breadcrumbs = ref(
  [
    {
      label: '设备管理',
      path: '/device',
    },
    {
      label: '设备详情',
    },
  ],
)
loadData()
</script>

<template>
  <div class="wh-full flex flex-col gap-20px">
    <Breadcrumb :breadcrumbs="breadcrumbs" />
    <div class="flex gap-20px">
      <LabelItem label="产品类型" :value="deviceDetail?.productType?.name" />
      <LabelItem label="产品厂家" :value="deviceDetail?.product?.manufacturer" />
      <LabelItem label="产品型号" :value="deviceDetail?.product?.model" />
    </div>
    <div class="flex gap-20px">
      <LabelItem label="设备编号" :value="deviceDetail.deviceKey" />
      <LabelItem label="设备名称" :value="deviceDetail.deviceName" />
      <div class="flex gap-20px">
        <div class="text-gray">
          在线状态
        </div>
        <div class="w-300px">
          <component :is="onlineOpts.find(item => item.value === deviceDetail.online)?.render()" />
        </div>
      </div>
    </div>
    <div v-if="devicePropData" class="flex gap-20px justify-center">
      <div v-for="item in devicePropData" :key="item.key" class="w-180px">
        <PropMetric :value="item.value" :tsl-prop="dictTsl(item.key)" />
      </div>
    </div>
    <div class=" flex-1">
      <el-tabs
        v-model="activeName"
      >
        <el-tab-pane label="基础信息" name="base">
          <div>你好</div>
        </el-tab-pane>
        <el-tab-pane label="服务定义" name="event">
          <div>你好</div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
