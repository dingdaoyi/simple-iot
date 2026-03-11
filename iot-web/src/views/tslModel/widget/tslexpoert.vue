<script setup>
import { Document, Loading, WarningFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import JsonViewer from 'vue-json-viewer'
import { loadTslData } from '@/api/index.js'

const props = defineProps(['productId'])

const jsonData = ref(null)
const loading = ref(true)
const error = ref(null)

// 复制 JSON 到剪贴板
async function copyJson() {
  try {
    await navigator.clipboard.writeText(JSON.stringify(jsonData.value, null, 2))
    ElMessage.success('已复制到剪贴板')
  }
  catch (e) {
    ElMessage.error('复制失败')
  }
}

onMounted(async () => {
  try {
    loading.value = true
    const { data } = await loadTslData(props.productId)
    jsonData.value = data
  }
  catch (e) {
    error.value = e.message || '加载失败'
    console.error('Failed to load TSL data:', e)
  }
  finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="tsl-container">
    <!-- 工具栏 -->
    <div class="toolbar">
      <span class="title">物模型定义 (TSL)</span>
      <el-button type="primary" size="small" @click="copyJson">
        复制 JSON
      </el-button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-state">
      <el-icon class="is-loading" :size="32">
        <Loading />
      </el-icon>
      <span>加载中...</span>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-state">
      <el-icon :size="32" color="var(--iot-color-danger)">
        <WarningFilled />
      </el-icon>
      <span>{{ error }}</span>
    </div>

    <!-- JSON 展示 -->
    <div v-else-if="jsonData" class="json-viewer-wrapper">
      <JsonViewer
        :value="jsonData"
        :expand-depth="3"
        :copyable="false"
        :expanded="true"
        theme="dark"
      />
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <el-icon :size="32" color="var(--iot-color-text-muted)">
        <Document />
      </el-icon>
      <span>暂无物模型数据</span>
    </div>
  </div>
</template>

<style scoped lang="scss">
.tsl-container {
  height: calc(80vh);
  display: flex;
  flex-direction: column;
  background: #1e1e1e; /* VS Code 深色背景 */
  border-radius: var(--radius-lg);
  border: 1px solid #333;
  overflow: hidden;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-md) var(--space-lg);
  border-bottom: 1px solid #333;
  background: #252526; /* VS Code 标题栏背景 */

  .title {
    font-size: 14px;
    font-weight: 600;
    color: #cccccc;
  }
}

.loading-state,
.error-state,
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
  color: #858585;
}

.error-state {
  color: #f44747;
}

.json-viewer-wrapper {
  flex: 1;
  overflow: auto;
  padding: var(--space-lg);
  background: #1e1e1e;

  :deep(.jv-container) {
    background: transparent;
    font-family: 'Fira Code', 'Consolas', 'Monaco', monospace;
    font-size: 13px;
    color: #d4d4d4;

    .jv-key {
      color: #9cdcfe; /* VS Code 蓝色键名 */
    }

    .jv-item {
      &.jv-string {
        color: #ce9178; /* VS Code 橙色字符串 */
      }

      &.jv-number {
        color: #b5cea8; /* VS Code 绿色数字 */
      }

      &.jv-boolean {
        color: #569cd6; /* VS Code 蓝色布尔值 */
      }

      &.jv-null {
        color: #569cd6;
      }
    }

    .jv-ellipsis {
      background: #333;
      color: #569cd6;
      padding: 2px 8px;
      border-radius: var(--radius-sm);
      cursor: pointer;

      &:hover {
        background: #007acc;
        color: white;
      }
    }

    .jv-chevron {
      color: #858585;
    }
  }
}
</style>
