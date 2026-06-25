<script setup>
import { useI18n } from 'vue-i18n'
import { Bell, Check, Close, RefreshRight, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, onMounted, ref } from 'vue'
import {
  alarmAcknowledgeApi,
  alarmClearApi,
  alarmPageApi,
  alarmStatisticsApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useTable } from '@/composables/useTable.js'

const { t } = useI18n()
// 统计数据
const statistics = ref({
  activeCount: 0,
  severityOptions: [],
  statusOptions: [],
})

// 告警详情弹窗
const detailVisible = ref(false)
const currentAlarm = ref(null)

const severityOpt = ref([])
const statusOpt = ref([])

const column = [
  {
    prop: 'severity',
    label: t('auto.alarm_index_08fe5024'),
    width: 100,
    align: 'center',
    slot: 'severity',
  },
  {
    prop: 'alarmName',
    label: t('auto.alarm_index_b4450f15'),
    minWidth: 180,
  },
  {
    prop: 'alarmType',
    label: t('auto.alarm_index_c62e34c5'),
    width: 120,
  },
  {
    prop: 'deviceName',
    label: t('auto.alarm_index_f55f1736'),
    width: 140,
  },
  {
    prop: 'message',
    label: t('auto.alarm_index_ed1cfa58'),
    minWidth: 200,
    slot: 'message',
    showOverflowTooltip: true,
  },
  {
    prop: 'status',
    label: t('auto.alarm_index_3fea7ca7'),
    width: 90,
    align: 'center',
    slot: 'status',
  },
  {
    prop: 'startTs',
    label: t('auto.alarm_index_592c5958'),
    width: 160,
    slot: 'startTs',
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 180,
    label: t('auto.alarm_index_2b6bc0f2'),
  },
]

const {
  params,
  updatePage,
  onSearch,
  onReset,
  tableData,
  total,
  loading,
  onPageChange,
  onSizeChange,
} = useTable({
  fetchApi: alarmPageApi,
  defParams: {},
})

// 加载统计
async function loadStatistics() {
  try {
    const res = await alarmStatisticsApi()
    const data = res.data || res
    statistics.value = data
    severityOpt.value = data.severityOptions || []
    statusOpt.value = data.statusOptions || []
  }
  catch (e) {
    console.error(t('auto.alarm_index_8522b582'), e)
  }
}

async function refreshAlarmView() {
  await updatePage()
  await loadStatistics()
}

// 确认告警
async function handleAcknowledge(row) {
  try {
    await alarmAcknowledgeApi(row.id)
    ElMessage.success(t('auto.alarm_index_082079d7'))
    if (currentAlarm.value?.id === row.id) {
      currentAlarm.value = { ...currentAlarm.value, status: 'ACKNOWLEDGED' }
    }
    await refreshAlarmView()
  }
  catch (e) {
    console.error(t('auto.alarm_index_c92aaa37'), e)
    ElMessage.error(t('auto.alarm_index_c92aaa37'))
  }
}

// 清除告警
async function handleClear(row) {
  try {
    await alarmClearApi(row.id)
    ElMessage.success(t('auto.alarm_index_3ebbc5f7'))
    if (currentAlarm.value?.id === row.id) {
      currentAlarm.value = { ...currentAlarm.value, status: 'CLEARED' }
      detailVisible.value = false
    }
    await refreshAlarmView()
  }
  catch (e) {
    console.error(t('auto.alarm_index_cd6849ea'), e)
    ElMessage.error(t('auto.alarm_index_cd6849ea'))
  }
}

// 在详情弹窗中确认告警
async function handleAcknowledgeInDetail() {
  if (!currentAlarm.value)
    return
  await handleAcknowledge(currentAlarm.value)
}

// 在详情弹窗中清除告警
async function handleClearInDetail() {
  if (!currentAlarm.value)
    return
  await handleClear(currentAlarm.value)
}

// 查看详情
function handleDetail(row) {
  currentAlarm.value = row
  detailVisible.value = true
}

// 获取严重程度标签
function getSeverityTag(severity) {
  const map = {
    CRITICAL: { type: 'danger', text: t('auto.alarm_index_e2e27a87'), color: '#dc2626' },
    MAJOR: { type: 'warning', text: t('auto.alarm_index_00dc03d7'), color: '#ea580c' },
    MINOR: { type: 'info', text: t('auto.alarm_index_bde77082'), color: '#f59e0b' },
    WARNING: { type: '', text: t('auto.alarm_index_900c70fa'), color: '#3b82f6' },
  }
  return map[severity] || { type: '', text: severity, color: '#6b7280' }
}

// 获取状态标签
function getStatusTag(status) {
  const map = {
    ACTIVE: { type: 'danger', text: t('auto.alarm_index_36c6f572') },
    CLEARED: { type: 'success', text: t('auto.alarm_index_59462c81') },
    ACKNOWLEDGED: { type: 'warning', text: t('auto.alarm_index_4113e726') },
  }
  return map[status] || { type: '', text: status }
}

// 获取显示消息（直接显示后端返回的消息）
function getDisplayMessage(row) {
  if (row.message && row.message.trim()) {
    return row.message
  }
  return '-'
}

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr)
    return '-'
  // 处理 ISO 格式时间
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime()))
    return timeStr
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}

// 详情数据格式化
const detailSections = computed(() => {
  if (!currentAlarm.value)
    return []

  const alarm = currentAlarm.value
  const sections = []

  // 基本信息
  sections.push({
    title: t('auto.alarm_index_9e5ffa06'),
    items: [
      { label: t('auto.alarm_index_b4450f15'), value: alarm.alarmName },
      { label: t('auto.alarm_index_c62e34c5'), value: alarm.alarmType },
      { label: t('auto.alarm_index_08fe5024'), value: getSeverityTag(alarm.severity).text, tag: true, tagType: getSeverityTag(alarm.severity).type },
      { label: t('auto.alarm_index_3fea7ca7'), value: getStatusTag(alarm.status).text, tag: true, tagType: getStatusTag(alarm.status).type },
    ],
  })

  // 设备信息
  sections.push({
    title: t('auto.alarm_index_b967fdef'),
    items: [
      { label: t('auto.alarm_index_9f694f60'), value: alarm.deviceName },
      { label: t('auto.alarm_index_89be6dad'), value: alarm.deviceKey },
    ],
  })

  // 时间信息
  sections.push({
    title: t('auto.alarm_index_a209df54'),
    items: [
      { label: t('auto.alarm_index_592c5958'), value: formatTime(alarm.startTs) },
      { label: t('auto.alarm_index_f782779e'), value: formatTime(alarm.endTs) },
      { label: t('auto.alarm_index_6441d8ca'), value: formatTime(alarm.clearTs) },
      { label: t('auto.alarm_index_e1d7b74b'), value: alarm.clearBy || '-' },
    ],
  })

  // 告警消息
  const messageContent = alarm.message && alarm.message.trim() ? alarm.message : '-'
  sections.push({
    title: t('auto.alarm_index_ed1cfa58'),
    items: [
      { label: t('auto.alarm_index_b87b7756'), value: messageContent, full: true },
    ],
  })

  // 触发数据详情
  if (alarm.details && Object.keys(alarm.details).length > 0) {
    const detailItems = Object.entries(alarm.details).map(([key, value]) => ({
      label: key,
      value: typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value),
    }))
    sections.push({
      title: t('auto.alarm_index_f1b82d02'),
      items: detailItems,
    })
  }

  return sections
})

onMounted(() => {
  updatePage()
  loadStatistics()
})</script>

<template>
  <div class="alarm-page">
    <!-- 页面标题 -->
    <PageHeader
      :title="t('auto.alarm_index_12dae261')"
      :subtitle="t('auto.alarm_index_cc629988')"
      :icon="Bell"
    >
      <template #actions>
        <div class="stat-cards">
          <div class="stat-card active">
            <div class="stat-value">
              {{ statistics.activeCount }}
            </div>
            <div class="stat-label">
              {{ t('auto.alarm_index_b150b553') }}
            </div>
          </div>
        </div>
      </template>
    </PageHeader>

    <!-- {{ t('common.search') }}栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('auto.alarm_index_89be6dad')">
            <el-input v-model="params.deviceKey" clearable :placeholder="t('auto.alarm_index_437e02fb')" @keyup.enter="onSearch" />
          </el-form-item>

          <el-form-item :label="t('auto.alarm_index_c62e34c5')">
            <el-input v-model="params.alarmType" clearable :placeholder="t('auto.alarm_index_c0e0f331')" @keyup.enter="onSearch" />
          </el-form-item>

          <el-form-item :label="t('auto.alarm_index_08fe5024')">
            <el-select v-model="params.severity" clearable :placeholder="t('auto.alarm_index_a8b0c204')">
              <el-option
                v-for="item in severityOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item :label="t('auto.alarm_index_3fea7ca7')">
            <el-select v-model="params.status" clearable :placeholder="t('auto.alarm_index_a8b0c204')">
              <el-option
                v-for="item in statusOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('common.search') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('common.reset') }}
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper glass-card">
      <IotTable
        :columns="column"
        :data="tableData"
        :total="total"
        :current-page="params.page"
        :page-size="params.size"
        :loading="loading"
        @page-change="onPageChange"
        @size-change="onSizeChange"
      >
        <template #severity="{ row }">
          <el-tag :type="getSeverityTag(row.severity).type" size="small">
            {{ getSeverityTag(row.severity).text }}
          </el-tag>
        </template>
        <template #status="{ row }">
          <el-tag :type="getStatusTag(row.status).type" size="small">
            {{ getStatusTag(row.status).text }}
          </el-tag>
        </template>
        <template #message="{ row }">
          <span :class="{ 'text-muted': !row.message || !row.message.trim() }">
            {{ getDisplayMessage(row) }}
          </span>
        </template>
        <template #startTs="{ row }">
          {{ formatTime(row.startTs) }}
        </template>
        <template #cz="{ row }">
          <el-button type="primary" link @click="handleDetail(row)">
            <el-icon><View /></el-icon>
            {{ t('auto.alarm_index_f26225bd') }}
          </el-button>
          <el-button
            v-if="row.status === 'ACTIVE'"
            type="warning"
            link
            @click="handleAcknowledge(row)"
          >
            <el-icon><Check /></el-icon>
            {{ t('auto.alarm_index_e83a256e') }}
          </el-button>
          <el-button
            v-if="row.status !== 'CLEARED'"
            type="success"
            link
            @click="handleClear(row)"
          >
            <el-icon><Close /></el-icon>
            {{ t('auto.alarm_index_4403fca0') }}
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 告警详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="t('auto.alarm_index_67837fb1')"
      width="640px"
      class="alarm-detail-dialog"
    >
      <div v-if="currentAlarm" class="detail-content">
        <div
          v-for="(section, idx) in detailSections"
          :key="idx"
          class="detail-section"
        >
          <h4 class="section-title">
            {{ section.title }}
          </h4>
          <div class="section-items">
            <div
              v-for="(item, itemIdx) in section.items"
              :key="itemIdx"
              class="detail-item"
              :class="{ 'full-width': item.full }"
            >
              <span class="item-label">{{ item.label }}</span>
              <span class="item-value">
                <el-tag v-if="item.tag" :type="item.tagType" size="small">
                  {{ item.value }}
                </el-tag>
                <span v-else>{{ item.value }}</span>
              </span>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            v-if="currentAlarm?.status === 'ACTIVE'"
            class="btn-acknowledge"
            @click="handleAcknowledgeInDetail"
          >
            <el-icon><Check /></el-icon>
            {{ t('auto.alarm_index_a7c8e9c3') }}
          </el-button>
          <el-button
            v-if="currentAlarm?.status !== 'CLEARED'"
            class="btn-clear"
            @click="handleClearInDetail"
          >
            <el-icon><Close /></el-icon>
            {{ t('auto.alarm_index_b38faf05') }}
          </el-button>
          <el-button @click="detailVisible = false">
            {{ t('auto.alarm_index_b15d9127') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.alarm-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

.page-header {
  .header-content {
    background: var(--iot-glass-bg);
    backdrop-filter: blur(20px);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-lg);
    padding: var(--space-xl) var(--space-2xl);
    box-shadow: var(--shadow-md);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, #ef4444, #f59e0b);
    }
  }

  .title-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .title-area {
    flex: 1;
  }

  .page-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0;
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    color: var(--iot-color-text-primary);

    .title-icon {
      color: #ef4444;
      font-size: 20px;
    }
  }

  .page-subtitle {
    margin: var(--space-sm) 0 0 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
  }

  .stat-cards {
    display: flex;
    gap: var(--space-md);
  }

  .stat-card {
    background: rgba(255, 255, 255, 0.5);
    border-radius: var(--radius-md);
    padding: var(--space-md) var(--space-xl);
    min-width: 100px;
    text-align: center;

    &.active {
      border-left: 3px solid #ef4444;

      .stat-value {
        color: #ef4444;
      }
    }

    .stat-value {
      font-size: 24px;
      font-weight: 700;
    }

    .stat-label {
      font-size: 12px;
      color: var(--iot-color-text-secondary);
      margin-top: var(--space-xs);
    }
  }
}

.search-bar {
  .search-form {
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
  }

  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-md);

    .el-form-item {
      flex: 1;
      min-width: 200px;
      margin-bottom: 0;
    }
  }

  .form-actions {
    display: flex;
    gap: var(--space-sm);
    justify-content: flex-end;
  }

  .btn-icon {
    margin-right: var(--space-xs);
    font-size: 16px;
  }
}

.table-wrapper {
  flex: 1;
  padding: var(--space-lg);
}

.text-muted {
  color: var(--iot-color-text-muted);
  font-style: italic;
}

// 告警详情弹窗样式
.alarm-detail-dialog {
  .detail-content {
    max-height: 60vh;
    overflow-y: auto;
  }

  .detail-section {
    margin-bottom: var(--space-lg);

    &:last-child {
      margin-bottom: 0;
    }
  }

  .section-title {
    font-size: 14px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    margin: 0 0 var(--space-sm) 0;
    padding-bottom: var(--space-xs);
    border-bottom: 1px solid var(--iot-glass-border);
  }

  .section-items {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-sm);
  }

  .detail-item {
    display: flex;
    align-items: flex-start;
    min-width: 200px;
    flex: 1;

    &.full-width {
      width: 100%;
      flex: none;
    }
  }

  .item-label {
    flex-shrink: 0;
    width: 80px;
    font-size: 13px;
    color: var(--iot-color-text-muted);
  }

  .item-value {
    flex: 1;
    font-size: 13px;
    color: var(--iot-color-text-primary);
    word-break: break-all;
  }

  .dialog-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .el-button {
      display: inline-flex;
      align-items: center;
      gap: var(--space-xs);
    }

    .btn-acknowledge {
      background-color: #fdf6ec;
      border-color: #f5dab1;
      color: #e6a23c;

      &:hover {
        background-color: #ecf5ff;
        border-color: #c6e2ff;
        color: #409eff;
      }
    }

    .btn-clear {
      background-color: #f0f9eb;
      border-color: #c2e7b0;
      color: #67c23a;

      &:hover {
        background-color: #e1f3d8;
        border-color: #a4da89;
        color: #529b2e;
      }
    }
  }
}
</style>