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
  { label: t('auto.rule_chain_index_a015434e'), value: 'PRODUCT' },
  { label: t('auto.rule_chain_index_e3900200'), value: 'DEVICE_GROUP' },
  { label: t('auto.rule_chain_index_c52cabf2'), value: 'DEVICE' },
]

const column = [
  {
    prop: 'name',
    label: t('auto.rule_chain_index_87080256'),
    minWidth: 150,
  },
  {
    prop: 'description',
    label: t('auto.rule_chain_index_3bdd08ad'),
    minWidth: 200,
    showOverflowTooltip: true,
  },
  {
    prop: 'sourceType',
    label: t('auto.rule_chain_index_6de026f2'),
    width: 120,
    formatter(row) {
      return sourceTypeOpt.find(item => item.value === row.sourceType)?.label ?? row.sourceType ?? '-'
    },
  },
  {
    prop: 'sourceName',
    label: t('auto.rule_chain_index_c11322c9'),
    minWidth: 150,
  },
  {
    prop: 'nodeCount',
    label: t('auto.rule_chain_index_93cb163f'),
    width: 80,
    align: 'center',
  },
  {
    prop: 'isRoot',
    label: t('auto.rule_chain_index_7995876b'),
    width: 90,
    align: 'center',
    formatter(row) {
      return row.isRoot ? t('auto.rule_chain_index_0a60ac8f') : t('auto.rule_chain_index_c9744f45')
    },
  },
  {
    prop: 'isEnabled',
    label: t('auto.rule_chain_index_3fea7ca7'),
    width: 100,
    slot: 'status',
  },
  {
    prop: 'updatedTime',
    label: t('auto.rule_chain_index_a001a226'),
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('auto.rule_chain_index_2b6bc0f2'),
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
  diaName: t('auto.rule_chain_index_b0fae043'),
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
    ElMessage.success(row.isEnabled ? t('auto.rule_chain_index_53ace430') : t('auto.rule_chain_index_1c1ed981'))
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
          {{ t('auto.rule_chain_index_31c02e22') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.rule_chain_index_4687f166') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('auto.rule_chain_index_87080256')">
            <el-input v-model="params.name" clearable :placeholder="t('auto.rule_chain_index_40f3f17d')" @keyup.enter="onSearch" />
          </el-form-item>

          <el-form-item :label="t('auto.rule_chain_index_6de026f2')">
            <el-select
              v-model="params.sourceType"
              :placeholder="t('auto.rule_chain_index_708c9d6d')"
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

          <el-form-item :label="t('auto.rule_chain_index_3fea7ca7')">
            <el-select v-model="params.isEnabled" :placeholder="t('auto.rule_chain_index_708c9d6d')" clearable>
              <el-option :label="t('auto.rule_chain_index_7854b52a')" :value="true" />
              <el-option :label="t('auto.rule_chain_index_710ad08b')" :value="false" />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('auto.rule_chain_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.rule_chain_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            <el-icon><Plus /></el-icon>
            {{ t('auto.rule_chain_index_f10ed468') }}
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
            {{ t('auto.rule_chain_index_95b351c8') }}
          </el-button>
          <el-button type="danger" link @click="onDelete(row)">
            <el-icon><Delete /></el-icon>
            {{ t('auto.rule_chain_index_2f4aaddd') }}
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