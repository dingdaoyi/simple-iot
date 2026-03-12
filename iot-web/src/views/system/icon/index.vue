<script setup>
import { Delete, Edit, Loading } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { iconDeleteApi, iconListApi } from '@/api/index.js'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/system/icon/widget/editDia.vue'

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  tableData,
  loading,
  onDelete,
  onAdd,
  onEdit,
  diaTitle,
  currentItem,
} = useTable({
  deleteApi: iconDeleteApi,
  fetchApi: iconListApi,
  diaName: '图标',
  defParams: {},
})

function closeEdite() {
  updatePage()
}

onMounted(() => {
  updatePage()
})
</script>

<template>
  <div class="icon-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">◈</span>
          图标管理
        </h1>
        <p class="page-subtitle">
          管理设备图标与视觉资源
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-input">
          <label class="input-label">图标名称</label>
          <el-input
            v-model="params.name"
            clearable
            placeholder="输入图标名称搜索..."
            prefix-icon="Search"
            @keyup.enter="onSearch"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <span class="btn-icon">+</span>
            添加图标
          </el-button>
        </div>
      </div>
    </div>

    <!-- 图标网格 -->
    <div class="icon-grid-wrapper">
      <div v-loading="loading" class="icon-grid">
        <!-- 空状态 -->
        <div v-if="!tableData?.length && !loading" class="empty-state">
          <div class="empty-icon">
            📁
          </div>
          <p class="empty-text">
            暂无图标数据
          </p>
          <el-button type="primary" @click="onAdd">
            添加第一个图标
          </el-button>
        </div>

        <!-- 图标卡片 -->
        <div
          v-for="item in tableData"
          :key="item.id"
          class="icon-card"
        >
          <div class="icon-card-inner">
            <!-- 图标预览区 -->
            <div class="icon-preview">
              <el-image
                :src="item.path"
                fit="contain"
                class="icon-image"
                :preview-src-list="[item.path]"
                :hide-on-click-modal="true"
              >
                <template #error>
                  <div class="icon-error">
                    <span class="error-icon">⚠️</span>
                    <span class="error-text">加载失败</span>
                  </div>
                </template>
                <template #placeholder>
                  <div class="icon-loading">
                    <el-icon :size="24" class="is-loading">
                      <Loading />
                    </el-icon>
                  </div>
                </template>
              </el-image>
            </div>

            <!-- 图标信息 -->
            <div class="icon-info">
              <div class="icon-name" :title="item.name">
                {{ item.name }}
              </div>
              <div class="icon-path">
                {{ item.path?.split('/').pop() || '-' }}
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="icon-actions">
              <el-button type="primary" link @click="onEdit(item)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" link @click="onDelete(item)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.icon-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  .header-content {
    background: var(--iot-glass-bg-dark);
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
      background: linear-gradient(90deg, var(--iot-color-primary), var(--iot-color-accent));
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
      font-size: 28px;
      background: linear-gradient(135deg, var(--iot-color-primary), var(--iot-color-accent));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-subtitle {
    margin: var(--space-sm) 0 0 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
  }
}

/* 搜索栏 */
.search-bar {
  .search-row {
    display: flex;
    align-items: flex-end;
    gap: var(--space-md);
    flex-wrap: wrap;
  }

  .search-input {
    flex: 1;
    min-width: 280px;
    max-width: 400px;

    .input-label {
      display: block;
      font-size: 13px;
      font-weight: 500;
      color: var(--iot-color-text-secondary);
      margin-bottom: var(--space-sm);
    }
  }

  .search-actions {
    display: flex;
    gap: var(--space-sm);
  }

  .btn-icon {
    margin-right: var(--space-xs);
    font-size: 16px;
  }
}

/* 图标网格容器 */
.icon-grid-wrapper {
  flex: 1;
  background: var(--iot-glass-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
}

.icon-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: var(--space-lg);
}

/* 图标卡片 */
.icon-card {
  perspective: 1000px;

  .icon-card-inner {
    background: var(--iot-glass-bg);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-lg);
    padding: var(--space-md);
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
    transition: all var(--transition-base) ease;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, var(--iot-color-primary), var(--iot-color-accent));
      opacity: 0;
      transition: opacity var(--transition-base) ease;
    }

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(99, 102, 241, 0.15);
      border-color: var(--iot-color-primary);

      &::before {
        opacity: 1;
      }

      .icon-image {
        transform: scale(1.1);
      }
    }
  }
}

/* 图标预览区 */
.icon-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 140px;
  background: var(--iot-color-background);
  border-radius: var(--radius-md);
  overflow: hidden;
  position: relative;

  .icon-image {
    width: 100%;
    height: 100%;
    transition: transform var(--transition-base) ease;
  }

  .icon-error,
  .icon-loading {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: var(--space-sm);
    width: 100%;
    height: 100%;
    background: var(--iot-color-background);
    color: var(--iot-color-text-muted);

    .error-icon {
      font-size: 32px;
    }

    .error-text {
      font-size: 12px;
    }
  }
}

/* 图标信息 */
.icon-info {
  text-align: center;

  .icon-name {
    font-size: 14px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    margin-bottom: var(--space-xs);
  }

  .icon-path {
    font-size: 11px;
    color: var(--iot-color-text-muted);
    font-family: 'Fira Code', monospace;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

/* 操作按钮 */
.icon-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-md);
  padding-top: var(--space-sm);
  border-top: 1px solid var(--iot-glass-border);

  .el-button {
    font-size: 13px;
  }
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-2xl);
  gap: var(--space-lg);

  .empty-icon {
    font-size: 64px;
    opacity: 0.5;
  }

  .empty-text {
    font-size: 16px;
    color: var(--iot-color-text-muted);
    margin: 0;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .icon-page {
    padding: var(--space-md);
  }

  .search-row {
    flex-direction: column;

    .search-input {
      width: 100%;
      max-width: 100%;
    }

    .search-actions {
      width: 100%;

      .el-button {
        flex: 1;
      }
    }
  }

  .icon-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: var(--space-md);
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
