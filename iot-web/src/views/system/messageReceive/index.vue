<script setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, RefreshRight } from '@element-plus/icons-vue'
import { onMounted } from 'vue'
import { messageReceiveDeleteApi, messageReceivePageApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/system/messageReceive/widget/editDia.vue'

const { t } = useI18n()
const notifyTypeOpt = [
  {
    label: t('auto.system_messagereceive_index_e9e8054f'),
    value: 1,
  },
  {
    label: t('auto.system_messagereceive_index_485c3abb'),
    value: 2,
  },
]

const column = [
  {
    prop: 'name',
    label: t('auto.system_messagereceive_index_26bba1d9'),
  },
  {
    prop: 'notifyType',
    label: t('auto.system_messagereceive_index_cd82d2bb'),
    render({ row }) {
      return notifyTypeOpt
        .find(item => item.value === row.notifyType)
        ?.label || '-'
    },
  },
  {
    prop: 'receiver',
    label: t('auto.system_messagereceive_index_dc2d48d5'),
  },
  {
    prop: 'remark',
    label: t('auto.system_messagereceive_index_2432b575'),
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 150,
    label: t('auto.system_messagereceive_index_2b6bc0f2'),
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
  diaName: t('auto.system_messagereceive_index_d1d4c33c'),
  defParams: {},
})

function closeEdite() {
  updatePage()
}

onMounted(() => {
  updatePage()
})</script>

<template>
  <div class="message-receive-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">✉</span>
          {{ t('auto.system_messagereceive_index_d1d4c33c') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.system_messagereceive_index_b79a6257') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">{{ t('auto.system_messagereceive_index_cd82d2bb') }}</label>
          <el-select
            v-model="params.notifyType"
            :placeholder="t('auto.system_messagereceive_index_5e382cb7')"
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
            {{ t('auto.system_messagereceive_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.system_messagereceive_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            {{ t('auto.system_messagereceive_index_d3cec019') }}
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
            {{ t('auto.system_messagereceive_index_95b351c8') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('auto.system_messagereceive_index_2f4aaddd') }}
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