<script lang="jsx" setup>
import { ElMessage } from 'element-plus'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { protocolAddApi, protocolEditApi, protocolTestDecodeApi, protocolTestEncodeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'
import CodeEditor from './CodeEditor.vue'

const props = defineProps(['datas', 'modelValue'])
const emits = defineEmits(['update', 'update:modelValue'])
const { t } = useI18n()
const protocolTypeOpt = [
  { label: 'JAVA', value: 1 },
  { label: t('protocol.system_default'), value: 2 },
  { label: 'JAVASCRIPT', value: 3 },
]

const scriptLangOpt = [
  { label: 'JavaScript', value: 'javascript' },
  { label: 'Python', value: 'python' },
  { label: 'Groovy', value: 'groovy' },
  { label: 'Lua', value: 'lua' },
]

// API 文档内容
const apiDocs = computed(() => ({
  request: {
    title: t('protocol.request'),
    content: t('protocol.api_doc_request'),
  },
  tslModel: {
    title: t('protocol.tslmodel'),
    content: t('protocol.api_doc_tsl_model'),
  },
  message: {
    title: t('protocol.message'),
    content: t('protocol.api_doc_message'),
  },
  decodeResult: {
    title: t('protocol.decode_return_value'),
    content: t('protocol.api_doc_decode_result'),
  },
  encodeResult: {
    title: t('protocol.encode_return_value'),
    content: t('protocol.api_doc_encode_result'),
  },
}))

const rules = ref({
  name: [{ required: true, message: t('protocol.protocol_name_required'), trigger: 'blur' }],
  protoKey: [{ required: true, message: t('protocol.key'), trigger: 'blur' }],
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

const testDialogTitle = computed(() => testMode.value === 'decode' ? t('protocol.script_type') : t('common.test'))
const formattedTestResult = computed(() => testResult.value ? JSON.stringify(testResult.value, null, 2) : '')

// 默认脚本模板
const scriptTemplates = computed(() => ({
  javascript: t('protocol.script_template_javascript'),
  python: t('protocol.script_template_python'),
  groovy: t('protocol.script_template_groovy'),
  lua: t('protocol.script_template_lua'),
}))

function loadScriptTemplate() {
  const lang = form.value.scriptLang || 'javascript'
  form.value.scriptContent = scriptTemplates.value[lang]
}

function onSubmit() {
  handleSubmit()
}

function onCancel() {
  emits('update:modelValue', false)
}

function openTestDialog(mode) {
  if (!isScriptType.value) {
    ElMessage.warning(t('protocol.only_script_protocols_support_online_debugging'))
    return
  }
  if (!form.value.scriptContent) {
    ElMessage.warning(t('protocol.enter_script_content_first'))
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
    name: form.value.name || t('common.description'),
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
      ElMessage.success(t('protocol.decode_test_succeeded'))
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
    ElMessage.success(t('protocol.encode_test_succeeded'))
  }
  catch (error) {
    testResult.value = {
      success: false,
      message: error?.msg || error?.message || t('protocol.dialog_decode'),
      detail: error,
    }
    if (error instanceof SyntaxError) {
      ElMessage.error(t('protocol.encode_parameters_valid_json'))
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
})
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? t('protocol.edit_protocol') : t('protocol.add_protocol')"
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
          <el-form-item :label="t('protocol.protocol_name')" prop="name">
            <el-input v-model="form.name" clearable :placeholder="t('protocol.enter_protocol_name')" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('protocol.protocol_type')" prop="protoType">
            <el-select
              v-model="form.protoType"
              :placeholder="t('protocol.select_protocol_type')"
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
          <el-form-item :label="t('protocol.dialog_key')" prop="protoKey">
            <el-input
              v-model="form.protoKey"
              clearable
              :placeholder="t('protocol.protocol_dialog')"
              :disabled="!!datas?.id"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <!-- JAVA 协议 - 处理类 -->
          <el-form-item
            v-if="form.protoType === 1"
            :label="t('protocol.handler_class')"
            prop="handlerClass"
          >
            <el-input
              v-model="form.handlerClass"
              clearable
              :placeholder="t('protocol.com_example_protocoldecoder')"
            />
          </el-form-item>
          <!-- 系统默认协议 - 处理入口 (只读) -->
          <el-form-item
            v-if="form.protoType === 2"
            :label="t('protocol.handler_entry')"
            prop="handlerClass"
          >
            <el-input
              :model-value="form.handlerClass"
              readonly
              :placeholder="t('protocol.system_default_protocol_handler_class')"
            />
          </el-form-item>
          <!-- JAVA 协议 - JAR 包路径 -->
          <el-form-item
            v-if="form.protoType === 1"
            :label="t('protocol.jar')"
            prop="url"
          >
            <el-input
              v-model="form.url"
              clearable
              :placeholder="t('protocol.dialog_jar')"
            />
          </el-form-item>
          <!-- 脚本协议 - 脚本语言选择 -->
          <el-form-item
            v-if="form.protoType === 3"
            :label="t('protocol.language')"
            prop="scriptLang"
          >
            <el-select
              v-model="form.scriptLang"
              :placeholder="t('protocol.select_script_language')"
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
      <el-form-item v-if="isScriptType" :label="t('protocol.script_content')" prop="scriptContent">
        <div class="script-editor-wrapper">
          <div class="editor-toolbar">
            <div class="toolbar-left">
              <el-button size="small" @click="loadScriptTemplate">
                {{ t('protocol.reset_template') }}
              </el-button>
              <!-- API 文档提示 -->
              <el-button size="small" type="success" plain @click="openTestDialog('decode')">
                {{ t('protocol.test_decode') }}
              </el-button>
              <el-button size="small" type="warning" plain @click="openTestDialog('encode')">
                {{ t('protocol.test_encode') }}
              </el-button>
              <el-popover
                placement="bottom"
                :width="480"
                trigger="click"
              >
                <template #reference>
                  <el-button size="small" type="primary" plain>
                    {{ t('protocol.api') }}
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

      <el-form-item :label="t('protocol.protocol_script')" prop="mark">
        <el-input
          v-model="form.mark"
          type="textarea"
          :rows="3"
          clearable
          :placeholder="t('common.save')"
        />
      </el-form-item>
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

  <el-dialog
    v-model="testDialogVisible"
    :title="testDialogTitle"
    width="720px"
  >
    <el-form label-width="110px" :model="testForm">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="t('protocol.dialog_encode')">
            <el-input v-model="testForm.deviceKey" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="t('protocol.protocol_key')">
            <el-input v-model="testForm.productKey" clearable />
          </el-form-item>
        </el-col>
      </el-row>

      <template v-if="testMode === 'decode'">
        <el-form-item :label="t('protocol.message_type')">
          <el-select v-model="testForm.messageType" style="width: 100%">
            <el-option :label="t('protocol.property')" value="PROPERTY" />
            <el-option :label="t('protocol.event')" value="EVENT" />
            <el-option :label="t('protocol.service_res')" value="SERVICE_RES" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('protocol.raw_message')">
          <el-input
            v-model="testForm.data"
            type="textarea"
            :rows="5"
            :placeholder="t('protocol.dialog_json')"
          />
        </el-form-item>
      </template>

      <template v-else>
        <el-form-item :label="t('protocol.function_identifier')">
          <el-input v-model="testForm.identifier" clearable :placeholder="t('protocol.setpower')" />
        </el-form-item>
        <el-form-item :label="t('protocol.downlink_parameters')">
          <el-input
            v-model="testForm.paramsText"
            type="textarea"
            :rows="6"
            :placeholder="t('protocol.json')"
          />
        </el-form-item>
      </template>
    </el-form>

    <div v-if="testResult" class="test-result">
      <div class="test-result-title">
        {{ t('protocol.protocol_name') }}
      </div>
      <pre>{{ formattedTestResult }}</pre>
    </div>

    <template #footer>
      <el-button @click="testDialogVisible = false">
        {{ t('protocol.dialog_close') }}
      </el-button>
      <el-button type="primary" :loading="testLoading" @click="runProtocolTest">
        {{ t('protocol.run_test') }}
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
