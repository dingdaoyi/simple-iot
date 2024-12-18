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
const option = ref({
  xAxis: {
    type: 'category',
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  },
  yAxis: {
    type: 'value',
  },
  series: [
    {
      data: [10, 11, 12, 13, 14, 15, 16],
      type: 'line',
      smooth: true,
    },
  ],
})
function loadChartDate() {
  deviceDataMetric(params.value)
    .then(({ data }) => {
      option.value.xAxis.data = data.map(item => item.key)
      option.value.series[0].data = data.map(item => item.value || 0)
      console.log(option.value, 'freferfre')
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
            v-model:start="params.startTime"
            v-model:end="params.endTime"
            class="mr-12px picker"
          />
          <el-button type="info" @click="loadChartDate">
            搜索
          </el-button>
        </div>
      </div>
      <div class="ml-40px w-1000px h-400px">
        <VCharts v-if="renderChart" style="height: 900px;width: 400px" :options="option" />
      </div>
    </div>
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
