<script setup>
import { Delete, Edit, Promotion, RefreshRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { pushConfigDeleteApi, pushConfigPageApi, pushConfigTestApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/push-config/widget/editDia.vue'

const { t } = useI18n()
const typeOpt = [
  { label: t('push_config.http'), value: 'HTTP' },
  { label: t('push_config.mqtt_forwarding'), value: 'MQTT' },
]

const column = [
  {
    prop: 'name',
    label: t('push_config.config_name'),
    minWidth: 150,
  },
  {
    prop: 'type',
    label: t('push_config.type_2'),
    width: 120,
    render({ row }) {
      return typeOpt.find(item => item.value === row.type)?.label || '-'
    },
  },
  {
    prop: 'description',
    label: t('common.description'),
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
    label: t('common.status'),
    width: 100,
    slot: 'status',
  },
  {
    prop: 'createTime',
    label: t('push_config.created_at'),
    width: 160,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 210,
    label: t('common.operation'),
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
  diaName: t('push_config.push_config'),
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
      ElMessage.success(t('push_config.test_push_succeeded'))
    }
    else {
      ElMessage.warning(data?.message || t('push_config.push_test_failed'))
    }
  }
  finally {
    testLoading.value = false
  }
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
          {{ t('push_config.push_config') }}
        </h1>
        <p class="page-subtitle">
          {{ t('push_config.http_mqtt') }}
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <div class="search-row">
        <div class="search-field">
          <label class="field-label">{{ t('push_config.config_name') }}</label>
          <el-input
            v-model="params.name"
            :placeholder="t('push_config.enter_config_name_2')"
            clearable
            style="width: 200px"
            @keyup.enter="onSearch"
          />
        </div>
        <div class="search-field">
          <label class="field-label">{{ t('push_config.type_2') }}</label>
          <el-select
            v-model="params.type"
            :placeholder="t('push_config.select_type')"
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
          <label class="field-label">{{ t('common.status') }}</label>
          <el-select
            v-model="params.isEnabled"
            :placeholder="t('common.please_select')"
            clearable
            style="width: 120px"
          >
            <el-option :label="t('common.enable')" :value="true" />
            <el-option :label="t('push_config.disable')" :value="false" />
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
            {{ t('push_config.push_config_page') }}
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
            {{ row.isEnabled ? t('common.enable') : t('push_config.disable') }}
          </el-tag>
        </template>
        <template #cz="{ row }">
          <el-button type="primary" link :icon="Promotion" @click="openTestDialog(row)">
            {{ t('push_config.test') }}
          </el-button>
          <el-button type="primary" link :icon="Edit" @click="onEdit(row)">
            {{ t('common.edit') }}
          </el-button>
          <el-button type="danger" link :icon="Delete" @click="onDelete(row)">
            {{ t('common.delete') }}
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 测试推送对话框 -->
    <el-dialog v-model="testDialogVisible" :title="t('push_config.test_push_config')" width="640px">
      <el-form label-width="90px">
        <el-form-item :label="t('push_config.config_name')">
          <el-input :model-value="testTarget?.name || '-'" disabled />
        </el-form-item>
        <el-form-item :label="t('device.event_type')">
          <el-input v-model="testForm.eventType" placeholder="push.test" />
        </el-form-item>
        <el-form-item :label="t('push_config.test_payload')">
          <el-input
            v-model="testForm.payload"
            type="textarea"
            :rows="8"
            :placeholder="t('push_config.enter_json_plain_text')"
          />
        </el-form-item>
        <el-alert
          v-if="testTarget?.httpSignEnabled"
          type="info"
          show-icon
          :closable="false"
          :title="t('push_config.hmac_x_simple_iot_timestamp_signature_event')"
        />
        <el-alert
          v-if="testResult"
          class="test-result"
          :type="testResult.success ? 'success' : 'error'"
          show-icon
          :closable="false"
          ::title="t('push_config.testresult_message_testresult_success')"
        >
          <template v-if="testResult.responseBody" #default>
            <pre>{{ testResult.responseBody }}</pre>
          </template>
        </el-alert>
      </el-form>
      <template #footer>
        <el-button @click="testDialogVisible = false">
          {{ t('push_config.close') }}
        </el-button>
        <el-button type="primary" :loading="testLoading" @click="runTestPush">
          {{ t('push_config.send_test') }}
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
