<script setup>
import { getSmsSupplier, getSmsTemplateType } from '@/api/dict'
import { addSmsConfig, addSmsTemplate, deleteSmsConfig, deleteSmsTemplate, getSmsConfigList, getSmsTemplateList, sendSms, sendSmsWithTemplate, setDefaultConfig, updateConfigStatus, updateSmsConfig, updateSmsTemplate } from '@/api/sms'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, reactive, ref } from 'vue'

const activeTab = ref('config')
const listLoading = ref(true)
const list = ref([])
const suppliers = ref([])
const templateTypes = ref([])
const templateList = ref([])

const listQuery = reactive({
  name: undefined,
})

const temp = reactive({
  id: undefined,
  name: '',
  supplier: '',
  accessKey: '',
  secretKey: '',
  signName: '',
  endpoint: '',
  region: '',
  configJson: {},
})

const sendTemp = reactive({
  phone: '',
  message: '',
  sendType: 'text',
  templateType: '',
  templateParamsStr: '',
  configId: undefined,
})

const templateTemp = reactive({
  id: undefined,
  configId: undefined,
  templateType: '',
  templateId: '',
  templateName: '',
  templateContent: '',
})

const dialogFormVisible = ref(false)
const templateDialogVisible = ref(false)
const templateFormVisible = ref(false)
const templateFormTitle = ref('添加模板')
const currentConfigId = ref(null)
const dialogStatus = ref('')

const textMap = {
  update: '编辑',
  create: '创建',
}

const rules = {
  name: [{ required: true, message: '配置名称必填', trigger: 'blur' }],
  supplier: [{ required: true, message: '供应商必选', trigger: 'change' }],
  accessKey: [{ required: true, message: '访问密钥必填', trigger: 'blur' }],
  secretKey: [{ required: true, message: '密钥必填', trigger: 'blur' }],
}

const sendRules = {
  phone: [
    { required: true, message: '手机号必填', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  message: [{ required: true, message: '短信内容必填', trigger: 'blur' }],
  templateType: [{ required: true, message: '模板类型必选', trigger: 'change' }],
}

const templateRules = {
  templateType: [{ required: true, message: '模板类型必选', trigger: 'change' }],
  templateName: [{ required: true, message: '模板名称必填', trigger: 'blur' }],
  templateId: [{ required: true, message: '模板ID必填', trigger: 'blur' }],
}

const dataForm = ref()
const sendForm = ref()
const templateForm = ref()

async function getList() {
  listLoading.value = true
  try {
    const response = await getSmsConfigList(listQuery)
    list.value = response.data
  }
  finally {
    listLoading.value = false
  }
}

async function getSuppliers() {
  const response = await getSmsSupplier()
  suppliers.value = response.data
}

async function getTemplateTypes() {
  const response = await getSmsTemplateType()
  templateTypes.value = response.data
}

function getSupplierName(code) {
  const supplier = suppliers.value.find(s => s.code === code)
  return supplier ? supplier.name : code
}

function getTemplateTypeName(code) {
  const type = templateTypes.value.find(t => t.code === code)
  return type ? type.desc : code
}

function handleFilter() {
  getList()
}

function resetTemp() {
  Object.assign(temp, {
    id: undefined,
    name: '',
    supplier: '',
    accessKey: '',
    secretKey: '',
    signName: '',
    endpoint: '',
    region: '',
    configJson: {},
  })
}

function handleSupplierChange() {
  temp.configJson = {}
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
    await addSmsConfig(temp)
    dialogFormVisible.value = false
    ElMessage.success('创建成功')
    getList()
  }
}

function handleUpdate(row) {
  Object.assign(temp, row)
  if (!temp.configJson) {
    temp.configJson = {}
  }
  dialogStatus.value = 'update'
  dialogFormVisible.value = true
  nextTick(() => {
    dataForm.value?.clearValidate()
  })
}

async function updateData() {
  const valid = await dataForm.value?.validate()
  if (valid) {
    await updateSmsConfig(temp)
    dialogFormVisible.value = false
    ElMessage.success('更新成功')
    getList()
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('此操作将永久删除该配置, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteSmsConfig(row.id)
  ElMessage.success('删除成功')
  getList()
}

async function handleSetDefault(row) {
  await setDefaultConfig(row.id)
  ElMessage.success('设置默认配置成功')
  getList()
}

async function handleStatusChange(row) {
  await updateConfigStatus(row.id, row.status)
  ElMessage.success('状态更新成功')
}

function handleTemplate(row) {
  currentConfigId.value = row.id
  templateDialogVisible.value = true
  getTemplateList()
}

async function getTemplateList() {
  const response = await getSmsTemplateList(currentConfigId.value)
  templateList.value = response.data
}

function handleAddTemplate() {
  Object.assign(templateTemp, {
    id: undefined,
    configId: currentConfigId.value,
    templateType: '',
    templateId: '',
    templateName: '',
    templateContent: '',
  })
  templateFormTitle.value = '添加模板'
  templateFormVisible.value = true
}

function handleEditTemplate(row) {
  Object.assign(templateTemp, row)
  templateFormTitle.value = '编辑模板'
  templateFormVisible.value = true
}

// 模板类型变化时自动填充内容
async function handleTemplateTypeChange() {
  if (templateTemp.templateType && templateTemp.configId) {
    // 1. 先查数据库模板
    const response = await getSmsTemplateList(templateTemp.configId)
    const found = response.data.find(t => t.templateType === templateTemp.templateType)
    if (found) {
      templateTemp.templateContent = found.templateContent
    }
    else {
      // 2. 数据库没有，再查枚举接口，取默认模板内容
      const type = templateTypes.value.find(t => t.code === templateTemp.templateType)
      templateTemp.templateContent = type ? type.template : ''
    }
  }
  else {
    templateTemp.templateContent = ''
  }
}

async function saveTemplate() {
  const valid = await templateForm.value?.validate()
  if (valid) {
    const action = templateTemp.id ? updateSmsTemplate : addSmsTemplate
    await action(templateTemp)
    templateFormVisible.value = false
    ElMessage.success('保存成功')
    getTemplateList()
  }
}

async function handleDeleteTemplate(row) {
  await ElMessageBox.confirm('此操作将永久删除该模板, 是否继续?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteSmsTemplate(row.id)
  ElMessage.success('删除成功')
  getTemplateList()
}

async function sendSmsMessage() {
  const valid = await sendForm.value?.validate()
  if (valid) {
    if (sendTemp.sendType === 'text') {
      const data = {
        phone: sendTemp.phone,
        message: sendTemp.message,
        configId: sendTemp.configId,
      }
      await sendSms(data)
    }
    else {
      let templateParams = {}
      if (sendTemp.templateParamsStr) {
        try {
          templateParams = JSON.parse(sendTemp.templateParamsStr)
        }
        catch (e) {
          ElMessage.error('模板参数格式错误')
          return
        }
      }
      const data = {
        phone: sendTemp.phone,
        templateType: sendTemp.templateType,
        templateParams,
        configId: sendTemp.configId,
      }
      await sendSmsWithTemplate(data)
    }
    ElMessage.success('短信发送成功')
  }
}

// 初始化
getList()
getSuppliers()
getTemplateTypes()
</script>

<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane label="短信配置" name="config">
        <div class="filter-container">
          <el-input
            v-model="listQuery.name"
            placeholder="配置名称"
            style="width: 200px;"
            class="filter-item"
            @keyup.enter="handleFilter"
          />
          <el-button class="filter-item" type="primary" @click="handleFilter">
            搜索
          </el-button>
          <el-button class="filter-item" style="margin-left: 10px;" type="primary" @click="handleCreate">
            添加
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
          <el-table-column align="center" label="ID" width="95">
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
          <el-table-column label="供应商" width="110" align="center">
            <template #default="scope">
              <span>{{ getSupplierName(scope.row.supplier) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="签名" width="110" align="center">
            <template #default="scope">
              {{ scope.row.signName }}
            </template>
          </el-table-column>
          <el-table-column label="状态" width="110" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="2"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="280" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
                编辑
              </el-button>
              <el-button type="info" size="small" @click="handleTemplate(scope.row)">
                模板
              </el-button>
              <el-button v-if="!scope.row.isDefault" type="success" size="small" @click="handleSetDefault(scope.row)">
                设为默认
              </el-button>
              <el-button v-if="!scope.row.isDefault" size="small" type="danger" @click="handleDelete(scope.row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="发送短信" name="send">
        <el-form
          ref="sendForm"
          :rules="sendRules"
          :model="sendTemp"
          label-position="left"
          label-width="100px"
          style="width: 500px;"
        >
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="sendTemp.phone" />
          </el-form-item>
          <el-form-item label="发送方式" prop="sendType">
            <el-radio-group v-model="sendTemp.sendType">
              <el-radio value="text">
                文本消息
              </el-radio>
              <el-radio value="template">
                模板消息
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'text'" label="短信内容" prop="message">
            <el-input v-model="sendTemp.message" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" label="模板类型" prop="templateType">
            <el-select v-model="sendTemp.templateType" placeholder="请选择模板类型">
              <el-option
                v-for="item in templateTypes"
                :key="item.code"
                :label="item.desc"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" label="模板参数">
            <el-input v-model="sendTemp.templateParamsStr" type="textarea" :rows="2" placeholder="{&quot;code&quot;:&quot;123456&quot;}" />
          </el-form-item>
          <el-form-item label="配置">
            <el-select v-model="sendTemp.configId" placeholder="使用默认配置" clearable>
              <el-option
                v-for="item in list"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="sendSmsMessage">
              发送短信
            </el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <!-- 配置对话框 -->
    <el-dialog v-model="dialogFormVisible" :title="textMap[dialogStatus]" width="600px">
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="temp"
        label-position="left"
        label-width="100px"
      >
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-select v-model="temp.supplier" placeholder="请选择供应商" @change="handleSupplierChange">
            <el-option
              v-for="item in suppliers"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="访问密钥" prop="accessKey">
          <el-input v-model="temp.accessKey" />
        </el-form-item>
        <el-form-item label="密钥" prop="secretKey">
          <el-input v-model="temp.secretKey" type="password" />
        </el-form-item>
        <el-form-item label="短信签名" prop="signName">
          <el-input v-model="temp.signName" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'tencent'" label="区域" prop="region">
          <el-input v-model="temp.region" placeholder="如: ap-beijing" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'huawei'" label="接入点" prop="endpoint">
          <el-input v-model="temp.endpoint" placeholder="如: https://smsapi.cn-north-4.myhuaweicloud.com:443" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'alibaba'" label="应用ID">
          <el-input v-model="temp.configJson.appId" />
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

    <!-- 模板管理对话框 -->
    <el-dialog v-model="templateDialogVisible" title="模板管理" width="800px">
      <div style="margin-bottom: 20px;">
        <el-button type="primary" @click="handleAddTemplate">
          添加模板
        </el-button>
      </div>
      <el-table :data="templateList" border>
        <el-table-column label="模板类型" width="120">
          <template #default="scope">
            {{ getTemplateTypeName(scope.row.templateType) }}
          </template>
        </el-table-column>
        <el-table-column label="模板名称" prop="templateName" />
        <el-table-column label="模板ID" prop="templateId" />
        <el-table-column label="模板内容" prop="templateContent" />
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEditTemplate(scope.row)">
              编辑
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteTemplate(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 模板编辑对话框 -->
    <el-dialog v-model="templateFormVisible" :title="templateFormTitle" width="500px">
      <el-form
        ref="templateForm"
        :rules="templateRules"
        :model="templateTemp"
        label-position="left"
        label-width="100px"
      >
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="templateTemp.templateType" placeholder="请选择模板类型" @change="handleTemplateTypeChange">
            <el-option
              v-for="item in templateTypes"
              :key="item.code"
              :label="item.desc"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="templateTemp.templateName" />
        </el-form-item>
        <el-form-item label="模板ID" prop="templateId">
          <el-input v-model="templateTemp.templateId" />
        </el-form-item>
        <el-form-item label="模板内容" prop="templateContent">
          <el-input v-model="templateTemp.templateContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="templateFormVisible = false">
            取消
          </el-button>
          <el-button type="primary" @click="saveTemplate">
            确认
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
