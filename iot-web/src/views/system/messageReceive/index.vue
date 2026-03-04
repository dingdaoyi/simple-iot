<script setup>
import { onMounted } from 'vue'
import { messageReceiveDeleteApi, messageReceivePageApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/system/messageReceive/widget/editDia.vue'

const notifyTypeOpt = [
  {
    label: '邮件',
    value: 1,
  },
  {
    label: '短信',
    value: 2,
  },
]

const column = [
  {
    prop: 'name',
    label: '接收名称',
  },
  {
    prop: 'notifyType',
    label: '通知类型',
    render({ row }) {
      return notifyTypeOpt
        .find(item => item.value === row.notifyType)
        ?.label || '-'
    },
  },
  {
    prop: 'receiver',
    label: '接收号码',
  },
  {
    prop: 'remark',
    label: '备注',
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 150,
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
  onEdit,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: messageReceiveDeleteApi,
  fetchApi: messageReceivePageApi,
  diaName: '消息通知',
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
          消息通知
        </h1>
        <p class="page-subtitle">
          配置邮件与短信通知接收方式
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">通知类型</label>
          <el-select
            v-model="params.notifyType"
            placeholder="请选择通知类型"
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
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            添加通知
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
