<script setup>
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { computed, ref, watch } from 'vue'
import { pushConfigAddApi, pushConfigEditApi } from '@/api/index.js'
import { useForm } from '@/composables/useForm.js'

const { t } = useI18n()
const props = defineProps(['datas', 'modelValue', 'title'])
const emits = defineEmits(['update', 'update:modelValue'])

const typeOpt = [
  { label: t('auto.push_config_editdia_f83e315e'), value: 'HTTP' },
  { label: t('auto.push_config_editdia_18bd3062'), value: 'MQTT' },
]

const methodOpt = [
  { label: 'POST', value: 'POST' },
  { label: 'GET', value: 'GET' },
  { label: 'PUT', value: 'PUT' },
]

const qosOpt = [
  { label: t('auto.push_config_editdia_c9f610e2'), value: 0 },
  { label: t('auto.push_config_editdia_4415944c'), value: 1 },
  { label: t('auto.push_config_editdia_7275bdac'), value: 2 },
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
    callback(new Error(t('auto.push_config_widget_editdia_97e482eb')))
    return
  }
  if (!/^https?:\/\//i.test(url)) {
    callback(new Error(t('auto.push_config_widget_editdia_adf864c2')))
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
    callback(new Error(t('auto.push_config_widget_editdia_b0d6f162')))
    return
  }
  if (!/^(?:tcp|ssl|ws|wss):\/\//i.test(broker)) {
    callback(new Error(t('auto.push_config_widget_editdia_2b1664c9')))
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
    callback(new Error(t('auto.push_config_widget_editdia_adb2781e')))
    return
  }
  if (topic.includes('#') || topic.includes('+')) {
    callback(new Error(t('auto.push_config_widget_editdia_91531ae7')))
    return
  }
  callback()
}

const rules = ref({
  name: [{ required: true, message: t('auto.push_config_editdia_bfd057f2'), trigger: 'blur' }],
  type: [{ required: true, message: t('auto.push_config_editdia_2ca68f45'), trigger: 'change' }],
  httpUrl: [{ validator: validateHttpUrl, trigger: 'blur' }],
  httpMethod: [{ required: true, message: t('auto.push_config_editdia_8b069eb0'), trigger: 'change' }],
  mqttBroker: [{ validator: validateMqttBroker, trigger: 'blur' }],
  mqttTopic: [{ validator: validateMqttTopic, trigger: 'blur' }],
  mqttQos: [{ required: true, message: t('auto.push_config_editdia_8ce583ba'), trigger: 'change' }],
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
      ElMessage.warning(t('auto.push_config_editdia_90b78fd7'))
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
      ElMessage.warning(t('auto.push_config_editdia_f1825b7d'))
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
}, { immediate: true })</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="t('auto.push_config_editdia_6912a8b1')"
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
      <el-form-item :label="t('auto.push_config_editdia_4fcad1c9')" prop="name">
        <el-input v-model="form.name" clearable :placeholder="t('auto.push_config_editdia_b984df04')" />
      </el-form-item>

      <el-form-item :label="t('auto.push_config_editdia_6d95c8c1')" prop="type">
        <el-select
          v-model="form.type"
          :placeholder="t('auto.push_config_editdia_6a777f85')"
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

      <el-form-item :label="t('auto.push_config_editdia_3bdd08ad')" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="2"
          :placeholder="t('auto.push_config_editdia_11956a43')"
        />
      </el-form-item>

      <el-form-item :label="t('auto.push_config_editdia_53c3dd59')" prop="isEnabled">
        <el-switch v-model="form.isEnabled" />
      </el-form-item>

      <el-divider content-position="left">
        {{ isHttp ? t('auto.push_config_widget_editdia_a69984b2') : t('auto.push_config_widget_editdia_a5601dd4') }}
      </el-divider>

      <!-- HTTP 配置 -->
      <template v-if="isHttp">
        <el-form-item :label="t('auto.push_config_editdia_a09a257e')" prop="httpUrl">
          <el-input
            v-model="form.httpUrl"
            clearable
            placeholder="https://api.example.com/webhook"
          />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_ec49329b')" prop="httpMethod">
          <el-select v-model="form.httpMethod" style="width: 100%">
            <el-option
              v-for="item in methodOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_be47bd27')" prop="httpHeaders">
          <div class="kv-list">
            <div v-for="(header, index) in httpHeaders" :key="index" class="kv-item">
              <el-input v-model="header.key" placeholder="Key" style="width: 150px" />
              <el-input v-model="header.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeHeader(index)">
                {{ t('auto.push_config_editdia_2f4aaddd') }}
              </el-button>
            </div>
            <el-button type="primary" link @click="addHeader">
              {{ t('auto.push_config_editdia_802d1c71') }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_56071a4f')" prop="httpTimeout">
          <el-input-number
            v-model="form.httpTimeout"
            :min="1000"
            :max="60000"
            :step="1000"
            style="width: 100%"
          />
          <div class="form-tip">
            {{ t('auto.push_config_editdia_fbcfb5a6') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_1e95f79b')" prop="httpSignEnabled">
          <el-switch v-model="form.httpSignEnabled" />
          <div class="form-tip">
            {{ t('auto.push_config_signature_tip') }}
          </div>
        </el-form-item>

        <el-form-item v-if="form.httpSignEnabled" :label="t('auto.push_config_editdia_95040dbe')" prop="httpSignSecret">
          <el-input
            v-model="form.httpSignSecret"
            type="password"
            show-password
            clearable
            :placeholder="t('auto.push_config_editdia_cf548065')"
          />
        </el-form-item>
      </template>

      <!-- MQTT 配置 -->
      <template v-if="isMqtt">
        <el-form-item :label="t('auto.push_config_editdia_5603e263')" prop="mqttBroker">
          <el-input
            v-model="form.mqttBroker"
            clearable
            placeholder="tcp://localhost:1883"
          />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_819767ad')" prop="mqttUsername">
          <el-input v-model="form.mqttUsername" clearable :placeholder="t('auto.push_config_editdia_5256a1d3')" />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_a8105204')" prop="mqttPassword">
          <el-input
            v-model="form.mqttPassword"
            type="password"
            show-password
            :placeholder="t('auto.push_config_editdia_7906c52e')"
          />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_99593f76')" prop="mqttClientId">
          <el-input v-model="form.mqttClientId" clearable :placeholder="t('auto.push_config_editdia_2461b947')" />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_ab03c5d5')" prop="mqttTopic">
          <el-input
            v-model="form.mqttTopic"
            clearable
            placeholder="device/{deviceKey}/forward"
          />
          <div class="form-tip">
            {{ t('auto.push_config_variables_tip') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_410d2d45')" prop="mqttQos">
          <el-select v-model="form.mqttQos" style="width: 100%">
            <el-option
              v-for="item in qosOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_480ed2cd')" prop="mqttRetain">
          <el-switch v-model="form.mqttRetain" />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_9901e9b6')" prop="mqttKeepAlive">
          <el-input-number
            v-model="form.mqttKeepAlive"
            :min="10"
            :max="300"
            style="width: 100%"
          />
          <div class="form-tip">
            {{ t('auto.push_config_editdia_8b864080') }}
          </div>
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_8efb748a')" prop="mqttCleanSession">
          <el-switch v-model="form.mqttCleanSession" />
        </el-form-item>

        <el-form-item :label="t('auto.push_config_editdia_e4e625f8')" prop="mqttOptions">
          <div class="kv-list">
            <div v-for="(opt, index) in mqttOptions" :key="index" class="kv-item">
              <el-input v-model="opt.key" :placeholder="t('auto.push_config_editdia_68f8d241')" style="width: 180px" />
              <el-input v-model="opt.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeMqttOption(index)">
                {{ t('auto.push_config_editdia_2f4aaddd') }}
              </el-button>
            </div>
            <el-button type="primary" link @click="addMqttOption">
              {{ t('auto.push_config_editdia_de627ae4') }}
            </el-button>
          </div>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="onCancel">
        {{ t('auto.push_config_editdia_625fb26b') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('auto.push_config_editdia_38cf16f2') }}
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