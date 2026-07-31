<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { deviceListApi, productListApi } from '@/api/index'
import {
  webhookCreateApi,
  webhookDeleteApi,
  webhookListApi,
  webhookRegenerateSecretApi,
  webhookUpdateApi,
} from '@/api/webhook'
import PageHeader from '@/components/PageHeader.vue'
import { Link, Plus, Refresh } from '@element-plus/icons-vue'

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
const selectedProductId = ref(null)

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
  // ponytail: no productId = load all devices, not return empty
  const res = await deviceListApi(productId ? { productId } : {})
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
  loadDevices()
  selectedProductId.value = null
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
  loadDevices()
  selectedProductId.value = null
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

const webhookUrl = computed(() => {
  const origin = window.location.origin
  return `${origin}/iot/webhook/${createdConfig.value?.token || '{token}'}`
})

const curlExample = computed(() => {
  const token = createdConfig.value?.token || 'YOUR_TOKEN'
  const secret = createdConfig.value?.secret || 'YOUR_SECRET'
  const ts = Date.now()
  const body = '{"temperature":25.5,"humidity":60}'
  return `# 1. 计算签名
TS=$(date +%s%3N)
BODY='{"temperature":25.5,"humidity":60}'
SIG=$(printf '%s.%s' "$TS" "$BODY" | openssl dgst -sha256 -hmac "${secret}" | awk '{print $NF}')

# 2. 发送请求
curl -X POST "${window.location.origin}/iot/webhook/${token}" \\
  -H "Content-Type: application/json" \\
  -H "X-Siot-Timestamp: $TS" \\
  -H "X-Siot-Signature: $SIG" \\
  -d "$BODY"`
})

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <PageHeader
      :title="t('webhook.title')"
      :subtitle="t('webhook.subtitle')"
      :icon="Link"
    >
      <template #actions>
        <el-button :icon="Refresh" @click="loadData">
          {{ t('common.refresh') }}
        </el-button>
        <el-button type="primary" :icon="Plus" @click="onAdd">
          {{ t('webhook.add') }}
        </el-button>
      </template>
    </PageHeader>

    <div class="glass-card">
      <el-table v-loading="loading" :data="list" border>
        <el-table-column prop="name" :label="t('webhook.name')" min-width="120" />
        <el-table-column :label="t('webhook.device')" min-width="140">
          <template #default="{ row }">
            {{ getDeviceName(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column prop="token" label="Token" min-width="200">
          <template #default="{ row }">
            <code class="mono-text">{{ row.token }}</code>
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
        <template #empty>
          <el-empty :description="t('webhook.empty')" />
        </template>
      </el-table>
    </div>

    <!-- Add/Edit dialog -->
    <el-dialog v-model="dialogVisible" :title="editing ? t('common.edit') : t('webhook.add')" width="600px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item :label="t('webhook.name')">
          <el-input v-model="form.name" :placeholder="t('webhook.namePlaceholder')" />
        </el-form-item>
        <el-form-item :label="t('webhook.device')">
          <el-select v-model="selectedProductId" :placeholder="t('webhook.devicePlaceholder')" filterable clearable style="width: 100%; margin-bottom: 8px" @change="loadDevices(selectedProductId)">
            <el-option v-for="p in products" :key="p.id" :label="p.model" :value="p.id" />
          </el-select>
          <el-select v-model="form.deviceId" :placeholder="t('webhook.devicePlaceholder')" filterable clearable style="width: 100%">
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
      <el-alert :title="t('webhook.secretWarning')" type="warning" :closable="false" show-icon class="secret-alert" />
      <el-descriptions :column="1" border>
        <el-descriptions-item label="Token">
          <code class="mono-text">{{ createdConfig?.token }}</code>
          <el-button link type="primary" size="small" class="copy-btn" @click="copyText(createdConfig?.token)">
            {{ t('common.copy') }}
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item label="Secret">
          <code class="mono-text">{{ createdConfig?.secret }}</code>
          <el-button link type="primary" size="small" class="copy-btn" @click="copyText(createdConfig?.secret)">
            {{ t('common.copy') }}
          </el-button>
        </el-descriptions-item>
        <el-descriptions-item :label="t('webhook.endpoint')">
          <code class="mono-text">POST {{ webhookUrl }}</code>
        </el-descriptions-item>
      </el-descriptions>

      <el-divider>
        <span class="guide-title">{{ t('webhook.usageGuide') }}</span>
      </el-divider>
      <el-descriptions :column="1" border size="small">
        <el-descriptions-item label="X-Siot-Signature">
          <span class="mono-text">HMAC-SHA256(secret, timestamp + "." + body) hex</span>
        </el-descriptions-item>
        <el-descriptions-item label="X-Siot-Timestamp">
          <span class="mono-text">毫秒级时间戳，5 分钟有效</span>
        </el-descriptions-item>
      </el-descriptions>
      <pre class="code-example">{{ curlExample }}</pre>
      <template #footer>
        <el-button type="primary" @click="secretDialogVisible = false">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.mono-text {
  font-size: 12px;
}

.secret-alert {
  margin-bottom: var(--space-md);
}

.copy-btn {
  margin-left: var(--space-sm);
}

.guide-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--iot-color-text-secondary);
}

.code-example {
  margin-top: var(--space-md);
  padding: var(--space-md);
  background: var(--iot-color-bg-darker, #1a1a2e);
  border-radius: var(--radius-md, 8px);
  font-size: 12px;
  line-height: 1.6;
  color: #a5b3ce;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
