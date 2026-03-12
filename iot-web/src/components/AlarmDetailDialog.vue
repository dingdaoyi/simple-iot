<script setup>
import { Check, Close } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { alarmAcknowledgeApi, alarmClearApi } from '@/api/index.js'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  alarm: {
    type: Object,
    default: null,
  },
})

const emits = defineEmits(['update:modelValue', 'refresh'])

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

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr) return '-'
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
  if (!props.alarm) return []

  const alarm = props.alarm
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

// 关闭弹窗
function handleClose() {
  emits('update:modelValue', false)
}

// 确认告警
async function handleAcknowledge() {
  if (!props.alarm) return
  try {
    await alarmAcknowledgeApi(props.alarm.id)
    emits('refresh')
  }
  catch (e) {
    console.error('确认失败', e)
  }
}

// 清除告警
async function handleClear() {
  if (!props.alarm) return
  try {
    await alarmClearApi(props.alarm.id)
    handleClose()
    emits('refresh')
  }
  catch (e) {
    console.error('清除失败', e)
  }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    title="告警详情"
    width="640px"
    class="alarm-detail-dialog"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <div v-if="alarm" class="detail-content">
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
        <div class="footer-actions">
          <el-button
            v-if="alarm?.status === 'ACTIVE'"
            class="btn-acknowledge"
            @click="handleAcknowledge"
          >
            <el-icon><Check /></el-icon>
            确认告警
          </el-button>
          <el-button
            v-if="alarm?.status !== 'CLEARED'"
            class="btn-clear"
            @click="handleClear"
          >
            <el-icon><Close /></el-icon>
            清除告警
          </el-button>
        </div>
        <el-button @click="handleClose">
          关闭
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
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
