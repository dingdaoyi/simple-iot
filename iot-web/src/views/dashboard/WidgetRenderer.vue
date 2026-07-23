<script setup>
import dayjs from 'dayjs'
import { BarChart, GaugeChart, LineChart } from 'echarts/charts'
import {
  DataZoomComponent,
  GridComponent,
  LegendComponent,
  TitleComponent,
  TooltipComponent,
} from 'echarts/components'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
/**
 * WidgetRenderer - 单个仪表盘组件渲染器
 * 支持 4 种类型: line-chart, gauge, value-card, data-table
 * 数据源: 复用现有 /device/data/property/metric + /device/data/property/last
 */
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import VChart from 'vue-echarts'
import { fetchAlarmPage, fetchLatestData, fetchMetric, fetchDeviceList } from '@/api/dashboard'

const props = defineProps({
  widget: { type: Object, required: true },
})

use([
  CanvasRenderer,
  LineChart,
  GaugeChart,
  BarChart,
  GridComponent,
  TooltipComponent,
  LegendComponent,
  TitleComponent,
  DataZoomComponent,
])

const loading = ref(false)
const chartData = ref([])
const latestValue = ref(null)
const latestTime = ref(null)
const gridDevices = ref([])
const alarmList = ref([])
let timer = null

const widgetType = computed(() => props.widget?.config?.type || props.widget?.type)
const widgetConfig = computed(() => props.widget?.config || {})
const title = computed(() => props.widget?.title || widgetConfig.value?.title || '')

async function loadData() {
  const cfg = widgetConfig.value
  const type = widgetType.value
  loading.value = true
  try {
    if (type === 'value-card') {
      const res = await fetchLatestData(cfg.deviceKey)
      const items = res?.data || []
      const found = items.find(i => i.key === cfg.identifier)
      if (found) {
        latestValue.value = found.value
        latestTime.value = dayjs().format('HH:mm:ss')
      }
    }
    else if (type === 'device-grid') {
      const res = await fetchDeviceList({ productId: cfg.productId, page: 1, size: 200 })
      gridDevices.value = res?.data?.records || res?.data || []
    }
    else if (type === 'alarm-list') {
      const res = await fetchAlarmPage({ page: 1, size: 10, severity: cfg.severity })
      alarmList.value = res?.data?.records || res?.data || []
    }
    else {
      const now = dayjs()
      const res = await fetchMetric({
        deviceKey: cfg.deviceKey,
        identifier: cfg.identifier,
        beginTime: now.subtract(cfg.timeRange || 1, 'hour').format('YYYY-MM-DD HH:mm:ss'),
        endTime: now.format('YYYY-MM-DD HH:mm:ss'),
      })
      chartData.value = res?.data || []
    }
  }
  catch {
    // 静默,仪表盘组件不应弹 toast
  }
  finally {
    loading.value = false
  }
}

const chartOption = computed(() => {
  const cfg = widgetConfig.value
  const type = widgetType.value

  if (type === 'line-chart') {
    return {
      title: { text: title.value, left: 'center', textStyle: { fontSize: 13 } },
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 20, top: 40, bottom: 30 },
      xAxis: {
        type: 'category',
        data: chartData.value.map(d => dayjs(d.time).format('HH:mm')),
        axisLabel: { fontSize: 10 },
      },
      yAxis: { type: 'value', scale: true, axisLabel: { fontSize: 10 } },
      series: [{
        name: cfg.identifier || 'value',
        type: 'line',
        smooth: true,
        showSymbol: false,
        data: chartData.value.map(d => d.value),
        lineStyle: { width: 2 },
        areaStyle: { opacity: 0.1 },
      }],
    }
  }

  if (type === 'gauge') {
    const val = latestValue.value ?? 0
    return {
      series: [{
        type: 'gauge',
        min: cfg.min ?? 0,
        max: cfg.max ?? 100,
        progress: { show: true, width: 12 },
        axisLine: { lineStyle: { width: 12 } },
        detail: { valueAnimation: true, formatter: '{value}', fontSize: 24, offsetCenter: [0, '70%'] },
        title: { offsetCenter: [0, '90%'], fontSize: 12 },
        data: [{ value: val, name: cfg.identifier || title.value }],
      }],
    }
  }

  if (type === 'bar-chart') {
    return {
      title: { text: title.value, left: 'center', textStyle: { fontSize: 13 } },
      tooltip: { trigger: 'axis' },
      grid: { left: 50, right: 20, top: 40, bottom: 30 },
      xAxis: {
        type: 'category',
        data: chartData.value.map(d => dayjs(d.time).format('HH:mm')),
        axisLabel: { fontSize: 10 },
      },
      yAxis: { type: 'value', scale: true, axisLabel: { fontSize: 10 } },
      series: [{
        name: cfg.identifier || 'value',
        type: 'bar',
        data: chartData.value.map(d => d.value),
        itemStyle: { borderRadius: [3, 3, 0, 0] },
      }],
    }
  }

  return {}
})

onMounted(() => {
  loadData()
  // ponytail: 30s 轮询, 实时推送后续可换 WebSocket
  timer = setInterval(loadData, 30000)
})

onUnmounted(() => {
  if (timer)
    clearInterval(timer)
})

watch(() => props.widget, loadData, { deep: true })
</script>

<template>
  <div v-loading="loading" class="widget-renderer">
    <!-- 数值卡 -->
    <div v-if="widgetType === 'value-card'" class="value-card">
      <div class="vc-label">
        {{ title }}
      </div>
      <div class="vc-value">
        {{ latestValue ?? '--' }}
        <span v-if="widgetConfig.unit" class="vc-unit">{{ widgetConfig.unit }}</span>
      </div>
      <div class="vc-time">
        {{ latestTime }}
      </div>
    </div>

    <!-- 仪表盘 -->
    <VChart
      v-else-if="widgetType === 'gauge'"
      :option="chartOption"
      autoresize
      style="height: 100%"
    />

    <!-- 折线图/柱状图 -->
    <VChart
      v-else-if="widgetType === 'line-chart' || widgetType === 'bar-chart'"
      :option="chartOption"
      autoresize
      style="height: 100%"
    />

    <!-- 数据表格 -->
    <div v-else-if="widgetType === 'data-table'" class="data-table-widget">
      <div class="dtw-title">
        {{ title }}
      </div>
      <el-table :data="chartData" size="small" max-height="200" style="width: 100%">
        <el-table-column prop="time" label="时间" width="120">
          <template #default="{ row }">
            {{ row.time ? dayjs(row.time).format('HH:mm:ss') : '' }}
          </template>
        </el-table-column>
        <el-table-column prop="value" label="值" />
      </el-table>
    </div>

    <!-- 未知类型 -->
    <div v-else-if="widgetType === 'device-grid'" class="device-grid-widget">
      <div class="dgw-title">{{ title }}</div>
      <div class="dgw-grid">
        <div v-for="d in gridDevices" :key="d.id" class="dgw-cell" :class="{ online: d.online, offline: !d.online }">
          <span class="dgw-dot" />
          <span class="dgw-name">{{ d.deviceName }}</span>
        </div>
      </div>
      <div v-if="gridDevices.length === 0" class="dgw-empty">暂无设备</div>
    </div>

    <div v-else-if="widgetType === 'alarm-list'" class="alarm-list-widget">
      <div class="alw-title">{{ title }}</div>
      <el-table :data="alarmList" size="small" max-height="200" style="width: 100%">
        <el-table-column prop="alarmType" label="类型" width="100" />
        <el-table-column prop="severity" label="级别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.severity === 'CRITICAL' ? 'danger' : row.severity === 'MAJOR' ? 'warning' : 'info'" size="small">{{ row.severity }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceName" label="设备" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="140">
          <template #default="{ row }">
            {{ row.createTime ? dayjs(row.createTime).format('MM-DD HH:mm') : '' }}
          </template>
        </el-table-column>
      </el-table>
      <div v-if="alarmList.length === 0" class="alw-empty">暂无告警</div>
    </div>

    <div v-else class="widget-unknown">
      <el-empty :description="`未知组件: ${widgetType}`" :image-size="40" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.widget-renderer {
  height: 100%;
  overflow: hidden;
}

.value-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100%;
  padding: 8px;

  .vc-label {
    font-size: 13px;
    color: var(--iot-color-text-secondary);
    margin-bottom: 8px;
  }

  .vc-value {
    font-size: 32px;
    font-weight: 700;
    color: var(--iot-color-primary);
    line-height: 1;

    .vc-unit {
      font-size: 14px;
      font-weight: 400;
      margin-left: 4px;
      color: var(--iot-color-text-muted);
    }
  }

  .vc-time {
    font-size: 11px;
    color: var(--iot-color-text-muted);
    margin-top: 8px;
  }
}

.data-table-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 4px;

  .dtw-title {
    font-size: 13px;
    font-weight: 600;
    margin-bottom: 4px;
    color: var(--iot-color-text-primary);
  }
}

.widget-unknown {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.device-grid-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 4px;
  overflow: hidden;

  .dgw-title { font-size: 13px; font-weight: 600; margin-bottom: 4px; }
  .dgw-grid { display: flex; flex-wrap: wrap; gap: 4px; overflow-y: auto; }
  .dgw-cell { display: flex; align-items: center; gap: 4px; padding: 2px 6px; border-radius: 4px; font-size: 11px; background: #f0f0f0; }
  .dgw-dot { width: 8px; height: 8px; border-radius: 50%; }
  .dgw-cell.online .dgw-dot { background: #22c55e; }
  .dgw-cell.offline .dgw-dot { background: #ef4444; }
  .dgw-empty { text-align: center; color: #999; padding: 20px; font-size: 12px; }
}

.alarm-list-widget {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 4px;
  overflow: hidden;

  .alw-title { font-size: 13px; font-weight: 600; margin-bottom: 4px; }
  .alw-empty { text-align: center; color: #999; padding: 20px; font-size: 12px; }
}
</style>
