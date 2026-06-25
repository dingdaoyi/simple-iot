<script setup>
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, reactive, ref } from 'vue'
import {
  addEmailConfig,
  deleteEmailConfig,
  getEmailConfigList,
  sendTestEmail,
  setDefaultEmailConfig,
  updateEmailConfig,
  updateEmailConfigStatus,
} from '@/api/email'

const { t } = useI18n()
const listLoading = ref(true)
const list = ref([])

const listQuery = reactive({
  name: undefined,
  status: undefined,
})

const temp = reactive({
  id: undefined,
  name: '',
  host: '',
  port: 587,
  username: '',
  password: '',
  protocol: 'smtp',
  encoding: 'UTF-8',
  sslEnabled: true,
})

const testEmailTemp = reactive({
  to: '',
  configId: undefined,
})

const dialogFormVisible = ref(false)
const testDialogVisible = ref(false)
const dialogStatus = ref('')

const textMap = {
  update: t('common.edit'),
  create: t('system.create'),
}

const rules = {
  name: [{ required: true, message: t('system.config_name_required'), trigger: 'blur' }],
  host: [{ required: true, message: t('system.smtp'), trigger: 'blur' }],
  port: [{ required: true, message: t('system.email_smtp'), trigger: 'blur' }],
  username: [
    { required: true, message: t('system.sender_email_required'), trigger: 'blur' },
    { type: 'email', message: t('system.invalid_email'), trigger: 'blur' },
  ],
  password: [{ required: true, message: t('system.email_password_required'), trigger: 'blur' }],
}

const testRules = {
  to: [
    { required: true, message: t('system.email_system_email_page'), trigger: 'blur' },
    { type: 'email', message: t('system.invalid_email'), trigger: 'blur' },
  ],
}

const dataForm = ref()
const testForm = ref()

async function getList() {
  listLoading.value = true
  try {
    const response = await getEmailConfigList({
      ...listQuery,
      page: 0,
      size: 100,
    })
    list.value = response.data
  }
  finally {
    listLoading.value = false
  }
}

function handleFilter() {
  getList()
}

function resetTemp() {
  Object.assign(temp, {
    id: undefined,
    name: '',
    host: '',
    port: 587,
    username: '',
    password: '',
    protocol: 'smtp',
    encoding: 'UTF-8',
    sslEnabled: true,
  })
}

function handleCreate() {
  resetTemp()
  dialogStatus.value = 'create'
  dialogFormVisible.value = true
  nextTick(() => {
    dataForm.value?.clearValidate()
  })
}

async function createData() {
  const valid = await dataForm.value?.validate()
  if (valid) {
    await addEmailConfig(temp)
    dialogFormVisible.value = false
    ElMessage.success(t('system.system_email_page'))
    getList()
  }
}

function handleUpdate(row) {
  Object.assign(temp, {
    ...row,
    password: '', // Hide password while editing
  })
  dialogStatus.value = 'update'
  dialogFormVisible.value = true
  nextTick(() => {
    dataForm.value?.clearValidate()
  })
}

async function updateData() {
  const valid = await dataForm.value?.validate()
  if (valid) {
    const data = { ...temp }
    if (!data.password) {
      delete data.password
    }
    await updateEmailConfig(data)
    dialogFormVisible.value = false
    ElMessage.success(t('system.updated_successfully'))
    getList()
  }
}

async function handleDelete(row) {
  if (row.isDefault) {
    ElMessage.warning(t('system.default_config_cannot_deleted'))
    return
  }
  await ElMessageBox.confirm(t('system.this_operation_will_permanently_delete_configuration_continue'), t('common.tip'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning',
  })
  await deleteEmailConfig(row.id)
  ElMessage.success(t('system.delete_success'))
  getList()
}

async function handleSetDefault(row) {
  await setDefaultEmailConfig(row.id)
  ElMessage.success(t('system.default_config_set_successfully'))
  getList()
}

async function handleStatusChange(row) {
  await updateEmailConfigStatus(row.id, row.status)
  ElMessage.success(t('system.status_updated_successfully'))
}

function handleTestEmail(row) {
  testEmailTemp.configId = row.id
  testEmailTemp.to = ''
  testDialogVisible.value = true
  nextTick(() => {
    testForm.value?.clearValidate()
  })
}

async function sendTestEmailMessage() {
  const valid = await testForm.value?.validate()
  if (valid) {
    await sendTestEmail({
      to: testEmailTemp.to,
      configId: testEmailTemp.configId,
    })
    ElMessage.success(t('system.test_email_sent_successfully'))
    testDialogVisible.value = false
  }
}

// 初始化
getList()</script>

<template>
  <div class="email-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">✉</span>
          {{ t('system.email_config') }}
        </h1>
        <p class="page-subtitle">
          {{ t('system.configure_smtp_email_service_system_notifications') }}
        </p>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="main-content glass-card">
      <div class="filter-row">
        <el-input
          v-model="listQuery.name"
          :placeholder="t('push_config.config_name')"
          style="width: 200px;"
          clearable
          @keyup.enter="handleFilter"
        />
        <el-select v-model="listQuery.status" :placeholder="t('common.status')" style="width: 120px;" clearable>
          <el-option :label="t('common.enable')" :value="1" />
          <el-option :label="t('system.disable')" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleFilter">
          {{ t('common.search') }}
        </el-button>
        <el-button type="primary" @click="handleCreate">
          {{ t('system.add_config') }}
        </el-button>
      </div>

      <el-table
        v-loading="listLoading"
        :data="list"
        element-loading-text="Loading"
        border
        fit
        highlight-current-row
      >
        <el-table-column align="center" label="ID" width="80">
          <template #default="scope">
            {{ scope.row.id }}
          </template>
        </el-table-column>
        <el-table-column :label="t('push_config.config_name')">
          <template #default="scope">
            {{ scope.row.name }}
            <el-tag v-if="scope.row.isDefault" type="success" size="small">
              {{ t('system.default') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('system.smtp_server')" prop="host" />
        <el-table-column :label="t('common.port')" width="80" align="center" prop="port" />
        <el-table-column :label="t('system.sender_email')" prop="username" />
        <el-table-column label="SSL" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sslEnabled ? 'success' : 'info'" size="small">
              {{ scope.row.sslEnabled ? t('system.email_system_email_page_16') : t('system.off') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.status')" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="2"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" align="center" width="280">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleUpdate(scope.row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button type="info" link size="small" @click="handleTestEmail(scope.row)">
              {{ t('system.email_test') }}
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="success" link size="small" @click="handleSetDefault(scope.row)">
              {{ t('system.set_default') }}
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="danger" link size="small" @click="handleDelete(scope.row)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 配置对话框 -->
    <el-dialog v-model="dialogFormVisible" :title="textMap[dialogStatus]" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="120px"
      >
        <el-form-item :label="t('push_config.config_name')" prop="name">
          <el-input v-model="temp.name" :placeholder="t('system.e_g_corporate_email')" />
        </el-form-item>
        <el-form-item :label="t('system.smtp_server')" prop="host">
          <el-input v-model="temp.host" :placeholder="t('system.e_g_smtp_qq_com')" />
        </el-form-item>
        <el-form-item :label="t('system.smtp_port')" prop="port">
          <el-input-number v-model="temp.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item :label="t('system.sender_email')" prop="username">
          <el-input v-model="temp.username" :placeholder="t('system.sender_email_address')" />
        </el-form-item>
        <el-form-item :label="t('system.email_password')" prop="password">
          <el-input v-model="temp.password" type="password" :placeholder="t('system.email_password_authorization_code')" />
          <div class="form-tip">
            {{ t('system.qq_mail_163_mail_similar_providers_require_authorization') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('system.enable_ssl')">
          <el-switch v-model="temp.sslEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            {{ t('system.email_confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 测试邮件对话框 -->
    <el-dialog v-model="testDialogVisible" :title="t('system.send_test_email')" width="500px">
      <el-form
        ref="testForm"
        :rules="testRules"
        :model="testEmailTemp"
        label-position="left"
        label-width="100px"
      >
        <el-form-item :label="t('system.recipient_email')" prop="to">
          <el-input v-model="testEmailTemp.to" :placeholder="t('system.test_recipient_email_address')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">
            {{ t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="sendTestEmailMessage">
            {{ t('system.send') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.email-page {
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
    }
  }

  .page-subtitle {
    margin: var(--space-sm) 0 0 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
  }
}

/* 主内容 */
.main-content {
  flex: 1;
  padding: var(--space-lg);
}

/* 筛选行 */
.filter-row {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

/* 表单提示 */
.form-tip {
  font-size: 12px;
  color: var(--iot-color-text-muted);
  margin-top: 4px;
}

/* 响应式 */
@media (max-width: 768px) {
  .email-page {
    padding: var(--space-md);
  }

  .filter-row {
    flex-direction: column;
    align-items: stretch;

    .el-input,
    .el-select {
      width: 100% !important;
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>