<script setup>
import { ElMessage } from 'element-plus'
import { computed, ref } from 'vue'
import { callDeviceServiceApi } from '@/api/index.js'

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
  // 枚举类型默认取第一个值或空
  if (param.enums && param.enums.length > 0) {
    return param.enums[0]?.key ?? ''
  }
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
  // 如果有枚举值，使用下拉选择
  if (param.enums && param.enums.length > 0) {
    return 'enum'
  }
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
  <div class="service-container">
    <!-- 服务列表 -->
    <div v-if="serviceList.length === 0" class="empty-state glass-card">
      <el-empty description="暂无可用服务" />
    </div>

    <div v-else class="service-grid">
      <div
        v-for="service in serviceList"
        :key="service.identifier"
        class="service-card glass-card"
      >
        <!-- 服务头部 -->
        <div class="service-header">
          <div class="service-title-row">
            <h3 class="service-name">
              {{ service.name }}
            </h3>
            <el-tag type="primary" size="small" effect="dark" class="service-tag">
              服务
            </el-tag>
          </div>

          <div class="service-meta">
            <code class="service-identifier">
              {{ service.identifier }}
            </code>
            <el-tag v-if="service.async" type="warning" size="small" effect="light">
              异步
            </el-tag>
            <el-tag v-if="service.required" type="danger" size="small" effect="light">
              必选
            </el-tag>
          </div>

          <!-- 服务描述 -->
          <div v-if="service.remark" class="service-desc">
            <p>{{ service.remark }}</p>
          </div>
        </div>

        <!-- 服务内容 -->
        <div class="service-body">
          <!-- 参数信息 -->
          <div class="params-grid">
            <!-- 输入参数 -->
            <div class="param-box param-input">
              <div class="param-header">
                <div class="param-header-left">
                  <div class="param-dot" />
                  <span class="param-label">输入</span>
                </div>
                <span class="param-count">{{ service.inputParams?.length || 0 }}</span>
              </div>
              <div class="param-list">
                <div v-if="service.inputParams && service.inputParams.length > 0">
                  <div
                    v-for="param in service.inputParams.slice(0, 2)"
                    :key="param.identifier"
                    class="param-item"
                    :title="param.name"
                  >
                    {{ param.name }}
                  </div>
                  <div v-if="service.inputParams.length > 2" class="param-more">
                    +{{ service.inputParams.length - 2 }} 更多
                  </div>
                </div>
                <div v-else class="param-empty">
                  无输入参数
                </div>
              </div>
            </div>

            <!-- 输出参数 -->
            <div class="param-box param-output">
              <div class="param-header">
                <div class="param-header-left">
                  <div class="param-dot" />
                  <span class="param-label">输出</span>
                </div>
                <span class="param-count">{{ service.outputParams?.length || 0 }}</span>
              </div>
              <div class="param-list">
                <div v-if="service.outputParams && service.outputParams.length > 0">
                  <div
                    v-for="param in service.outputParams.slice(0, 2)"
                    :key="param.identifier"
                    class="param-item"
                    :title="param.name"
                  >
                    {{ param.name }}
                  </div>
                  <div v-if="service.outputParams.length > 2" class="param-more">
                    +{{ service.outputParams.length - 2 }} 更多
                  </div>
                </div>
                <div v-else class="param-empty">
                  无输出参数
                </div>
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="service-action">
            <el-button
              type="primary"
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
        <div class="dialog-info">
          <div class="info-row">
            <strong>服务标识:</strong>
            <code class="info-code">{{ activeService.identifier }}</code>
            <el-tag v-if="activeService.async" type="warning" size="small">
              异步
            </el-tag>
            <el-tag v-if="activeService.required" type="danger" size="small">
              必选
            </el-tag>
          </div>
          <p v-if="activeService.remark" class="info-desc">
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

            <!-- 枚举类型 -->
            <el-select
              v-else-if="renderFormItem(param) === 'enum'"
              v-model="serviceForm[param.identifier]"
              :placeholder="`请选择${param.name}`"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="item in param.enums"
                :key="item.key"
                :label="item.value"
                :value="item.key"
              />
            </el-select>

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

            <div v-if="param.description" class="form-item-hint">
              {{ param.description }}
            </div>
          </el-form-item>
        </el-form>

        <!-- 调用结果显示 -->
        <div v-if="showResult" class="result-section">
          <el-divider content-position="left">
            调用结果
          </el-divider>
          <div v-if="callResult" class="result-content">
            <pre>{{ JSON.stringify(callResult, null, 2) }}</pre>
          </div>
          <div v-else class="result-empty">
            <el-tag type="success">
              服务调用成功，无返回数据
            </el-tag>
          </div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
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

<style lang="scss" scoped>
.service-container {
  padding: var(--space-lg);
  height: 100%;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: var(--space-lg);
}

.service-card {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: rgba(99, 102, 241, 0.3);
  }
}

.service-header {
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.08) 0%, rgba(6, 182, 212, 0.08) 100%);
  padding: var(--space-lg);
  border-bottom: 1px solid var(--iot-glass-border);
}

.service-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-bottom: var(--space-sm);
}

.service-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--iot-color-text-primary);
  margin: 0;
}

.service-tag {
  font-weight: 500;
}

.service-meta {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-md);
}

.service-identifier {
  background: var(--iot-glass-bg);
  color: var(--iot-color-text-secondary);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 12px;
  font-family: 'Fira Code', monospace;
}

.service-desc {
  background: rgba(99, 102, 241, 0.1);
  border-left: 3px solid var(--iot-color-primary);
  padding: var(--space-md);
  border-radius: 0 var(--radius-sm) var(--radius-sm) 0;

  p {
    margin: 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
    line-height: 1.5;
  }
}

.service-body {
  padding: var(--space-lg);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.params-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.param-box {
  background: var(--iot-glass-bg);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
}

.param-input {
  .param-dot {
    background: var(--iot-color-success);
  }
  .param-count {
    background: rgba(16, 185, 129, 0.15);
    color: var(--iot-color-success);
  }
}

.param-output {
  .param-dot {
    background: var(--iot-color-primary);
  }
  .param-count {
    background: rgba(99, 102, 241, 0.15);
    color: var(--iot-color-primary);
  }
}

.param-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-sm);
}

.param-header-left {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.param-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.param-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--iot-color-text-secondary);
}

.param-count {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: var(--radius-full);
  font-weight: 600;
}

.param-list {
  min-height: 48px;
}

.param-item {
  font-size: 12px;
  background: var(--iot-color-bg-card);
  color: var(--iot-color-text-secondary);
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  border: 1px solid var(--iot-glass-border);
}

.param-more {
  font-size: 12px;
  color: var(--iot-color-text-muted);
  font-weight: 500;
}

.param-empty {
  font-size: 12px;
  color: var(--iot-color-text-muted);
  font-style: italic;
}

.service-action {
  display: flex;
  justify-content: flex-end;
  padding-top: var(--space-md);
  border-top: 1px solid var(--iot-glass-border);
  margin-top: auto;
}

/* Dialog styles */
.dialog-info {
  background: var(--iot-glass-bg);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  padding: var(--space-lg);
  margin-bottom: var(--space-lg);
}

.info-row {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-bottom: var(--space-sm);

  strong {
    color: var(--iot-color-text-secondary);
  }
}

.info-code {
  background: var(--iot-color-bg-card);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-family: 'Fira Code', monospace;
}

.info-desc {
  margin: 0;
  color: var(--iot-color-text-secondary);
  font-size: 14px;
}

.form-item-hint {
  font-size: 12px;
  color: var(--iot-color-text-muted);
  margin-top: 4px;
  line-height: 1.4;
}

.result-section {
  margin-top: var(--space-lg);
}

.result-content {
  background: var(--iot-glass-bg);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  padding: var(--space-md);
  max-height: 200px;
  overflow-y: auto;

  pre {
    margin: 0;
    font-family: 'Fira Code', monospace;
    font-size: 12px;
    line-height: 1.5;
    color: var(--iot-color-text-primary);
  }
}

.result-empty {
  text-align: center;
  padding: var(--space-md);
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-sm);
}
</style>
