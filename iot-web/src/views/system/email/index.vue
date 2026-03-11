<script setup>
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
  update: '编辑',
  create: '创建',
}

const rules = {
  name: [{ required: true, message: '配置名称必填', trigger: 'blur' }],
  host: [{ required: true, message: 'SMTP服务器地址必填', trigger: 'blur' }],
  port: [{ required: true, message: 'SMTP端口必填', trigger: 'blur' }],
  username: [
    { required: true, message: '发件人邮箱必填', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [{ required: true, message: '邮箱密码必填', trigger: 'blur' }],
}

const testRules = {
  to: [
    { required: true, message: '收件人邮箱必填', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
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
    ElMessage.success('创建成功')
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
    ElMessage.success('更新成功')
    getList()
  }
}

async function handleDelete(row) {
  if (row.isDefault) {
    ElMessage.warning('默认配置不能删除')
    return
  }
  await ElMessageBox.confirm('此操作将永久删除该配置, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteEmailConfig(row.id)
  ElMessage.success('删除成功')
  getList()
}

async function handleSetDefault(row) {
  await setDefaultEmailConfig(row.id)
  ElMessage.success('设置默认配置成功')
  getList()
}

async function handleStatusChange(row) {
  await updateEmailConfigStatus(row.id, row.status)
  ElMessage.success('状态更新成功')
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
    ElMessage.success('测试邮件发送成功')
    testDialogVisible.value = false
  }
}

// 初始化
getList()
</script>

<template>
  <div class="email-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">✉</span>
          邮箱配置
        </h1>
        <p class="page-subtitle">
          配置SMTP邮箱服务，用于系统邮件通知
        </p>
      </div>
    </div>

    <!-- 主内容 -->
    <div class="main-content glass-card">
      <div class="filter-row">
        <el-input
          v-model="listQuery.name"
          placeholder="配置名称"
          style="width: 200px;"
          clearable
          @keyup.enter="handleFilter"
        />
        <el-select v-model="listQuery.status" placeholder="状态" style="width: 120px;" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="2" />
        </el-select>
        <el-button type="primary" @click="handleFilter">
          搜索
        </el-button>
        <el-button type="success" @click="handleCreate">
          添加配置
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
        <el-table-column label="配置名称">
          <template #default="scope">
            {{ scope.row.name }}
            <el-tag v-if="scope.row.isDefault" type="success" size="small">
              默认
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="SMTP服务器" prop="host" />
        <el-table-column label="端口" width="80" align="center" prop="port" />
        <el-table-column label="发件人邮箱" prop="username" />
        <el-table-column label="SSL" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sslEnabled ? 'success' : 'info'" size="small">
              {{ scope.row.sslEnabled ? '开启' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="2"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="280">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleUpdate(scope.row)">
              编辑
            </el-button>
            <el-button type="info" link size="small" @click="handleTestEmail(scope.row)">
              测试
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="success" link size="small" @click="handleSetDefault(scope.row)">
              设为默认
            </el-button>
            <el-button v-if="!scope.row.isDefault" type="danger" link size="small" @click="handleDelete(scope.row)">
              删除
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
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="temp.name" placeholder="如: 企业邮箱" />
        </el-form-item>
        <el-form-item label="SMTP服务器" prop="host">
          <el-input v-model="temp.host" placeholder="如: smtp.qq.com" />
        </el-form-item>
        <el-form-item label="SMTP端口" prop="port">
          <el-input-number v-model="temp.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="发件人邮箱" prop="username">
          <el-input v-model="temp.username" placeholder="发件人邮箱地址" />
        </el-form-item>
        <el-form-item label="邮箱密码" prop="password">
          <el-input v-model="temp.password" type="password" placeholder="邮箱密码或授权码" />
          <div class="form-tip">
            QQ邮箱、163邮箱等需要使用授权码而非登录密码
          </div>
        </el-form-item>
        <el-form-item label="启用SSL">
          <el-switch v-model="temp.sslEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 测试邮件对话框 -->
    <el-dialog v-model="testDialogVisible" title="发送测试邮件" width="500px">
      <el-form
        ref="testForm"
        :rules="testRules"
        :model="testEmailTemp"
        label-position="left"
        label-width="100px"
      >
        <el-form-item label="收件人邮箱" prop="to">
          <el-input v-model="testEmailTemp.to" placeholder="测试收件人邮箱地址" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="testDialogVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="sendTestEmailMessage">
            发送
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
