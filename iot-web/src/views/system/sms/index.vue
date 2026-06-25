<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, nextTick, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
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
const templateFormTitle = ref(t('system.sms_add_template'))
const currentConfigId = ref(null)
const dialogStatus = ref('')

const textMap = {
  update: t('common.edit'),
  create: t('system.sms_create'),
}

const rules = {
  name: [{ required: true, message: t('system.config_name_required_2'), trigger: 'blur' }],
  supplier: [{ required: true, message: t('system.supplier_required'), trigger: 'change' }],
  accessKey: [{ required: true, message: t('system.access_key_required'), trigger: 'blur' }],
  secretKey: [{ required: true, message: t('system.secret_key_required'), trigger: 'blur' }],
}

const sendRules = {
  phone: [
    { required: true, message: t('system.phone_number_required'), trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: t('system.invalid_phone_number'), trigger: 'blur' },
  ],
  message: [{ required: true, message: t('system.sms_content_required'), trigger: 'blur' }],
  templateType: [{ required: true, message: t('system.template_type_required'), trigger: 'change' }],
}

const templateRules = {
  templateType: [{ required: true, message: t('system.template_type_required'), trigger: 'change' }],
  templateName: [{ required: true, message: t('system.template_name_required'), trigger: 'blur' }],
  templateId: [{ required: true, message: t('system.id'), trigger: 'blur' }],
}

const dataForm = ref()
const sendForm = ref()
const templateForm = ref()

// 模板内容映射
const templateVariablePrefix = '$'
const templateContentMap = computed(() => ({
  verify_code: t('system.sms_template_verify_code', { prefix: templateVariablePrefix }),
  login_notify: t('system.sms_template_login_notify', { prefix: templateVariablePrefix }),
  alarm_notify: t('system.sms_template_alarm_notify', { prefix: templateVariablePrefix }),
  device_offline: t('system.sms_template_device_offline', { prefix: templateVariablePrefix }),
  system_notify: t('system.sms_template_system_notify', { prefix: templateVariablePrefix }),
}))

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
    ElMessage.success(t('system.created_successfully'))
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
    ElMessage.success(t('system.updated_successfully_2'))
    getList()
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(t('system.sms_this_operation_will_permanently_delete_configuration_continue'), t('common.tip'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning',
  })
  await deleteSmsConfig(row.id)
  ElMessage.success(t('system.sms_delete_success'))
  getList()
}

async function handleSetDefault(row) {
  await setDefaultConfig(row.id)
  ElMessage.success(t('system.default_config_set_successfully_2'))
  getList()
}

async function handleStatusChange(row) {
  await updateConfigStatus(row.id, row.status)
  ElMessage.success(t('system.status_updated_successfully_2'))
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
  templateFormTitle.value = t('system.sms_add_template')
  templateFormVisible.value = true
}

function handleEditTemplate(row) {
  Object.assign(templateTemp, row)
  templateFormTitle.value = t('system.edit_template')
  templateFormVisible.value = true
}

// 模板类型变化时自动填充内容
function handleTemplateTypeChange() {
  if (templateTemp.templateType && templateContentMap.value[templateTemp.templateType]) {
    templateTemp.templateContent = templateContentMap.value[templateTemp.templateType]
  }
}

async function saveTemplate() {
  const valid = await templateForm.value?.validate()
  if (valid) {
    const action = templateTemp.id ? updateSmsTemplate : addSmsTemplate
    await action(templateTemp)
    templateFormVisible.value = false
    ElMessage.success(t('system.saved_successfully'))
    getTemplateList()
  }
}

async function handleDeleteTemplate(row) {
  await ElMessageBox.confirm(t('system.this_operation_will_permanently_delete_template_continue'), t('common.tip'), {
    confirmButtonText: t('common.confirm'),
    cancelButtonText: t('common.cancel'),
    type: 'warning',
  })
  await deleteSmsTemplate(row.id)
  ElMessage.success(t('system.sms_delete_success'))
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
          ElMessage.error(t('system.template_parameter_format_error'))
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
    ElMessage.success(t('system.sms_sent_successfully'))
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
      <el-tab-pane :label="t('system.sms_config')" name="config">
        <div class="filter-container">
          <el-input
            v-model="listQuery.name"
            :placeholder="t('push_config.config_name')"
            style="width: 200px;"
            class="filter-item"
            @keyup.enter="handleFilter"
          />
          <el-button class="filter-item" type="primary" @click="handleFilter">
            {{ t('common.search') }}
          </el-button>
          <el-button class="filter-item" style="margin-left: 10px;" type="primary" @click="handleCreate">
            {{ t('system.sms_system_sms_add') }}
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
          <el-table-column :label="t('push_config.config_name')">
            <template #default="scope">
              {{ scope.row.name }}
              <el-tag v-if="scope.row.isDefault" type="success" size="small">
                {{ t('system.default_2') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column :label="t('system.provider')" width="110" align="center">
            <template #default="scope">
              <span>{{ getSupplierName(scope.row.supplier) }}</span>
            </template>
          </el-table-column>
          <el-table-column :label="t('system.signature')" width="110" align="center">
            <template #default="scope">
              {{ scope.row.signName }}
            </template>
          </el-table-column>
          <el-table-column :label="t('common.status')" width="110" align="center">
            <template #default="scope">
              <el-switch
                v-model="scope.row.status"
                :active-value="1"
                :inactive-value="2"
                @change="handleStatusChange(scope.row)"
              />
            </template>
          </el-table-column>
          <el-table-column :label="t('common.operation')" align="center" width="280" class-name="small-padding fixed-width">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleUpdate(scope.row)">
                {{ t('common.edit') }}
              </el-button>
              <el-button type="info" size="small" @click="handleTemplate(scope.row)">
                {{ t('system.template') }}
              </el-button>
              <el-button v-if="!scope.row.isDefault" type="success" size="small" @click="handleSetDefault(scope.row)">
                {{ t('system.set_default_2') }}
              </el-button>
              <el-button v-if="!scope.row.isDefault" size="small" type="danger" @click="handleDelete(scope.row)">
                {{ t('common.delete') }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane :label="t('system.sms_send_sms')" name="send">
        <el-form
          ref="sendForm"
          :rules="sendRules"
          :model="sendTemp"
          label-position="left"
          label-width="100px"
          style="width: 500px;"
        >
          <el-form-item :label="t('system.phone_number')" prop="phone">
            <el-input v-model="sendTemp.phone" />
          </el-form-item>
          <el-form-item :label="t('system.send_method')" prop="sendType">
            <el-radio-group v-model="sendTemp.sendType">
              <el-radio value="text">
                {{ t('system.text_message') }}
              </el-radio>
              <el-radio value="template">
                {{ t('system.template_message') }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'text'" :label="t('system.sms_content')" prop="message">
            <el-input v-model="sendTemp.message" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" :label="t('system.template_type')" prop="templateType">
            <el-select v-model="sendTemp.templateType" :placeholder="t('system.select_template_type')">
              <el-option
                v-for="item in templateTypes"
                :key="item.code"
                :label="item.desc"
                :value="item.code"
              />
            </el-select>
          </el-form-item>
          <el-form-item v-if="sendTemp.sendType === 'template'" :label="t('system.template_parameters')">
            <el-input v-model="sendTemp.templateParamsStr" type="textarea" :rows="2" placeholder="{&quot;code&quot;:&quot;123456&quot;}" />
          </el-form-item>
          <el-form-item :label="t('common.config')">
            <el-select v-model="sendTemp.configId" :placeholder="t('system.use_default_config')" clearable>
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
              {{ t('system.send_sms') }}
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
        <el-form-item :label="t('push_config.config_name')" prop="name">
          <el-input v-model="temp.name" />
        </el-form-item>
        <el-form-item :label="t('system.provider')" prop="supplier">
          <el-select v-model="temp.supplier" :placeholder="t('system.select_supplier')" @change="handleSupplierChange">
            <el-option
              v-for="item in suppliers"
              :key="item.code"
              :label="item.name"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('system.access_key')" prop="accessKey">
          <el-input v-model="temp.accessKey" />
        </el-form-item>
        <el-form-item :label="t('system.secret_key')" prop="secretKey">
          <el-input v-model="temp.secretKey" type="password" />
        </el-form-item>
        <el-form-item :label="t('system.sms_signature')" prop="signName">
          <el-input v-model="temp.signName" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'tencent'" :label="t('system.sms_region')" prop="region">
          <el-input v-model="temp.region" :placeholder="t('system.ap_beijing')" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'huawei'" :label="t('system.endpoint')" prop="endpoint">
          <el-input v-model="temp.endpoint" :placeholder="t('system.https_smsapi_cn_north_4_myhuaweicloud_com_443')" />
        </el-form-item>
        <el-form-item v-if="temp.supplier === 'alibaba'" :label="t('system.app_id')">
          <el-input v-model="temp.configJson.appId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogFormVisible = false">
            {{ t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="dialogStatus === 'create' ? createData() : updateData()">
            {{ t('system.sms_confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 模板管理对话框 -->
    <el-dialog v-model="templateDialogVisible" :title="t('system.template_management')" width="800px">
      <div style="margin-bottom: 20px;">
        <el-button type="primary" @click="handleAddTemplate">
          {{ t('system.add_template') }}
        </el-button>
      </div>
      <el-table :data="templateList" border>
        <el-table-column :label="t('system.template_type')" width="120">
          <template #default="scope">
            {{ getTemplateTypeName(scope.row.templateType) }}
          </template>
        </el-table-column>
        <el-table-column :label="t('system.template_name')" prop="templateName" />
        <el-table-column :label="t('system.template_id')" prop="templateId" />
        <el-table-column :label="t('system.template_content')" prop="templateContent" />
        <el-table-column :label="t('common.status')" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? t('common.enable') : t('system.sms_disable') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" width="150">
          <template #default="scope">
            <el-button size="small" @click="handleEditTemplate(scope.row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button size="small" type="danger" @click="handleDeleteTemplate(scope.row)">
              {{ t('common.delete') }}
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
        <el-form-item :label="t('system.template_type')" prop="templateType">
          <el-select v-model="templateTemp.templateType" :placeholder="t('system.select_template_type')" @change="handleTemplateTypeChange">
            <el-option
              v-for="item in templateTypes"
              :key="item.code"
              :label="item.desc"
              :value="item.code"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('system.template_name')" prop="templateName">
          <el-input v-model="templateTemp.templateName" />
        </el-form-item>
        <el-form-item :label="t('system.template_id')" prop="templateId">
          <el-input v-model="templateTemp.templateId" />
        </el-form-item>
        <el-form-item :label="t('system.template_content')" prop="templateContent">
          <el-input v-model="templateTemp.templateContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="templateFormVisible = false">
            {{ t('common.cancel') }}
          </el-button>
          <el-button type="primary" @click="saveTemplate">
            {{ t('system.sms_confirm') }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
