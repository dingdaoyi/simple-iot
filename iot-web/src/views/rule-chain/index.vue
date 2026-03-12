<script setup>
import { Delete, Edit, Operation, Plus } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ruleChainDeleteApi,
  ruleChainPageApi,
  ruleChainToggleApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'

const router = useRouter()

const sourceTypeOpt = [
  { label: '产品', value: 'PRODUCT' },
  { label: '设备分组', value: 'DEVICE_GROUP' },
  { label: '特定设备', value: 'DEVICE' },
]

const column = [
  {
    prop: 'name',
    label: '规则名称',
    minWidth: 150,
  },
  {
    prop: 'description',
    label: '描述',
    minWidth: 200,
    showOverflowTooltip: true,
  },
  {
    prop: 'sourceType',
    label: '数据源类型',
    width: 120,
    formatter(row) {
      return sourceTypeOpt.find(item => item.value === row.sourceType)?.label ?? row.sourceType ?? '-'
    },
  },
  {
    prop: 'sourceName',
    label: '数据源',
    minWidth: 150,
  },
  {
    prop: 'nodeCount',
    label: '节点数',
    width: 80,
    align: 'center',
  },
  {
    prop: 'isRoot',
    label: '根规则',
    width: 90,
    align: 'center',
    formatter(row) {
      return row.isRoot ? '是' : '否'
    },
  },
  {
    prop: 'isEnabled',
    label: '状态',
    width: 100,
    slot: 'status',
  },
  {
    prop: 'updatedTime',
    label: '更新时间',
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: '操作',
    fixed: 'right',
  },
]

const {
  params,
  updatePage,
  onSearch,
  tableData,
  total,
  loading,
  onDelete,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: ruleChainDeleteApi,
  fetchApi: ruleChainPageApi,
  diaName: '规则',
  defParams: {},
})

// 编辑
function onEdit(row) {
  router.push(`/rule-chain/editor/${row.id}`)
}

// 新增
function onAdd() {
  router.push('/rule-chain/editor/new')
}

// 切换启用状态
async function onToggle(row) {
  try {
    await ruleChainToggleApi(row.id, !row.isEnabled)
    row.isEnabled = !row.isEnabled
    ElMessage.success(row.isEnabled ? '已启用' : '已禁用')
  }
  catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  updatePage()
})
</script>

<template>
  <div class="rule-chain-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon :size="24" class="title-icon">
            <Operation />
          </el-icon>
          规则引擎
        </h1>
        <p class="page-subtitle">
          可视化配置设备数据处理规则
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item label="规则名称">
            <el-input v-model="params.name" clearable placeholder="请输入规则名称" />
          </el-form-item>

          <el-form-item label="数据源类型">
            <el-select
              v-model="params.sourceType"
              placeholder="请选择"
              filterable
              clearable
            >
              <el-option
                v-for="item in sourceTypeOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select v-model="params.isEnabled" placeholder="请选择" clearable>
              <el-option label="启用" :value="true" />
              <el-option label="禁用" :value="false" />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <el-icon><Plus /></el-icon>
            新建规则
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper glass-card">
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
        <template #status="{ row }">
          <el-switch
            v-model="row.isEnabled"
            @change="onToggle(row)"
          />
        </template>
        <template #cz="{ row }">
          <el-button type="primary" link @click="onEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button type="danger" link @click="onDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </IotTable>
    </div>
  </div>
</template>

<style scoped lang="scss">
.rule-chain-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

.page-header {
  .header-content {
    background: var(--iot-glass-bg);
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
}

.table-wrapper {
  flex: 1;
  padding: var(--space-lg);
}
</style>
