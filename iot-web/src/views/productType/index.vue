<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <div class="w-200px mr-12px">
        <el-input v-model="params.name" clearable placeholder="请输入产品类型名称" />
      </div>
      <el-button type="primary" @click="onSearch">搜索</el-button>
      <el-button type="success" @click="onAdd">添加</el-button>
    </div>
    <DwTable
        ref="dwTable"
        :column="column"
        :params="params"
        :isPage="false"
        :api="productTypeListApi"
    >
      <template #cz="{ row }">
        <dw-button type="primary" link @click="onEdit(row)">编辑</dw-button>
        <dw-button type="danger" link @click="onDelete(row)">删除</dw-button>
      </template>
    </DwTable>
  </div>
</template>
<script setup>
import { dwHooks } from 'dwyl-ui'
import {productTypeListApi} from "@/api/index.js";
const { useDwTable } = dwHooks


const column = [
  {
    type:'selection',
  },
  {
    prop: 'name',
    label: '品类名称'
  },
  {
    prop: 'status',
    label: '状态',
    formatter (row) {
      return row.status?'启用':'禁用'
    }
  },
  {
    prop: 'mark',
    label: '备注信息'
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 120,
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

})

</script>