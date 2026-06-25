<script setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, Operation, Plus, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  ruleChainDeleteApi,
  ruleChainPageApi,
  ruleChainToggleApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'

const { t } = useI18n()
const router = useRouter()

const sourceTypeOpt = [
  { label: t('ruleChain.product_2'), value: 'PRODUCT' },
  { label: t('ruleChain.device_group'), value: 'DEVICE_GROUP' },
  { label: t('ruleChain.specific_device_2'), value: 'DEVICE' },
]

const column = [
  {
    prop: 'name',
    label: t('ruleChain.rule_name_3'),
    minWidth: 150,
  },
  {
    prop: 'description',
    label: t('common.description'),
    minWidth: 200,
    showOverflowTooltip: true,
  },
  {
    prop: 'sourceType',
    label: t('ruleChain.data_source_type'),
    width: 120,
    formatter(row) {
      return sourceTypeOpt.find(item => item.value === row.sourceType)?.label ?? row.sourceType ?? '-'
    },
  },
  {
    prop: 'sourceName',
    label: t('ruleChain.data_source_2'),
    minWidth: 150,
  },
  {
    prop: 'nodeCount',
    label: t('ruleChain.nodes_2'),
    width: 80,
    align: 'center',
  },
  {
    prop: 'isRoot',
    label: t('ruleChain.root_rule'),
    width: 90,
    align: 'center',
    formatter(row) {
      return row.isRoot ? t('common.yes') : t('common.no')
    },
  },
  {
    prop: 'isEnabled',
    label: t('common.status'),
    width: 100,
    slot: 'status',
  },
  {
    prop: 'updatedTime',
    label: t('device.property_time'),
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('common.operation'),
    fixed: 'right',
  },
]

const {
  params,
  updatePage,
  onSearch,
  onReset,
  tableData,
  total,
  loading,
  onDelete,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: ruleChainDeleteApi,
  fetchApi: ruleChainPageApi,
  diaName: t('ruleChain.rule'),
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
    ElMessage.success(row.isEnabled ? t('common.enabled') : t('ruleChain.disabled'))
  }
  catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  updatePage()
})</script>

<template>
  <div class="rule-chain-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon :size="24" class="title-icon">
            <Operation />
          </el-icon>
          {{ t('ruleChain.rule_chain_page') }}
        </h1>
        <p class="page-subtitle">
          {{ t('ruleChain.visually_configure_device_data_processing_rules') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('ruleChain.rule_name_3')">
            <el-input v-model="params.name" clearable :placeholder="t('ruleChain.enter_rule_name')" @keyup.enter="onSearch" />
          </el-form-item>

          <el-form-item :label="t('ruleChain.data_source_type')">
            <el-select
              v-model="params.sourceType"
              :placeholder="t('common.please_select')"
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

          <el-form-item :label="t('common.status')">
            <el-select v-model="params.isEnabled" :placeholder="t('common.please_select')" clearable>
              <el-option :label="t('common.enable')" :value="true" />
              <el-option :label="t('ruleChain.disable')" :value="false" />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('common.search') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('common.reset') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            <el-icon><Plus /></el-icon>
            {{ t('ruleChain.create_rule') }}
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
            {{ t('common.edit') }}
          </el-button>
          <el-button type="danger" link @click="onDelete(row)">
            <el-icon><Delete /></el-icon>
            {{ t('common.delete') }}
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