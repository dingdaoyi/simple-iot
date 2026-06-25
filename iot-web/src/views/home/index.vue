<script setup>
import {
  Bell,
  Box,
  CircleCheck,
  CircleClose,
  Connection,
  Cpu,
  Grid,
  Lightning,
  Monitor,
  Plus,
  Setting,
} from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { getDashboardStatistics } from '@/api/dashboard'
import { alarmPageApi, alarmStatisticsApi, devicePageApi } from '@/api/index.js'
import AlarmDetailDialog from '@/components/AlarmDetailDialog.vue'

const router = useRouter()
const { t, locale } = useI18n()

const stats = ref({
  total: 0,
  online: 0,
  offline: 0,
  todayAdd: 0,
  cpuUsage: 0,
  memoryUsage: 0,
  diskUsage: 0,
})

// 最近设备
const recentDevices = ref([])
const devicesLoading = ref(false)

// 告警数据
const alerts = ref([])
const alertsLoading = ref(false)
const alertStats = ref({
  activeCount: 0,
})

// 告警详情弹窗
const detailVisible = ref(false)
const currentAlarm = ref(null)

// 计算在线率
const onlineRate = computed(() => {
  if (stats.value.total === 0)
    return 0
  return ((stats.value.online / stats.value.total) * 100).toFixed(1)
})

// 概览卡片数据
const overviewCards = computed(() => [
  {
    key: 'total',
    label: t('home.device_total'),
    value: stats.value.total,
    icon: Monitor,
    color: 'primary',
    trend: stats.value.todayAdd > 0 ? t('home.today_count', { count: stats.value.todayAdd }) : null,
  },
  {
    key: 'online',
    label: t('home.online_device'),
    value: stats.value.online,
    icon: CircleCheck,
    color: 'success',
    trend: `${onlineRate.value}%`,
  },
  {
    key: 'offline',
    label: t('home.offline_device'),
    value: stats.value.offline,
    icon: CircleClose,
    color: 'danger',
    trend: null,
  },
  {
    key: 'todayAdd',
    label: t('home.today_add'),
    value: stats.value.todayAdd,
    icon: Plus,
    color: 'accent',
    trend: null,
  },
])

// 快捷入口
const quickLinks = computed(() => [
  { name: t('menu.device'), icon: Monitor, path: '/device', color: 'var(--iot-color-primary)' },
  { name: t('menu.product'), icon: Box, path: '/product', color: 'var(--iot-color-secondary)' },
  { name: t('menu.protocol'), icon: Connection, path: '/protocol', color: 'var(--iot-color-success)' },
  { name: t('menu.ruleChain'), icon: Lightning, path: '/rule-chain', color: 'var(--iot-color-warning)' },
  { name: t('menu.alarm'), icon: Bell, path: '/alarm', color: 'var(--iot-color-danger)' },
  { name: t('common.settings'), icon: Setting, path: '/icon', color: 'var(--iot-color-accent)' },
])

// 跳转页面
function navigateTo(path) {
  router.push(path)
}

// 加载最近设备
async function loadRecentDevices() {
  devicesLoading.value = true
  try {
    const res = await devicePageApi({ page: 1, size: 5 })
    recentDevices.value = res.data?.records || res.data || []
  }
  catch (e) {
    console.error(t('home.failed_load_devices'), e)
  }
  finally {
    devicesLoading.value = false
  }
}

// 加载告警数据
async function loadAlerts() {
  alertsLoading.value = true
  try {
    const res = await alarmPageApi({
      page: 1,
      size: 5,
      status: 'ACTIVE',
    })
    const records = res.data?.records || res.data || []
    alerts.value = records.map(alarm => ({
      ...alarm,
      device: alarm.deviceName || alarm.deviceKey || t('device.unknown_device'),
      message: alarm.message || alarm.alarmName || t('alarm.alarm'),
      level: getAlarmLevel(alarm.severity),
      time: formatRelativeTime(alarm.startTs),
    }))
  }
  catch (e) {
    console.error(t('home.home_page'), e)
  }
  finally {
    alertsLoading.value = false
  }
}

// 加载告警统计
async function loadAlertStats() {
  try {
    const res = await alarmStatisticsApi()
    const data = res.data || res
    alertStats.value.activeCount = data.activeCount || 0
  }
  catch (e) {
    console.error(t('home.failed_load_alarm_statistics'), e)
  }
}

// 根据严重程度获取告警级别
function getAlarmLevel(severity) {
  const map = {
    CRITICAL: 'danger',
    MAJOR: 'danger',
    MINOR: 'warning',
    WARNING: 'warning',
  }
  return map[severity] || 'warning'
}

// 格式化相对时间
function formatRelativeTime(timeStr) {
  if (!timeStr)
    return ''
  const date = new Date(timeStr)
  if (Number.isNaN(date.getTime()))
    return timeStr

  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1)
    return t('home.just_now')
  if (minutes < 60)
    return t('home.minutes_ago', { count: minutes })
  if (hours < 24)
    return t('home.hours_ago', { count: hours })
  if (days < 7)
    return t('home.days_ago', { count: days })

  return date.toLocaleDateString(locale.value, {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 查看告警详情
function handleAlarmDetail(alert) {
  currentAlarm.value = alert
  detailVisible.value = true
}

// 刷新告警数据
function refreshAlerts() {
  loadAlerts()
  loadAlertStats()
}

// 获取设备状态标签类型
function getDeviceStatusType(online) {
  return online ? 'success' : 'danger'
}

// 获取设备状态文字
function getDeviceStatusText(online) {
  return online ? t('device.online') : t('device.offline')
}

onMounted(() => {
  getDashboardStatistics().then((res) => {
    if (res && res.data)
      Object.assign(stats.value, res.data)
  })

  loadRecentDevices()
  loadAlerts()
  loadAlertStats()
})</script>

<template>
  <div class="dashboard-page">
    <!-- 顶部概览卡片 -->
    <div class="overview-section">
      <div
        v-for="card in overviewCards"
        :key="card.key"
        class="overview-card"
        :class="`overview-card--${card.color}`"
      >
        <div class="card-icon">
          <el-icon :size="28">
            <component :is="card.icon" />
          </el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">
            {{ card.value }}
          </div>
          <div class="card-label">
            {{ card.label }}
          </div>
          <div v-if="card.trend" class="card-trend">
            {{ card.trend }}
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧 -->
      <div class="left-column">
        <!-- 系统资源监控 -->
        <div class="resource-section glass-card">
          <div class="section-header">
            <el-icon :size="20">
              <Cpu />
            </el-icon>
            <h3>{{ t('home.system_resources') }}</h3>
          </div>
          <div class="resource-list">
            <div class="resource-item">
              <div class="resource-info">
                <span class="resource-label">CPU</span>
                <span class="resource-value">{{ stats.cpuUsage }}%</span>
              </div>
              <el-progress
                :percentage="stats.cpuUsage"
                :stroke-width="8"
                :color="stats.cpuUsage > 80 ? 'var(--iot-color-danger)' : 'var(--iot-color-primary)'"
                :show-text="false"
              />
            </div>
            <div class="resource-item">
              <div class="resource-info">
                <span class="resource-label">{{ t('home.memory') }}</span>
                <span class="resource-value">{{ stats.memoryUsage }}%</span>
              </div>
              <el-progress
                :percentage="stats.memoryUsage"
                :stroke-width="8"
                :color="stats.memoryUsage > 80 ? 'var(--iot-color-danger)' : 'var(--iot-color-secondary)'"
                :show-text="false"
              />
            </div>
            <div class="resource-item">
              <div class="resource-info">
                <span class="resource-label">{{ t('home.disk') }}</span>
                <span class="resource-value">{{ stats.diskUsage }}%</span>
              </div>
              <el-progress
                :percentage="stats.diskUsage"
                :stroke-width="8"
                :color="stats.diskUsage > 80 ? 'var(--iot-color-danger)' : 'var(--iot-color-accent)'"
                :show-text="false"
              />
            </div>
          </div>
        </div>

        <!-- 快捷入口 -->
        <div class="quick-links glass-card">
          <div class="section-header">
            <el-icon :size="20">
              <Grid />
            </el-icon>
            <h3>{{ t('home.quick_actions') }}</h3>
          </div>
          <div class="links-grid">
            <div
              v-for="link in quickLinks"
              :key="link.path"
              class="link-item"
              :style="{ '--link-color': link.color }"
              @click="navigateTo(link.path)"
            >
              <el-icon :size="22">
                <component :is="link.icon" />
              </el-icon>
              <span>{{ link.name }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧 -->
      <div class="right-column">
        <!-- 最近设备 -->
        <div class="devices-section glass-card">
          <div class="section-header">
            <el-icon :size="20">
              <Monitor />
            </el-icon>
            <h3>{{ t('home.recent_devices') }}</h3>
            <el-button type="primary" link size="small" class="view-all" @click="navigateTo('/device')">
              {{ t('home.view_all') }}
            </el-button>
          </div>
          <div v-loading="devicesLoading" class="devices-list">
            <div
              v-for="device in recentDevices"
              :key="device.id"
              class="device-item"
              @click="navigateTo('/device')"
            >
              <div class="device-info">
                <span class="device-name">{{ device.deviceName || device.deviceKey }}</span>
                <span class="device-key">{{ device.deviceKey }}</span>
              </div>
              <el-tag :type="getDeviceStatusType(device.online)" size="small">
                {{ getDeviceStatusText(device.online) }}
              </el-tag>
            </div>
            <div v-if="!devicesLoading && recentDevices.length === 0" class="empty-tip">
              {{ t('home.no_device_data') }}
            </div>
          </div>
        </div>

        <!-- 告警动态 -->
        <div class="alerts-section glass-card">
          <div class="section-header">
            <el-icon :size="20" class="alert-bell">
              <Bell />
            </el-icon>
            <h3>{{ t('home.alarm_activity') }}</h3>
            <el-badge :value="alertStats.activeCount" class="alert-badge" :hidden="alertStats.activeCount === 0" />
            <el-button type="primary" link size="small" class="view-all" @click="navigateTo('/alarm')">
              {{ t('home.view_all') }}
            </el-button>
          </div>
          <div v-loading="alertsLoading" class="alerts-list">
            <div
              v-for="alert in alerts"
              :key="alert.id"
              class="alert-item"
              :class="`alert-item--${alert.level}`"
              @click="handleAlarmDetail(alert)"
            >
              <div class="alert-indicator" />
              <div class="alert-content">
                <div class="alert-header">
                  <span class="alert-device">{{ alert.device }}</span>
                  <span class="alert-time">{{ alert.time }}</span>
                </div>
                <div class="alert-message">
                  {{ alert.message }}
                </div>
              </div>
            </div>
            <div v-if="!alertsLoading && alerts.length === 0" class="empty-tip">
              {{ t('home.no_active_alarm') }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 告警详情弹窗 -->
    <AlarmDetailDialog
      v-model="detailVisible"
      :alarm="currentAlarm"
      @refresh="refreshAlerts"
    />
  </div>
</template>

<style scoped lang="scss">
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

/* 概览卡片 */
.overview-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--space-lg);
}

.overview-card {
  background: var(--iot-color-bg-card);
  backdrop-filter: blur(20px);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  transition: all var(--transition-base);
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }

  .card-icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-md);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
  }

  .card-content {
    flex: 1;
  }

  .card-value {
    font-size: 32px;
    font-weight: 700;
    font-family: 'Fira Code', monospace;
    color: var(--iot-color-text-primary);
    line-height: 1.2;
  }

  .card-label {
    font-size: 14px;
    color: var(--iot-color-text-secondary);
    margin-top: var(--space-xs);
  }

  .card-trend {
    font-size: 12px;
    color: var(--iot-color-success);
    margin-top: var(--space-xs);
    font-weight: 500;
  }

  &--primary .card-icon {
    background: linear-gradient(135deg, var(--iot-color-primary) 0%, var(--iot-color-primary-dark) 100%);
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
  }

  &--success .card-icon {
    background: linear-gradient(135deg, var(--iot-color-success) 0%, #059669 100%);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  }

  &--danger .card-icon {
    background: linear-gradient(135deg, var(--iot-color-danger) 0%, #dc2626 100%);
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.3);
  }

  &--accent .card-icon {
    background: linear-gradient(135deg, var(--iot-color-accent) 0%, #0891b2 100%);
    box-shadow: 0 4px 12px rgba(6, 182, 212, 0.3);
  }
}

/* 主内容区 - 两栏布局 */
.main-content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: var(--space-lg);
  flex: 1;
}

.left-column,
.right-column {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

/* 通用区块样式 */
.section-header {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-md);

  h3 {
    font-size: 15px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    margin: 0;
  }

  .view-all {
    margin-left: auto;
  }

  .alert-bell {
    color: var(--iot-color-warning);
  }
}

/* 系统资源 */
.resource-section {
  flex-shrink: 0;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.resource-item {
  .resource-info {
    display: flex;
    justify-content: space-between;
    margin-bottom: var(--space-xs);
  }

  .resource-label {
    font-size: 13px;
    color: var(--iot-color-text-secondary);
  }

  .resource-value {
    font-size: 13px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    font-family: 'Fira Code', monospace;
  }
}

/* 快捷入口 */
.quick-links {
  flex: 1;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-sm);
}

.link-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-xs);
  padding: var(--space-md);
  border-radius: var(--radius-md);
  background: var(--iot-glass-bg);
  cursor: pointer;
  transition: all var(--transition-base);
  color: var(--iot-color-text-secondary);

  &:hover {
    background: var(--link-color);
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }

  span {
    font-size: 12px;
    font-weight: 500;
  }
}

/* 设备列表 */
.devices-section {
  flex-shrink: 0;
}

.devices-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  min-height: 200px;
}

.device-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  background: var(--iot-glass-bg);
  cursor: pointer;
  transition: all var(--transition-base);

  &:hover {
    background: var(--iot-glass-bg-hover);
    transform: translateX(4px);
  }

  .device-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
  }

  .device-name {
    font-size: 13px;
    font-weight: 500;
    color: var(--iot-color-text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .device-key {
    font-size: 11px;
    color: var(--iot-color-text-muted);
    font-family: 'Fira Code', monospace;
  }
}

.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  color: var(--iot-color-text-muted);
  font-size: 14px;
}

/* 告警动态 */
.alerts-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.alert-badge {
  margin-left: auto;
}

.alerts-list {
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.alert-item {
  display: flex;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  background: var(--iot-glass-bg);
  border-left: 3px solid var(--iot-color-border);
  cursor: pointer;
  transition: all var(--transition-base);

  &:hover {
    transform: translateX(4px);
    box-shadow: var(--shadow-sm);
  }

  &--danger {
    border-left-color: var(--iot-color-danger);
    background: rgba(239, 68, 68, 0.08);
  }

  &--warning {
    border-left-color: var(--iot-color-warning);
    background: rgba(245, 158, 11, 0.08);
  }

  .alert-indicator {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: var(--iot-color-danger);
    margin-top: 6px;
    flex-shrink: 0;
  }

  &--warning .alert-indicator {
    background: var(--iot-color-warning);
  }

  .alert-content {
    flex: 1;
    min-width: 0;
  }

  .alert-header {
    display: flex;
    justify-content: space-between;
    gap: var(--space-sm);
    margin-bottom: 2px;
  }

  .alert-device {
    font-size: 12px;
    font-weight: 500;
    color: var(--iot-color-text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .alert-time {
    font-size: 11px;
    color: var(--iot-color-text-muted);
    font-family: 'Fira Code', monospace;
    flex-shrink: 0;
  }

  .alert-message {
    font-size: 12px;
    color: var(--iot-color-text-secondary);
  }
}

/* 响应式 */
@media (max-width: 1200px) {
  .overview-section {
    grid-template-columns: repeat(2, 1fr);
  }

  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: var(--space-md);
  }

  .overview-section {
    grid-template-columns: 1fr;
  }

  .links-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>