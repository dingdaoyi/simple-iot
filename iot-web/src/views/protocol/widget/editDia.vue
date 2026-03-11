<script lang="jsx" setup>
import { computed, ref, watch } from 'vue'
import { protocolAddApi, protocolEditApi } from '@/api'
import { useForm } from '@/composables/useForm.js'
import CodeEditor from './CodeEditor.vue'

const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const protocolTypeOpt = [
  { label: 'JAVA', value: 1 },
  { label: '系统默认', value: 2 },
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
    title: 'request - 设备上报数据',
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
    title: 'tslModel - 物模型',
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
    title: 'message - 下发命令',
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
    title: 'decode 返回值',
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
    title: 'encode 返回值',
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
  name: [{ required: true, message: '协议名称不能为空', trigger: 'blur' }],
  protoKey: [{ required: true, message: '协议 Key 不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? protocolEditApi : protocolAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

const isScriptType = computed(() => form.value.protoType === 3)

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
})
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑协议' : '新增协议'"
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
          <el-form-item label="协议名称" prop="name">
            <el-input v-model="form.name" clearable placeholder="请输入协议名称" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="协议类型" prop="protoType">
            <el-select
              v-model="form.protoType"
              placeholder="请选择协议类型"
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
          <el-form-item label="协议 Key" prop="protoKey">
            <el-input
              v-model="form.protoKey"
              clearable
              placeholder="请输入协议唯一标识"
              :disabled="!!datas?.id"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <!-- JAVA 协议 - 处理类 -->
          <el-form-item
            v-if="form.protoType === 1"
            label="处理类"
            prop="handlerClass"
          >
            <el-input
              v-model="form.handlerClass"
              clearable
              placeholder="例如: com.example.ProtocolDecoder"
            />
          </el-form-item>
          <!-- 系统默认协议 - 处理入口 (只读) -->
          <el-form-item
            v-if="form.protoType === 2"
            label="处理入口"
            prop="handlerClass"
          >
            <el-input
              :model-value="form.handlerClass"
              readonly
              placeholder="系统默认协议处理类"
            />
          </el-form-item>
          <!-- JAVA 协议 - JAR 包路径 -->
          <el-form-item
            v-if="form.protoType === 1"
            label="JAR 包路径"
            prop="url"
          >
            <el-input
              v-model="form.url"
              clearable
              placeholder="JAR 包文件路径"
            />
          </el-form-item>
          <!-- 脚本协议 - 脚本语言选择 -->
          <el-form-item
            v-if="form.protoType === 3"
            label="脚本语言"
            prop="scriptLang"
          >
            <el-select
              v-model="form.scriptLang"
              placeholder="选择脚本语言"
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
      <el-form-item v-if="isScriptType" label="脚本内容" prop="scriptContent">
        <div class="script-editor-wrapper">
          <div class="editor-toolbar">
            <div class="toolbar-left">
              <el-button size="small" @click="loadScriptTemplate">
                📄 重置模板
              </el-button>
              <!-- API 文档提示 -->
              <el-popover
                placement="bottom"
                :width="480"
                trigger="click"
              >
                <template #reference>
                  <el-button size="small" type="primary" plain>
                    📖 API 文档
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

      <el-form-item label="备注说明" prop="mark">
        <el-input
          v-model="form.mark"
          type="textarea"
          :rows="3"
          clearable
          placeholder="请输入协议描述、使用说明等信息"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">
        取消
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        确定
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
