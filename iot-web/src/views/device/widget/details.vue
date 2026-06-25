<script setup>
import { CopyDocument, Hide, Key, Link, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { deviceDataLast, deviceDetailApi } from '@/api/index.js'
import Breadcrumb from '@/components/Breadcrumb.vue'
import { onlineOpts } from '@/utils/base.jsx'
import DeviceEvent from '@/views/device/widget/deviceEvent.vue'
import DeviceService from '@/views/device/widget/deviceService.vue'
import LabelItem from '@/views/device/widget/LabelItem.vue'
import PropChart from '@/views/device/widget/propChart.vue'
import PropMetric from '@/views/device/widget/PropMetric.vue'

const { t } = useI18n()
const activeName = ref('control')
const route = useRoute()
const id = route.query.id
const deviceDetail = ref({})
const propDialogShow = ref(false)
const devicePropData = ref(null)
const tslPropOpt = ref(null)
const secretVisible = ref(false)

const breadcrumbs = computed(() => [
  { label: t('menu.device'), path: '/device' },
  { label: t('device.device_detail') },
])

const currentHost = computed(() => window.location.hostname || 'localhost')
const mqttTcpEndpoint = computed(() => `mqtt://${currentHost.value}:1883`)
const mqttWsEndpoint = computed(() => `ws://${currentHost.value}:8083/mqtt`)
const mqttClientId = computed(() => deviceDetail.value.deviceKey ? `sample_${deviceDetail.value.deviceKey}` : '-')
const mqttUsername = computed(() => deviceDetail.value.deviceKey || '-')
const mqttPassword = computed(() => deviceDetail.value.deviceSecret || '')
const mqttPasswordText = computed(() => {
  if (!mqttPassword.value)
    return '-'
  return secretVisible.value ? mqttPassword.value : '••••••••••••••••••••'
})
const productKey = computed(() => deviceDetail.value.product?.productKey || '-')
const mqttTopics = computed(() => {
  if (!deviceDetail.value.product?.productKey) {
    return [
      { label: t('device.property_report'), value: 'sampleiot/pro/{productKey}' },
      { label: t('device.event_report'), value: 'sampleiot/ev/{productKey}' },
      { label: t('device.service_reply'), value: 'sampleiot/cam_res/{productKey}' },
    ]
  }

  return [
    { label: t('device.property_report'), value: `sampleiot/pro/${deviceDetail.value.product.productKey}` },
    { label: t('device.event_report'), value: `sampleiot/ev/${deviceDetail.value.product.productKey}` },
    { label: t('device.service_reply'), value: `sampleiot/cam_res/${deviceDetail.value.product.productKey}` },
  ]
})

async function loadData() {
  const { data } = await deviceDetailApi(id)
  deviceDetail.value = data

  try {
    const deviceDataRes = await deviceDataLast(data.deviceKey)
    devicePropData.value = deviceDataRes.data
  }
  catch {
    devicePropData.value = []
  }
}

function dictTsl(identifier) {
  const properties = deviceDetail.value.tslModel?.properties
  return properties?.find(item => item.identifier === identifier)
}

function showPro(identifier) {
  tslPropOpt.value = dictTsl(identifier)
  propDialogShow.value = true
}

function toggleSecretVisible() {
  secretVisible.value = !secretVisible.value
}

async function copyValue(value, label) {
  if (!value || value === '-') {
    ElMessage.warning(t('device.no_copy_content', { label }))
    return
  }

  try {
    await navigator.clipboard.writeText(value)
    ElMessage.success(t('device.copy_label_success', { label }))
  }
  catch {
    const textarea = document.createElement('textarea')
    textarea.value = value
    textarea.setAttribute('readonly', '')
    textarea.style.position = 'fixed'
    textarea.style.left = '-9999px'
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    ElMessage.success(t('device.copy_label_success', { label }))
  }
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
      <h2 class="text-lg font-semibold text-gray-800 mb-4">
        {{ t('device.device_info') }}
      </h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <LabelItem :label="t('product.product_type')" :value="deviceDetail?.productType?.name" />
        <LabelItem :label="t('device.product_manufacturer')" :value="deviceDetail?.product?.manufacturer" />
        <LabelItem :label="t('device.product_model')" :value="deviceDetail?.product?.model" />
        <LabelItem label="ProductKey" :value="productKey" />
        <LabelItem :label="t('device.device_key')" :value="deviceDetail.deviceKey" />
        <LabelItem :label="t('device.device_name')" :value="deviceDetail.deviceName" />
        <div class="flex items-center gap-3">
          <span class="text-gray-600 font-medium">{{ t('device.online_status') }}</span>
          <span :style="{ color: onlineOpts.find(item => item.value === deviceDetail.online)?.color }">
            {{ deviceDetail.online ? t('device.online') : t('device.offline') }}
          </span>
        </div>
      </div>
    </div>

    <!-- 接入指引 -->
    <div class="bg-white rounded-lg p-6 shadow-sm">
      <div class="access-header">
        <div>
          <h2 class="text-lg font-semibold text-gray-800">
            {{ t('device.access_guide') }}
          </h2>
          <p class="text-sm text-gray-500 mt-1">
            {{ t('device.access_guide_desc') }}
          </p>
        </div>
        <el-tag type="success" effect="light">
          MQTT
        </el-tag>
      </div>

      <div class="access-grid">
        <div class="access-card">
          <div class="access-title">
            <el-icon><Link /></el-icon>
            {{ t('device.broker_address') }}
          </div>
          <div class="copy-row">
            <code>{{ mqttTcpEndpoint }}</code>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(mqttTcpEndpoint, t('device.mqtt_tcp_address'))">
              {{ t('common.copy') }}
            </el-button>
          </div>
          <div class="copy-row">
            <code>{{ mqttWsEndpoint }}</code>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(mqttWsEndpoint, t('device.mqtt_websocket_address'))">
              {{ t('common.copy') }}
            </el-button>
          </div>
        </div>

        <div class="access-card">
          <div class="access-title">
            <el-icon><Key /></el-icon>
            {{ t('device.auth_params') }}
          </div>
          <div class="copy-row">
            <span>Client ID</span>
            <code>{{ mqttClientId }}</code>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(mqttClientId, 'Client ID')">
              {{ t('common.copy') }}
            </el-button>
          </div>
          <div class="copy-row">
            <span>Username</span>
            <code>{{ mqttUsername }}</code>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(mqttUsername, 'Username')">
              {{ t('common.copy') }}
            </el-button>
          </div>
          <div class="copy-row">
            <span>Password</span>
            <code>{{ mqttPasswordText }}</code>
            <el-button link :icon="secretVisible ? Hide : View" @click="toggleSecretVisible">
              {{ secretVisible ? t('common.hide') : t('common.show') }}
            </el-button>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(mqttPassword, t('device.device_secret'))">
              {{ t('common.copy') }}
            </el-button>
          </div>
        </div>
      </div>

      <div class="topic-section">
        <div class="topic-title">
          {{ t('device.topic_template') }}
        </div>
        <div class="topic-list">
          <div v-for="topic in mqttTopics" :key="topic.label" class="copy-row topic-row">
            <span>{{ topic.label }}</span>
            <code>{{ topic.value }}</code>
            <el-button link type="primary" :icon="CopyDocument" @click="copyValue(topic.value, topic.label)">
              {{ t('common.copy') }}
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 设备属性指标 -->
    <div v-if="devicePropData" class="bg-white rounded-lg p-6 shadow-sm">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">
        {{ t('device.realtime_data') }}
      </h2>
      <div v-if="devicePropData.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-4">
        <div v-for="item in devicePropData" :key="item.key" class="cursor-pointer hover:bg-gray-50 p-3 rounded-lg transition-colors" @click="showPro(item.key)">
          <PropMetric :value="item.value" :tsl-prop="dictTsl(item.key)" />
        </div>
      </div>
      <el-empty v-else :description="t('device.no_realtime_data')" />
    </div>

    <!-- 设备操作区域 -->
    <div class="bg-white rounded-lg shadow-sm flex-1">
      <el-tabs v-model="activeName" class="h-full">
        <el-tab-pane :label="t('device.device_event')" name="events">
          <DeviceEvent v-if="deviceDetail.deviceKey" :device-detail="deviceDetail" />
        </el-tab-pane>
        <el-tab-pane :label="t('device.device_control')" name="control">
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

<style scoped lang="scss">
.access-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.access-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.access-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
}

.access-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  color: #1f2937;
}

.copy-row {
  display: flex;
  align-items: center;
  gap: 10px;
  min-height: 34px;
  color: #4b5563;

  + .copy-row {
    margin-top: 8px;
  }

  span {
    min-width: 88px;
    font-size: 13px;
    color: #6b7280;
  }

  code {
    flex: 1;
    min-width: 0;
    padding: 6px 10px;
    border-radius: 8px;
    background: #eef2ff;
    color: #3730a3;
    font-size: 13px;
    word-break: break-all;
  }
}

.topic-section {
  margin-top: 16px;
  border: 1px dashed #c7d2fe;
  border-radius: 12px;
  padding: 16px;
  background: #f8fafc;
}

.topic-title {
  margin-bottom: 10px;
  font-weight: 600;
  color: #1f2937;
}

.topic-row span {
  min-width: 72px;
}

@media (max-width: 960px) {
  .access-grid {
    grid-template-columns: 1fr;
  }

  .copy-row {
    align-items: flex-start;
    flex-direction: column;

    span {
      min-width: auto;
    }

    code {
      width: 100%;
    }
  }
}
</style>
