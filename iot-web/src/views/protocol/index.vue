<script setup>
import { protocolDeleteApi, protocolListApi } from '@/api/index.js'
import EditDia from '@/views/protocol/widget/editDia.vue'
import { dwHooks } from 'dwyl-ui'

const { useDwTable } = dwHooks

const protocolTypeOpt = [
  {
    label: 'JAVA',
    value: 1,
  },
  {
    label: '系统默认',
    value: 2,
  },
  {
    label: 'JAVASCRIPT',
    value: 3,
  },
]
const column = [
  {
    prop: 'name',
    label: '协议名称',
  },
  {
    prop: 'protoType',
    label: '协议类型',
    formatter(row) {
      return protocolTypeOpt
        .find(item => item.value === row.protoType)
        ?.label
    },
  },
  {
    prop: 'protoKey',
    label: '协议protoKey',
  },
  {
    prop: 'handlerClass',
    label: '处理入口',
  },
  {
    prop: 'mark',
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
  diaTitle,
  currentItem,
} = useDwTable({
  deleteApi: protocolDeleteApi,
  diaName: '协议',
  defParams: {
  },
})

function closeEdite() {
  updatePage()
}
</script>

<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <dw-select
        v-model="params.productTypeId"
        placeholder="请选择协议类型"
        class="w-200px mr-12px"
        filterable
        clearable
      >
        <dw-option
          v-for="item in protocolTypeOpt"
          :key="item.value"
          :label="item.label"
          :value="item.value"
        />
      </dw-select>
      <div class="w-200px mr-12px">
        <el-input v-model="params.model" clearable placeholder="请输入产品型号" />
      </div>

      <div class="w-200px mr-12px">
        <el-input v-model="params.manufacturer" clearable placeholder="请输厂家名称" />
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
      :api="protocolListApi"
    >
      <template #cz="{ row }">
        <dw-button v-if="row.protoType !== 2" type="danger" link @click="onDelete(row)">
          删除
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
