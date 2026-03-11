<script setup>
import { Bell, Check, Close } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
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
  },
  {
    prop: 'alarmName',
    label: '告警名称',
    minWidth: 200,
  },
  {
    prop: 'alarmType',
    label: '告警类型',
    width: 150,
  },
  {
    prop: 'deviceName',
    label: '设备',
    minWidth: 150,
  },
  {
    prop: 'message',
    label: '告警消息',
    minWidth: 250,
    showOverflowTooltip: true,
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    align: 'center',
  },
  {
    prop: 'startTs',
    label: '开始时间',
    width: 170,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 150,
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
    const { data } = await alarmStatisticsApi()
    statistics.value = data.data
    severityOpt.value = data.data.severityOptions || []
    statusOpt.value = data.data.statusOptions || []
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

// 获取严重程度标签
function getSeverityTag(severity) {
  const map = {
    CRITICAL: { type: 'danger', text: '严重' },
    MAJOR: { type: 'warning', text: '主要' },
    MINOR: { type: 'info', text: '次要' },
    WARNING: { type: '', text: '警告' },
  }
  return map[severity] || { type: '', text: severity }
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
        <h1 class="page-title">
          <el-icon :size="24" class="title-icon">
            <Bell />
          </el-icon>
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
        <template #cz="{ row }">
          <el-button
            v-if="row.status === 'ACTIVE'"
            type="primary"
            link
            @click="handleAcknowledge(row)"
          >
            <el-icon><Check /></el-icon>
            确认
          </el-button>
          <el-button
            v-if="row.status !== 'CLEARED'"
            type="success"
            link
            @click="handleClear(row)"
          >
            <el-icon><Close /></el-icon>
            清除
          </el-button>
        </template>
      </IotTable>
    </div>
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;

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
      font-size: 24px;
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
    background: var(--iot-glass-bg);
    backdrop-filter: blur(20px);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-lg);
    padding: var(--space-lg) var(--space-xl);
    min-width: 120px;
    text-align: center;

    &.active {
      border-left: 4px solid #ef4444;

      .stat-value {
        color: #ef4444;
      }
    }

    .stat-value {
      font-size: 28px;
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
</style>
