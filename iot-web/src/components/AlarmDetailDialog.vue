<script setup>
import { Check, Close } from '@element-plus/icons-vue'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
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
const { t, locale } = useI18n()

// 获取严重程度标签
function getSeverityTag(severity) {
  const map = {
    CRITICAL: { type: 'danger', text: t('alarm.severity_critical') },
    MAJOR: { type: 'warning', text: t('alarm.severity_major') },
    MINOR: { type: 'info', text: t('alarm.severity_minor') },
    WARNING: { type: '', text: t('common.warning') },
  }
  return map[severity] || { type: '', text: severity }
}

// 获取状态标签
function getStatusTag(status) {
  const map = {
    ACTIVE: { type: 'danger', text: t('alarm.status_active') },
    CLEARED: { type: 'success', text: t('alarm.status_cleared') },
    ACKNOWLEDGED: { type: 'warning', text: t('alarm.status_acknowledged') },
  }
  return map[status] || { type: '', text: status }
}

// 格式化时间
function formatTime(timeStr) {
  if (!timeStr)
    return '-'
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime()))
    return timeStr
  return date.toLocaleString(locale.value, {
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
  if (!props.alarm)
    return []

  const alarm = props.alarm
  const sections = []

  // 基本信息
  sections.push({
    title: t('alarm.basic_info'),
    items: [
      { label: t('alarm.alarm_name'), value: alarm.alarmName },
      { label: t('alarm.alarm_type'), value: alarm.alarmType },
      { label: t('alarm.severity'), value: getSeverityTag(alarm.severity).text, tag: true, tagType: getSeverityTag(alarm.severity).type },
      { label: t('common.status'), value: getStatusTag(alarm.status).text, tag: true, tagType: getStatusTag(alarm.status).type },
    ],
  })

  // 设备信息
  sections.push({
    title: t('alarm.device_info'),
    items: [
      { label: t('device.device_name'), value: alarm.deviceName },
      { label: t('device.device_key'), value: alarm.deviceKey },
    ],
  })

  // 时间信息
  sections.push({
    title: t('alarm.time_info'),
    items: [
      { label: t('alarm.start_time'), value: formatTime(alarm.startTs) },
      { label: t('alarm.end_time'), value: formatTime(alarm.endTs) },
      { label: t('alarm.clear_time'), value: formatTime(alarm.clearTs) },
      { label: t('alarm.clear_by'), value: alarm.clearBy || '-' },
    ],
  })

  // 告警消息
  const messageContent = alarm.message && alarm.message.trim() ? alarm.message : '-'
  sections.push({
    title: t('alarm.alarm_message'),
    items: [
      { label: t('alarm.message_content'), value: messageContent, full: true },
    ],
  })

  // 触发数据详情
  if (alarm.details && Object.keys(alarm.details).length > 0) {
    const detailItems = Object.entries(alarm.details).map(([key, value]) => ({
      label: key,
      value: typeof value === 'object' ? JSON.stringify(value, null, 2) : String(value),
    }))
    sections.push({
      title: t('alarm.trigger_data'),
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
  if (!props.alarm)
    return
  try {
    await alarmAcknowledgeApi(props.alarm.id)
    emits('refresh')
  }
  catch (e) {
    console.error(t('alarm.alarmdetaildialog'), e)
  }
}

// 清除告警
async function handleClear() {
  if (!props.alarm)
    return
  try {
    await alarmClearApi(props.alarm.id)
    handleClose()
    emits('refresh')
  }
  catch (e) {
    console.error(t('alarm.alarmdetaildialog_alarmdetaildialog'), e)
  }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="t('alarm.alarm_detail')"
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
            {{ t('alarm.acknowledge_alarm') }}
          </el-button>
          <el-button
            v-if="alarm?.status !== 'CLEARED'"
            class="btn-clear"
            @click="handleClear"
          >
            <el-icon><Close /></el-icon>
            {{ t('alarm.clear_alarm') }}
          </el-button>
        </div>
        <el-button @click="handleClose">
          {{ t('common.close') }}
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