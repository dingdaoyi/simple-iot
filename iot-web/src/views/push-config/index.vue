<script setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, Promotion, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { pushConfigDeleteApi, pushConfigPageApi, pushConfigTestApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/push-config/widget/editDia.vue'

const { t } = useI18n()
const typeOpt = [
  { label: t('auto.push_config_index_f83e315e'), value: 'HTTP' },
  { label: t('auto.push_config_index_18bd3062'), value: 'MQTT' },
]

const column = [
  {
    prop: 'name',
    label: t('auto.push_config_index_4fcad1c9'),
    minWidth: 150,
  },
  {
    prop: 'type',
    label: t('auto.push_config_index_6d95c8c1'),
    width: 120,
    render({ row }) {
      return typeOpt.find(item => item.value === row.type)?.label || '-'
    },
  },
  {
    prop: 'description',
    label: t('auto.push_config_index_3bdd08ad'),
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
    label: t('auto.push_config_index_3fea7ca7'),
    width: 100,
    slot: 'status',
  },
  {
    prop: 'createTime',
    label: t('auto.push_config_index_eca37cb0'),
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 210,
    label: t('auto.push_config_index_2b6bc0f2'),
    fixed: 'right',
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
  deleteApi: pushConfigDeleteApi,
  fetchApi: pushConfigPageApi,
  diaName: t('auto.push_config_index_1bab3e3f'),
  defParams: {},
})

function closeEdite() {
  updatePage()
}

const testDialogVisible = ref(false)
const testLoading = ref(false)
const testTarget = ref(null)
const testForm = ref({
  eventType: 'push.test',
  payload: '{\n  "temperature": 36.5,\n  "humidity": 60\n}',
})
const testResult = ref(null)

function openTestDialog(row) {
  testTarget.value = row
  testResult.value = null
  testForm.value = {
    eventType: 'push.test',
    payload: '{\n  "temperature": 36.5,\n  "humidity": 60\n}',
  }
  testDialogVisible.value = true
}

function parseTestPayload() {
  const payload = testForm.value.payload
  if (!payload || !payload.trim()) {
    return {}
  }
  try {
    return JSON.parse(payload)
  }
  catch {
    return payload
  }
}

async function runTestPush() {
  if (!testTarget.value?.id) {
    return
  }
  testLoading.value = true
  testResult.value = null
  try {
    const res = await pushConfigTestApi(testTarget.value.id, {
      eventType: testForm.value.eventType || 'push.test',
      payload: parseTestPayload(),
    })
    const data = res?.data || res
    testResult.value = data
    if (data?.success) {
      ElMessage.success(t('auto.push_config_index_bd89aeb0'))
    }
    else {
      ElMessage.warning(data?.message || t('auto.push_config_index_1b213b21'))
    }
  }
  finally {
    testLoading.value = false
  }
}

onMounted(() => {
  updatePage()
})</script>

<template>
  <div class="push-config-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">📡</span>
          {{ t('auto.push_config_index_1bab3e3f') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.push_config_index_ab561d89') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">{{ t('auto.push_config_index_4fcad1c9') }}</label>
          <el-input
            v-model="params.name"
            :placeholder="t('auto.push_config_index_b984df04')"
            clearable
            style="width: 200px"
            @keyup.enter="onSearch"
          />
        </div>
        <div class="search-field">
          <label class="field-label">{{ t('auto.push_config_index_6d95c8c1') }}</label>
          <el-select
            v-model="params.type"
            :placeholder="t('auto.push_config_index_95f11c56')"
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
          <label class="field-label">{{ t('auto.push_config_index_3fea7ca7') }}</label>
          <el-select
            v-model="params.isEnabled"
            :placeholder="t('auto.push_config_index_708c9d6d')"
            clearable
            style="width: 120px"
          >
            <el-option :label="t('auto.push_config_index_7854b52a')" :value="true" />
            <el-option :label="t('auto.push_config_index_710ad08b')" :value="false" />
          </el-select>
        </div>
        <div class="search-actions">
          <el-button type="primary" @click="onSearch">
            {{ t('auto.push_config_index_e5f71fc3') }}
          </el-button>
          <el-button :icon="RefreshRight" @click="onReset">
            {{ t('auto.push_config_index_4b9c3271') }}
          </el-button>
          <el-button type="primary" @click="onAdd">
            {{ t('auto.push_config_index_11e5e642') }}
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
            {{ row.isEnabled ? t('auto.push_config_index_7854b52a') : t('auto.push_config_index_710ad08b') }}
          </el-tag>
        </template>
        <template #cz="{ row }">
          <el-button type="primary" link :icon="Promotion" @click="openTestDialog(row)">
            {{ t('auto.push_config_index_db06c78d') }}
          </el-button>
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('auto.push_config_index_95b351c8') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('auto.push_config_index_2f4aaddd') }}
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 测试推送对话框 -->
    <el-dialog v-model="testDialogVisible" :title="t('auto.push_config_index_f9761bc9')" width="640px">
      <el-form label-width="90px">
        <el-form-item :label="t('auto.push_config_index_4fcad1c9')">
          <el-input :model-value="testTarget?.name || '-'" disabled />
        </el-form-item>
        <el-form-item :label="t('auto.push_config_index_f974c846')">
          <el-input v-model="testForm.eventType" placeholder="push.test" />
        </el-form-item>
        <el-form-item :label="t('auto.push_config_index_aa56ca1c')">
          <el-input
            v-model="testForm.payload"
            type="textarea"
            :rows="8"
            :placeholder="t('auto.push_config_index_d8a06149')"
          />
        </el-form-item>
        <el-alert
          v-if="testTarget?.httpSignEnabled"
          type="info"
          show-icon
          :closable="false"
          :title="t('auto.push_config_index_299b4b6a')"
        />
        <el-alert
          v-if="testResult"
          class="test-result"
          :type="testResult.success ? 'success' : 'error'"
          show-icon
          :closable="false"
          ::title="t('auto.push_config_index_832722d2')"
        >
          <template v-if="testResult.responseBody" #default>
            <pre>{{ testResult.responseBody }}</pre>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">
          {{ t('auto.push_config_index_b15d9127') }}
        </el-button>
        <el-button type="primary" :loading="testLoading" @click="runTestPush">
          {{ t('auto.push_config_index_99b3a7ac') }}
        </el-button>
      </template>
    </el-dialog>

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

.test-result {
  margin-top: var(--space-md);

  pre {
    margin: 8px 0 0;
    padding: 8px;
    max-height: 160px;
    overflow: auto;
    background: rgba(15, 23, 42, 0.06);
    border-radius: var(--radius-sm);
    white-space: pre-wrap;
    word-break: break-word;
  }
}
</style>