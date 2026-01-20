<script setup>
import { productTypeDelApi, productTypeListApi } from '@/api/index.js'
import EditDia from '@/views/productType/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import { nextTick, ref } from 'vue'
import { useRouter } from 'vue-router'


const router = useRouter()
const parentId = ref(-1)
const column = [
  {
    prop: 'cz',
    width: 70,
    slot: 'expand',
  },
  {
    prop: 'name',
    label: '品类名称',
  },
  {
    prop: 'status',
    label: '状态',
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
    width: 220,
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
  diaTitle,
  currentItem,
} = useTable({
  deleteApi: productTypeDelApi,
  diaName: '用户',
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
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <div class="w-200px mr-12px">
        <el-input v-model="params.name" clearable placeholder="请输入产品类型名称" />
      </div>
      <el-button type="primary" @click="onSearch">
        搜索
      </el-button>
      <el-button type="success" @click="onAdd">
        添加
      </el-button>
    </div>
    <IotTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :is-page="false"
      :api="productTypeListApi"
    >
      <template #cz="{ row }">
        <!--        <el-button type="primary" link @click="onEdit(row)">编辑</el-button> -->
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
