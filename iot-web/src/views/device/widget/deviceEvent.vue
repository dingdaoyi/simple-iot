<script setup>
import { computed, ref } from 'vue'
import { deviceEventLogsApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import { DateUtils } from '@/utils/date_utils.js'

const props = defineProps({
  deviceDetail: {
    type: Object,
    required: true,
  },
})

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
} = useTable({
  defParams: {
    deviceKey: props.deviceDetail.deviceKey,
    beginTime: DateUtils.formatDate(DateUtils.getStartOfDay()),
    endTime: DateUtils.formatDate(DateUtils.getEndOfDay()),
  },
})

const dateRange = ref([DateUtils.getStartOfDay(), DateUtils.getEndOfDay()])

function handleDateChange(val) {
  if (val && val.length === 2) {
    params.beginTime = DateUtils.formatDate(val[0])
    params.endTime = DateUtils.formatDate(val[1])
  }
}
</script>

<template>
  <div class="event-container">
    <!-- 搜索区域 -->
    <div class="search-area glass-card">
      <div class="search-item">
        <span class="search-label">事件类型:</span>
        <el-select
          v-model="params.identifier"
          placeholder="请选择事件类型"
          class="search-select"
          filterable
          clearable
        >
          <el-option
            v-for="item in tlsEventOpt"
            :key="item.identifier"
            :label="item.name"
            :value="item.identifier"
          />
        </el-select>
      </div>

      <div class="search-item">
        <span class="search-label">时间范围:</span>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          class="search-date"
          @change="handleDateChange"
        />
      </div>

      <el-button type="primary" @click="onSearch">
        搜索
      </el-button>
    </div>

    <!-- 表格区域 -->
    <div class="table-area glass-card">
      <IotTable
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

<style lang="scss" scoped>
.event-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  height: 100%;
  padding: var(--space-lg);
}

.search-area {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  flex-wrap: wrap;
}

.search-item {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.search-label {
  font-size: 14px;
  font-weight: 500;
  color: var(--iot-color-text-secondary);
  min-width: 70px;
}

.search-select {
  width: 200px;
}

.search-date {
  width: 360px;
}

.table-area {
  flex: 1;
  overflow: hidden;
}
</style>
