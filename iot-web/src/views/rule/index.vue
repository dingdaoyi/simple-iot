<script setup>
import { Operation } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { ruleDeleteApi, rulePageApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/rule/widget/editDia.vue'

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
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon :size="24" class="title-icon">
            <Operation />
          </el-icon>
          规则管理
        </h1>
        <p class="page-subtitle">
          配置数据处理规则与转发策略
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
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
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <span class="btn-icon">+</span>
            添加规则
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
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
    </div>

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
.rule-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  .header-content {
    background: var(--iot-glass-bg-dark);
    backdrop-filter: blur(20px);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-lg);
    padding: var(--space-xl) var(--space-2xl);
    box-shadow: var(--shadow-md);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, var(--iot-color-primary), var(--iot-color-accent));
    }
  }

  .page-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0;
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    color: var(--iot-color-text-primary);

    .title-icon {
      color: var(--iot-color-primary);
      font-size: 24px;
    }
  }

  .page-subtitle {
    margin: var(--space-sm) 0 0 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
  }
}

/* 搜索栏 */
.search-bar {
  .search-form {
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
  }

  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-md);

    .el-form-item {
      flex: 1;
      min-width: 200px;
      margin-bottom: 0;
    }
  }

  .form-actions {
    display: flex;
    gap: var(--space-sm);
    justify-content: flex-end;
  }

  .btn-icon {
    margin-right: var(--space-xs);
    font-size: 16px;
  }
}

/* 表格容器 */
.table-wrapper {
  flex: 1;
}

/* 响应式 */
@media (max-width: 768px) {
  .rule-page {
    padding: var(--space-md);
  }

  .form-row {
    flex-direction: column;

    .el-form-item {
      width: 100%;
    }
  }

  .form-actions {
    width: 100%;
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
