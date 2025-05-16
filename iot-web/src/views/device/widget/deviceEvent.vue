<script setup>
import { deviceEventLogsApi } from '@/api/index.js'
import { DateUtils } from '@/utils/date_utils.js'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['deviceDetail'])
const { useDwTable } = dwHooks
const tlsEventOpt = ref([])
function tlsEventName() {
  tlsEventOpt.value = props.deviceDetail.tslModel?.events
}
tlsEventName()
const column = [
  {
    prop: 'eventType',
    label: '事件类型',
  },
  {
    prop: 'identifier',
    label: '事件名称',
    formatter(row) {
      return tlsEventOpt.value
        .find(item => item.identifier === row.identifier)
        ?.name
    },
  },
  {
    prop: 'rowValue',
    width: 420,
    label: '原始数据',
  },
  {
    prop: 'time',
    label: '事件时间',
  },
  {
    prop: 'params',
    label: '事件参数',
    formatter(row) {
      return JSON.stringify(row.params)
    },
  },
]
const {
  params,
  onSearch,
  dwTable,
  onDelete,
} = useDwTable({
  defParams: {
    deviceKey: props.deviceDetail.deviceKey,
    beginTime: DateUtils.formatDate(DateUtils.getStartOfDay()),
    endTime: DateUtils.formatDate(DateUtils.getEndOfDay()),
  },
})
</script>

<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <dw-select
        v-model="params.identifier"
        placeholder="请选择事件类型"
        class="w-200px mr-12px"
        filterable
        clearable
      >
        <dw-option
          v-for="item in tlsEventOpt"
          :key="item.identifier"
          :label="item.name"
          :value="item.identifier"
        />
      </dw-select>

      <div class="w-350px mr-12px">
        <DwPicker
          v-model:start="params.beginTime"
          v-model:end="params.endTime"
          placeholder="请输厂家名称"
          class="mr-12px picker"
        />
        <!--        <el-input v-model="params.manufacturer" clearable placeholder="请输厂家名称" /> -->
      </div>
      <el-button type="primary" @click="onSearch">
        搜索
      </el-button>
    </div>
    <DwTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :is-page="false"
      :params="params"
      :api="deviceEventLogsApi"
    >
    </DwTable>
  </div>
</template>

<style scoped lang="scss">

</style>
