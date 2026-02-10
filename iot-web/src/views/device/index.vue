<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deviceDeleteApi, devicePageApi, manufacturerListApi, productListApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import { activateOpts, onlineOpts } from '@/utils/base.jsx'
import EditDia from '@/views/device/widget/editDia.vue'

const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])
const manufacturerListOpt = ref([])
const productListOpt = ref([])

const column = [
  {
    prop: 'productTypeName',
    label: '产品类型',
    width: 140,
  },
  {
    prop: 'productModel',
    label: '型号',
    width: 140,
  },
  {
    prop: 'manufacturer',
    label: '厂家',
    width: 120,
  },
  {
    prop: 'deviceName',
    label: '设备名称',
    width: 160,
  },
  {
    prop: 'deviceKey',
    label: '设备编号',
    width: 180,
  },
  {
    prop: 'online',
    label: '在线状态',
    width: 100,
    render({ row }) {
      const data = onlineOpts.find(o => o.value === row.online)
      return data?.render() || '-'
    },
  },
  {
    prop: 'activeStatus',
    label: '激活状态',
    width: 100,
    render({ row }) {
      const data = activateOpts.find(o => o.value === row.activeStatus)
      return data?.render() || '-'
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 240,
    label: '操作',
  },
]

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
  diaTitle,
  currentItem,
  onEdit,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: deviceDeleteApi,
  fetchApi: devicePageApi,
  diaName: '设备',
  defParams: {
    parentId: parentId.value,
  },
})

function closeEdite() {
  updatePage()
}

function showDetails(row) {
  router.push(`/deviceDetails?id=${row.id}`)
}

function changeProductType() {
  if (params.productTypeId) {
    manufacturerListApi({
      productTypeId: params.productTypeId,
    })
      .then(({ data }) => {
        manufacturerListOpt.value = data
      })
  }
  else {
    manufacturerListOpt.value = []
  }
}

function changeManufacturer() {
  if (!params.manufacturer) {
    productListOpt.value = []
    return
  }
  productListApi({
    productTypeId: params.productTypeId,
    manufacturer: params.manufacturer,
  })
    .then(({ data }) => {
      productListOpt.value = data
    })
}

onMounted(() => {
  updatePage()
  productTypeListApi()
    .then(({ data }) => {
      productTypeList.value = data
    })
})
</script>

<template>
  <div class="device-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">◈</span>
          设备管理
        </h1>
        <p class="page-subtitle">
          管理物联网设备连接与状态监控
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item label="产品类型">
            <el-select
              v-model="params.productTypeId"
              placeholder="选择产品类型"
              filterable
              clearable
              @change="changeProductType"
            >
              <el-option
                v-for="item in productTypeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="厂家">
            <el-select
              v-model="params.manufacturer"
              placeholder="选择厂家"
              filterable
              clearable
              @change="changeManufacturer"
            >
              <el-option
                v-for="item in manufacturerListOpt"
                :key="item"
                :label="item"
                :value="item"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="型号">
            <el-select
              v-model="params.productId"
              placeholder="选择型号"
              filterable
              clearable
            >
              <el-option
                v-for="item in productListOpt"
                :key="item.id"
                :label="item.model"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="设备编号">
            <el-input v-model="params.deviceKey" clearable placeholder="输入设备编号" />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <span class="btn-icon">+</span>
            添加设备
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <IotTable
        :columns="column"
        :data="tableData"
        :total="total"
        :current-page="params.page"
        :page-size="params.size"
        :loading="loading"
        @page-change="onPageChange"
        @size-change="onSizeChange"
      >
        <template #cz="{ row }">
          <el-button type="primary" link @click="showDetails(row)">
            设备详情
          </el-button>
          <el-button type="primary" link @click="onEdit(row)">
            编辑
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
      :product-type-list="productTypeList"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.device-page {
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
  .search-form {
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
  }

  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-md);

    .el-form-item {
      flex: 1;
      min-width: 180px;
      margin-bottom: 0;
    }
  }

  .form-actions {
    display: flex;
    gap: var(--space-sm);
    justify-content: flex-end;
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

/* 响应式 */
@media (max-width: 768px) {
  .device-page {
    padding: var(--space-md);
  }

  .form-row {
    flex-direction: column;

    .el-form-item {
      width: 100%;
    }
  }

  .form-actions {
    width: 100%;
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
