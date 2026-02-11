<script setup>
import { nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'
import { productTypeDelApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/productType/widget/editDia.vue'

const router = useRouter()
const parentId = ref(-1)

const column = [
  {
    prop: 'cz',
    width: 60,
    slot: 'expand',
  },
  {
    prop: 'name',
    label: '品类名称',
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    formatter(row) {
      return row.status ? '启用' : '禁用'
    },
  },
  {
    prop: 'mark',
    label: '备注信息',
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 280,
    label: '操作',
  },
]

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  onDelete,
  onAdd,
  onEdit,
  diaTitle,
  currentItem,
} = useTable({
  deleteApi: productTypeDelApi,
  fetchApi: productTypeListApi,
  diaName: '产品类型',
  defParams: {
    withChild: true,
  },
})

function closeEdite() {
  updatePage()
}

function onAddChild(row) {
  parentId.value = row.id
  nextTick(() => {
    dialogVisible.value = true
  })
}

function tslConfig(row) {
  router.push(`/tslModel?typeId=${row.id}`)
}
</script>

<template>
  <div class="product-type-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">◈</span>
          产品类型管理
        </h1>
        <p class="page-subtitle">
          管理产品分类与层级结构
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-input">
          <label class="input-label">搜索类型</label>
          <el-input
            v-model="params.name"
            clearable
            placeholder="输入产品类型名称搜索..."
            prefix-icon="Search"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <span class="btn-icon">+</span>
            添加类型
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <IotTable
        row-key="id"
        :column="column"
        :params="params"
        :is-page="false"
        :api="productTypeListApi"
      >
        <template #expand="{ row }">
          <div class="expand-content">
            <div class="expand-item">
              <span class="expand-label">类型 ID</span>
              <span class="expand-value">{{ row.id }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">类型代码</span>
              <span class="expand-value">{{ row.partTypeCode || '-' }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">父级 ID</span>
              <span class="expand-value">{{ row.parentId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">子级数量</span>
              <span class="expand-value">{{ row.children?.length || 0 }}</span>
            </div>
          </div>
        </template>

        <template #cz="{ row }">
          <el-button type="primary" link @click="tslConfig(row)">
            功能配置
          </el-button>
          <el-button type="primary" link @click="onEdit(row)">
            编辑
          </el-button>
          <el-button v-if="row.parentId === -1" type="success" link @click="onAddChild(row)">
            添加子级
          </el-button>
          <el-button type="danger" link @click="onDelete(row)">
            删除
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 编辑对话框 -->
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :parent-id="parentId"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.product-type-page {
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

/* 表格容器 */
.table-wrapper {
  flex: 1;
}

/* 展开内容 */
.expand-content {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-lg);
  padding: var(--space-md);
}

.expand-item {
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);

  .expand-label {
    font-size: 12px;
    color: var(--iot-color-text-muted);
    text-transform: uppercase;
    letter-spacing: 0.05em;
  }

  .expand-value {
    font-size: 14px;
    font-weight: 500;
    color: var(--iot-color-text-primary);
    font-family: 'Fira Code', monospace;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .product-type-page {
    padding: var(--space-md);
  }

  .search-row {
    flex-direction: column;

    .search-input,
    .search-actions {
      width: 100%;
    }

    .search-actions {
      flex-direction: column;

      .el-button {
        width: 100%;
      }
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
