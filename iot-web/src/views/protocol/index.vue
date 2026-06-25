<script setup>
import { useI18n } from 'vue-i18n'
import { Connection, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, ElTag } from 'element-plus'
import { h, onMounted } from 'vue'
import { protocolDeleteApi, protocolListApi, protocolSetStatusApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import PageHeader from '@/components/PageHeader.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/protocol/widget/editDia.vue'

const { t } = useI18n()
const protocolTypeOpt = [
  {
    label: 'JAVA',
    value: 1,
    color: 'warning',
  },
  {
    label: t('auto.protocol_index_a52f5c20'),
    value: 2,
    color: 'info',
  },
  {
    label: 'JAVASCRIPT',
    value: 3,
    color: 'success',
  },
]

const scriptLangOpt = {
  javascript: { label: 'JavaScript', icon: '📜' },
  python: { label: 'Python', icon: '🐍' },
  groovy: { label: 'Groovy', icon: '☕' },
  lua: { label: 'Lua', icon: '🌙' },
}

const column = [
  {
    prop: 'name',
    label: t('auto.protocol_index_6924dc1d'),
    width: 180,
    fixed: 'left',
  },
  {
    prop: 'status',
    label: t('auto.protocol_index_3fea7ca7'),
    width: 100,
    render({ row }) {
      return h(ElTag, { type: row.status === 1 ? 'success' : 'info' }, () => row.status === 1 ? t('auto.protocol_index_53ace430') : t('auto.protocol_index_1c1ed981'))
    },
  },
  {
    prop: 'protoType',
    label: t('auto.protocol_index_e825ec78'),
    width: 120,
    render({ row }) {
      const opt = protocolTypeOpt.find(item => item.value === row.protoType)
      return opt ? h(ElTag, { type: opt.color }, () => opt.label) : '-'
    },
  },
  {
    prop: 'protoKey',
    label: t('auto.protocol_index_b58edac0'),
    width: 160,
  },
  {
    prop: 'scriptLang',
    label: t('auto.protocol_index_aae5ff1f'),
    width: 120,
    render({ row }) {
      if (row.protoType !== 3)
        return '-'
      const lang = scriptLangOpt[row.scriptLang]
      return lang ? `${lang.icon} ${lang.label}` : '-'
    },
  },
  {
    prop: 'mark',
    label: t('auto.protocol_index_8a4cf07c'),
    minWidth: 150,
    showOverflowTooltip: true,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 200,
    fixed: 'right',
    label: t('auto.protocol_index_2b6bc0f2'),
  },
]

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  onReset,
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
  diaName: t('auto.protocol_index_faa1ad5e'),
  defParams: {},
})

function closeEdite() {
  updatePage()
}

function onEdit(row) {
  currentItem.value = row
  dialogVisible.value = true
}

async function toggleStatus(row) {
  const newStatus = row.status !== 1
  const action = newStatus ? t('auto.protocol_index_7854b52a') : t('auto.protocol_index_710ad08b')

  try {
    await ElMessageBox.confirm(
      t('auto.protocol_confirm_status', { action, name: row.name, tip: newStatus ? t('auto.protocol_enable_tip') : t('auto.protocol_disable_tip') }),
      t('auto.protocol_index_ce828a79'),
      {
        confirmButtonText: t('auto.protocol_index_38cf16f2'),
        cancelButtonText: t('auto.protocol_index_625fb26b'),
        type: 'warning',
      },
    )
    await protocolSetStatusApi(row.id, newStatus)
    row.status = newStatus ? 1 : 2
    ElMessage.success(t('auto.protocol_status_success', { action }))
  }
  catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('auto.operation_failed_with_msg', { message: error.message || t('auto.unknown_error') }))
    }
  }
}

onMounted(() => {
  updatePage()
})</script>

<template>
  <div class="protocol-page">
    <!-- 页面标题 -->
    <PageHeader
      :title="t('auto.protocol_index_6f8e6b0c')"
      :subtitle="t('auto.protocol_index_631f6ded')"
      :icon="Connection"
    />

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('auto.protocol_index_e825ec78')">
            <el-select
              v-model="params.protoType"
              :placeholder="t('auto.protocol_index_728cbe38')"
              filterable
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="item in protocolTypeOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item :label="t('auto.protocol_index_aae5ff1f')">
            <el-select
              v-model="params.scriptLang"
              :placeholder="t('auto.protocol_index_2b21eccb')"
              filterable
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="(lang, key) in scriptLangOpt"
                :key="key"
                :label="lang.label"
                :value="key"
              />
            </el-select>
          </el-form-item>

          <el-form-item :label="t('auto.protocol_index_6924dc1d')">
            <el-input
              v-model="params.name"
              clearable
              :placeholder="t('auto.protocol_index_f79bd6a2')"
              style="width: 200px"
              @keyup.enter="onSearch"
            />
          </el-form-item>

          <el-form-item :label="t('auto.protocol_index_b58edac0')">
            <el-input
              v-model="params.protoKey"
              clearable
              :placeholder="t('auto.protocol_index_98182d06')"
              style="width: 180px"
              @keyup.enter="onSearch"
            />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            {{ t('auto.protocol_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.protocol_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            <span class="btn-icon">+</span>
            {{ t('auto.protocol_index_c601ea4f') }}
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
        <template #cz="{ row }">
          <el-button
            :type="row.status === 1 ? 'warning' : 'success'"
            link
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? t('auto.protocol_index_710ad08b') : t('auto.protocol_index_7854b52a') }}
          </el-button>
          <el-button type="primary" link @click="onEdit(row)">
            {{ t('auto.protocol_index_95b351c8') }}
          </el-button>
          <el-button
            v-if="row.protoType !== 2"
            type="danger"
            link
            @click="onDelete(row)"
          >
            {{ t('auto.protocol_index_2f4aaddd') }}
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
.protocol-page {
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
      font-size: 28px;
      background: linear-gradient(135deg, var(--iot-color-primary), var(--iot-color-accent));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
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
  .protocol-page {
    padding: var(--space-md);
  }

  .form-row {
    flex-direction: column;

    .el-form-item {
      width: 100%;

      :deep(.el-select),
      :deep(.el-input) {
        width: 100% !important;
      }
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