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
    label: t('protocol.system_default_2'),
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
    label: t('protocol.protocol_name'),
    width: 180,
    fixed: 'left',
  },
  {
    prop: 'status',
    label: t('common.status'),
    width: 100,
    render({ row }) {
      return h(ElTag, { type: row.status === 1 ? 'success' : 'info' }, () => row.status === 1 ? t('common.enabled') : t('protocol.disabled'))
    },
  },
  {
    prop: 'protoType',
    label: t('protocol.protocol_type_2'),
    width: 120,
    render({ row }) {
      const opt = protocolTypeOpt.find(item => item.value === row.protoType)
      return opt ? h(ElTag, { type: opt.color }, () => opt.label) : '-'
    },
  },
  {
    prop: 'protoKey',
    label: t('protocol.protocol_key_2'),
    width: 160,
  },
  {
    prop: 'scriptLang',
    label: t('protocol.language'),
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
    label: t('protocol.remark'),
    minWidth: 150,
    showOverflowTooltip: true,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 200,
    fixed: 'right',
    label: t('common.operation'),
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
  diaName: t('product.protocol'),
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
  const action = newStatus ? t('common.enable') : t('protocol.disable')

  try {
    await ElMessageBox.confirm(
      t('protocol.you_sure_you_want_action_protocol_name_tip', { action, name: row.name, tip: newStatus ? t('protocol.after_enabling_protocol_will_be_loaded_into_memory') : t('protocol.after_disabling_protocol_will_be_unloaded_from_memory') }),
      t('protocol.operation_confirmation'),
      {
        confirmButtonText: t('common.confirm'),
        cancelButtonText: t('common.cancel'),
        type: 'warning',
      },
    )
    await protocolSetStatusApi(row.id, newStatus)
    row.status = newStatus ? 1 : 2
    ElMessage.success(t('protocol.protocol_action', { action }))
  }
  catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(t('protocol.operation_failed_message', { message: error.message || t('protocol.unknown_error') }))
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
      :title="t('protocol.protocol')"
      :subtitle="t('protocol.manage_device_communication_protocols_supporting_java_javascript_other')"
      :icon="Connection"
    />

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item :label="t('protocol.protocol_type_2')">
            <el-select
              v-model="params.protoType"
              :placeholder="t('protocol.select_protocol_type_2')"
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

          <el-form-item :label="t('protocol.language')">
            <el-select
              v-model="params.scriptLang"
              :placeholder="t('protocol.protocol_page')"
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

          <el-form-item :label="t('protocol.protocol_name')">
            <el-input
              v-model="params.name"
              clearable
              :placeholder="t('protocol.enter_protocol_name_2')"
              style="width: 200px"
              @keyup.enter="onSearch"
            />
          </el-form-item>

          <el-form-item :label="t('protocol.protocol_key_2')">
            <el-input
              v-model="params.protoKey"
              clearable
              :placeholder="t('protocol.enter_protocol_key')"
              style="width: 180px"
              @keyup.enter="onSearch"
            />
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
            <span class="btn-icon">+</span>
            {{ t('protocol.add_protocol_2') }}
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
            {{ row.status === 1 ? t('protocol.disable') : t('common.enable') }}
          </el-button>
          <el-button type="primary" link @click="onEdit(row)">
            {{ t('common.edit') }}
          </el-button>
          <el-button
            v-if="row.protoType !== 2"
            type="danger"
            link
            @click="onDelete(row)"
          >
            {{ t('common.delete') }}
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