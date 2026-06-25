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
  update: t('auto.system_email_index_95b351c8'),
  create: t('auto.system_email_index_d9ac9228'),
}

const rules = {
  name: [{ required: true, message: t('auto.system_email_index_5785b4eb'), trigger: 'blur' }],
  host: [{ required: true, message: t('auto.system_email_index_47983c2c'), trigger: 'blur' }],
  port: [{ required: true, message: t('auto.system_email_index_56de7cae'), trigger: 'blur' }],
  username: [
    { required: true, message: t('auto.system_email_index_d478aa6a'), trigger: 'blur' },
    { type: 'email', message: t('auto.system_email_index_75262e18'), trigger: 'blur' },
  ],
  password: [{ required: true, message: t('auto.system_email_index_7e615242'), trigger: 'blur' }],
}

const testRules = {
  to: [
    { required: true, message: t('auto.system_email_index_05423760'), trigger: 'blur' },
    { type: 'email', message: t('auto.system_email_index_75262e18'), trigger: 'blur' },
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
    ElMessage.success(t('auto.system_email_index_04a691b3'))
    getList()
  }
}

function handleUpdate(row) {
  Object.assign(temp, {
    ...row,
    password: '', // 编辑时不显示密码
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
    ElMessage.success(t('auto.system_email_index_55aa6366'))
    getList()
  }
}

async function handleDelete(row) {
  if (row.isDefault) {
    ElMessage.warning(t('auto.system_email_index_9c99996d'))
    return
  }
  await ElMessageBox.confirm(t('auto.system_email_index_76e8c282'), t('auto.system_email_index_02d9819d'), {
    confirmButtonText: t('auto.system_email_index_38cf16f2'),
    cancelButtonText: t('auto.system_email_index_625fb26b'),
    type: 'warning',
  })
  await deleteEmailConfig(row.id)
  ElMessage.success(t('auto.system_email_index_0007d170'))
  getList()
}

async function handleSetDefault(row) {
  await setDefaultEmailConfig(row.id)
  ElMessage.success(t('auto.system_email_index_6355477c'))
  getList()
}

async function handleStatusChange(row) {
  await updateEmailConfigStatus(row.id, row.status)
  ElMessage.success(t('auto.system_email_index_ece8fdff'))
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
    ElMessage.success(t('auto.system_email_index_d0e31e7f'))
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
          {{ t('auto.system_email_index_b3f77544') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.system_email_index_7edb3920') }}
        </p>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="main-content glass-card">
      <div class="filter-row">
        <el-input
          v-model="listQuery.name"
          :placeholder="t('auto.system_email_index_4fcad1c9')"
          style="width: 200px;"
          clearable
          @keyup.enter="handleFilter"
        />
        <el-select v-model="listQuery.status" :placeholder="t('auto.system_email_index_3fea7ca7')" style="width: 120px;" clearable>
          <el-option :label="t('auto.system_email_index_7854b52a')" :value="1" />
          <el-option :label="t('auto.system_email_index_710ad08b')" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleFilter">
          {{ t('auto.system_email_index_e5f71fc3') }}
        </el-button>
        <el-button type="primary" @click="handleCreate">
          {{ t('auto.system_email_index_11e5e642') }}
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
        <el-table-column :label="t('auto.system_email_index_4fcad1c9')">
          <template #default="scope">
            {{ scope.row.name }}
            <el-tag v-if="scope.row.isDefault" type="success" size="small">
              {{ t('auto.system_email_index_18c63459') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.system_email_index_4723b1de')" prop="host" />
        <el-table-column :label="t('auto.system_email_index_c76cfefe')" width="80" align="center" prop="port" />
        <el-table-column :label="t('auto.system_email_index_b9d830c2')" prop="username" />
        <el-table-column label="SSL" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sslEnabled ? 'success' : 'info'" size="small">
              {{ scope.row.sslEnabled ? t('auto.system_email_index_cc42dd31') : t('auto.system_email_index_b15d9127') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.system_email_index_3fea7ca7')" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="2"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.system_email_index_2b6bc0f2')" align="center" width="280">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleUpdate(scope.row)">
              {{ t('auto.system_email_index_95b351c8') }}
            </el-button>
            <el-button type="info" link size="small" @click="handleTestEmail(scope.row)">
              {{ t('auto.system_email_index_db06c78d') }}
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="success" link size="small" @click="handleSetDefault(scope.row)">
              {{ t('auto.system_email_index_1af3ec21') }}
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="danger" link size="small" @click="handleDelete(scope.row)">
              {{ t('auto.system_email_index_2f4aaddd') }}
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
        <el-form-item :label="t('auto.system_email_index_4fcad1c9')" prop="name">
          <el-input v-model="temp.name" :placeholder="t('auto.system_email_index_88f76a9c')" />
        </el-form-item>
        <el-form-item :label="t('auto.system_email_index_4723b1de')" prop="host">
          <el-input v-model="temp.host" :placeholder="t('auto.system_email_index_8a005771')" />
        </el-form-item>
        <el-form-item :label="t('auto.system_email_index_3ff4f51f')" prop="port">
          <el-input-number v-model="temp.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item :label="t('auto.system_email_index_b9d830c2')" prop="username">
          <el-input v-model="temp.username" :placeholder="t('auto.system_email_index_e0fbe870')" />
        </el-form-item>
        <el-form-item :label="t('auto.system_email_index_9e4d59f0')" prop="password">
          <el-input v-model="temp.password" type="password" :placeholder="t('auto.system_email_index_4eeaf5b3')" />
          <div class="form-tip">
            {{ t('auto.system_email_auth_code_tip') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('auto.system_email_index_cd2495b1')">
          <el-switch v-model="temp.sslEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ t('auto.system_email_index_625fb26b') }}
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            {{ t('auto.system_email_index_e83a256e') }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 测试邮件对话框 -->
    <el-dialog v-model="testDialogVisible" :title="t('auto.system_email_index_65803afc')" width="500px">
      <el-form
        ref="testForm"
        :rules="testRules"
        :model="testEmailTemp"
        label-position="left"
        label-width="100px"
      >
        <el-form-item :label="t('auto.system_email_index_78dd8a0c')" prop="to">
          <el-input v-model="testEmailTemp.to" :placeholder="t('auto.system_email_index_c46fb766')" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">
            {{ t('auto.system_email_index_625fb26b') }}
          </el-button>
          <el-button type="primary" @click="sendTestEmailMessage">
            {{ t('auto.system_email_index_1535fcfa') }}
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