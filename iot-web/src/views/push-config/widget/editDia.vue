<script setup>
import { ref, watch, computed } from 'vue'
import { pushConfigAddApi, pushConfigEditApi } from '@/api/index.js'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'modelValue', 'title'])
const emits = defineEmits(['update', 'update:modelValue'])

const typeOpt = [
  { label: 'HTTP回调', value: 'HTTP' },
  { label: 'MQTT转发', value: 'MQTT' },
]

const methodOpt = [
  { label: 'POST', value: 'POST' },
  { label: 'GET', value: 'GET' },
  { label: 'PUT', value: 'PUT' },
]

const qosOpt = [
  { label: '0 - 最多一次', value: 0 },
  { label: '1 - 至少一次', value: 1 },
  { label: '2 - 恰好一次', value: 2 },
]

const rules = ref({
  name: [{ required: true, message: '配置名称不能为空', trigger: 'blur' }],
  type: [{ required: true, message: '配置类型不能为空', trigger: 'change' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? pushConfigEditApi : pushConfigAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

const isHttp = computed(() => form.value.type === 'HTTP')
const isMqtt = computed(() => form.value.type === 'MQTT')

// HTTP Headers 数组
const httpHeaders = ref([])

// MQTT Options 数组
const mqttOptions = ref([])

function onSubmit() {
  // 转换数组为提交格式
  const submitData = { ...form.value }
  if (isHttp.value) {
    submitData.httpHeaders = httpHeaders.value.filter(h => h.key)
  }
  if (isMqtt.value) {
    submitData.mqttOptions = mqttOptions.value.filter(h => h.key)
  }
  form.value = submitData
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
  } else if (val === 'MQTT') {
    resetHttpFields()
  }
}

function resetHttpFields() {
  form.value.httpUrl = ''
  form.value.httpMethod = 'POST'
  form.value.httpTimeout = 5000
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
    } else {
      httpHeaders.value = []
    }
    // 解析 mqttOptions
    if (Array.isArray(val.mqttOptions)) {
      mqttOptions.value = val.mqttOptions
    } else {
      mqttOptions.value = []
    }
  } else {
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
    :title="datas?.id ? '编辑推送配置' : '新增推送配置'"
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
      <el-form-item label="配置名称" prop="name">
        <el-input v-model="form.name" clearable placeholder="请输入配置名称" />
      </el-form-item>

      <el-form-item label="配置类型" prop="type">
        <el-select
          v-model="form.type"
          placeholder="请选择配置类型"
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

      <el-form-item label="描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="2"
          placeholder="请输入描述"
        />
      </el-form-item>

      <el-form-item label="是否启用" prop="isEnabled">
        <el-switch v-model="form.isEnabled" />
      </el-form-item>

      <el-divider content-position="left">
        {{ isHttp ? 'HTTP 配置' : 'MQTT 配置' }}
      </el-divider>

      <!-- HTTP 配置 -->
      <template v-if="isHttp">
        <el-form-item label="请求URL" prop="httpUrl">
          <el-input
            v-model="form.httpUrl"
            clearable
            placeholder="https://api.example.com/webhook"
          />
        </el-form-item>

        <el-form-item label="请求方法" prop="httpMethod">
          <el-select v-model="form.httpMethod" style="width: 100%">
            <el-option
              v-for="item in methodOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="请求头" prop="httpHeaders">
          <div class="kv-list">
            <div v-for="(header, index) in httpHeaders" :key="index" class="kv-item">
              <el-input v-model="header.key" placeholder="Key" style="width: 150px" />
              <el-input v-model="header.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeHeader(index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addHeader">+ 添加请求头</el-button>
          </div>
        </el-form-item>

        <el-form-item label="超时时间" prop="httpTimeout">
          <el-input-number
            v-model="form.httpTimeout"
            :min="1000"
            :max="60000"
            :step="1000"
            style="width: 100%"
          />
          <div class="form-tip">单位：毫秒</div>
        </el-form-item>
      </template>

      <!-- MQTT 配置 -->
      <template v-if="isMqtt">
        <el-form-item label="Broker地址" prop="mqttBroker">
          <el-input
            v-model="form.mqttBroker"
            clearable
            placeholder="tcp://localhost:1883"
          />
        </el-form-item>

        <el-form-item label="用户名" prop="mqttUsername">
          <el-input v-model="form.mqttUsername" clearable placeholder="MQTT用户名" />
        </el-form-item>

        <el-form-item label="密码" prop="mqttPassword">
          <el-input
            v-model="form.mqttPassword"
            type="password"
            show-password
            placeholder="MQTT密码"
          />
        </el-form-item>

        <el-form-item label="客户端ID" prop="mqttClientId">
          <el-input v-model="form.mqttClientId" clearable placeholder="留空自动生成" />
        </el-form-item>

        <el-form-item label="目标Topic" prop="mqttTopic">
          <el-input
            v-model="form.mqttTopic"
            clearable
            placeholder="device/{deviceKey}/forward"
          />
          <div class="form-tip">支持变量: {deviceKey}, {productId}, {deviceName}</div>
        </el-form-item>

        <el-form-item label="QoS等级" prop="mqttQos">
          <el-select v-model="form.mqttQos" style="width: 100%">
            <el-option
              v-for="item in qosOpt"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="保留消息" prop="mqttRetain">
          <el-switch v-model="form.mqttRetain" />
        </el-form-item>

        <el-form-item label="心跳间隔" prop="mqttKeepAlive">
          <el-input-number
            v-model="form.mqttKeepAlive"
            :min="10"
            :max="300"
            style="width: 100%"
          />
          <div class="form-tip">单位：秒</div>
        </el-form-item>

        <el-form-item label="清除会话" prop="mqttCleanSession">
          <el-switch v-model="form.mqttCleanSession" />
        </el-form-item>

        <el-form-item label="扩展配置" prop="mqttOptions">
          <div class="kv-list">
            <div v-for="(opt, index) in mqttOptions" :key="index" class="kv-item">
              <el-input v-model="opt.key" placeholder="Key (如: autoReconnect)" style="width: 180px" />
              <el-input v-model="opt.value" placeholder="Value" style="flex: 1" />
              <el-button type="danger" link @click="removeMqttOption(index)">删除</el-button>
            </div>
            <el-button type="primary" link @click="addMqttOption">+ 添加配置项</el-button>
          </div>
        </el-form-item>
      </template>
    </el-form>

    <template #footer>
      <el-button @click="onCancel">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">确定</el-button>
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
