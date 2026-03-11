<script setup>
import {
  Box,
  Delete,
  Edit,
  Grid,
  List,
  Monitor,
  Plus,
  Search,
  Setting,
} from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { productDeleteApi, productPageApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/product/widget/editDia.vue'

const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])
const viewMode = ref('card') // 'card' | 'table'

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
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
  diaName: '产品',
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
  diaTitle.value = '添加子产品'
}

function tslConfig(row) {
  router.push(`/tslModel?typeId=${row.productTypeId}&productId=${row.id}`)
}

// 获取产品类型名称
function getProductTypeName(typeId) {
  const type = productTypeList.value.find(t => t.id === typeId)
  return type?.name || '未知类型'
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
    label: '图标',
    width: 80,
    slot: 'icon',
  },
  {
    prop: 'productTypeName',
    label: '产品类型',
    width: 150,
  },
  {
    prop: 'model',
    label: '型号',
    width: 150,
  },
  {
    prop: 'manufacturer',
    label: '厂家',
    width: 150,
  },
  {
    prop: 'remark',
    label: '备注信息',
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 280,
    label: '操作',
  },
]

onMounted(() => {
  updatePage()
  productTypeListApi().then(({ data }) => {
    productTypeList.value = data
  }).catch((err) => {
    console.error('获取产品类型失败:', err)
  })
})
</script>

<template>
  <div class="product-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon class="title-icon" :size="28">
            <Box />
          </el-icon>
          产品管理
        </h1>
        <p class="page-subtitle">
          管理物联网设备产品信息与配置
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-fields">
          <el-select
            v-model="params.productTypeId"
            placeholder="选择产品类型"
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
            placeholder="输入产品型号"
            :prefix-icon="Search"
            class="search-input"
          />
          <el-input
            v-model="params.manufacturer"
            clearable
            placeholder="输入厂家名称"
            class="search-input"
          />
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <el-icon><Plus /></el-icon>
            添加产品
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
            <span class="detail-label">厂家</span>
            <span class="detail-value">{{ product.manufacturer || '未设置' }}</span>
          </div>
          <div v-if="product.remark" class="detail-row">
            <span class="detail-label">备注</span>
            <span class="detail-value">{{ product.remark }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="card-actions">
          <el-button type="primary" size="small" @click="goToDevices(product)">
            <el-icon><Monitor /></el-icon>
            查看设备
          </el-button>
          <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, product)">
            <el-button size="small">
              <el-icon><Setting /></el-icon>
              更多
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="config">
                  <el-icon><Setting /></el-icon>功能配置
                </el-dropdown-item>
                <el-dropdown-item command="edit">
                  <el-icon><Edit /></el-icon>编辑
                </el-dropdown-item>
                <el-dropdown-item command="delete" divided>
                  <el-icon color="#f56c6c">
                    <Delete />
                  </el-icon>
                  <span style="color: #f56c6c">删除</span>
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
        <p>暂无产品数据</p>
        <el-button type="primary" @click="onAdd">
          <el-icon><Plus /></el-icon>
          添加第一个产品
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
              <span class="expand-label">产品 ID</span>
              <span class="expand-value">{{ row.id }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">类型 ID</span>
              <span class="expand-value">{{ row.productTypeId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">父级 ID</span>
              <span class="expand-value">{{ row.parentId }}</span>
            </div>
            <div class="expand-item">
              <span class="expand-label">创建时间</span>
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

    <!-- 视图切换按钮 -->
    <div class="view-toggle">
      <el-button-group>
        <el-button
          :type="viewMode === 'card' ? 'primary' : 'default'"
          @click="viewMode = 'card'"
        >
          <el-icon><Grid /></el-icon>
          卡片
        </el-button>
        <el-button
          :type="viewMode === 'table' ? 'primary' : 'default'"
          @click="viewMode = 'table'"
        >
          <el-icon><List /></el-icon>
          列表
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
