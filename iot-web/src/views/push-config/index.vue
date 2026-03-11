<script setup>
import { onMounted } from 'vue'
import { pushConfigDeleteApi, pushConfigPageApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/push-config/widget/editDia.vue'

const typeOpt = [
  { label: 'HTTP回调', value: 'HTTP' },
  { label: 'MQTT转发', value: 'MQTT' },
]

const column = [
  {
    prop: 'name',
    label: '配置名称',
    minWidth: 150,
  },
  {
    prop: 'type',
    label: '配置类型',
    width: 120,
    render({ row }) {
      return typeOpt.find(item => item.value === row.type)?.label || '-'
    },
  },
  {
    prop: 'description',
    label: '描述',
    minWidth: 180,
    showOverflowTooltip: true,
  },
  {
    prop: 'endpoint',
    label: 'URL/Topic',
    minWidth: 200,
    render({ row }) {
      return row.httpUrl || row.mqttTopic || '-'
    },
  },
  {
    prop: 'isEnabled',
    label: '状态',
    width: 100,
    slot: 'status',
  },
  {
    prop: 'createTime',
    label: '创建时间',
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 150,
    label: '操作',
    fixed: 'right',
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
  deleteApi: pushConfigDeleteApi,
  fetchApi: pushConfigPageApi,
  diaName: '推送配置',
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
  <div class="push-config-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">📡</span>
          推送配置
        </h1>
        <p class="page-subtitle">
          配置 HTTP 回调和 MQTT 转发，供规则链使用
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">配置名称</label>
          <el-input
            v-model="params.name"
            placeholder="请输入配置名称"
            clearable
            style="width: 200px"
          />
        </div>
        <div class="search-field">
          <label class="field-label">配置类型</label>
          <el-select
            v-model="params.type"
            placeholder="请选择类型"
            filterable
            clearable
            style="width: 140px"
          >
            <el-option
              v-for="item in typeOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
        <div class="search-field">
          <label class="field-label">状态</label>
          <el-select
            v-model="params.isEnabled"
            placeholder="请选择"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            添加配置
          </el-button>
        </div>
      </div>
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
          <el-tag :type="row.isEnabled ? 'success' : 'info'" size="small">
            {{ row.isEnabled ? '启用' : '禁用' }}
          </el-tag>
        </template>
        <template #cz="{ row }">
          <el-button type="primary" link @click="onEdit(row)">
            编辑
          </el-button>
          <el-button type="danger" link @click="onDelete(row)">
            删除
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
.push-config-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

/* 页面标题 */
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
      font-size: 28px;
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
  padding: var(--space-lg);
}

/* 响应式 */
@media (max-width: 768px) {
  .push-config-page {
    padding: var(--space-md);
  }

  .search-row {
    flex-direction: column;
    align-items: stretch;

    .search-field {
      .el-input,
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
