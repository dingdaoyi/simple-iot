<script setup>
import {
  ArrowLeft,
  Check,
  Delete,
  Setting,
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
/**
 * DashboardEditor - 拖拽式仪表盘编辑器
 * 使用 vue-grid-layout 实现拖拽缩放, 右侧面板配置 widget 属性
 */
import { computed, onMounted, ref, watch } from 'vue'
import { GridItem, GridLayout } from 'vue-grid-layout'
import { useRoute, useRouter } from 'vue-router'
import { dashboardDelete, dashboardGet, dashboardSave, dashboardUpdate } from '@/api/dashboard'
import { deviceListApi, productListApi } from '@/api/index'
import request from '@/utils/request'
import WidgetRenderer from './WidgetRenderer.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const saving = ref(false)
const dashboard = ref({ id: null, name: '', description: '', layout: [] })
const selectedWidgetIdx = ref(null)

// ponytail: 12列网格, 行高按容器自适应
const colNum = 12
const rowHeight = 60

const widgetTypes = [
  { type: 'line-chart', label: '折线图', icon: '📈' },
  { type: 'gauge', label: '仪表盘', icon: '⏱' },
  { type: 'value-card', label: '数值卡', icon: '🔢' },
  { type: 'data-table', label: '数据表', icon: '📋' },
  { type: 'bar-chart', label: '柱状图', icon: '📊' },
]

const selectedWidget = computed(() => {
  if (selectedWidgetIdx.value === null)
    return null
  return dashboard.value.layout[selectedWidgetIdx.value] || null
})

// 级联选择数据
const products = ref([])
const devices = ref([])
const properties = ref([])

async function loadProducts() {
  const res = await productListApi({})
  products.value = res.data || []
}

async function loadDevices(productId) {
  if (!productId) {
    devices.value = []
    return
  }
  const res = await deviceListApi({ productId })
  devices.value = res.data || []
}

async function loadProperties(productId) {
  if (!productId) {
    properties.value = []
    return
  }
  const product = products.value.find(p => p.id === productId)
  if (!product)
    return
  const res = await request({
    url: '/model/property/product/type',
    method: 'get',
    params: { productTypeId: product.productTypeId, productId, all: true },
  })
  properties.value = res.data || []
}

function onProductChange(productId) {
  selectedWidget.value.config.productId = productId
  selectedWidget.value.config.deviceKey = ''
  selectedWidget.value.config.identifier = ''
  loadDevices(productId)
  loadProperties(productId)
}

function onDeviceChange(deviceKey) {
  selectedWidget.value.config.deviceKey = deviceKey
}

function onPropertyChange(identifier) {
  selectedWidget.value.config.identifier = identifier
}

watch(selectedWidgetIdx, async (idx) => {
  if (idx === null)
    return
  const cfg = dashboard.value.layout[idx]?.config
  if (!cfg)
    return
  if (cfg.productId) {
    await Promise.all([loadDevices(cfg.productId), loadProperties(cfg.productId)])
  }
})

function addWidget(type) {
  const idx = dashboard.value.layout.length
  dashboard.value.layout.push({
    i: `w-${Date.now()}`,
    x: (idx * 2) % colNum,
    y: Infinity, // 放到底部
    w: type === 'value-card' ? 3 : 6,
    h: type === 'value-card' ? 2 : 4,
    title: widgetTypes.find(w => w.type === type)?.label || type,
    config: {
      type,
      productId: null,
      deviceKey: '',
      identifier: '',
      timeRange: 1,
      unit: '',
      min: 0,
      max: 100,
    },
  })
  selectedWidgetIdx.value = idx
}

function removeWidget(idx) {
  dashboard.value.layout.splice(idx, 1)
  if (selectedWidgetIdx.value === idx)
    selectedWidgetIdx.value = null
  else if (selectedWidgetIdx.value !== null && selectedWidgetIdx.value > idx)
    selectedWidgetIdx.value--
}

function onLayoutUpdated(newLayout) {
  dashboard.value.layout = newLayout
}

async function loadDashboard() {
  const id = route.params.id
  if (!id)
    return
  loading.value = true
  try {
    const res = await dashboardGet(id)
    if (res?.data) {
      dashboard.value = res.data
      if (!dashboard.value.layout)
        dashboard.value.layout = []
    }
  }
  finally {
    loading.value = false
  }
}

async function onSave() {
  if (!dashboard.value.name?.trim()) {
    ElMessage.warning('请输入仪表盘名称')
    return
  }
  saving.value = true
  try {
    const fn = dashboard.value.id ? dashboardUpdate : dashboardSave
    await fn(dashboard.value)
    ElMessage.success('保存成功')
    if (!dashboard.value.id) {
      // 新建后返回列表
      router.push('/dashboard')
    }
  }
  catch {
    ElMessage.error('保存失败')
  }
  finally {
    saving.value = false
  }
}

async function onDelete() {
  if (!dashboard.value.id)
    return
  await ElMessageBox.confirm('确认删除该仪表盘?', '提示', { type: 'warning' })
  await dashboardDelete(dashboard.value.id)
  ElMessage.success('已删除')
  router.push('/dashboard')
}

onMounted(() => {
  loadDashboard()
  loadProducts()
})
</script>

<template>
  <div v-loading="loading" class="dashboard-editor">
    <!-- 顶部工具栏 -->
    <div class="editor-toolbar glass-card">
      <div class="toolbar-left">
        <el-button :icon="ArrowLeft" text @click="$router.push('/dashboard')" />
        <el-input
          v-model="dashboard.name"
          placeholder="仪表盘名称"
          style="width: 200px"
          size="small"
        />
      </div>
      <div class="toolbar-right">
        <el-button link type="danger" :icon="Delete" size="small" :disabled="!dashboard.id" @click="onDelete">
          删除
        </el-button>
        <el-button link type="primary" :icon="Check" size="small" :loading="saving" @click="onSave">
          保存
        </el-button>
      </div>
    </div>

    <div class="editor-body">
      <!-- 左侧: 组件库 -->
      <div class="widget-palette glass-card">
        <div class="palette-title">
          组件库
        </div>
        <div
          v-for="wt in widgetTypes"
          :key="wt.type"
          class="palette-item"
          @click="addWidget(wt.type)"
        >
          <span class="palette-icon">{{ wt.icon }}</span>
          <span>{{ wt.label }}</span>
        </div>
      </div>

      <!-- 中间: 画布 -->
      <div class="canvas-area glass-card">
        <GridLayout
          :layout="dashboard.layout"
          :col-num="colNum"
          :row-height="rowHeight"
          :is-draggable="true"
          :is-resizable="true"
          :vertical-compact="true"
          :margin="[8, 8]"
          @layout-updated="onLayoutUpdated"
        >
          <GridItem
            v-for="(item, idx) in dashboard.layout"
            :key="item.i"
            :x="item.x"
            :y="item.y"
            :w="item.w"
            :h="item.h"
            :i="item.i"
            @click="selectedWidgetIdx = idx"
          >
            <div
              class="widget-card"
              :class="{ selected: selectedWidgetIdx === idx }"
            >
              <div class="widget-header">
                <span class="widget-title">{{ item.title }}</span>
                <el-button
                  :icon="Delete"
                  text
                  size="small"
                  class="widget-delete"
                  @click.stop="removeWidget(idx)"
                />
              </div>
              <div class="widget-body">
                <WidgetRenderer :widget="item" />
              </div>
            </div>
          </GridItem>
        </GridLayout>
        <div v-if="dashboard.layout.length === 0" class="empty-canvas">
          <el-empty description="从左侧拖入组件开始构建仪表盘" />
        </div>
      </div>

      <!-- 右侧: 属性面板 -->
      <div class="config-panel glass-card">
        <div class="panel-title">
          <el-icon><Setting /></el-icon>
          组件配置
        </div>
        <template v-if="selectedWidget">
          <el-form label-width="80px" size="small">
            <el-form-item label="标题">
              <el-input v-model="selectedWidget.title" />
            </el-form-item>
            <el-form-item label="产品">
              <el-select
                :model-value="selectedWidget.config.productId"
                placeholder="选择产品"
                filterable
                style="width: 100%"
                @change="onProductChange"
              >
                <el-option
                  v-for="p in products"
                  :key="p.id"
                  :label="p.model"
                  :value="p.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="设备">
              <el-select
                :model-value="selectedWidget.config.deviceKey"
                placeholder="选择设备"
                filterable
                style="width: 100%"
                :disabled="!selectedWidget.config.productId"
                @change="onDeviceChange"
              >
                <el-option
                  v-for="d in devices"
                  :key="d.deviceKey"
                  :label="`${d.deviceName} (${d.deviceKey})`"
                  :value="d.deviceKey"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="属性">
              <el-select
                :model-value="selectedWidget.config.identifier"
                placeholder="选择属性"
                filterable
                style="width: 100%"
                :disabled="!selectedWidget.config.productId"
                @change="onPropertyChange"
              >
                <el-option
                  v-for="p in properties"
                  :key="p.identifier"
                  :label="`${p.name} (${p.identifier})`"
                  :value="p.identifier"
                />
              </el-select>
            </el-form-item>
            <el-form-item v-if="['line-chart', 'bar-chart', 'data-table'].includes(selectedWidget.config.type)" label="时间范围">
              <el-select v-model="selectedWidget.config.timeRange" style="width: 100%">
                <el-option :value="1" label="最近1小时" />
                <el-option :value="6" label="最近6小时" />
                <el-option :value="24" label="最近24小时" />
                <el-option :value="168" label="最近7天" />
              </el-select>
            </el-form-item>
            <el-form-item v-if="selectedWidget.config.type === 'value-card'" label="单位">
              <el-input v-model="selectedWidget.config.unit" placeholder="°C, %, V..." />
            </el-form-item>
            <el-form-item v-if="selectedWidget.config.type === 'gauge'" label="最小值">
              <el-input-number v-model="selectedWidget.config.min" :controls="false" style="width: 100%" />
            </el-form-item>
            <el-form-item v-if="selectedWidget.config.type === 'gauge'" label="最大值">
              <el-input-number v-model="selectedWidget.config.max" :controls="false" style="width: 100%" />
            </el-form-item>
          </el-form>
        </template>
        <div v-else class="panel-empty">
          <el-empty description="选择一个组件进行配置" :image-size="60" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-editor {
  display: flex;
  flex-direction: column;
  height: 100%;
  gap: 8px;
  padding: 8px;
}

.editor-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;

  .toolbar-left,
  .toolbar-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.editor-body {
  display: flex;
  gap: 8px;
  flex: 1;
  min-height: 0;
}

.widget-palette {
  width: 140px;
  flex-shrink: 0;
  padding: 8px;

  .palette-title {
    font-size: 12px;
    color: var(--iot-color-text-muted);
    margin-bottom: 8px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .palette-item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 10px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all var(--transition-fast);
    font-size: 13px;
    margin-bottom: 4px;

    &:hover {
      background: var(--iot-glass-bg-hover);
    }

    .palette-icon {
      font-size: 16px;
    }
  }
}

.canvas-area {
  flex: 1;
  min-width: 0;
  overflow: auto;
  position: relative;
  padding: 4px;
}

.empty-canvas {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.config-panel {
  width: 260px;
  flex-shrink: 0;
  padding: 12px;
  overflow-y: auto;

  .panel-title {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    color: var(--iot-color-text-muted);
    margin-bottom: 12px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
  }

  .panel-empty {
    display: flex;
    justify-content: center;
    padding-top: 40px;
  }
}

.widget-card {
  height: 100%;
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  background: var(--iot-glass-bg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  cursor: move;

  &.selected {
    border-color: var(--iot-color-primary);
    box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.2);
  }

  .widget-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 8px;
    font-size: 12px;
    font-weight: 600;
    border-bottom: 1px solid var(--iot-glass-border);
    flex-shrink: 0;

    .widget-delete {
      opacity: 0;
      transition: opacity var(--transition-fast);
    }
  }

  &:hover .widget-delete {
    opacity: 1;
  }

  .widget-body {
    flex: 1;
    min-height: 0;
    padding: 4px;
  }
}
</style>
