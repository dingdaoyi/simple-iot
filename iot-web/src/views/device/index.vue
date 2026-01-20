<script setup>
import { deviceDeleteApi, devicePageApi, manufacturerListApi, productListApi, productTypeListApi } from '@/api/index.js'
import { activateOpts, onlineOpts } from '@/utils/base.jsx'
import EditDia from '@/views/device/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import IotTable from '@/components/IotTable.vue'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])
const manufacturerListOpt = ref([])
const productListOpt = ref([])

const column = [
  {
    prop: 'productTypeName',
    label: '产品类型',
  },
  {
    prop: 'productModel',
    label: '型号',
  },
  {
    prop: 'manufacturer',
    label: '厂家',
  },
  {
    prop: 'deviceName',
    label: '设备名称',
  },
  {
    prop: 'deviceKey',
    label: '设备编号',
  },
  {
    prop: 'online',
    label: '在线状态',
    render({ row }) {
      const data = onlineOpts.find(o => o.value === row.online)
      return data?.render() || '-'
    },
  },
  {
    prop: 'activeStatus',
    label: '激活状态',
    render({ row }) {
      const data = activateOpts.find(o => o.value === row.activeStatus)
      return data?.render() || '-'
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
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
    <!-- 搜索栏 -->
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="产品类型">
          <el-select
            v-model="params.productTypeId"
            placeholder="请选择产品类型"
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
            placeholder="请选择厂家"
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
            placeholder="请选择型号"
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
          <el-input v-model="params.deviceKey" clearable placeholder="设备编号模糊搜索" />
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

    <!-- 表格 -->
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
        <el-button type="danger" link @click="onDelete(row)">
          删除
        </el-button>
        <el-button type="primary" link @click="onEdit(row)">
          编辑
        </el-button>
        <el-button type="primary" link @click="showDetails(row)">
          设备详情
        </el-button>
      </template>
    </IotTable>

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
</style>
