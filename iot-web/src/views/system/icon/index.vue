<script setup>
import { ElImage } from 'element-plus'
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
    <!-- 搜索栏 -->
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="图标名称">
          <el-input v-model="params.model" clearable placeholder="请输入图标名称" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="onSearch">
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            添加
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 图标网格 -->
    <div class="iot-card icon-grid">
      <div v-loading="loading" class="icon-list">
        <div v-for="item in tableData" :key="item.id" class="icon-item">
          <el-card class="icon-card">
            <template #header>
              <div class="icon-name">
                {{ item.name }}
              </div>
            </template>
            <div class="icon-image-wrapper">
              <ElImage
                :src="item.path"
                :preview-src-list="[item.path]"
                class="icon-image"
                fit="cover"
              />
            </div>
            <template #footer>
              <div class="icon-actions">
                <el-button type="danger" link @click="onDelete(item)">
                  删除
                </el-button>
              </div>
            </template>
          </el-card>
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
  gap: var(--space-lg);
  height: 100%;
}

.search-bar {
  padding: var(--space-md);
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 0;
  }
}

.icon-grid {
  flex: 1;
  padding: var(--space-lg);
  overflow-y: auto;
}

.icon-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: var(--space-lg);
}

.icon-item {
  height: 100%;
}

.icon-card {
  height: 100%;
  display: flex;
  flex-direction: column;

  :deep(.el-card__header) {
    padding: var(--space-sm);
    background: #f8fafc;
  }

  :deep(.el-card__body) {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: var(--space-lg);
  }
}

.icon-name {
  text-align: center;
  font-weight: 600;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-text);
}

.icon-image-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s ease;

  &:hover {
    transform: scale(1.05);
  }
}

.icon-actions {
  display: flex;
  justify-content: flex-end;
}
</style>
