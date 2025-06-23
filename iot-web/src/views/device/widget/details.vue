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
const tslPropOpt = ref(null)

const breadcrumbs = [
  { label: '设备管理', path: '/device' },
  { label: '设备详情' },
]

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

function showPro(identifier) {
  tslPropOpt.value = dictTsl(identifier)
  propDialogShow.value = true
}

loadData()
</script>

<template>
  <div class="wh-full flex flex-col gap-5 p-6 bg-gray-50">
    <!-- 面包屑导航 -->
    <div class="bg-white rounded-lg p-4 shadow-sm">
      <Breadcrumb :breadcrumbs="breadcrumbs" />
    </div>

    <!-- 设备基本信息 -->
    <div class="bg-white rounded-lg p-6 shadow-sm">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">设备信息</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <LabelItem label="产品类型" :value="deviceDetail?.productType?.name" />
        <LabelItem label="产品厂家" :value="deviceDetail?.product?.manufacturer" />
        <LabelItem label="产品型号" :value="deviceDetail?.product?.model" />
        <LabelItem label="设备编号" :value="deviceDetail.deviceKey" />
        <LabelItem label="设备名称" :value="deviceDetail.deviceName" />
        <div class="flex items-center gap-3">
          <span class="text-gray-600 font-medium">在线状态</span>
          <component :is="onlineOpts.find(item => item.value === deviceDetail.online)?.render()" />
        </div>
      </div>
    </div>

    <!-- 设备属性指标 -->
    <div v-if="devicePropData" class="bg-white rounded-lg p-6 shadow-sm">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">实时数据</h2>
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <div v-for="item in devicePropData" :key="item.key" class="cursor-pointer hover:bg-gray-50 p-3 rounded-lg transition-colors" @click="showPro(item.key)">
          <PropMetric :value="item.value" :tsl-prop="dictTsl(item.key)" />
        </div>
      </div>
    </div>

    <!-- 设备操作区域 -->
    <div class="bg-white rounded-lg shadow-sm flex-1">
      <el-tabs v-model="activeName" class="h-full">
        <el-tab-pane label="设备事件" name="设备事件">
          <DeviceEvent v-if="deviceDetail.deviceKey" :device-detail="deviceDetail" />
        </el-tab-pane>
        <el-tab-pane label="服务定义" name="服务定义">
          <DeviceService v-if="deviceDetail.deviceKey" :device-detail="deviceDetail" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 属性图表对话框 -->
    <PropChart 
      v-if="propDialogShow" 
      v-model="propDialogShow" 
      :device-key="deviceDetail.deviceKey" 
      :tsl-prop-opt="tslPropOpt" 
    />
  </div>
</template>


