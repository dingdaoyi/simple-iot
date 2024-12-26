<script setup>
import { messageReceiveDeleteApi, messageReceivePageApi } from '@/api/index.js'
import EditDia from '@/views/system/messageReceive/widget/editDia.vue'
import { dwHooks } from 'dwyl-ui'

const { useDwTable } = dwHooks
const notifyTypeOpt = [
  {
    label: '邮件',
    value: 1,
  },
  {
    label: '短信',
    value: 2,
  },
  {
    label: '电话',
    value: 3,
  },
]
const column = [
  {
    prop: 'name',
    label: '接收名称',
  },
  {
    prop: 'notifyType',
    label: '通知类型',
    formatter(row) {
      return notifyTypeOpt
        .find(item => item.value === row.notifyType)
        ?.label
    },
  },
  {
    prop: 'receiver',
    label: '接收号码',
  },
  {
    prop: 'remark',
    label: '备注',
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
  onEdit,
  diaTitle,
  currentItem,
} = useDwTable({
  deleteApi: messageReceiveDeleteApi,
  diaName: '消息通知',
  defParams: {},
})

function closeEdite() {
  updatePage()
}
</script>

<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <dw-select
        v-model="params.notifyType"
        placeholder="请选择通知类型"
        class="w-200px mr-12px"
        filterable
        clearable
      >
        <dw-option
          v-for="item in notifyTypeOpt"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </dw-select>
      <div class="w-200px mr-12px">
        <el-input v-model="params.name" clearable placeholder="请输入通知名称" />
      </div>
      <div class="w-200px mr-12px">
        <el-input v-model="params.receiver" clearable placeholder="电话/邮箱搜索" />
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
      :api="messageReceivePageApi"
    >
      <template #cz="{ row }">
        <dw-button type="danger" link @click="onDelete(row)">
          删除
        </dw-button>
        <dw-button type="primary" link @click="onEdit(row)">
          编辑
        </dw-button>
      </template>
    </DwTable>
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>
