<script setup>
import { deviceDataLast, deviceDetailApi } from '@/api/index.js'
import Breadcrumb from '@/components/Breadcrumb.vue'
import { onlineOpts } from '@/utils/base.jsx'
import DeviceEvent from '@/views/device/widget/deviceEvent.vue'
import DeviceService from '@/views/device/widget/deviceService.vue'
import LabelItem from '@/views/device/widget/LabelItem.vue'
import PropChart from '@/views/device/widget/propChart.vue'
import PropMetric from '@/views/device/widget/PropMetric.vue'
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const activeName = ref('设备事件')
const route = useRoute()
const id = route.query.id
const deviceDetail = ref({})
const propDialogShow = ref(false)

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

const tslPropOpt = ref(null)
function showPro(identifier) {
  tslPropOpt.value = dictTsl(identifier)
  propDialogShow.value = true
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
        <PropMetric :value="item.value" :tsl-prop="dictTsl(item.key)" @click="showPro(item.key)" />
      </div>
    </div>
    <div class=" flex-1">
      <el-tabs
        v-model="activeName"
      >
        <el-tab-pane label="设备事件" name="设备事件">
          <DeviceEvent
            v-if="deviceDetail.deviceKey"
            :device-detail="deviceDetail"
          />
        </el-tab-pane>
        <el-tab-pane label="服务定义" name="服务定义">
          <DeviceService
            v-if="deviceDetail.deviceKey"
            :device-detail="deviceDetail"
          />
        </el-tab-pane>
      </el-tabs>
    </div>
    <PropChart v-if="propDialogShow" v-model="propDialogShow" :device-key="deviceDetail.deviceKey" :tsl-prop-opt="tslPropOpt" />
  </div>
</template>

<style scoped lang="scss">

</style>
