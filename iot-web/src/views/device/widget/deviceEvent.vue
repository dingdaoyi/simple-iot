<script setup>
import { deviceEventLogsApi } from '@/api/index.js'
import { DateUtils } from '@/utils/date_utils.js'
import { dwHooks } from 'dwyl-ui'
import { computed } from 'vue'

const props = defineProps({
  deviceDetail: {
    type: Object,
    required: true,
  },
})

const { useDwTable } = dwHooks

const tlsEventOpt = computed(() => props.deviceDetail.tslModel?.events || [])

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
        ?.name || row.identifier
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
} = useDwTable({
  defParams: {
    deviceKey: props.deviceDetail.deviceKey,
    beginTime: DateUtils.formatDate(DateUtils.getStartOfDay()),
    endTime: DateUtils.formatDate(DateUtils.getEndOfDay()),
  },
})
</script>

<template>
  <div class="flex flex-col h-full p-4">
    <!-- 搜索区域 -->
    <div class="flex items-center gap-4 mb-6 p-4 bg-gray-50 rounded-lg">
      <div class="flex items-center gap-2 w-300px">
        <span class="text-sm font-medium text-gray-700 w-100px">事件类型:</span>
        <dw-select
          v-model="params.identifier"
          placeholder="请选择事件类型"
          class="w-48"
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
      </div>

      <div class="flex items-center gap-2">
        <span class="text-sm font-medium text-gray-700">时间范围:</span>
        <DwPicker
          v-model:start="params.beginTime"
          v-model:end="params.endTime"
          placeholder="请选择时间范围"
          class="w-80"
        />
      </div>

      <el-button type="primary" @click="onSearch">
        <i class="i-ep-search mr-1" />搜索
      </el-button>
    </div>

    <!-- 表格区域 -->
    <div class="flex-1 bg-white rounded-lg border border-gray-200">
      <DwTable
        ref="dwTable"
        row-key="id"
        :column="column"
        :is-page="false"
        :params="params"
        :api="deviceEventLogsApi"
      />
    </div>
  </div>
</template>
