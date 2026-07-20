<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { deviceListApi, productListApi } from '@/api/index'
import {
  webhookCreateApi,
  webhookDeleteApi,
  webhookListApi,
  webhookRegenerateSecretApi,
  webhookUpdateApi,
} from '@/api/webhook'

const { t } = useI18n()
const loading = ref(false)
const list = ref([])
const products = ref([])
const devices = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const secretDialogVisible = ref(false)
const createdConfig = ref(null)

const form = reactive({
  id: null,
  name: '',
  deviceId: null,
  enabled: true,
  description: '',
})

async function loadData() {
  loading.value = true
  const [cfgRes, prodRes] = await Promise.all([
    webhookListApi({}),
    productListApi({}),
  ])
  list.value = cfgRes.data || []
  products.value = prodRes.data || []
  loading.value = false
}

async function loadDevices(productId) {
  if (!productId) {
    devices.value = []
    return
  }
  const res = await deviceListApi({ productId })
  devices.value = res.data || []
}

function getDeviceName(deviceId) {
  const d = devices.value.find(d => d.id === deviceId)
  return d ? `${d.deviceName} (${d.deviceKey})` : deviceId
}

function onAdd() {
  editing.value = false
  Object.assign(form, {
    id: null,
    name: '',
    deviceId: null,
    enabled: true,
    description: '',
  })
  dialogVisible.value = true
}

function onEdit(row) {
  editing.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    deviceId: row.deviceId,
    enabled: row.enabled,
    description: row.description || '',
  })
  dialogVisible.value = true
}

async function onSubmit() {
  const payload = {
    id: form.id,
    name: form.name,
    deviceId: form.deviceId,
    enabled: form.enabled,
    description: form.description,
  }
  if (editing.value) {
    await webhookUpdateApi(payload)
    ElMessage.success(t('common.editSuccess'))
    dialogVisible.value = false
  }
  else {
    const res = await webhookCreateApi(payload)
    createdConfig.value = res.data
    dialogVisible.value = false
    secretDialogVisible.value = true
  }
  loadData()
}

async function onDelete(id) {
  await ElMessageBox.confirm(t('common.confirmDelete'), t('common.tip'), { type: 'warning' })
  await webhookDeleteApi(id)
  ElMessage.success(t('common.deleteSuccess'))
  loadData()
}

async function onRegenerateSecret(id) {
  await ElMessageBox.confirm(t('webhook.regenerateConfirm'), t('common.tip'), { type: 'warning' })
  const res = await webhookRegenerateSecretApi(id)
  createdConfig.value = { id, secret: res.data, token: list.value.find(w => w.id === id)?.token }
  secretDialogVisible.value = true
}

function copyText(text) {
  navigator.clipboard.writeText(text)
  ElMessage.success(t('common.copySuccess'))
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          {{ t('webhook.title') }}
        </h1>
        <p class="page-subtitle">
          {{ t('webhook.subtitle') }}
        </p>
      </div>
    </div>

    <div class="action-bar">
      <el-button type="primary" @click="onAdd">
        + {{ t('webhook.add') }}
      </el-button>
    </div>

    <div class="table-wrapper glass-card">
      <el-table v-loading="loading" :data="list" border>
        <el-table-column prop="name" :label="t('webhook.name')" min-width="120" />
        <el-table-column :label="t('webhook.device')" min-width="140">
          <template #default="{ row }">
            {{ getDeviceName(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column prop="token" label="Token" min-width="200">
          <template #default="{ row }">
            <code style="font-size: 12px;">{{ row.token }}</code>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.status')" width="70">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? t('common.enabled') : t('common.disabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.actions')" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" type="primary" @click="onEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button link size="small" type="warning" @click="onRegenerateSecret(row.id)">
              {{ t('webhook.regenerateSecret') }}
            </el-button>
            <el-button link size="small" type="danger" @click="onDelete(row.id)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- Add/Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="editing ? t('common.edit') : t('webhook.add')" width="600px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item :label="t('webhook.name')">
          <el-input v-model="form.name" :placeholder="t('webhook.namePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('webhook.device')">
          <el-select v-model="form.deviceId" :placeholder="t('webhook.devicePlaceholder')" filterable style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.model" :value="p.id" @click="loadDevices(p.id)" />
          </el-select>
          <el-select v-if="form.deviceId" v-model="form.deviceId" :placeholder="t('webhook.devicePlaceholder')" filterable style="width: 100%; margin-top: 8px">
            <el-option v-for="d in devices" :key="d.id" :label="`${d.deviceName} (${d.deviceKey})`" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('webhook.description')">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item :label="t('common.enabled')">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="onSubmit">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- Secret display dialog (after create or regenerate) -->
    <el-dialog v-model="secretDialogVisible" :title="t('webhook.secretTitle')" width="600px" :close-on-click-modal="false">
      <el-alert :title="t('webhook.secretWarning')" type="warning" :closable="false" show-icon style="margin-bottom: 16px" />
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Token">
          <code>{{ createdConfig?.token }}</code>
          <el-button link type="primary" size="small" style="margin-left: 8px" @click="copyText(createdConfig?.token)">
            {{ t('common.copy') }}
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item label="Secret">
          <code>{{ createdConfig?.secret }}</code>
          <el-button link type="primary" size="small" style="margin-left: 8px" @click="copyText(createdConfig?.secret)">
            {{ t('common.copy') }}
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item :label="t('webhook.endpoint')">
          <code>POST /iot/webhook/{token}</code>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="secretDialogVisible = false">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
