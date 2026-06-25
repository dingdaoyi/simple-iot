<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { computed, ref, watch } from 'vue'
import { protocolAddApi, protocolEditApi, protocolTestDecodeApi, protocolTestEncodeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'
import CodeEditor from './CodeEditor.vue'

const { t } = useI18n()
const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const protocolTypeOpt = [
  { label: 'JAVA', value: 1 },
  { label: t('auto.protocol_editdia_a52f5c20'), value: 2 },
  { label: 'JAVASCRIPT', value: 3 },
]

const scriptLangOpt = [
  { label: 'JavaScript', value: 'javascript' },
  { label: 'Python', value: 'python' },
  { label: 'Groovy', value: 'groovy' },
  { label: 'Lua', value: 'lua' },
]

// API 文档内容
const apiDocs = {
  request: {
    title: t('auto.protocol_editdia_ca9c2a90'),
    content: `decode 函数输入参数

{
  deviceKey: String,    // 设备唯一标识
  messageType: String,  // PROPERTY(属性) / EVENT(事件) / SERVICE_RES(服务响应)
  data: String,        // 原始数据内容
  productId: Number    // 产品ID(可选)
}

示例：
{
  "deviceKey": "device001",
  "messageType": "PROPERTY",
  "data": "{\\"temperature\\": 25.5}"
}`,
  },
  tslModel: {
    title: t('auto.protocol_editdia_665ceea8'),
    content: `产品的物模型定义

属性：
- properties: Array<Property>     // 属性列表
- events: Array<Event>            // 事件列表
- services: Array<Service>        // 服务列表

方法：
- propertyByIdentifier(id)         // 查找属性定义
- eventByIdentifier(id)            // 查找事件定义
- serviceByIdentifier(id)          // 查找服务定义

示例：
const property = tslModel.propertyByIdentifier("temperature");
console.log(property.dataType);`,
  },
  message: {
    title: t('auto.protocol_editdia_62bca6f2'),
    content: `encode 函数输入参数

{
  identifier: String,   // 功能/属性标识符
  params: Object,       // 命令参数对象
  productKey: String,   // 产品Key(可选)
  deviceKey: String     // 设备Key(可选)
}

示例：
{
  "identifier": "setPower",
  "params": { "power": "on" }
}`,
  },
  decodeResult: {
    title: t('auto.protocol_editdia_5bfaed9f'),
    content: `decode 函数返回值

{
  messageId: Number,              // 消息ID(可选)
  rawData: String,               // 原始数据(可选)
  dataList: Array<DataItem>,     // 解析后的数据列表

  // EVENT 类型时需要
  eventData: {
    eventIdentifier: String,     // 事件标识符
    eventType: String,           // 事件类型
    params: Array<DataItem>       // 事件参数
  },

  // SERVICE_RES 类型时需要
  serviceResData: {
    serviceIdentifier: String,   // 服务标识符
    resultData: Array<DataItem>  // 服务输出参数
  }
}

DataItem 结构：
{
  identifier: String,  // 属性标识符(必须与物模型一致)
  type: String,        // 数据类型: INT/LONG/FLOAT/DOUBLE/BOOL/TEXT/ENUM
  value: Any          // 属性值
}`,
  },
  encodeResult: {
    title: t('auto.protocol_editdia_6549cf1b'),
    content: `encode 函数返回值

{
  messageId: Number,        // 消息ID(可选)
  message: String,         // 编码后的消息内容
  metadata: {              // 元数据(可选)
    topic: String          // MQTT topic等
  }
}

示例：
{
  "messageId": 456,
  "message": "{\\"result\\": \\"success\\"}",
  "metadata": {
    "topic": "/device/device001/command"
  }
}`,
  },
}

const rules = ref({
  name: [{ required: true, message: t('auto.protocol_editdia_1d3f6638'), trigger: 'blur' }],
  protoKey: [{ required: true, message: t('auto.protocol_editdia_3e515e9c'), trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? protocolEditApi : protocolAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

const isScriptType = computed(() => form.value.protoType === 3)
const testDialogVisible = ref(false)
const testMode = ref('decode')
const testLoading = ref(false)
const testResult = ref(null)
const testForm = ref({
  deviceKey: 'debug-device-001',
  productKey: 'debug-product',
  messageType: 'PROPERTY',
  data: '{"temperature": 25.5, "humidity": 60}',
  identifier: 'setPower',
  paramsText: `{
  "power": "on"
}`,
})

const testDialogTitle = computed(() => testMode.value === 'decode' ? t('auto.protocol_widget_editdia_5647a3dd') : t('auto.protocol_widget_editdia_55f78076'))
const formattedTestResult = computed(() => testResult.value ? JSON.stringify(testResult.value, null, 2) : '')

// 默认脚本模板
const scriptTemplates = {
  javascript: `/**
 * 协议脚本模板 - JavaScript
 * @description 使用 JavaScript 编写设备协议解析逻辑
 */

// ==================== 解码函数 ====================
/**
 * 解码设备上报的数据
 * @param {Object} request - 请求对象
 * @param {string} request.deviceKey - 设备key
 * @param {string} request.messageType - 消息类型: PROPERTY/EVENT/SERVICE_RES
 * @param {string} request.data - 原始数据
 * @param {Object} tslModel - 物模型
 * @returns {Object} 解码结果 { messageId, rawData, dataList, eventData?, serviceResData? }
 */
exports.decode = function(request, tslModel) {
    // 1. 解析原始数据
    const rawData = request.data;
    let parsedData;

    try {
        // 尝试解析 JSON
        parsedData = JSON.parse(rawData);
    } catch (e) {
        // 如果不是 JSON，直接使用原始字符串
        parsedData = { raw: rawData };
    }

    // 2. 构建返回结果
    const result = {
        messageId: 0,
        rawData: rawData,
        dataList: []
    };

    // 3. 根据消息类型处理数据
    if (request.messageType === 'PROPERTY') {
        // 属性上报：解析属性数据
        for (const key in parsedData) {
            result.dataList.push({
                identifier: key,
                type: 'DOUBLE',  // 根据实际类型选择: INT/LONG/FLOAT/DOUBLE/BOOL/TEXT/ENUM
                value: parsedData[key]
            });
        }
    } else if (request.messageType === 'EVENT') {
        // 事件上报
        result.eventData = {
            eventIdentifier: 'xxx',
            eventType: 'INFO',
            params: result.dataList
        };
    } else if (request.messageType === 'SERVICE_RES') {
        // 服务响应
        result.serviceResData = {
            serviceIdentifier: 'xxx',
            resultData: result.dataList
        };
    }

    return result;
};

// ==================== 编码函数 ====================
/**
 * 编码下发给设备的命令
 * @param {Object} message - 命令消息
 * @param {string} message.identifier - 功能/属性标识符
 * @param {Object} message.params - 参数对象
 * @param {Object} tslModel - 物模型
 * @returns {Object} 编码结果 { messageId, message, metadata? }
 */
exports.encode = function(message, tslModel) {
    return {
        messageId: Math.floor(Math.random() * 1000000),
        message: JSON.stringify(message.params),
        metadata: {
            topic: "/device/" + message.productKey + "/" + message.deviceKey + "/command"
        }
    };
};
`,
  python: `# Python 协议脚本模板

def decode(request, tslModel):
    """解码设备上报的数据"""
    data = request.get('data', '')
    result = {
        'messageId': 0,
        'rawData': data,
        'dataList': []
    }
    # 解析数据并添加到 dataList
    return result

def encode(message, tslModel):
    """编码下发给设备的命令"""
    import json
    return {
        'messageId': 0,
        'message': json.dumps(message.get('params', {}))
    }
`,
  groovy: `// Groovy 协议脚本模板

def decode(request, tslModel) {
    def data = request.data
    return [
        messageId: 0,
        rawData: data,
        dataList: []
    ]
}

def encode(message, tslModel) {
    return [
        messageId: 0,
        message: groovy.json.JsonOutput.toJson(message.params)
    ]
}
`,
  lua: `-- Lua 协议脚本模板

function decode(request, tslModel)
    local data = request.data or ""
    return {
        messageId = 0,
        rawData = data,
        dataList = {}
    }
end

function encode(message, tslModel)
    return {
        messageId = 0,
        message = tostring(message.params)
    }
end
`,
}

function loadScriptTemplate() {
  const lang = form.value.scriptLang || 'javascript'
  form.value.scriptContent = scriptTemplates[lang]
}

function onSubmit() {
  handleSubmit()
}

function onCancel() {
  emits('update:modelValue', false)
}

function openTestDialog(mode) {
  if (!isScriptType.value) {
    ElMessage.warning(t('auto.protocol_editdia_2b1f95fc'))
    return
  }
  if (!form.value.scriptContent) {
    ElMessage.warning(t('auto.protocol_editdia_6e5557bd'))
    return
  }
  testMode.value = mode
  testResult.value = null
  testDialogVisible.value = true
}

function buildProtocolDraft() {
  return {
    ...form.value,
    protoType: 3,
    scriptLang: form.value.scriptLang || 'javascript',
    protoKey: form.value.protoKey || 'debug-script',
    name: form.value.name || t('auto.protocol_widget_editdia_dbded722'),
  }
}

async function runProtocolTest() {
  try {
    testLoading.value = true
    testResult.value = null
    if (testMode.value === 'decode') {
      const { data } = await protocolTestDecodeApi({
        protocol: buildProtocolDraft(),
        deviceKey: testForm.value.deviceKey,
        productKey: testForm.value.productKey,
        messageType: testForm.value.messageType,
        data: testForm.value.data,
      })
      testResult.value = data
      ElMessage.success(t('auto.protocol_editdia_ce06b95e'))
      return
    }

    let params = {}
    if (testForm.value.paramsText?.trim()) {
      params = JSON.parse(testForm.value.paramsText)
    }
    const { data } = await protocolTestEncodeApi({
      protocol: buildProtocolDraft(),
      deviceKey: testForm.value.deviceKey,
      productKey: testForm.value.productKey,
      identifier: testForm.value.identifier,
      params,
    })
    testResult.value = data
    ElMessage.success(t('auto.protocol_editdia_14bac008'))
  }
  catch (error) {
    testResult.value = {
      success: false,
      message: error?.msg || error?.message || t('auto.protocol_widget_editdia_102c5dc2'),
      detail: error,
    }
    if (error instanceof SyntaxError) {
      ElMessage.error(t('auto.protocol_editdia_5974fe0f'))
    }
  }
  finally {
    testLoading.value = false
  }
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = { ...val }
  }
  else {
    form.value = {
      protoType: 3,
      scriptLang: 'javascript',
    }
    loadScriptTemplate()
  }
}, { immediate: true })

watch(() => form.value.protoType, (newVal, oldVal) => {
  if (newVal === 3 && oldVal !== 3 && !form.value.scriptContent) {
    loadScriptTemplate()
  }
})

watch(() => form.value.scriptLang, () => {
  if (isScriptType.value && !props.datas?.id) {
    loadScriptTemplate()
  }
})</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="t('auto.protocol_editdia_3838bb8c')"
    width="900px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="120px"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('auto.protocol_editdia_6924dc1d')" prop="name">
            <el-input v-model="form.name" clearable :placeholder="t('auto.protocol_editdia_b2f485f2')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('auto.protocol_editdia_e825ec78')" prop="protoType">
            <el-select
              v-model="form.protoType"
              :placeholder="t('auto.protocol_editdia_27a1e7f4')"
              style="width: 100%"
              filterable
              :disabled="!!datas?.id"
            >
              <el-option
                v-for="item in protocolTypeOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item :label="t('auto.protocol_editdia_b58edac0')" prop="protoKey">
            <el-input
              v-model="form.protoKey"
              clearable
              :placeholder="t('auto.protocol_editdia_0d3692c5')"
              :disabled="!!datas?.id"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <!-- JAVA 协议 - 处理类 -->
          <el-form-item
            v-if="form.protoType === 1"
            :label="t('auto.protocol_editdia_288951b7')"
            prop="handlerClass"
          >
            <el-input
              v-model="form.handlerClass"
              clearable
              :placeholder="t('auto.protocol_editdia_bd66f50b')"
            />
          </el-form-item>
          <!-- 系统默认协议 - 处理入口 (只读) -->
          <el-form-item
            v-if="form.protoType === 2"
            :label="t('auto.protocol_editdia_42ddaa45')"
            prop="handlerClass"
          >
            <el-input
              :model-value="form.handlerClass"
              readonly
              :placeholder="t('auto.protocol_editdia_f32d0911')"
            />
          </el-form-item>
          <!-- JAVA 协议 - JAR 包路径 -->
          <el-form-item
            v-if="form.protoType === 1"
            :label="t('auto.protocol_editdia_6650ce7a')"
            prop="url"
          >
            <el-input
              v-model="form.url"
              clearable
              :placeholder="t('auto.protocol_editdia_c578c307')"
            />
          </el-form-item>
          <!-- 脚本协议 - 脚本语言选择 -->
          <el-form-item
            v-if="form.protoType === 3"
            :label="t('auto.protocol_editdia_aae5ff1f')"
            prop="scriptLang"
          >
            <el-select
              v-model="form.scriptLang"
              :placeholder="t('auto.protocol_editdia_2b21eccb')"
              style="width: 100%"
            >
              <el-option
                v-for="item in scriptLangOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 脚本编辑器 -->
      <el-form-item v-if="isScriptType" :label="t('auto.protocol_editdia_7be2dcb0')" prop="scriptContent">
        <div class="script-editor-wrapper">
          <div class="editor-toolbar">
            <div class="toolbar-left">
              <el-button size="small" @click="loadScriptTemplate">
                {{ t('auto.protocol_editdia_f6265c7f') }}
              </el-button>
              <!-- API 文档提示 -->
              <el-button size="small" type="success" plain @click="openTestDialog('decode')">
                {{ t('auto.protocol_editdia_77529ee3') }}
              </el-button>
              <el-button size="small" type="warning" plain @click="openTestDialog('encode')">
                {{ t('auto.protocol_editdia_b5460015') }}
              </el-button>
              <el-popover
                placement="bottom"
                :width="480"
                trigger="click"
              >
                <template #reference>
                  <el-button size="small" type="primary" plain>
                    {{ t('auto.protocol_editdia_c2cc9d28') }}
                  </el-button>
                </template>
                <el-tabs type="border-card" class="api-tabs">
                  <el-tab-pane
                    v-for="(doc, key) in apiDocs"
                    :key="key"
                    :label="doc.title"
                  >
                    <pre class="api-content">{{ doc.content }}</pre>
                  </el-tab-pane>
                </el-tabs>
              </el-popover>
            </div>
          </div>
          <CodeEditor
            v-model="form.scriptContent"
            :language="form.scriptLang || 'javascript'"
            :height="400"
          />
        </div>
      </el-form-item>

      <el-form-item :label="t('auto.protocol_widget_editdia_941d5acf')" prop="mark">
        <el-input
          v-model="form.mark"
          type="textarea"
          :rows="3"
          clearable
          :placeholder="t('auto.protocol_widget_editdia_de95db5f')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">
        {{ t('auto.protocol_editdia_625fb26b') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('auto.protocol_editdia_38cf16f2') }}
      </el-button>
    </template>
  </el-dialog>

  <el-dialog
    v-model="testDialogVisible"
    :title="testDialogTitle"
    width="720px"
  >
    <el-form label-width="110px" :model="testForm">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="t('auto.protocol_widget_editdia_3f04bb6a')">
            <el-input v-model="testForm.deviceKey" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('auto.protocol_widget_editdia_db1d3115')">
            <el-input v-model="testForm.productKey" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <template v-if="testMode === 'decode'">
        <el-form-item :label="t('auto.protocol_editdia_6d00710a')">
          <el-select v-model="testForm.messageType" style="width: 100%">
            <el-option :label="t('auto.protocol_editdia_0da89e2c')" value="PROPERTY" />
            <el-option :label="t('auto.protocol_editdia_e7c8879e')" value="EVENT" />
            <el-option :label="t('auto.protocol_editdia_bfdf53e4')" value="SERVICE_RES" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('auto.protocol_editdia_f3aeedcd')">
          <el-input
            v-model="testForm.data"
            type="textarea"
            :rows="5"
            :placeholder="t('auto.protocol_editdia_581ef620')"
          />
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item :label="t('auto.protocol_editdia_50a76051')">
          <el-input v-model="testForm.identifier" clearable :placeholder="t('auto.protocol_editdia_d81ee48e')" />
        </el-form-item>
        <el-form-item :label="t('auto.protocol_editdia_59a8b62c')">
          <el-input
            v-model="testForm.paramsText"
            type="textarea"
            :rows="6"
            :placeholder="t('auto.protocol_editdia_072796bb')"
          />
        </el-form-item>
      </template>
    </el-form>

    <div v-if="testResult" class="test-result">
      <div class="test-result-title">
        {{ t('auto.protocol_widget_editdia_d16a4d51') }}
      </div>
      <pre>{{ formattedTestResult }}</pre>
    </div>

    <template #footer>
      <el-button @click="testDialogVisible = false">
        {{ t('auto.protocol_editdia_b15d9127') }}
      </el-button>
      <el-button type="primary" :loading="testLoading" @click="runProtocolTest">
        {{ t('auto.protocol_editdia_9c484867') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.script-editor-wrapper {
  width: 100%;
  border: 1px solid var(--iot-color-border);
  border-radius: var(--radius-md);
  overflow: hidden;

  .editor-toolbar {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: var(--space-sm) var(--space-md);
    background: var(--iot-glass-bg);
    border-bottom: 1px solid var(--iot-color-border);

    .toolbar-left {
      display: flex;
      align-items: center;
      gap: var(--space-sm);
    }
  }
}

.test-result {
  margin-top: var(--space-md);
  border: 1px solid var(--iot-color-border);
  border-radius: var(--radius-md);
  overflow: hidden;

  .test-result-title {
    padding: var(--space-sm) var(--space-md);
    font-size: 13px;
    font-weight: 600;
    color: var(--iot-color-text-primary);
    background: var(--iot-glass-bg);
    border-bottom: 1px solid var(--iot-color-border);
  }

  pre {
    margin: 0;
    padding: var(--space-md);
    max-height: 320px;
    overflow: auto;
    font-size: 12px;
    line-height: 1.6;
    color: #d4d4d4;
    background: #1e1e1e;
    white-space: pre-wrap;
    word-break: break-all;
  }
}

.api-tabs {
  :deep(.el-tabs__header) {
    background: transparent;
    margin-bottom: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  :deep(.el-tabs__item) {
    padding: 8px 12px;
    font-size: 12px;
    color: var(--iot-color-text-secondary);

    &.is-active {
      color: var(--iot-color-primary);
    }
  }

  :deep(.el-tabs__content) {
    padding: 0;
  }
}

.api-content {
  margin: 0;
  padding: var(--space-md);
  font-size: 12px;
  line-height: 1.6;
  font-family: 'Fira Code', monospace;
  color: #d4d4d4;
  background: #1e1e1e;
  border-radius: var(--radius-sm);
  max-height: 400px;
  overflow-y: auto;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>
