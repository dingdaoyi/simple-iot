<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <dw-select
        v-model="params.productTypeId"
        placeholder="请选择产品类型"
        class="w-200px mr-12px"
        filterable
        clearable
      >
        <dw-option
          v-for="item in productTypeList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </dw-select>
      <div class="w-200px mr-12px">
        <el-input v-model="params.model" clearable placeholder="请输入产品型号" />
      </div>

      <div class="w-200px mr-12px">
        <el-input v-model="params.manufacturer" clearable placeholder="请输厂家名称" />
      </div>
      <el-button type="primary" @click="onSearch">搜索</el-button>
      <el-button type="success" @click="onAdd">添加</el-button>
    </div>
    <DwTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :api="productPageApi"
    >
      <template #cz="{ row }">
        <dw-button type="danger" link @click="onDelete(row)">删除</dw-button>
        <dw-button type="primary" link @click="tslConfig(row)">功能配置</dw-button>
        <dw-button v-if="row.parentId===-1" type="primary" link @click="onAddChild(row)">添加子级</dw-button>
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
<script setup>
import { dwHooks } from 'dwyl-ui'
import { productDeleteApi, productPageApi, productTypeListApi } from '@/api/index.js'
import EditDia from '@/views/product/widget/editDia.vue'
import { nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'

const { useDwTable } = dwHooks

const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])
const column = [
  {
    prop: 'cz',
    width: 70,
    slot: 'expand'
  },
  {
    prop: 'productTypeName',
    label: '产品类型'
  },
  {
    prop: 'model',
    label: '型号'
  },
  {
    prop: 'manufacturer',
    label: '厂家'
  },
  {
    prop: 'mark',
    label: '备注信息'
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: '操作'
  }
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
  currentItem
} = useDwTable({
  deleteApi: productDeleteApi,
  diaName: '协议',
  defParams: {
  }
})

function closeEdite () {
  updatePage()
}

function onAddChild (row) {
  parentId.value = row.id
  nextTick(() => {
    dialogVisible.value = true
  })
}
const tslConfig = (row) => {
  router.push(`/tslModel?typeId=${row.productTypeId}&productId=${row.id}`)
}
productTypeListApi()
  .then(({ data }) => {
    productTypeList.value = data
  })
</script>
