<script setup>
import { ElMessage } from 'element-plus'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { pushConfigAddApi, pushConfigEditApi } from '@/api/index.js'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'modelValue', 'title'])
const emits = defineEmits(['update', 'update:modelValue'])
const { t } = useI18n()
const typeOpt = [
  { label: t('push_config.http'), value: 'HTTP' },
  { label: t('push_config.dialog_mqtt'), value: 'MQTT' },
]

const methodOpt = [
  { label: 'POST', value: 'POST' },
  { label: 'GET', value: 'GET' },
  { label: 'PUT', value: 'PUT' },
]

const qosOpt = [
  { label: t('push_config.text_0_at_most_once'), value: 0 },
  { label: t('push_config.text'), value: 1 },
  { label: t('push_config.dialog_text'), value: 2 },
]

const blankToEmpty = value => `${value ?? ''}`.trim()

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? pushConfigEditApi : pushConfigAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

const isHttp = computed(() => form.value.type === 'HTTP')
const isMqtt = computed(() => form.value.type === 'MQTT')

function validateHttpUrl(_rule, value, callback) {
  const url = blankToEmpty(value)
  if (!isHttp.value) {
    callback()
    return
  }
  if (!url) {
    callback(new Error(t('push_config.request_url_cannot_be_empty')))
    return
  }
  if (!/^https?:\/\//i.test(url)) {
    callback(new Error(t('push_config.request_url_must_start_http_https')))
    return
  }
  callback()
}

function validateMqttBroker(_rule, value, callback) {
  const broker = blankToEmpty(value)
  if (!isMqtt.value) {
    callback()
    return
  }
  if (!broker) {
    callback(new Error(t('push_config.broker_address_cannot_be_empty')))
    return
  }
  if (!/^(?:tcp|ssl|ws|wss):\/\//i.test(broker)) {
    callback(new Error(t('push_config.broker_address_must_start_tcp_ssl_ws_wss')))
    return
  }
  callback()
}

function validateMqttTopic(_rule, value, callback) {
  const topic = blankToEmpty(value)
  if (!isMqtt.value) {
    callback()
    return
  }
  if (!topic) {
    callback(new Error(t('push_config.target_topic_cannot_be_empty')))
    return
  }
  if (topic.includes('#') || topic.includes('+')) {
    callback(new Error(t('push_config.publish_topic_cannot_contain_wildcards')))
    return
  }
  callback()
}

const rules = ref({
  name: [{ required: true, message: t('push_config.config_name_required'), trigger: 'blur' }],
  type: [{ required: true, message: t('push_config.config_type_required'), trigger: 'change' }],
  httpUrl: [{ validator: validateHttpUrl, trigger: 'blur' }],
  httpMethod: [{ required: true, message: t('push_config.request_method_required'), trigger: 'change' }],
  mqttBroker: [{ validator: validateMqttBroker, trigger: 'blur' }],
  mqttTopic: [{ validator: validateMqttTopic, trigger: 'blur' }],
  mqttQos: [{ required: true, message: t('push_config.qos_level_required'), trigger: 'change' }],
})

// HTTP Headers 数组
const httpHeaders = ref([])

// MQTT Options 数组
const mqttOptions = ref([])

function normalizeKeyValueList(list) {
  const normalized = []
  for (const item of list) {
    const key = blankToEmpty(item.key)
    const value = blankToEmpty(item.value)
    if (!key && !value) {
      continue
    }
    if (!key || !value) {
      return null
    }
    normalized.push({ key, value })
  }
  return normalized
}

function normalizeFormBeforeSubmit() {
  const submitData = {
    ...form.value,
    name: blankToEmpty(form.value.name),
    description: blankToEmpty(form.value.description),
  }

  if (isHttp.value) {
    const headers = normalizeKeyValueList(httpHeaders.value)
    if (!headers) {
      ElMessage.warning(t('push_config.key_value'))
      return false
    }
    Object.assign(submitData, {
      httpUrl: blankToEmpty(form.value.httpUrl),
      httpMethod: form.value.httpMethod || 'POST',
      httpHeaders: headers,
      httpTimeout: form.value.httpTimeout || 5000,
      mqttBroker: '',
      mqttUsername: '',
      mqttPassword: '',
      mqttClientId: '',
      mqttTopic: '',
      mqttQos: 0,
      mqttRetain: false,
      mqttKeepAlive: 60,
      mqttCleanSession: true,
      mqttOptions: [],
    })
  }

  if (isMqtt.value) {
    const options = normalizeKeyValueList(mqttOptions.value)
    if (!options) {
      ElMessage.warning(t('push_config.mqtt_key_value'))
      return false
    }
    Object.assign(submitData, {
      mqttBroker: blankToEmpty(form.value.mqttBroker),
      mqttUsername: blankToEmpty(form.value.mqttUsername),
      mqttPassword: form.value.mqttPassword || '',
      mqttClientId: blankToEmpty(form.value.mqttClientId),
      mqttTopic: blankToEmpty(form.value.mqttTopic),
      mqttQos: form.value.mqttQos ?? 0,
      mqttRetain: !!form.value.mqttRetain,
      mqttKeepAlive: form.value.mqttKeepAlive || 60,
      mqttCleanSession: form.value.mqttCleanSession !== false,
      mqttOptions: options,
      httpUrl: '',
      httpMethod: 'POST',
      httpHeaders: [],
      httpTimeout: 5000,
    })
  }

  form.value = submitData
  return true
}

function onSubmit() {
  if (!normalizeFormBeforeSubmit()) {
    return
  }
  handleSubmit()
}

function onCancel() {
  emits('update:modelValue', false)
}

// 添加Header项
function addHeader() {
  httpHeaders.value.push({ key: '', value: '' })
}

// 删除Header项
function removeHeader(index) {
  httpHeaders.value.splice(index, 1)
}

// 添加MQTT Option项
function addMqttOption() {
  mqttOptions.value.push({ key: '', value: '' })
}

// 删除MQTT Option项
function removeMqttOption(index) {
  mqttOptions.value.splice(index, 1)
}

// 类型切换时重置字段
function onTypeChange(val) {
  if (val === 'HTTP') {
    resetMqttFields()
  }
  else if (val === 'MQTT') {
    resetHttpFields()
  }
}

function resetHttpFields() {
  form.value.httpUrl = ''
  form.value.httpMethod = 'POST'
  form.value.httpTimeout = 5000
  form.value.httpSignEnabled = false
  form.value.httpSignSecret = ''
  httpHeaders.value = []
}

function resetMqttFields() {
  form.value.mqttBroker = ''
  form.value.mqttUsername = ''
  form.value.mqttPassword = ''
  form.value.mqttClientId = ''
  form.value.mqttTopic = ''
  form.value.mqttQos = 0
  form.value.mqttRetain = false
  form.value.mqttKeepAlive = 60
  form.value.mqttCleanSession = true
  mqttOptions.value = []
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = { ...val }
    // 解析 httpHeaders
    if (Array.isArray(val.httpHeaders)) {
      httpHeaders.value = val.httpHeaders
    }
    else {
      httpHeaders.value = []
    }
    // 解析 mqttOptions
    if (Array.isArray(val.mqttOptions)) {
      mqttOptions.value = val.mqttOptions
    }
    else {
      mqttOptions.value = []
    }
  }
  else {
    form.value = {
      name: '',
      type: 'HTTP',
      description: '',
      isEnabled: true,
      // HTTP
      httpUrl: '',
      httpMethod: 'POST',
      httpHeaders: [],
      httpTimeout: 5000,
      httpSignEnabled: false,
      httpSignSecret: '',
      // MQTT
      mqttBroker: '',
      mqttUsername: '',
      mqttPassword: '',
      mqttClientId: '',
      mqttTopic: '',
      mqttQos: 0,
      mqttRetain: false,
      mqttKeepAlive: 60,
      mqttCleanSession: true,
      mqttOptions: [],
    }
    httpHeaders.value = []
    mqttOptions.value = []
  }
}, { immediate: true })
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? t('push_config.edit_config') : t('push_config.add_config')"
    width="700px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
    >
      <!-- 基础信息 -->
      <el-form-item :label="t('push_config.config_name')" prop="name">
        <el-input v-model="form.name" clearable :placeholder="t('push_config.enter_config_name')" />
      </el-form-item>

      <el-form-item :label="t('push_config.type')" prop="type">
        <el-select
          v-model="form.type"
          :placeholder="t('push_config.select_config_type')"
          style="width: 100%"
          :disabled="!!datas?.id"
          @change="onTypeChange"
        >
          <el-option
            v-for="item in typeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item :label="t('common.description')" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="2"
          :placeholder="t('push_config.push_config_dialog')"
        />
      </el-form-item>

      <el-form-item :label="t('push_config.enabled')" prop="isEnabled">
        <el-switch v-model="form.isEnabled" />
      </el-form-item>

      <el-divider content-position="left">
        {{ isHttp ? t('push_config.dialog_http') : t('push_config.mqtt_config') }}
      </el-divider>

      <!-- HTTP 配置 -->
      <template v-if="isHttp">
        <el-form-item :label="t('push_config.url')" prop="httpUrl">
          <el-input
            v-model="form.httpUrl"
            clearable
            placeholder="https://api.example.com/webhook"
          />
        </el-form-item>

        <el-form-item :label="t('push_config.method')" prop="httpMethod">
          <el-select v-model="form.httpMethod" style="width: 100%">
            <el-option
              v-for="item in methodOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('push_config.headers')" prop="httpHeaders">
          <div class="kv-list">
            <div v-for="(header, index) in httpHeaders" :key="index" class="kv-item">
              <el-input v-model="header.key" placeholder="Key" style="width: 150px" />
              <el-input v-model="header.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeHeader(index)">
                {{ t('common.delete') }}
              </el-button>
            </div>
            <el-button type="primary" link @click="addHeader">
              {{ t('push_config.add_header') }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item :label="t('push_config.timeout')" prop="httpTimeout">
          <el-input-number
            v-model="form.httpTimeout"
            :min="1000"
            :max="60000"
            :step="1000"
            style="width: 100%"
          />
          <div class="form-tip">
            {{ t('push_config.unit_milliseconds') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('push_config.hmac')" prop="httpSignEnabled">
          <el-switch v-model="form.httpSignEnabled" />
          <div class="form-tip">
            {{ t('push_config.when_enabled_pushes_automatically_include_x_simple_iot') }}
          </div>
        </el-form-item>

        <el-form-item v-if="form.httpSignEnabled" :label="t('push_config.signature_secret')" prop="httpSignSecret">
          <el-input
            v-model="form.httpSignSecret"
            type="password"
            show-password
            clearable
            :placeholder="t('push_config.dialog_webhook')"
          />
        </el-form-item>
      </template>

      <!-- MQTT 配置 -->
      <template v-if="isMqtt">
        <el-form-item :label="t('push_config.broker')" prop="mqttBroker">
          <el-input
            v-model="form.mqttBroker"
            clearable
            placeholder="tcp://localhost:1883"
          />
        </el-form-item>

        <el-form-item :label="t('push_config.username')" prop="mqttUsername">
          <el-input v-model="form.mqttUsername" clearable :placeholder="t('push_config.mqtt_username')" />
        </el-form-item>

        <el-form-item :label="t('push_config.password')" prop="mqttPassword">
          <el-input
            v-model="form.mqttPassword"
            type="password"
            show-password
            :placeholder="t('push_config.mqtt_password')"
          />
        </el-form-item>

        <el-form-item :label="t('push_config.id')" prop="mqttClientId">
          <el-input v-model="form.mqttClientId" clearable :placeholder="t('push_config.dialog_push_config_dialog')" />
        </el-form-item>

        <el-form-item :label="t('push_config.dialog_topic')" prop="mqttTopic">
          <el-input
            v-model="form.mqttTopic"
            clearable
            placeholder="device/{deviceKey}/forward"
          />
          <div class="form-tip">
            {{ t('push_config.supported_variables_device_key_productid_devicename') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('push_config.dialog_qos')" prop="mqttQos">
          <el-select v-model="form.mqttQos" style="width: 100%">
            <el-option
              v-for="item in qosOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('push_config.retain_message')" prop="mqttRetain">
          <el-switch v-model="form.mqttRetain" />
        </el-form-item>

        <el-form-item :label="t('push_config.heartbeat_interval')" prop="mqttKeepAlive">
          <el-input-number
            v-model="form.mqttKeepAlive"
            :min="10"
            :max="300"
            style="width: 100%"
          />
          <div class="form-tip">
            {{ t('push_config.unit_seconds') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('push_config.clean_session')" prop="mqttCleanSession">
          <el-switch v-model="form.mqttCleanSession" />
        </el-form-item>

        <el-form-item :label="t('push_config.extra_config')" prop="mqttOptions">
          <div class="kv-list">
            <div v-for="(opt, index) in mqttOptions" :key="index" class="kv-item">
              <el-input v-model="opt.key" :placeholder="t('push_config.key_autoreconnect')" style="width: 180px" />
              <el-input v-model="opt.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeMqttOption(index)">
                {{ t('common.delete') }}
              </el-button>
            </div>
            <el-button type="primary" link @click="addMqttOption">
              {{ t('push_config.add_config_item') }}
            </el-button>
          </div>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="onCancel">
        {{ t('common.cancel') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('common.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: var(--iot-color-text-muted);
}

.kv-list {
  width: 100%;
}

.kv-item {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
}

:deep(.el-divider__text) {
  font-size: 13px;
  color: var(--iot-color-text-secondary);
}
</style>
