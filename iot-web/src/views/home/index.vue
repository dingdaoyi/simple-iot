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
  <div class="flex min-h-screen bg-[#f5f7fa]">
    <!-- 左侧：统计卡片+系统资源 -->
    <div class="flex flex-col gap-4 w-200px p-4 h-full">
      <!-- 设备统计卡片 -->
      <div class="flex flex-col gap-3">
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            设备总数
          </div>
          <div class="text-2xl font-bold text-gray-800">
            {{ stats.total }}
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            在线设备
          </div>
          <div class="text-2xl font-bold text-green-500">
            {{ stats.online }}
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            离线设备
          </div>
          <div class="text-2xl font-bold text-red-400">
            {{ stats.offline }}
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            今日新增
          </div>
          <div class="text-2xl font-bold text-gray-800">
            {{ stats.todayAdd }}
          </div>
        </div>
      </div>
      <!-- 系统资源统计卡片 -->
      <div class="flex flex-col gap-3 mt-2">
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            CPU使用率
          </div>
          <div class="text-2xl font-bold text-blue-500">
            {{ stats.cpuUsage }}%
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            内存使用率
          </div>
          <div class="text-2xl font-bold text-purple-500">
            {{ stats.memoryUsage }}%
          </div>
        </div>
        <div class="bg-white rounded-lg shadow p-4 flex flex-col items-center">
          <div class="text-sm text-gray-500">
            磁盘使用率
          </div>
          <div class="text-2xl font-bold text-yellow-500">
            {{ stats.diskUsage }}%
          </div>
        </div>
      </div>
    </div>
    <!-- 中间：地图最大化 -->
    <div class="flex-1 flex justify-center p-4 h-full">
      <div class="w-full h-full bg-white rounded-lg shadow flex items-center justify-center">
        <div id="amap-container" class="w-[96%] h-[90%] bg-[#f0f2f5] rounded-lg" />
      </div>
    </div>
    <!-- 右侧：告警动态 -->
    <div class="flex flex-col gap-4 w-300px p-4 h-full">
      <div class="bg-white rounded-lg shadow p-4 flex-1 flex flex-col">
        <div class="text-base text-yellow-600 font-medium mb-2">
          最新告警动态
        </div>
        <ul class="list-none p-0 m-0 flex-1 flex flex-col gap-2">
          <li class="py-2 border-b border-gray-100 text-red-500">
            [08:01] 设备A 温度超限告警
          </li>
          <li class="py-2 border-b border-gray-100 text-red-500">
            [08:05] 设备B 离线
          </li>
          <li class="py-2 border-b border-gray-100 text-yellow-500">
            [08:10] 设备C 电池低电量
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
