<script lang="jsx" setup>
import { deviceDataMetric } from '@/api/index.js'
import { DateUtils } from '@/utils/date_utils.js'
import { nextTick, ref } from 'vue'
import VCharts from 'vue-echarts'
import 'echarts/lib/chart/line'
import 'echarts/lib/component/polar'

const props = defineProps(['deviceKey', 'tslPropOpt'])
const params = ref({
  identifier: props.tslPropOpt.identifier,
  deviceKey: props.deviceKey,
  beginTime: DateUtils.formatDate(DateUtils.getStartOfDay()),
  endTime: DateUtils.formatDate(DateUtils.getEndOfDay()),
})
const options = ref({
  tooltip: {
    trigger: 'axis',
  },
  grid: {
    top: '25%',
    bottom: '15%',
    left: '8%',
    right: '8%',
  },
  xAxis: [{
    type: 'category',
    name: '日期',
    axisTick: {
      alignWithLabel: true,
    },
    axisLine: {
      lineStyle: {
        color: 'rgb(186,231,255)', // 设置x轴轴线颜色
      },
    },
    axisLabel: {
      color: '##76889F', // 设置x轴标签文字颜色
    },
    data: ['2024-01-01', '2024-02-01', '2024-03-01', '2024-04-01', '2024-05-01', '2024-06-01'],
  }],
  yAxis: [{
    type: 'value',
    name: '指标',
    nameTextStyle: {
      color: '#76889F',
    },
    position: 'left',
    axisLine: {
      show: true,
      lineStyle: {
        color: 'rgb(186,231,255)', // 设置x轴轴线颜色
      },
    },
    splitLine: {
      show: true,
      lineStyle: {
        type: 'dashed',
        color: 'rgba(255, 255, 255, 0.2)',
      },
    },
    axisLabel: {
      color: '#76889F', // 设置右y轴标签文字颜色
      // formatter: '{value} %'
    },
  }],
  series: [{
    name: '同比',
    type: 'line',
    smooth: true,
    showSymbol: false,
    lineStyle: {
      normal: {
        width: 2,
        color: '#FE9022',
      },
    },
    data: [40, 50, 45, 48, 38, 42],
  }],
})
function loadChartDate() {
  deviceDataMetric(params.value)
    .then(({ data }) => {
      options.value.xAxis[0].data = data.map(item => item.key)
      options.value.series[0].data = data.map(item => item.value || 0)
    })
}
const renderChart = ref(false)
// wait container expand
nextTick(() => {
  renderChart.value = true
})
loadChartDate()
</script>

<template>
  <dw-dialog
    title="参数列表"
    width="1200px"
  >
    <div class="flex flex-col gap-20px">
      <div class="flex flex-row">
        <div class="flex-1 color-#292A2C ml-40px">
          {{ tslPropOpt.name }}
        </div>
        <div>
          <DwPicker
            v-model:start="params.beginTime"
            v-model:end="params.endTime"
            class="mr-12px picker"
          />
          <el-button type="info" @click="loadChartDate">
            搜索
          </el-button>
        </div>
      </div>
      <div class="w-full h-400px">
        <VCharts v-if="renderChart" :option="options" />
      </div>
    </div>
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
