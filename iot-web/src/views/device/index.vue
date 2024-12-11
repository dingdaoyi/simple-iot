<script  setup>
import { deviceDeleteApi, devicePageApi, manufacturerListApi, productListApi, productTypeListApi } from '@/api/index.js'
import { activateOpts, onlineOpts } from '@/utils/base.jsx'
import EditDia from '@/views/device/widget/editDia.vue'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const { useDwTable } = dwHooks

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
  dwTable,
  onDelete,
  onAdd,
  diaTitle,
  currentItem,
  onEdit,
} = useDwTable({
  deleteApi: deviceDeleteApi,
  diaName: '设备',
  defParams: {
    parentId: parentId.value,
  },
})

function closeEdite() {
  updatePage()
}

function showDetails(row) {
  console.log('设备详情', row.id)
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
productTypeListApi()
  .then(({ data }) => {
    productTypeList.value = data
  })
</script>

<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <dw-select
        v-model="params.productTypeId"
        placeholder="请选择产品类型"
        class="w-200px mr-12px"
        filterable
        clearable
        @change="changeProductType"
      >
        <dw-option
          v-for="item in productTypeList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </dw-select>
      <dw-select
        v-model="params.manufacturer"
        placeholder="请选择厂家"
        class="w-200px mr-12px"
        filterable
        clearable
        @change="changeManufacturer"
      >
        <dw-option
          v-for="item in manufacturerListOpt"
          :key="item"
          :label="item"
          :value="item"
        />
      </dw-select>
      <dw-select
        v-model="params.productId"
        placeholder="请选择型号"
        class="w-200px mr-12px"
        filterable
        clearable
      >
        <dw-option
          v-for="item in productListOpt"
          :key="item.id"
          :label="item.model"
          :value="item.id"
        />
      </dw-select>
      <div class="w-200px mr-12px">
        <el-input v-model="params.deviceKey" clearable placeholder="设备编号模糊搜索" />
      </div>
      <el-button type="primary" @click="onSearch">
        搜索
      </el-button>
      <el-button type="success" @click="onAdd">
        添加
      </el-button>
    </div>
    <DwTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :api="devicePageApi"
    >
      <template #cz="{ row }">
        <dw-button type="danger" link @click="onDelete(row)">
          删除
        </dw-button>
        <dw-button type="primary" link @click="onEdit(row)">
          编辑
        </dw-button>
        <dw-button type="primary" link @click="showDetails(row)">
          设备详情
        </dw-button>
      </template>
    </DwTable>
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
