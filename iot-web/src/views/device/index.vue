<script setup>
import { Cpu, Delete, Download, Edit, Monitor, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, h, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import { batchDeviceServiceApi, deviceDeleteApi, devicePageApi, manufacturerListApi, productListApi, productTypeListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useTable } from '@/composables/useTable.js'
import { activateOpts, onlineOpts } from '@/utils/base.jsx'
import EditDia from '@/views/device/widget/editDia.vue'

const router = useRouter()
const { t } = useI18n()
const parentId = ref(-1)
const productTypeList = ref([])
const manufacturerListOpt = ref([])
const productListOpt = ref([])

const column = computed(() => [
  {
    type: 'selection',
    width: 55,
  },
  {
    prop: 'productTypeName',
    label: t('product.product_type'),
  },
  {
    prop: 'productModel',
    label: t('device.product_model'),
  },
  {
    prop: 'manufacturer',
    label: t('device.manufacturer'),
  },
  {
    prop: 'deviceName',
    label: t('device.device_name'),
  },
  {
    prop: 'deviceKey',
    label: t('device.device_key'),
    width: 180,
  },
  {
    prop: 'online',
    label: t('device.online_status'),
    width: 120,
    render({ row }) {
      const data = onlineOpts.find(o => o.value === row.online)
      if (!data)
        return '-'
      return h('div', { style: { color: data.color } }, row.online ? t('device.online') : t('device.offline'))
    },
  },
  {
    prop: 'activeStatus',
    label: t('device.active_status'),
    width: 120,
    render({ row }) {
      const data = activateOpts.find(o => o.value === row.activeStatus)
      if (!data)
        return '-'
      return h('div', { style: { color: data.color } }, row.activeStatus ? t('device.activated') : t('device.not_activated'))
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('common.operation'),
  },
])

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
  onReset,
  diaTitle,
  currentItem,
  onEdit,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: deviceDeleteApi,
  fetchApi: devicePageApi,
  diaName: t('device.device'),
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

// 批量指令
const batchDialogVisible = ref(false)
const batchForm = ref({ identifier: '', params: '{}' })
const batchLoading = ref(false)
const selectedRows = ref([])

function onBatchCommand() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请先勾选设备')
    return
  }
  batchForm.value = { identifier: '', params: '{}' }
  batchDialogVisible.value = true
}

async function onBatchSend() {
  if (!batchForm.value.identifier) {
    ElMessage.warning('请输入服务标识符')
    return
  }
  let parsedParams = {}
  try {
    parsedParams = JSON.parse(batchForm.value.params)
  } catch {
    ElMessage.warning('参数 JSON 格式错误')
    return
  }
  batchLoading.value = true
  try {
    const deviceKeys = selectedRows.value.map(r => r.deviceKey)
    await batchDeviceServiceApi(batchForm.value.identifier, { deviceKeys, params: parsedParams })
    ElMessage.success(`已向 ${deviceKeys.length} 台设备发送指令`)
    batchDialogVisible.value = false
  } catch (e) {
    ElMessage.error('发送失败')
  } finally {
    batchLoading.value = false
  }
}

function changeProductType() {
  params.manufacturer = undefined
  params.productId = undefined
  productListOpt.value = []

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
  params.productId = undefined

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

function resetFilters() {
  manufacturerListOpt.value = []
  productListOpt.value = []
  onReset()
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
    <PageHeader
      :title="t('menu.device')"
      :subtitle="t('device.page_subtitle')"
      :icon="Cpu"
    />

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('product.product_type')">
            <el-select
              v-model="params.productTypeId"
              :placeholder="t('common.placeholder_select', { field: t('product.product_type') })"
              filterable
              clearable
              @change="changeProductType"
            >
              <el-option
                v-for="item in productTypeList.filter(p => p.status === 1 || p.status === undefined)"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item :label="t('device.manufacturer')">
            <el-select
              v-model="params.manufacturer"
              :placeholder="t('common.placeholder_select', { field: t('device.manufacturer') })"
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

          <el-form-item :label="t('device.product_model')">
            <el-select
              v-model="params.productId"
              :placeholder="t('common.placeholder_select', { field: t('device.product_model') })"
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

          <el-form-item :label="t('device.device_key')">
            <el-input v-model="params.deviceKey" clearable :placeholder="t('common.placeholder_input', { field: t('device.device_key') })" @keyup.enter="onSearch" />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('common.search') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="resetFilters">
            {{ t('common.reset') }}
          </el-button>
          <el-button :icon="Download" @click="() => window.open('/iot/device/export')">
            导出 Excel
          </el-button>
          <el-button type="primary" @click="onAdd">
            <span class="btn-icon">+</span>
            {{ t('device.add_device') }}
          </el-button>
          <el-button type="warning" @click="onBatchCommand" :disabled="selectedRows.length === 0">
            批量指令 ({{ selectedRows.length }})
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
        @selection-change="(val) => selectedRows = val"
      >
        <template #cz="{ row }">
          <el-button type="primary" link :icon="Monitor" @click="showDetails(row)">
            {{ t('device.device_detail') }}
          </el-button>
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('common.edit') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('common.delete') }}
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

    <!-- 批量指令对话框 -->
    <el-dialog v-model="batchDialogVisible" title="批量设备指令" width="500px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="设备数量">
          <el-text>{{ selectedRows.length }} 台设备</el-text>
        </el-form-item>
        <el-form-item label="服务标识符">
          <el-input v-model="batchForm.identifier" placeholder="如：setSwitch" />
        </el-form-item>
        <el-form-item label="服务参数">
          <el-input v-model="batchForm.params" type="textarea" :rows="4" placeholder='{"value": 1}' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="onBatchSend">发送</el-button>
      </template>
    </el-dialog>
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
