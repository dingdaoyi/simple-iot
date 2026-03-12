<script setup>
import { Bell, Check, Close, View } from '@element-plus/icons-vue'
import { onMounted, ref, computed } from 'vue'
import {
  alarmAcknowledgeApi,
  alarmClearApi,
  alarmPageApi,
  alarmStatisticsApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'

// 统计数据
const statistics = ref({
  activeCount: 0,
  severityOptions: [],
  statusOptions: [],
})

// 告警详情弹窗
const detailVisible = ref(false)
const currentAlarm = ref(null)

// 告警严重程度颜色映射
const severityColors = {
  CRITICAL: '#dc2626',
  MAJOR: '#ea580c',
  MINOR: '#f59e0b',
  WARNING: '#3b82f6',
}

// 告警状态颜色映射
const statusColors = {
  ACTIVE: '#dc2626',
  CLEARED: '#22c55e',
  ACKNOWLEDGED: '#f59e0b',
}

const severityOpt = ref([])
const statusOpt = ref([])

const column = [
  {
    prop: 'severity',
    label: '严重程度',
    width: 100,
    align: 'center',
    slot: 'severity',
  },
  {
    prop: 'alarmName',
    label: '告警名称',
    minWidth: 180,
  },
  {
    prop: 'alarmType',
    label: '告警类型',
    width: 120,
  },
  {
    prop: 'deviceName',
    label: '设备',
    width: 140,
  },
  {
    prop: 'message',
    label: '告警消息',
    minWidth: 200,
    slot: 'message',
    showOverflowTooltip: true,
  },
  {
    prop: 'status',
    label: '状态',
    width: 90,
    align: 'center',
    slot: 'status',
  },
  {
    prop: 'startTs',
    label: '开始时间',
    width: 160,
    slot: 'startTs',
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 80,
    label: '操作',
  },
]

const {
  params,
  updatePage,
  onSearch,
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
    console.error('加载统计失败', e)
  }
}

// 确认告警
async function handleAcknowledge(row) {
  try {
    await alarmAcknowledgeApi(row.id)
    updatePage()
  }
  catch (e) {
    console.error('确认失败', e)
  }
}

// 清除告警
async function handleClear(row) {
  try {
    await alarmClearApi(row.id)
    updatePage()
  }
  catch (e) {
    console.error('清除失败', e)
  }
}

// 在详情弹窗中确认告警
async function handleAcknowledgeInDetail() {
  if (!currentAlarm.value) return
  try {
    await alarmAcknowledgeApi(currentAlarm.value.id)
    // 更新当前告警状态
    currentAlarm.value = { ...currentAlarm.value, status: 'ACKNOWLEDGED' }
    updatePage()
  }
  catch (e) {
    console.error('确认失败', e)
  }
}

// 在详情弹窗中清除告警
async function handleClearInDetail() {
  if (!currentAlarm.value) return
  try {
    await alarmClearApi(currentAlarm.value.id)
    // 更新当前告警状态并关闭弹窗
    detailVisible.value = false
    updatePage()
  }
  catch (e) {
    console.error('清除失败', e)
  }
}

// 查看详情
function handleDetail(row) {
  currentAlarm.value = row
  detailVisible.value = true
}

// 获取严重程度标签
function getSeverityTag(severity) {
  const map = {
    CRITICAL: { type: 'danger', text: '严重', color: '#dc2626' },
    MAJOR: { type: 'warning', text: '主要', color: '#ea580c' },
    MINOR: { type: 'info', text: '次要', color: '#f59e0b' },
    WARNING: { type: '', text: '警告', color: '#3b82f6' },
  }
  return map[severity] || { type: '', text: severity, color: '#6b7280' }
}

// 获取状态标签
function getStatusTag(status) {
  const map = {
    ACTIVE: { type: 'danger', text: '活动' },
    CLEARED: { type: 'success', text: '已清除' },
    ACKNOWLEDGED: { type: 'warning', text: '已确认' },
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
  if (!timeStr) return '-'
  // 处理 ISO 格式时间
  const date = new Date(timeStr)
  if (isNaN(date.getTime())) return timeStr
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
  if (!currentAlarm.value) return []

  const alarm = currentAlarm.value
  const sections = []

  // 基本信息
  sections.push({
    title: '基本信息',
    items: [
      { label: '告警名称', value: alarm.alarmName },
      { label: '告警类型', value: alarm.alarmType },
      { label: '严重程度', value: getSeverityTag(alarm.severity).text, tag: true, tagType: getSeverityTag(alarm.severity).type },
      { label: '状态', value: getStatusTag(alarm.status).text, tag: true, tagType: getStatusTag(alarm.status).type },
    ],
  })

  // 设备信息
  sections.push({
    title: '设备信息',
    items: [
      { label: '设备名称', value: alarm.deviceName },
      { label: '设备Key', value: alarm.deviceKey },
    ],
  })

  // 时间信息
  sections.push({
    title: '时间信息',
    items: [
      { label: '开始时间', value: formatTime(alarm.startTs) },
      { label: '结束时间', value: formatTime(alarm.endTs) },
      { label: '清除时间', value: formatTime(alarm.clearTs) },
      { label: '清除人', value: alarm.clearBy || '-' },
    ],
  })

  // 告警消息
  const messageContent = alarm.message && alarm.message.trim() ? alarm.message : '-'
  sections.push({
    title: '告警消息',
    items: [
      { label: '消息内容', value: messageContent, full: true },
    ],
  })

  // 触发数据详情
  if (alarm.details && Object.keys(alarm.details).length > 0) {
    const detailItems = Object.entries(alarm.details).map(([key, value]) => ({
      label: key,
      value: typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value),
    }))
    sections.push({
      title: '触发数据',
      items: detailItems,
    })
  }

  return sections
})

onMounted(() => {
  updatePage()
  loadStatistics()
})
</script>

<template>
  <div class="alarm-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-row">
          <div class="title-area">
            <h1 class="page-title">
              <span class="title-icon">◈</span>
              告警管理
            </h1>
            <p class="page-subtitle">
              设备告警监控与处理
            </p>
          </div>
          <!-- 统计卡片 -->
          <div class="stat-cards">
            <div class="stat-card active">
              <div class="stat-value">
                {{ statistics.activeCount }}
              </div>
              <div class="stat-label">
                活动告警
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item label="设备Key">
            <el-input v-model="params.deviceKey" clearable placeholder="请输入设备Key" />
          </el-form-item>

          <el-form-item label="告警类型">
            <el-input v-model="params.alarmType" clearable placeholder="请输入告警类型" />
          </el-form-item>

          <el-form-item label="严重程度">
            <el-select v-model="params.severity" clearable placeholder="全部">
              <el-option
                v-for="item in severityOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="params.status" clearable placeholder="全部">
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
            搜索
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
            详情
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 告警详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="告警详情"
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
            确认告警
          </el-button>
          <el-button
            v-if="currentAlarm?.status !== 'CLEARED'"
            class="btn-clear"
            @click="handleClearInDetail"
          >
            <el-icon><Close /></el-icon>
            清除告警
          </el-button>
          <el-button @click="detailVisible = false">
            关闭
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
