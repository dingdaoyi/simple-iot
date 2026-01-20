<script setup>
import { messageReceiveDeleteApi, messageReceivePageApi } from '@/api/index.js'
import EditDia from '@/views/system/messageReceive/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import IotTable from '@/components/IotTable.vue'
import { onMounted } from 'vue'

const notifyTypeOpt = [
  {
    label: '邮件',
    value: 1,
  },
  {
    label: '短信',
    value: 2,
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
    render({ row }) {
      return notifyTypeOpt
        .find(item => item.value === row.notifyType)
        ?.label || '-'
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
    width: 150,
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
  onEdit,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: messageReceiveDeleteApi,
  fetchApi: messageReceivePageApi,
  diaName: '消息通知',
  defParams: {},
})

function closeEdite() {
  updatePage()
}

onMounted(() => {
  updatePage()
})
</script>

<template>
  <div class="message-receive-page">
    <!-- 搜索栏 -->
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="通知类型">
          <el-select
            v-model="params.notifyType"
            placeholder="请选择通知类型"
            filterable
            clearable
          >
            <el-option
              v-for="item in notifyTypeOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
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
      </template>
    </IotTable>

    <!-- 编辑对话框 -->
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.message-receive-page {
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
