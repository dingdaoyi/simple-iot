<script setup>
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { nextTick, reactive, ref } from 'vue'
import { getSmsSupplier, getSmsTemplateType } from '@/api/dict'
import { addSmsConfig, addSmsTemplate, deleteSmsConfig, deleteSmsTemplate, getSmsConfigList, getSmsTemplateList, sendSms, sendSmsWithTemplate, setDefaultConfig, updateConfigStatus, updateSmsConfig, updateSmsTemplate } from '@/api/sms'

const { t } = useI18n()
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
const templateFormTitle = ref(t('auto.system_sms_index_6fef15ad'))
const currentConfigId = ref(null)
const dialogStatus = ref('')

const textMap = {
  update: t('auto.system_sms_index_95b351c8'),
  create: t('auto.system_sms_index_d9ac9228'),
}

const rules = {
  name: [{ required: true, message: t('auto.system_sms_index_5785b4eb'), trigger: 'blur' }],
  supplier: [{ required: true, message: t('auto.system_sms_index_711d0fed'), trigger: 'change' }],
  accessKey: [{ required: true, message: t('auto.system_sms_index_8ba0ccb6'), trigger: 'blur' }],
  secretKey: [{ required: true, message: t('auto.system_sms_index_468c7d30'), trigger: 'blur' }],
}

const sendRules = {
  phone: [
    { required: true, message: t('auto.system_sms_index_a026b8f6'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('auto.system_sms_index_19c896fe'), trigger: 'blur' },
  ],
  message: [{ required: true, message: t('auto.system_sms_index_234a13b6'), trigger: 'blur' }],
  templateType: [{ required: true, message: t('auto.system_sms_index_41252c43'), trigger: 'change' }],
}

const templateRules = {
  templateType: [{ required: true, message: t('auto.system_sms_index_41252c43'), trigger: 'change' }],
  templateName: [{ required: true, message: t('auto.system_sms_index_ea85b58c'), trigger: 'blur' }],
  templateId: [{ required: true, message: t('auto.system_sms_index_81830041'), trigger: 'blur' }],
}

const dataForm = ref()
const sendForm = ref()
const templateForm = ref()

// 模板内容映射
const templateVariablePrefix = '$'
const templateContentMap = {
  verify_code: `您的验证码是${templateVariablePrefix}{code}，5分钟内有效`,
  login_notify: `您的账号于${templateVariablePrefix}{time}在${templateVariablePrefix}{location}登录`,
  alarm_notify: `设备${templateVariablePrefix}{deviceName}发生${templateVariablePrefix}{alarmType}告警`,
  device_offline: `设备${templateVariablePrefix}{deviceName}已离线，请及时处理`,
  system_notify: `系统通知：${templateVariablePrefix}{message}`,
}

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
    ElMessage.success(t('auto.system_sms_index_04a691b3'))
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
    ElMessage.success(t('auto.system_sms_index_55aa6366'))
    getList()
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(t('auto.system_sms_index_76e8c282'), t('auto.system_sms_index_02d9819d'), {
    confirmButtonText: t('auto.system_sms_index_38cf16f2'),
    cancelButtonText: t('auto.system_sms_index_625fb26b'),
    type: 'warning',
  })
  await deleteSmsConfig(row.id)
  ElMessage.success(t('auto.system_sms_index_0007d170'))
  getList()
}

async function handleSetDefault(row) {
  await setDefaultConfig(row.id)
  ElMessage.success(t('auto.system_sms_index_6355477c'))
  getList()
}

async function handleStatusChange(row) {
  await updateConfigStatus(row.id, row.status)
  ElMessage.success(t('auto.system_sms_index_ece8fdff'))
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
  templateFormTitle.value = t('auto.system_sms_index_6fef15ad')
  templateFormVisible.value = true
}

function handleEditTemplate(row) {
  Object.assign(templateTemp, row)
  templateFormTitle.value = t('auto.system_sms_index_c6aa3504')
  templateFormVisible.value = true
}

// 模板类型变化时自动填充内容
function handleTemplateTypeChange() {
  if (templateTemp.templateType && templateContentMap[templateTemp.templateType]) {
    templateTemp.templateContent = templateContentMap[templateTemp.templateType]
  }
}

async function saveTemplate() {
  const valid = await templateForm.value?.validate()
  if (valid) {
    const action = templateTemp.id ? updateSmsTemplate : addSmsTemplate
    await action(templateTemp)
    templateFormVisible.value = false
    ElMessage.success(t('auto.system_sms_index_3b108349'))
    getTemplateList()
  }
}

async function handleDeleteTemplate(row) {
  await ElMessageBox.confirm(t('auto.system_sms_index_a32098b4'), t('auto.system_sms_index_02d9819d'), {
    confirmButtonText: t('auto.system_sms_index_38cf16f2'),
    cancelButtonText: t('auto.system_sms_index_625fb26b'),
    type: 'warning',
  })
  await deleteSmsTemplate(row.id)
  ElMessage.success(t('auto.system_sms_index_0007d170'))
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
        catch {
          ElMessage.error(t('auto.system_sms_index_a73c4dde'))
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
    ElMessage.success(t('auto.system_sms_index_b3136072'))
  }
}

// 初始化
getList()
getSuppliers()
getTemplateTypes()</script>

<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" type="card">
      <el-tab-pane :label="t('auto.system_sms_index_2e23452d')" name="config">
        <div class="filter-container">
          <el-input
            v-model="listQuery.name"
            :placeholder="t('auto.system_sms_index_4fcad1c9')"
            style="width: 200px;"
            class="filter-item"
            @keyup.enter="handleFilter"
          />
          <el-button class="filter-item" type="primary" @click="handleFilter">
            {{ t('auto.system_sms_index_e5f71fc3') }}
          </el-button>
          <el-button class="filter-item" style="margin-left: 10px;" type="primary" @click="handleCreate">
            {{ t('auto.system_sms_index_b58c7549') }}
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
          <el-table-column :label="t('auto.system_sms_index_4fcad1c9')">
            <template #default="scope">
              {{ scope.row.name }}
              <el-tag v-if="scope.row.isDefault" type="success" size="small">
                {{ t('auto.system_sms_index_18c63459') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="t('auto.system_sms_index_bab268d5')" width="110" align="center">
            <template #default="scope">
              <span>{{ getSupplierName(scope.row.supplier) }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="t('auto.system_sms_index_be2525eb')" width="110" align="center">
            <template #default="scope">
              {{ scope.row.signName }}
            </template>
          </el-table-column>
          <el-table-column :label="t('auto.system_sms_index_3fea7ca7')" width="110" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="2"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('auto.system_sms_index_2b6bc0f2')" align="center" width="280" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
                {{ t('auto.system_sms_index_95b351c8') }}
              </el-button>
              <el-button type="info" size="small" @click="handleTemplate(scope.row)">
                {{ t('auto.system_sms_index_59cf15fe') }}
              </el-button>
              <el-button v-if="!scope.row.isDefault" type="success" size="small" @click="handleSetDefault(scope.row)">
                {{ t('auto.system_sms_index_1af3ec21') }}
              </el-button>
              <el-button v-if="!scope.row.isDefault" size="small" type="danger" @click="handleDelete(scope.row)">
                {{ t('auto.system_sms_index_2f4aaddd') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="t('auto.system_sms_index_2d728886')" name="send">
        <el-form
          ref="sendForm"
          :rules="sendRules"
          :model="sendTemp"
          label-position="left"
          label-width="100px"
          style="width: 500px;"
        >
          <el-form-item :label="t('auto.system_sms_index_8098e2b4')" prop="phone">
            <el-input v-model="sendTemp.phone" />
          </el-form-item>
          <el-form-item :label="t('auto.system_sms_index_6aa351f5')" prop="sendType">
            <el-radio-group v-model="sendTemp.sendType">
              <el-radio value="text">
                {{ t('auto.system_sms_text_message') }}
              </el-radio>
              <el-radio value="template">
                {{ t('auto.system_sms_template_message') }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'text'" :label="t('auto.system_sms_index_4e963cc0')" prop="message">
            <el-input v-model="sendTemp.message" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" :label="t('auto.system_sms_index_bfd70885')" prop="templateType">
            <el-select v-model="sendTemp.templateType" :placeholder="t('auto.system_sms_index_2c57063c')">
              <el-option
                v-for="item in templateTypes"
                :key="item.code"
                :label="item.desc"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" :label="t('auto.system_sms_index_6fc05af5')">
            <el-input v-model="sendTemp.templateParamsStr" type="textarea" :rows="2" placeholder="{&quot;code&quot;:&quot;123456&quot;}" />
          </el-form-item>
          <el-form-item :label="t('auto.system_sms_index_224e2ccd')">
            <el-select v-model="sendTemp.configId" :placeholder="t('auto.system_sms_index_3248aac7')" clearable>
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
              {{ t('auto.system_sms_send_sms') }}
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
        <el-form-item :label="t('auto.system_sms_index_4fcad1c9')" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_bab268d5')" prop="supplier">
          <el-select v-model="temp.supplier" :placeholder="t('auto.system_sms_index_4ceb3942')" @change="handleSupplierChange">
            <el-option
              v-for="item in suppliers"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_ec3720b6')" prop="accessKey">
          <el-input v-model="temp.accessKey" />
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_cdb81cf6')" prop="secretKey">
          <el-input v-model="temp.secretKey" type="password" />
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_f32c04f9')" prop="signName">
          <el-input v-model="temp.signName" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'tencent'" :label="t('auto.system_sms_index_d3ce40d8')" prop="region">
          <el-input v-model="temp.region" :placeholder="t('auto.system_sms_index_1223875e')" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'huawei'" :label="t('auto.system_sms_index_95e6190e')" prop="endpoint">
          <el-input v-model="temp.endpoint" :placeholder="t('auto.system_sms_index_3c61b6ac')" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'alibaba'" :label="t('auto.system_sms_index_3751f65f')">
          <el-input v-model="temp.configJson.appId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ t('auto.system_sms_index_625fb26b') }}
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            {{ t('auto.system_sms_index_e83a256e') }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 模板管理对话框 -->
    <el-dialog v-model="templateDialogVisible" :title="t('auto.system_sms_index_f19bc873')" width="800px">
      <div style="margin-bottom: 20px;">
        <el-button type="primary" @click="handleAddTemplate">
          {{ t('auto.system_sms_add_template') }}
        </el-button>
      </div>
      <el-table :data="templateList" border>
        <el-table-column :label="t('auto.system_sms_index_bfd70885')" width="120">
          <template #default="scope">
            {{ getTemplateTypeName(scope.row.templateType) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.system_sms_index_a5d1c511')" prop="templateName" />
        <el-table-column :label="t('auto.system_sms_index_5cb25268')" prop="templateId" />
        <el-table-column :label="t('auto.system_sms_index_03ae7940')" prop="templateContent" />
        <el-table-column :label="t('auto.system_sms_index_3fea7ca7')" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? t('auto.system_sms_index_7854b52a') : t('auto.system_sms_index_710ad08b') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.system_sms_index_2b6bc0f2')" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEditTemplate(scope.row)">
              {{ t('auto.system_sms_index_95b351c8') }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteTemplate(scope.row)">
              {{ t('auto.system_sms_index_2f4aaddd') }}
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
        <el-form-item :label="t('auto.system_sms_index_bfd70885')" prop="templateType">
          <el-select v-model="templateTemp.templateType" :placeholder="t('auto.system_sms_index_2c57063c')" @change="handleTemplateTypeChange">
            <el-option
              v-for="item in templateTypes"
              :key="item.code"
              :label="item.desc"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_a5d1c511')" prop="templateName">
          <el-input v-model="templateTemp.templateName" />
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_5cb25268')" prop="templateId">
          <el-input v-model="templateTemp.templateId" />
        </el-form-item>
        <el-form-item :label="t('auto.system_sms_index_03ae7940')" prop="templateContent">
          <el-input v-model="templateTemp.templateContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="templateFormVisible = false">
            {{ t('auto.system_sms_index_625fb26b') }}
          </el-button>
          <el-button type="primary" @click="saveTemplate">
            {{ t('auto.system_sms_index_e83a256e') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>