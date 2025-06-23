<script setup>
import { callDeviceServiceApi } from '@/api/index.js'
import { ElMessage } from 'element-plus'
import { computed, ref } from 'vue'

const props = defineProps({
  deviceDetail: {
    type: Object,
    required: true,
  },
})

// 获取设备的服务列表
const serviceList = computed(() => {
  return props.deviceDetail?.tslModel?.services || []
})

const activeService = ref(null)
const serviceDialogVisible = ref(false)
const serviceForm = ref({})
const serviceFormRef = ref(null)
const loading = ref(false)
const callResult = ref(null)
const showResult = ref(false)

// 显示服务调用对话框
function showServiceDialog(service) {
  activeService.value = service
  resetForm()
  serviceDialogVisible.value = true
}

// 重置表单
function resetForm() {
  serviceForm.value = {}
  callResult.value = null
  showResult.value = false

  // 初始化表单数据
  if (activeService.value?.inputParams) {
    activeService.value.inputParams.forEach((param) => {
      serviceForm.value[param.identifier] = getDefaultValue(param)
    })
  }

  // 清除表单验证
  if (serviceFormRef.value) {
    serviceFormRef.value.clearValidate()
  }
}

// 根据参数类型获取默认值
function getDefaultValue(param) {
  switch (param.dataType) {
    case 'int':
    case 'float':
    case 'double':
      return 0
    case 'bool':
      return false
    case 'text':
    case 'string':
      return ''
    case 'date':
      return new Date()
    default:
      return ''
  }
}

// 获取表单验证规则
function getValidationRules(param) {
  const rules = []

  if (param.required) {
    const message = param.dataType === 'bool' ? `请选择${param.name}` : `请输入${param.name}`
    rules.push({
      required: true,
      message,
      trigger: param.dataType === 'bool' ? 'change' : 'blur',
    })
  }

  // 数值范围验证
  if (['int', 'float', 'double'].includes(param.dataType)) {
    if (param.min !== undefined || param.max !== undefined) {
      rules.push({
        type: 'number',
        min: param.min,
        max: param.max,
        message: `值应在${param.min ?? '无限制'}到${param.max ?? '无限制'}之间`,
        trigger: 'blur',
      })
    }
  }

  // 字符串长度验证
  if (['text', 'string'].includes(param.dataType)) {
    if (param.maxLength) {
      rules.push({
        max: param.maxLength,
        message: `长度不能超过${param.maxLength}个字符`,
        trigger: 'blur',
      })
    }
  }

  return rules
}

// 调用设备服务
async function callDeviceService() {
  if (!serviceFormRef.value)
    return

  try {
    await serviceFormRef.value.validate()
    loading.value = true
    callResult.value = null
    showResult.value = false

    const result = await callDeviceServiceApi(
      props.deviceDetail.deviceKey,
      activeService.value.identifier,
      serviceForm.value,
    )

    if (result.success) {
      ElMessage.success('服务调用成功')
      callResult.value = result.data
      showResult.value = true

      // 如果没有返回数据，3秒后关闭对话框
      if (!result.data) {
        setTimeout(() => {
          serviceDialogVisible.value = false
        }, 2000)
      }
    }
    else {
      ElMessage.error(result.msg || '服务调用失败')
    }
  }
  finally {
    loading.value = false
  }
}

// 渲染表单项
function renderFormItem(param) {
  switch (param.dataType) {
    case 'bool':
      return 'switch'
    case 'int':
    case 'float':
    case 'double':
      return 'number'
    case 'date':
      return 'date'
    case 'text':
      return 'textarea'
    default:
      return 'input'
  }
}
</script>

<template>
  <div class="p-5">
    <!-- 服务列表 -->
    <div v-if="serviceList.length === 0" class="text-center py-10">
      <el-empty description="暂无可用服务" />
    </div>

    <div v-else class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
      <div
        v-for="service in serviceList"
        :key="service.identifier"
        class="bg-white rounded-xl border border-gray-200 hover:border-blue-300 hover:shadow-xl transition-all duration-300 overflow-hidden group"
      >
        <!-- 服务头部 -->
        <div class="bg-gradient-to-r from-blue-50 to-indigo-50 px-6 py-4 border-b border-gray-100">
          <div class="flex items-start justify-between mb-2">
            <h3 class="font-semibold text-lg text-gray-800 group-hover:text-blue-600 transition-colors">
              {{ service.name }}
            </h3>
            <div class="flex items-center gap-2">
              <el-tag type="primary" size="small" class="font-medium">
                服务
              </el-tag>
            </div>
          </div>

          <div class="flex items-center gap-2 mb-3">
            <code class="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs font-mono">
              {{ service.identifier }}
            </code>
            <el-tag v-if="service.async" type="warning" size="small">
              异步
            </el-tag>
            <el-tag v-if="service.required" type="danger" size="small">
              必选
            </el-tag>
          </div>

          <!-- 服务描述 -->
          <div v-if="service.remark" class="bg-blue-100 border-l-4 border-blue-400 p-3 rounded-r">
            <p class="text-sm text-blue-800 leading-relaxed m-0">
              {{ service.remark }}
            </p>
          </div>
        </div>

        <!-- 服务内容 -->
        <div class="p-6">
          <!-- 参数信息 -->
          <div class="grid grid-cols-2 gap-4 mb-6">
            <!-- 输入参数 -->
            <div class="bg-green-50 rounded-lg p-4 border border-green-200">
              <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-green-500 rounded-full" />
                  <span class="text-sm font-medium text-green-700">输入</span>
                </div>
                <span class="bg-green-200 text-green-700 text-xs px-2 py-1 rounded-full font-medium">
                  {{ service.inputParams?.length || 0 }}
                </span>
              </div>
              <div class="space-y-1 min-h-12">
                <div v-if="service.inputParams && service.inputParams.length > 0">
                  <div
                    v-for="param in service.inputParams.slice(0, 2)"
                    :key="param.identifier"
                    class="text-xs bg-white text-green-700 px-2 py-1 rounded border border-green-300 truncate"
                    :title="param.name"
                  >
                    {{ param.name }}
                  </div>
                  <div v-if="service.inputParams.length > 2" class="text-xs text-green-600 font-medium">
                    +{{ service.inputParams.length - 2 }} 更多
                  </div>
                </div>
                <div v-else class="text-xs text-green-600 italic">
                  无输入参数
                </div>
              </div>
            </div>

            <!-- 输出参数 -->
            <div class="bg-blue-50 rounded-lg p-4 border border-blue-200">
              <div class="flex items-center justify-between mb-3">
                <div class="flex items-center gap-2">
                  <div class="w-2 h-2 bg-blue-500 rounded-full" />
                  <span class="text-sm font-medium text-blue-700">输出</span>
                </div>
                <span class="bg-blue-200 text-blue-700 text-xs px-2 py-1 rounded-full font-medium">
                  {{ service.outputParams?.length || 0 }}
                </span>
              </div>
              <div class="space-y-1 min-h-12">
                <div v-if="service.outputParams && service.outputParams.length > 0">
                  <div
                    v-for="param in service.outputParams.slice(0, 2)"
                    :key="param.identifier"
                    class="text-xs bg-white text-blue-700 px-2 py-1 rounded border border-blue-300 truncate"
                    :title="param.name"
                  >
                    {{ param.name }}
                  </div>
                  <div v-if="service.outputParams.length > 2" class="text-xs text-blue-600 font-medium">
                    +{{ service.outputParams.length - 2 }} 更多
                  </div>
                </div>
                <div v-else class="text-xs text-blue-600 italic">
                  无输出参数
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="flex justify-end pt-4 border-t border-gray-100">
            <el-button
              type="primary"
              size="default"
              class="px-6 font-medium"
              @click="showServiceDialog(service)"
            >
              调用服务
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 服务调用对话框 -->
    <el-dialog
      v-model="serviceDialogVisible"
      :title="`调用服务: ${activeService?.name}`"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="activeService">
        <div class="bg-gray-50 p-4 rounded mb-5">
          <div class="flex items-center gap-2 mb-2">
            <strong>服务标识:</strong>
            <code class="bg-gray-200 px-2 py-1 rounded text-sm">{{ activeService.identifier }}</code>
            <el-tag v-if="activeService.async" type="warning" size="small">
              异步
            </el-tag>
            <el-tag v-if="activeService.required" type="danger" size="small">
              必选
            </el-tag>
          </div>
          <p v-if="activeService.remark" class="mb-0 text-gray-700">
            <strong>服务描述:</strong> {{ activeService.remark }}
          </p>
        </div>

        <el-form
          ref="serviceFormRef"
          :model="serviceForm"
          label-width="120px"
        >
          <div v-if="!activeService.inputParams || activeService.inputParams.length === 0">
            <el-alert
              title="此服务无需输入参数"
              type="info"
              :closable="false"
              show-icon
            />
          </div>

          <el-form-item
            v-for="param in activeService.inputParams"
            :key="param.identifier"
            :label="param.name"
            :prop="param.identifier"
            :rules="getValidationRules(param)"
          >
            <!-- 布尔类型 -->
            <el-switch
              v-if="renderFormItem(param) === 'switch'"
              v-model="serviceForm[param.identifier]"
              :active-text="param.trueText || '开'"
              :inactive-text="param.falseText || '关'"
            />

            <!-- 数值类型 -->
            <el-input-number
              v-else-if="renderFormItem(param) === 'number'"
              v-model="serviceForm[param.identifier]"
              :min="param.min"
              :max="param.max"
              :step="param.step || 1"
              :precision="param.dataType === 'int' ? 0 : 2"
              :placeholder="`请输入${param.name}`"
              style="width: 100%"
            />

            <!-- 日期类型 -->
            <el-date-picker
              v-else-if="renderFormItem(param) === 'date'"
              v-model="serviceForm[param.identifier]"
              type="datetime"
              :placeholder="`请选择${param.name}`"
              style="width: 100%"
            />

            <!-- 文本域 -->
            <el-input
              v-else-if="renderFormItem(param) === 'textarea'"
              v-model="serviceForm[param.identifier]"
              type="textarea"
              :rows="3"
              :maxlength="param.maxLength"
              :placeholder="`请输入${param.name}`"
              show-word-limit
            />

            <!-- 普通输入框 -->
            <el-input
              v-else
              v-model="serviceForm[param.identifier]"
              :maxlength="param.maxLength"
              :placeholder="`请输入${param.name}`"
              clearable
            />

            <div v-if="param.description" class="text-xs text-gray-500 mt-1 leading-relaxed">
              {{ param.description }}
            </div>
          </el-form-item>
        </el-form>

        <!-- 调用结果显示 -->
        <div v-if="showResult" class="mt-5">
          <el-divider content-position="left">
            调用结果
          </el-divider>
          <div v-if="callResult" class="bg-gray-50 border border-gray-200 rounded p-4 max-h-50 overflow-y-auto">
            <pre class="m-0 font-mono text-xs leading-relaxed text-gray-800">{{
                JSON.stringify(callResult, null, 2)
            }}</pre>
          </div>
          <div v-else class="text-center py-3">
            <el-tag type="success">
              服务调用成功，无返回数据
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end gap-3">
          <el-button @click="serviceDialogVisible = false">
            取消
          </el-button>
          <el-button @click="resetForm">
            重置
          </el-button>
          <el-button
            type="primary"
            :loading="loading"
            @click="callDeviceService"
          >
            调用服务
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
