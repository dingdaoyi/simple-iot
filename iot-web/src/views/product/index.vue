<script setup>
import { useI18n } from 'vue-i18n'
import {
  Box,
  Delete,
  Edit,
  Grid,
  List,
  Monitor,
  Plus,
  RefreshRight,
  Search,
  Setting,
} from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { productDeleteApi, productPageApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/product/widget/editDia.vue'

const { t } = useI18n()
const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])
const viewMode = ref('card') // 'card' | 'table'

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  onReset,
  tableData,
  total,
  loading,
  onDelete,
  onAdd,
  onEdit,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: productDeleteApi,
  fetchApi: productPageApi,
  diaName: t('auto.product_index_a015434e'),
  defParams: {},
})

// 计算产品数据（兼容卡片和列表视图）
const products = computed(() => tableData.value || [])

function closeEdite() {
  updatePage()
}

function onAddChild(row) {
  parentId.value = row.id
  dialogVisible.value = true
  diaTitle.value = t('auto.product_index_5ff09388')
}

function tslConfig(row) {
  router.push(`/tslModel?typeId=${row.productTypeId}&productId=${row.id}`)
}

// 获取产品类型名称
function getProductTypeName(typeId) {
  const type = productTypeList.value.find(t => t.id === typeId)
  return type?.name || t('auto.product_index_485d34dc')
}

// 跳转到设备列表
function goToDevices(product) {
  router.push({
    path: '/device',
    query: { productId: product.id },
  })
}

// 处理下拉菜单命令
function handleCommand(command, product) {
  switch (command) {
    case 'config':
      tslConfig(product)
      break
    case 'edit':
      onEdit(product)
      break
    case 'delete':
      onDelete(product)
      break
  }
}

// 列表视图的列配置
const column = [
  {
    prop: 'cz',
    width: 60,
    slot: 'expand',
  },
  {
    prop: 'icon',
    label: t('auto.product_index_5ef69f62'),
    width: 80,
    slot: 'icon',
  },
  {
    prop: 'productTypeName',
    label: t('auto.product_index_2db97cae'),
    width: 150,
  },
  {
    prop: 'model',
    label: t('auto.product_index_ac4190df'),
    width: 150,
  },
  {
    prop: 'manufacturer',
    label: t('auto.product_index_8284e8da'),
    width: 150,
  },
  {
    prop: 'remark',
    label: t('auto.product_index_b57447a7'),
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 280,
    label: t('auto.product_index_2b6bc0f2'),
  },
]

onMounted(() => {
  updatePage()
  productTypeListApi().then(({ data }) => {
    productTypeList.value = data
  }).catch((err) => {
    console.error(t('auto.product_index_59c02f94'), err)
  })
})</script>

<template>
  <div class="product-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon" :size="28">
            <Box />
          </el-icon>
          {{ t('auto.product_index_fc38494c') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.product_index_2ba1a498') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-fields">
          <el-select
            v-model="params.productTypeId"
            :placeholder="t('auto.product_index_b82ec15f')"
            filterable
            clearable
            class="search-select"
          >
            <el-option
              v-for="item in productTypeList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
          <el-input
            v-model="params.model"
            clearable
            :placeholder="t('auto.product_index_7cab8429')"
            :prefix-icon="Search"
            class="search-input"
            @keyup.enter="onSearch"
          />
          <el-input
            v-model="params.manufacturer"
            clearable
            :placeholder="t('auto.product_index_19034848')"
            class="search-input"
            @keyup.enter="onSearch"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            <el-icon><Search /></el-icon>
            {{ t('auto.product_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.product_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            <el-icon><Plus /></el-icon>
            {{ t('auto.product_index_03f6134c') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 卡片视图 -->
    <div v-if="viewMode === 'card'" v-loading="loading" class="products-grid">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-card glass-card"
      >
        <!-- 卡片头部 - 图标和主要信息 -->
        <div class="card-main" @click="goToDevices(product)">
          <div class="product-icon">
            <img v-if="product.icon" :src="product.icon" :alt="product.model">
            <el-icon v-else :size="40">
              <Box />
            </el-icon>
          </div>
          <div class="product-info">
            <div class="product-model">
              {{ product.model }}
            </div>
            <div class="product-type-tag">
              <el-tag size="small" type="info">
                {{ getProductTypeName(product.productTypeId) }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 详细信息 -->
        <div class="card-details">
          <div class="detail-row">
            <span class="detail-label">{{ t('auto.product_index_8284e8da') }}</span>
            <span class="detail-value">{{ product.manufacturer || t('auto.product_index_fe2d26a2') }}</span>
          </div>
          <div v-if="product.remark" class="detail-row">
            <span class="detail-label">{{ t('auto.product_index_2432b575') }}</span>
            <span class="detail-value">{{ product.remark }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="card-actions">
          <el-button type="primary" size="small" @click="goToDevices(product)">
            <el-icon><Monitor /></el-icon>
            {{ t('auto.product_index_6d0a0414') }}
          </el-button>
          <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, product)">
            <el-button size="small">
              <el-icon><Setting /></el-icon>
              {{ t('auto.product_index_0ec9eaf9') }}
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="config">
                  <el-icon><Setting /></el-icon>{{ t('auto.product_index_c2f1f925') }}
                </el-dropdown-item>
                <el-dropdown-item command="edit">
                  <el-icon><Edit /></el-icon>{{ t('auto.product_index_95b351c8') }}
                </el-dropdown-item>
                <el-dropdown-item command="delete" divided>
                  <el-icon color="#f56c6c">
                    <Delete />
                  </el-icon>
                  <span style="color: #f56c6c">{{ t('auto.product_index_2f4aaddd') }}</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && products.length === 0" class="empty-state">
        <el-icon :size="64" color="var(--iot-color-text-muted)">
          <Box />
        </el-icon>
        <p>{{ t('auto.product_index_empty_data') }}</p>
        <el-button type="primary" @click="onAdd">
          <el-icon><Plus /></el-icon>
          {{ t('auto.product_index_add_first') }}
        </el-button>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-else class="table-wrapper glass-card">
      <IotTable
        :columns="column"
        :data="products"
        :total="total"
        :current-page="params.page"
        :page-size="params.size"
        :loading="loading"
        @page-change="onPageChange"
        @size-change="onSizeChange"
      >
        <template #expand="{ row }">
          <div class="expand-content">
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.product_index_2125533e') }}</span>
              <span class="expand-value">{{ row.id }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.product_index_116afd6d') }}</span>
              <span class="expand-value">{{ row.productTypeId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.product_index_2e540d11') }}</span>
              <span class="expand-value">{{ row.parentId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">{{ t('auto.product_index_eca37cb0') }}</span>
              <span class="expand-value">{{ row.createTime || '-' }}</span>
            </div>
          </div>
        </template>

        <template #icon="{ row }">
          <div class="table-icon">
            <img v-if="row.icon" :src="row.icon" :alt="row.model">
            <el-icon v-else :size="28">
              <Box />
            </el-icon>
          </div>
        </template>

        <template #cz="{ row }">
          <el-button type="primary" link :icon="Setting" @click="tslConfig(row)">
            {{ t('auto.product_index_c2f1f925') }}
          </el-button>
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('auto.product_index_95b351c8') }}
          </el-button>
          <el-button v-if="row.parentId === -1" type="primary" link :icon="Plus" @click="onAddChild(row)">
            {{ t('auto.product_index_c38907a1') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('auto.product_index_2f4aaddd') }}
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 视图切换按钮 -->
    <div class="view-toggle">
      <el-button-group>
        <el-button
          :type="viewMode === 'card' ? 'primary' : 'default'"
          @click="viewMode = 'card'"
        >
          <el-icon><Grid /></el-icon>
          {{ t('auto.product_index_card_view') }}
        </el-button>
        <el-button
          :type="viewMode === 'table' ? 'primary' : 'default'"
          @click="viewMode = 'table'"
        >
          <el-icon><List /></el-icon>
          {{ t('auto.product_index_list_view') }}
        </el-button>
      </el-button-group>
    </div>

    <!-- 编辑对话框 -->
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :product-type-list="productTypeList"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.product-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
  position: relative;
}

/* 页面标题 */
.page-header {
  .header-content {
    background: var(--iot-color-bg-card);
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
      color: var(--iot-color-primary);
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
    align-items: center;
    gap: var(--space-lg);
    flex-wrap: wrap;
  }

  .search-fields {
    display: flex;
    gap: var(--space-md);
    flex: 1;
    flex-wrap: wrap;
  }

  .search-select,
  .search-input {
    width: 180px;
  }

  .search-actions {
    display: flex;
    gap: var(--space-sm);
  }
}

/* 产品卡片网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-lg);
  min-height: 200px;
}

.product-card {
  transition: all var(--transition-base);
  padding: 0;
  border-radius: var(--radius-lg);
  overflow: hidden;

  &:hover {
    transform: translateY(-2px);
    box-shadow: var(--shadow-lg);
    border-color: var(--iot-color-primary);
  }

  // 主要区域 - 图标和名称
  .card-main {
    display: flex;
    align-items: center;
    gap: var(--space-md);
    padding: var(--space-lg);
    cursor: pointer;
    background: linear-gradient(135deg, rgba(99, 102, 241, 0.05) 0%, rgba(6, 182, 212, 0.05) 100%);

    &:hover {
      background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(6, 182, 212, 0.1) 100%);
    }
  }

  .product-icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-md);
    background: linear-gradient(135deg, var(--iot-color-primary) 0%, var(--iot-color-accent) 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    flex-shrink: 0;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.25);
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .product-info {
    flex: 1;
    min-width: 0;
  }

  .product-model {
    font-size: 16px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    margin-bottom: var(--space-xs);
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  .product-type-tag {
    .el-tag {
      border-radius: var(--radius-full);
    }
  }

  // 详细信息区域
  .card-details {
    padding: var(--space-md) var(--space-lg);
    border-top: 1px solid var(--iot-color-border-light);
  }

  .detail-row {
    display: flex;
    align-items: flex-start;
    margin-bottom: var(--space-xs);

    &:last-child {
      margin-bottom: 0;
    }
  }

  .detail-label {
    font-size: 13px;
    color: var(--iot-color-text-muted);
    min-width: 50px;
    flex-shrink: 0;
  }

  .detail-value {
    font-size: 13px;
    color: var(--iot-color-text-primary);
    font-weight: 500;
    word-break: break-all;
  }

  // 操作按钮区域
  .card-actions {
    display: flex;
    gap: var(--space-sm);
    padding: var(--space-md) var(--space-lg);
    border-top: 1px solid var(--iot-color-border-light);
    background: var(--iot-glass-bg);

    .el-button {
      flex: 1;
    }
  }
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 24px;
  gap: var(--space-md);
  color: var(--iot-color-text-muted);
  background: var(--iot-glass-bg);
  border-radius: var(--radius-lg);
  border: 2px dashed var(--iot-color-border);

  p {
    font-size: 15px;
    margin: 0;
  }
}

/* 表格容器 */
.table-wrapper {
  flex: 1;
}

/* 表格图标 */
.table-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: linear-gradient(135deg, var(--iot-color-primary) 0%, var(--iot-color-accent) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
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

/* 视图切换按钮 */
.view-toggle {
  position: fixed;
  bottom: var(--space-xl);
  right: var(--space-xl);
  z-index: 100;

  .el-button {
    padding: var(--space-sm) var(--space-md);
  }
}

/* 响应式 */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  }
}

@media (max-width: 768px) {
  .product-page {
    padding: var(--space-md);
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;
  }

  .search-fields {
    flex-direction: column;

    .search-select,
    .search-input {
      width: 100%;
    }
  }

  .search-actions {
    width: 100%;

    .el-button {
      flex: 1;
    }
  }

  .products-grid {
    grid-template-columns: 1fr;
  }

  .view-toggle {
    bottom: var(--space-md);
    right: var(--space-md);
  }

  .page-title {
    font-size: 20px !important;
  }

  .product-card {
    .card-main {
      padding: var(--space-md);
    }

    .card-details {
      padding: var(--space-sm) var(--space-md);
    }

    .card-actions {
      padding: var(--space-sm) var(--space-md);
    }
  }
}
</style>