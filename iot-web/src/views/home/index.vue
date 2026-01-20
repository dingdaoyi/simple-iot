<script setup>
import { getDashboardStatistics } from '@/api/dashboard'
import { useAmapConfig } from '@/config/amapConfig'
import AMapLoader from '@amap/amap-jsapi-loader'
import { onMounted, ref } from 'vue'

const { amapKey } = useAmapConfig()

const stats = ref({
  total: 0,
  online: 0,
  offline: 0,
  todayAdd: 0,
  cpuUsage: 0,
  memoryUsage: 0,
  diskUsage: 0,
})

const alerts = ref([
  { time: '08:01', device: '设备A', message: '温度超限告警', level: 'danger' },
  { time: '08:05', device: '设备B', message: '离线', level: 'danger' },
  { time: '08:10', device: '设备C', message: '电池低电量', level: 'warning' },
])

onMounted(() => {
  // 地图加载
  AMapLoader.load({
    key: amapKey,
    version: '2.0',
    plugins: ['AMap.Marker'],
  }).then((AMap) => {
    const map = new AMap.Map('amap-container', {
      zoom: 11,
      // center: [116.397428, 39.90923]
    })
  })
  // 统计数据加载
  getDashboardStatistics().then((res) => {
    if (res && res.data)
      Object.assign(stats.value, res.data)
  })
})
</script>

<template>
  <div class="dashboard-container">
    <!-- 左侧：统计卡片+系统资源 -->
    <div class="dashboard-sidebar">
      <!-- 设备统计卡片 -->
      <div class="stat-section">
        <h3 class="section-title">设备统计</h3>
        <div class="stat-grid">
          <div class="iot-card stat-card">
            <div class="stat-label">设备总数</div>
            <div class="stat-value">{{ stats.total }}</div>
          </div>
          <div class="iot-card stat-card stat-card-success">
            <div class="stat-label">在线设备</div>
            <div class="stat-value">{{ stats.online }}</div>
          </div>
          <div class="iot-card stat-card stat-card-danger">
            <div class="stat-label">离线设备</div>
            <div class="stat-value">{{ stats.offline }}</div>
          </div>
          <div class="iot-card stat-card">
            <div class="stat-label">今日新增</div>
            <div class="stat-value">{{ stats.todayAdd }}</div>
          </div>
        </div>
      </div>

      <!-- 系统资源统计卡片 -->
      <div class="stat-section">
        <h3 class="section-title">系统资源</h3>
        <div class="stat-grid">
          <div class="iot-card stat-card stat-card-info">
            <div class="stat-label">CPU使用率</div>
            <div class="stat-value">{{ stats.cpuUsage }}%</div>
          </div>
          <div class="iot-card stat-card stat-card-purple">
            <div class="stat-label">内存使用率</div>
            <div class="stat-value">{{ stats.memoryUsage }}%</div>
          </div>
          <div class="iot-card stat-card stat-card-warning">
            <div class="stat-label">磁盘使用率</div>
            <div class="stat-value">{{ stats.diskUsage }}%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 中间：地图最大化 -->
    <div class="dashboard-main">
      <div class="map-container">
        <div id="amap-container" class="amap-wrapper" />
      </div>
    </div>

    <!-- 右侧：告警动态 -->
    <div class="dashboard-alerts">
      <div class="iot-card alert-card">
        <div class="alert-header">
          <div class="alert-icon">
            <el-icon><Bell /></el-icon>
          </div>
          <h3 class="alert-title">最新告警动态</h3>
        </div>
        <ul class="alert-list">
          <li v-for="(alert, index) in alerts" :key="index" class="alert-item" :class="`alert-item-${alert.level}`">
            <div class="alert-time">{{ alert.time }}</div>
            <div class="alert-content">
              <span class="alert-device">{{ alert.device }}</span>
              <span class="alert-message">{{ alert.message }}</span>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-container {
  display: grid;
  grid-template-columns: 280px 1fr 320px;
  gap: var(--space-lg);
  height: 100%;
  padding: 0;
}

.dashboard-sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  overflow-y: auto;
}

.stat-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-text);
  margin: 0;
}

.stat-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--space-md);
}

.stat-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--space-lg);
  min-height: 100px;
}

.stat-label {
  font-size: 13px;
  color: #64748B;
  margin-bottom: var(--space-sm);
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-text);
}

.stat-card-success .stat-value {
  color: #10B981;
}

.stat-card-danger .stat-value {
  color: #EF4444;
}

.stat-card-info .stat-value {
  color: #3B82F6;
}

.stat-card-purple .stat-value {
  color: #8B5CF6;
}

.stat-card-warning .stat-value {
  color: #F59E0B;
}

.dashboard-main {
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-container {
  width: 100%;
  height: 100%;
  background: white;
  border-radius: 12px;
  box-shadow: var(--shadow-md);
  padding: var(--space-md);
  display: flex;
  align-items: center;
  justify-content: center;
}

.amap-wrapper {
  width: 100%;
  height: 100%;
  background: #F1F5F9;
  border-radius: 8px;
}

.dashboard-alerts {
  display: flex;
  flex-direction: column;
}

.alert-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.alert-header {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-lg);
}

.alert-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #FEF3C7;
  color: #D97706;
  border-radius: 8px;
  font-size: 18px;
}

.alert-title {
  font-size: 16px;
  font-weight: 600;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-text);
  margin: 0;
}

.alert-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.alert-item {
  padding: var(--space-md);
  border-radius: 8px;
  background: #F8FAFC;
  border-left: 3px solid #CBD5E1;
  transition: all 0.2s ease;
  cursor: pointer;
}

.alert-item:hover {
  background: #F1F5F9;
  transform: translateX(2px);
}

.alert-item-danger {
  border-left-color: #EF4444;
  background: #FEF2F2;
}

.alert-item-warning {
  border-left-color: #F59E0B;
  background: #FFFBEB;
}

.alert-time {
  font-size: 12px;
  color: #64748B;
  margin-bottom: var(--space-xs);
  font-family: 'Fira Code', monospace;
}

.alert-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.alert-device {
  font-size: 13px;
  font-weight: 600;
  color: var(--iot-color-text);
}

.alert-message {
  font-size: 13px;
  color: #64748B;
}

@media (max-width: 1440px) {
  .dashboard-container {
    grid-template-columns: 240px 1fr 280px;
  }
}

@media (max-width: 1024px) {
  .dashboard-container {
    grid-template-columns: 1fr;
    grid-template-rows: auto auto auto;
  }

  .dashboard-sidebar {
    flex-direction: row;
    overflow-x: auto;
  }

  .stat-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  }
}
</style>
