<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable placeholder="请输入属性名称或标识符搜索" />
      </div>
      <el-button type="primary" @click="onSearch">搜索</el-button>
      <el-button type="success" @click="onAdd">添加</el-button>
    </div>
    <DwTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :isPage="false"
      :api="propertyListApi"
    >
      <template #cz="{ row }">
        <dw-button type="danger" link @click="onDelete(row)">删除</dw-button>
        <dw-button type="primary" link @click="onEdit(row)">编辑定义</dw-button>
      </template>
    </DwTable>
    <property-edite
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :type-id="typeId"
      :datas="currentItem"
      @update="updatePage"
    />
  </div>
</template>
<script setup>
import { propertyDeleteApi, propertyListApi } from '@/api/index.js'
import { dwHooks } from 'dwyl-ui'
import PropertyEdite from '@/views/tslModel/widget/propertyEdite.vue'

const { useDwTable } = dwHooks

const props = defineProps(['typeId'])

const column = [

  {
    prop: 'name',
    label: '属性名称'
  },
  {
    prop: 'identifier',
    label: '属性标识'
  },
  {
    prop: 'id',
    label: '属性ID'
  },
  {
    prop: 'dataTypeName',
    label: '数据类型'
  },
  {
    prop: 'definitions',
    label: '数据定义'
  },
  {
    prop: 'accessMode',
    label: '访问权限',
    formatter (row) {
      return row.accessMode === 'r' ? '只读' : '读写'
    }
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
  onEdit,
  onAdd,
  diaTitle,
  currentItem
} = useDwTable({
  deleteApi: propertyDeleteApi,
  diaName: '属性',
  defParams: {
    productTypeId: props.typeId
  }
})
</script>

<style scoped lang="scss">

</style>
