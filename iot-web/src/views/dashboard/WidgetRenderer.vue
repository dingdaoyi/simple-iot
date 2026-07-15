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
import { fetchLatestData, fetchMetric } from '@/api/dashboard'

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
let timer = null

const widgetType = computed(() => props.widget?.config?.type || props.widget?.type)
const widgetConfig = computed(() => props.widget?.config || {})
const title = computed(() => props.widget?.title || widgetConfig.value?.title || '')

async function loadData() {
  const cfg = widgetConfig.value
  if (!cfg.deviceKey)
    return
  loading.value = true
  try {
    if (widgetType.value === 'value-card') {
      const res = await fetchLatestData(cfg.deviceKey)
      const items = res?.data || []
      const found = items.find(i => i.key === cfg.identifier)
      if (found) {
        latestValue.value = found.value
        latestTime.value = dayjs().format('HH:mm:ss')
      }
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
</style>
