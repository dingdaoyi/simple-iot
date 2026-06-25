<script setup>
import { RefreshRight } from '@element-plus/icons-vue'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
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

const { t } = useI18n()
const tlsEventOpt = computed(() => props.deviceDetail.tslModel?.events || [])

const column = computed(() => [
  {
    prop: 'eventType',
    label: t('device.event_type'),
  },
  {
    prop: 'identifier',
    label: t('device.event_name'),
    formatter(row) {
      return tlsEventOpt.value
        .find(item => item.identifier === row.identifier)
        ?.name || row.identifier
    },
  },
  {
    prop: 'rowValue',
    width: 420,
    label: t('device.raw_data'),
  },
  {
    prop: 'time',
    label: t('device.event_time'),
  },
  {
    prop: 'params',
    label: t('device.event_params'),
    formatter(row) {
      return JSON.stringify(row.params)
    },
  },
])

const {
  params,
  onSearch,
  onReset,
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

function resetFilters() {
  dateRange.value = [DateUtils.getStartOfDay(), DateUtils.getEndOfDay()]
  onReset()
}
</script>

<template>
  <div class="event-container">
    <!-- 搜索区域 -->
    <div class="search-area glass-card">
      <div class="search-item">
        <span class="search-label">{{ t('device.event_type') }}:</span>
        <el-select
          v-model="params.identifier"
          :placeholder="t('common.placeholder_select', { field: t('device.event_type') })"
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
        <span class="search-label">{{ t('device.time_range') }}:</span>
        <el-date-picker
          v-model="dateRange"
          type="datetimerange"
          :range-separator="t('common.to')"
          :start-placeholder="t('device.start_time')"
          :end-placeholder="t('device.end_time')"
          class="search-date"
          @change="handleDateChange"
        />
      </div>

      <el-button type="primary" @click="onSearch">
        {{ t('common.search') }}
      </el-button>
      <el-button :icon="RefreshRight" @click="resetFilters">
        {{ t('common.reset') }}
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