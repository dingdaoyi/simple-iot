<script setup>
import { Delete, Edit, RefreshRight } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { messageReceiveDeleteApi, messageReceivePageApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/system/messageReceive/widget/editDia.vue'

const { t } = useI18n()
const notifyTypeOpt = [
  {
    label: t('system.email_2'),
    value: 1,
  },
  {
    label: t('system.messagereceive_system_messagereceive_page'),
    value: 2,
  },
]

const column = [
  {
    prop: 'name',
    label: t('system.system_messagereceive_page'),
  },
  {
    prop: 'notifyType',
    label: t('system.notification_type'),
    render({ row }) {
      return notifyTypeOpt
        .find(item => item.value === row.notifyType)
        ?.label || '-'
    },
  },
  {
    prop: 'receiver',
    label: t('system.receiver_number'),
  },
  {
    prop: 'remark',
    label: t('common.remark'),
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 150,
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
  onEdit,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: messageReceiveDeleteApi,
  fetchApi: messageReceivePageApi,
  diaName: t('system.notifications'),
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
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">✉</span>
          {{ t('system.notifications') }}
        </h1>
        <p class="page-subtitle">
          {{ t('system.configure_email_sms_notification_recipients') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">{{ t('system.notification_type') }}</label>
          <el-select
            v-model="params.notifyType"
            :placeholder="t('system.select_notification_type')"
            filterable
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in notifyTypeOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            {{ t('common.search') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('common.reset') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            {{ t('system.add_notification_2') }}
          </el-button>
        </div>
      </div>
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
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('common.edit') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
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
.message-receive-page {
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
  .search-row {
    display: flex;
    align-items: flex-end;
    gap: var(--space-md);
    flex-wrap: wrap;
  }

  .search-field {
    display: flex;
    flex-direction: column;
    gap: var(--space-xs);

    .field-label {
      font-size: 13px;
      color: var(--iot-color-text-secondary);
    }
  }

  .search-actions {
    display: flex;
    gap: var(--space-sm);
  }
}

/* 表格容器 */
.table-wrapper {
  flex: 1;
}

/* 响应式 */
@media (max-width: 768px) {
  .message-receive-page {
    padding: var(--space-md);
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;

    .search-field {
      .el-select {
        width: 100% !important;
      }
    }

    .search-actions {
      width: 100%;

      .el-button {
        flex: 1;
      }
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
