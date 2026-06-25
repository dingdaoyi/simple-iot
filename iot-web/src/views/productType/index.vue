<script setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, Plus, RefreshRight, Setting } from '@element-plus/icons-vue'
import { nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'
import { productTypeDelApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/productType/widget/editDia.vue'

const { t } = useI18n()
const router = useRouter()
const parentId = ref(-1)
const iotTableRef = ref()

const column = [
  {
    prop: 'cz',
    width: 60,
    slot: 'expand',
  },
  {
    prop: 'name',
    label: t('auto.producttype_index_11382905'),
  },
  {
    prop: 'status',
    label: t('auto.producttype_index_3fea7ca7'),
    width: 100,
    formatter(row) {
      return row.status ? t('auto.producttype_index_7854b52a') : t('auto.producttype_index_710ad08b')
    },
  },
  {
    prop: 'mark',
    label: t('auto.producttype_index_b57447a7'),
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 280,
    label: t('auto.producttype_index_2b6bc0f2'),
  },
]

const {
  params,
  dialogVisible,
  onDelete,
  onAdd,
  onEdit,
  diaTitle,
  currentItem,
} = useTable({
  deleteApi: productTypeDelApi,
  fetchApi: productTypeListApi,
  diaName: t('auto.producttype_index_2db97cae'),
  defParams: {
    withChild: true,
  },
})

// 搜索时刷新 IotTable
function onSearch() {
  params.page = 1
  iotTableRef.value?.refresh()
}

function onReset() {
  params.name = undefined
  params.page = 1
  iotTableRef.value?.refresh()
}

function closeEdite() {
  iotTableRef.value?.refresh()
}

function onAddChild(row) {
  parentId.value = row.id
  nextTick(() => {
    dialogVisible.value = true
  })
}

function tslConfig(row) {
  router.push(`/tslModel?typeId=${row.id}`)
}</script>

<template>
  <div class="product-type-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">◈</span>
          {{ t('auto.producttype_index_a4d2e3eb') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.producttype_index_bc20ea4a') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-input">
          <label class="input-label">{{ t('auto.producttype_index_1cb81449') }}</label>
          <el-input
            v-model="params.name"
            clearable
            :placeholder="t('auto.producttype_index_688d8509')"
            prefix-icon="Search"
            @keyup.enter="onSearch"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('auto.producttype_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.producttype_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            <span class="btn-icon">+</span>
            {{ t('auto.producttype_index_13c0562f') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <IotTable
        ref="iotTableRef"
        row-key="id"
        :column="column"
        :params="params"
        :is-page="false"
        :api="productTypeListApi"
      >
        <template #expand="{ row }">
          <div class="expand-content">
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.producttype_index_116afd6d') }}</span>
              <span class="expand-value">{{ row.id }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.producttype_index_8867216a') }}</span>
              <span class="expand-value">{{ row.partTypeCode || '-' }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.producttype_index_2e540d11') }}</span>
              <span class="expand-value">{{ row.parentId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.producttype_index_d332d16b') }}</span>
              <span class="expand-value">{{ row.children?.length || 0 }}</span>
            </div>
          </div>
        </template>

        <template #cz="{ row }">
          <el-button type="primary" link :icon="Setting" @click="tslConfig(row)">
            {{ t('auto.producttype_index_c2f1f925') }}
          </el-button>
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('auto.producttype_index_95b351c8') }}
          </el-button>
          <el-button v-if="row.parentId === -1" type="primary" link :icon="Plus" @click="onAddChild(row)">
            {{ t('auto.producttype_index_c38907a1') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('auto.producttype_index_2f4aaddd') }}
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