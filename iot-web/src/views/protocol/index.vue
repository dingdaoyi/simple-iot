<script setup>
import { protocolDeleteApi, protocolListApi } from '@/api/index.js'
import EditDia from '@/views/protocol/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import { onMounted } from 'vue'

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
    render({ row }) {
      return protocolTypeOpt
        .find(item => item.value === row.protoType)
        ?.label || '-'
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
    width: 120,
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
  deleteApi: protocolDeleteApi,
  fetchApi: protocolListApi,
  diaName: '协议',
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
  <div class="protocol-page">
    <!-- 搜索栏 -->
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="协议类型">
          <el-select
            v-model="params.productTypeId"
            placeholder="请选择协议类型"
            filterable
            clearable
          >
            <el-option
              v-for="item in protocolTypeOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="产品型号">
          <el-input v-model="params.model" clearable placeholder="请输入产品型号" />
        </el-form-item>

        <el-form-item label="厂家名称">
          <el-input v-model="params.manufacturer" clearable placeholder="请输厂家名称" />
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
        <el-button v-if="row.protoType !== 2" type="danger" link @click="onDelete(row)">
          删除
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
.protocol-page {
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
