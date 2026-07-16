<script setup>
import { Delete, Edit, Plus, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { dashboardDelete, dashboardList } from '@/api/dashboard'
import WidgetRenderer from './WidgetRenderer.vue'

const router = useRouter()
const dashboards = ref([])
const loading = ref(false)
const previewVisible = ref(false)
const previewDashboard = ref(null)

async function loadList() {
  loading.value = true
  try {
    const res = await dashboardList()
    dashboards.value = res?.data || []
  }
  finally {
    loading.value = false
  }
}

function onEdit(id) {
  router.push(`/dashboard/editor/${id}`)
}

function onCreate() {
  router.push('/dashboard/editor')
}

function onPreview(d) {
  previewDashboard.value = d
  previewVisible.value = true
}

async function onDelete(d) {
  await ElMessageBox.confirm(`确认删除「${d.name}」?`, '提示', { type: 'warning' })
  await dashboardDelete(d.id)
  ElMessage.success('已删除')
  loadList()
}

onMounted(loadList)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          自定义仪表盘
        </h1>
        <p class="page-subtitle">
          创建可视化面板，实时监控设备数据
        </p>
      </div>
      <el-button type="primary" :icon="Plus" @click="onCreate">
        新建仪表盘
      </el-button>
    </div>

    <div v-loading="loading" class="dashboard-grid">
      <div
        v-for="d in dashboards"
        :key="d.id"
        class="dashboard-card glass-card"
      >
        <div class="card-top">
          <span class="card-name">{{ d.name }}</span>
          <el-tag v-if="d.isDefault" type="success" size="small" effect="plain">
            默认
          </el-tag>
        </div>
        <p class="card-desc">
          {{ d.description || '暂无描述' }}
        </p>
        <div class="card-meta">
          <span>{{ d.layout?.length || 0 }} 个组件</span>
          <span>{{ d.updateTime }}</span>
        </div>
        <div class="card-actions">
          <el-button text size="small" :icon="View" @click="onPreview(d)">
            预览
          </el-button>
          <el-button text type="primary" size="small" :icon="Edit" @click="onEdit(d.id)">
            编辑
          </el-button>
          <el-button text type="danger" size="small" :icon="Delete" @click="onDelete(d)">
            删除
          </el-button>
        </div>
      </div>
      <div v-if="!loading && dashboards.length === 0" class="empty-state">
        <el-empty description="还没有仪表盘，点击右上角创建">
          <el-button type="primary" :icon="Plus" @click="onCreate">
            新建仪表盘
          </el-button>
        </el-empty>
      </div>
    </div>

    <el-dialog
      v-model="previewVisible"
      :title="previewDashboard?.name || '预览'"
      width="90%"
      top="3vh"
      destroy-on-close
    >
      <div v-if="previewDashboard?.layout?.length" class="preview-container">
        <GridLayout
          v-if="previewDashboard"
          :layout="previewDashboard.layout"
          :col-num="12"
          :row-height="60"
          :is-draggable="false"
          :is-resizable="false"
          :margin="[8, 8]"
        >
          <GridItem
            v-for="item in previewDashboard.layout"
            :key="item.i"
            :x="item.x"
            :y="item.y"
            :w="item.w"
            :h="item.h"
            :i="item.i"
          >
            <div class="preview-widget">
              <div class="widget-header">
                {{ item.title }}
              </div>
              <div class="widget-body">
                <WidgetRenderer :widget="item" />
              </div>
            </div>
          </GridItem>
        </GridLayout>
      </div>
      <el-empty v-else description="该仪表盘没有组件" />
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--space-lg);
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-lg);
  padding-bottom: var(--space-xl);
}

.dashboard-card {
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);

  .card-top {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .card-name {
      font-size: 15px;
      font-weight: 600;
      color: var(--iot-color-text-primary);
    }
  }

  .card-desc {
    font-size: 13px;
    color: var(--iot-color-text-secondary);
    min-height: 20px;
    margin: 0;
    line-height: 1.5;
  }

  .card-meta {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    color: var(--iot-color-text-muted);
  }

  .card-actions {
    display: flex;
    justify-content: flex-end;
    gap: 2px;
    border-top: 1px solid var(--iot-color-border-light);
    padding-top: var(--space-sm);
    margin-top: auto;
  }
}

.empty-state {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  padding: 60px 0;
}

.preview-container {
  min-height: 400px;
}

.preview-widget {
  height: 100%;
  border: 1px solid var(--iot-color-border-light);
  border-radius: var(--radius-sm);
  display: flex;
  flex-direction: column;
  overflow: hidden;

  .widget-header {
    padding: 6px 10px;
    font-size: 12px;
    font-weight: 600;
    border-bottom: 1px solid var(--iot-color-border-light);
    color: var(--iot-color-text-secondary);
  }

  .widget-body {
    flex: 1;
    min-height: 0;
    padding: 4px;
  }
}
</style>
