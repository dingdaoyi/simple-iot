<script setup>
import { ruleDeleteApi, rulePageApi } from '@/api/index.js'
import EditDia from '@/views/rule/widget/editDia.vue'
import { useTable } from '@/composables/useTable.js'
import IotTable from '@/components/IotTable.vue'
import { onMounted } from 'vue'


const ruleTypeOpt = [
  {
    label: '数据判断',
    value: 1,
  },
  {
    label: '数值转换',
    value: 2,
  },
]
const inputTypeOpt = [
  {
    label: '属性',
    value: 1,
  },
  {
    label: '事件',
    value: 2,
  },
  {
    label: '在离线',
    value: 3,
  },
]

const sourceTypeOpt = [
  {
    label: '产品',
    value: 1,
  },
  {
    label: '设备分组',
    value: 2,
  },
  {
    label: '具体设备',
    value: 3,
  },
]

const targetTypeOpt = [
  {
    label: 'HTTP推送',
    value: 1,
  },
  {
    label: 'MQTT推送',
    value: 2,
  },
  {
    label: '消息推送',
    value: 3,
  },
]

const column = [
  {
    prop: 'name',
    label: '规则名称',
  },
  {
    prop: 'ruleType',
    label: '处理类型',
    formatter(row) {
      return ruleTypeOpt
        .find(item => item.value === row.ruleType)
        ?.label
    },
  },
  {
    prop: 'inputType',
    label: '输入类型',
    formatter(row) {
      return inputTypeOpt
        .find(item => item.value === row.inputType)
        ?.label
    },
  },
  {
    prop: 'sourceType',
    label: '数据范围',
    formatter(row) {
      return sourceTypeOpt
        .find(item => item.value === row.sourceType)
        ?.label
    },
  },
  {
    prop: 'sourceName',
    width: 200,
    label: '源',
  },
  {
    prop: 'targetType',
    label: '输出类型',
    formatter(row) {
      return targetTypeOpt
        .find(item => item.value === row.targetType)
        ?.label
    },
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
  tableData,
  total,
  loading,
  onDelete,
  onEdit,
  onAdd,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: ruleDeleteApi,
  fetchApi: rulePageApi,
  diaName: '规则',
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
  <div class="rule-page">
    <div class="iot-card search-bar">
      <el-form :inline="true" class="search-form">
        <el-form-item label="处理类型">
          <el-select
            v-model="params.productTypeId"
            placeholder="请选择处理类型"
            filterable
            clearable
          >
            <el-option
              v-for="item in ruleTypeOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="规则名称">
          <el-input v-model="params.name" clearable placeholder="请输入规则名称" />
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
.rule-page {
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
