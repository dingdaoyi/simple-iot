<script setup>
import { productDeleteApi, productPageApi, productTypeListApi } from '@/api/index.js'
import EditDia from '@/views/product/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import { nextTick, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const parentId = ref(-1)
const productTypeList = ref([])

const column = [
  {
    prop: 'cz',
    width: 70,
    slot: 'expand',
  },
  {
    prop: 'productTypeName',
    label: '产品类型',
  },
  {
    prop: 'model',
    label: '型号',
  },
  {
    prop: 'manufacturer',
    label: '厂家',
  },
  {
    prop: 'remark',
    label: '备注信息',
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
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: productDeleteApi,
  fetchApi: productPageApi,
  diaName: '产品',
  defParams: {},
})

function closeEdite() {
  updatePage()
}

function onAddChild(row) {
  parentId.value = row.id
  nextTick(() => {
    dialogVisible.value = true
    diaTitle.value = '添加子产品'
  })
}

function tslConfig(row) {
  router.push(`/tslModel?typeId=${row.productTypeId}&productId=${row.id}`)
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
  <div class="product-page">
    <!-- 搜索栏 -->
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="产品类型">
          <el-select
            v-model="params.productTypeId"
            placeholder="请选择产品类型"
            filterable
            clearable
          >
            <el-option
              v-for="item in productTypeList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="产品型号">
          <el-input v-model="params.model" clearable placeholder="请输入产品型号" />
        </el-form-item>

        <el-form-item label="厂家名称">
          <el-input v-model="params.manufacturer" clearable placeholder="请输入厂家名称" />
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
        <el-button type="primary" link @click="tslConfig(row)">
          功能配置
        </el-button>
        <el-button v-if="row.parentId === -1" type="primary" link @click="onAddChild(row)">
          添加子级
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
.product-page {
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
