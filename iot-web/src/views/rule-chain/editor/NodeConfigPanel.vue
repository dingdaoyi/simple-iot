<script setup>
/**
 * 规则链节点配置面板
 * 统一管理所有节点类型的配置界面
 */
import { computed, ref, watch } from 'vue'
import { productListApi, deviceListApi, ruleChainTemplateVariablesApi } from '@/api/index.js'

const props = defineProps({
  selectedNode: {
    type: Object,
    default: null,
  },
  ruleChain: {
    type: Object,
    default: () => ({}),
  },
  tslProperties: {
    type: Array,
    default: () => [],
  },
  tslEvents: {
    type: Array,
    default: () => [],
  },
  upstreamNode: {
    type: Object,
    default: null,
  },
  productOptions: {
    type: Array,
    default: () => [],
  },
  productTypeOptions: {
    type: Array,
    default: () => [],
  },
  messageReceiveOptions: {
    type: Array,
    default: () => [],
  },
  pushConfigOptions: {
    type: Array,
    default: () => [],
  },
  deviceOptions: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:node'])

// 本地节点配置
const config = computed(() => props.selectedNode?.config || {})

// ==================== 模板变量 ====================
const templateVariables = ref([])
const loadingVariables = ref(false)

// 加载模板变量
async function loadTemplateVariables() {
  if (!props.ruleChain.productId) {
    templateVariables.value = []
    return
  }
  loadingVariables.value = true
  try {
    const { data } = await ruleChainTemplateVariablesApi(props.ruleChain.productId)
    templateVariables.value = data || []
  }
  catch (e) {
    console.error('加载模板变量失败', e)
    templateVariables.value = []
  }
  finally {
    loadingVariables.value = false
  }
}

// 监听产品ID变化
watch(() => props.ruleChain.productId, () => {
  loadTemplateVariables()
}, { immediate: true })

// 系统变量列表
const systemVariables = computed(() =>
  templateVariables.value.filter(v => v.category === 'SYSTEM'),
)

// 属性变量列表
const propertyVariables = computed(() =>
  templateVariables.value.filter(v => v.category === 'PROPERTY'),
)

// 变量提示文本
const variableHintText = computed(() => {
  const system = systemVariables.value.map(v => v.variable).join(', ')
  const props = propertyVariables.value.slice(0, 5).map(v => v.variable).join(', ')
  const more = propertyVariables.value.length > 5 ? '...' : ''
  return `可用变量: ${system}, ${props}${more}`
})

// 插入变量到输入框
function insertVariable(variable, configKey) {
  const currentValue = config.value[configKey] || ''
  updateConfig(configKey, currentValue + variable)
}

// ==================== 计算属性 ====================

// 可用属性列表（考虑上游节点过滤）
const availableProperties = computed(() => {
  const upstream = props.upstreamNode
  if (upstream?.type === 'INPUT_PROPERTY' && upstream.config?.identifiers?.length > 0) {
    return props.tslProperties.filter(
      prop => upstream.config.identifiers.includes(prop.identifier),
    )
  }
  return props.tslProperties
})

// 可用事件列表（考虑上游节点过滤）
const availableEvents = computed(() => {
  const upstream = props.upstreamNode
  if (upstream?.type === 'INPUT_EVENT' && upstream.config?.identifiers?.length > 0) {
    return props.tslEvents.filter(
      event => upstream.config.identifiers.includes(event.identifier),
    )
  }
  return props.tslEvents
})

// 选中的属性
const selectedProperty = computed(() => {
  if (!config.value.identifier)
    return null
  return availableProperties.value.find(
    prop => prop.identifier === config.value.identifier,
  )
})

// 选中的事件
const selectedEvent = computed(() => {
  if (!config.value.identifier)
    return null
  return availableEvents.value.find(
    event => event.identifier === config.value.identifier,
  )
})

// 选中的事件参数
const selectedEventParam = computed(() => {
  if (!config.value.paramIdentifier || !selectedEvent.value)
    return null
  return selectedEvent.value.outputParams?.find(
    param => param.identifier === config.value.paramIdentifier,
  )
})

// ==================== 操作符选项 ====================

const propertyOperatorOptions = [
  { label: '大于 (GT)', value: 'GT' },
  { label: '大于等于 (GE)', value: 'GE' },
  { label: '等于 (EQ)', value: 'EQ' },
  { label: '小于 (LT)', value: 'LT' },
  { label: '小于等于 (LE)', value: 'LE' },
  { label: '不等于 (NE)', value: 'NE' },
]

const eventOperatorOptions = [
  { label: '大于 (GT)', value: 'GT' },
  { label: '大于等于 (GE)', value: 'GE' },
  { label: '等于 (EQ)', value: 'EQ' },
  { label: '小于 (LT)', value: 'LT' },
  { label: '小于等于 (LE)', value: 'LE' },
  { label: '不等于 (NE)', value: 'NE' },
]

// ==================== 工具函数 ====================

function updateConfig(key, value) {
  emit('update:node', {
    ...props.selectedNode,
    config: { ...config.value, [key]: value },
  })
}

function clearConfigKey(key) {
  const newConfig = { ...config.value }
  delete newConfig[key]
  emit('update:node', {
    ...props.selectedNode,
    config: newConfig,
  })
}

// 当选择新事件时，清除参数相关配置
function onEventIdentifierChange(value) {
  const newConfig = { ...config.value, identifier: value }
  delete newConfig.paramIdentifier
  delete newConfig.operator
  delete newConfig.value
  emit('update:node', {
    ...props.selectedNode,
    config: newConfig,
  })
}

// 设备级联选择器配置（懒加载）
const deviceCascadeProps = {
  lazy: true,
  value: 'id',
  label: 'name',
  async lazyLoad(node, resolve) {
    const { level, value } = node
    if (level === 0) {
      // 第一级：产品类型
      resolve((props.productTypeOptions || []).map(pt => ({
        id: pt.id,
        name: pt.name,
        leaf: false,
      })))
    }
    else if (level === 1) {
      // 第二级：根据产品类型加载产品
      try {
        const response = await productListApi({ productTypeId: value })
        const products = (response.data || []).map(p => ({
          id: p.id,
          name: p.model || p.manufacturer || `产品${p.id}`,
          leaf: false,
        }))
        resolve(products)
      }
      catch (e) {
        console.error('Failed to load products:', e)
        resolve([])
      }
    }
    else if (level === 2) {
      // 第三级：根据产品加载设备
      try {
        const { data } = await deviceListApi({ productId: value })
        const devices = (data || []).map(d => ({
          id: d.id,
          name: d.deviceName,
          leaf: true,
        }))
        resolve(devices)
      }
      catch {
        resolve([])
      }
    }
    else {
      resolve([])
    }
  },
}

// 获取设备级联选择器的当前值
function getDeviceCascadeValue() {
  const cfg = config.value
  if (cfg.targetDeviceId) {
    return [cfg.targetProductTypeId, cfg.targetProductId, cfg.targetDeviceId].filter(Boolean)
  }
  return []
}

// 处理设备级联选择变化
function onDeviceCascadeChange(value) {
  if (!value || value.length < 3) {
    // 清除选择
    const newConfig = { ...config.value }
    delete newConfig.targetProductTypeId
    delete newConfig.targetProductId
    delete newConfig.targetDeviceId
    emit('update:node', {
      ...props.selectedNode,
      config: newConfig,
    })
    return
  }
  // 更新目标设备配置
  emit('update:node', {
    ...props.selectedNode,
    config: {
      ...config.value,
      targetProductTypeId: value[0],
      targetProductId: value[1],
      targetDeviceId: value[2],
    },
  })
}
</script>

<template>
  <div v-if="selectedNode" class="node-config-panel">
    <!-- 节点名称 -->
    <el-form label-width="80px" size="small">
      <el-form-item label="节点名称">
        <el-input
          :model-value="selectedNode.name"
          @update:model-value="emit('update:node', { ...selectedNode, name: $event })"
        />
      </el-form-item>

      <!-- ==================== 输入节点配置 ==================== -->

      <!-- 属性上报输入节点 -->
      <template v-if="selectedNode.type === 'INPUT_PROPERTY'">
        <el-form-item label="监听属性">
          <el-select
            :model-value="config.identifiers"
            multiple
            filterable
            placeholder="留空表示监听所有属性"
            :disabled="!ruleChain.productId"
            @update:model-value="updateConfig('identifiers', $event)"
          >
            <el-option
              v-for="prop in tslProperties"
              :key="prop.identifier"
              :label="`${prop.name} (${prop.identifier})`"
              :value="prop.identifier"
            />
          </el-select>
          <div v-if="!ruleChain.productId" class="form-tip">
            请先在左侧选择产品
          </div>
          <div v-else class="form-tip">
            不选择则监听所有属性上报
          </div>
        </el-form-item>
      </template>

      <!-- 事件上报输入节点 -->
      <template v-if="selectedNode.type === 'INPUT_EVENT'">
        <el-form-item label="监听事件">
          <el-select
            :model-value="config.identifiers"
            multiple
            filterable
            placeholder="留空表示监听所有事件"
            :disabled="!ruleChain.productId"
            @update:model-value="updateConfig('identifiers', $event)"
          >
            <el-option
              v-for="event in tslEvents"
              :key="event.identifier"
              :label="`${event.name} (${event.identifier})`"
              :value="event.identifier"
            />
          </el-select>
          <div v-if="!ruleChain.productId" class="form-tip">
            请先在左侧选择产品
          </div>
          <div v-else class="form-tip">
            不选择则监听所有事件上报
          </div>
        </el-form-item>
      </template>

      <!-- 设备上下线节点 -->
      <template v-if="selectedNode.type === 'INPUT_ONLINE'">
        <el-form-item label="事件类型">
          <el-select
            :model-value="config.onlineStatus"
            placeholder="请选择上下线事件"
            @update:model-value="updateConfig('onlineStatus', $event)"
          >
            <el-option label="设备上线" value="online" />
            <el-option label="设备下线" value="offline" />
            <el-option label="全部（上线和下线）" value="all" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <div class="form-tip">
            监听规则链关联产品/设备的上线和下线事件
          </div>
        </el-form-item>
      </template>

      <!-- ==================== 过滤节点配置 ==================== -->

      <!-- 属性条件过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_PROPERTY'">
        <!-- 上游节点信息 -->
        <el-form-item v-if="upstreamNode" label="上游节点">
          <div class="upstream-info">
            <el-tag size="small" :type="upstreamNode.type === 'INPUT_PROPERTY' ? 'success' : 'info'">
              {{ upstreamNode.name }}
            </el-tag>
            <span v-if="upstreamNode.config?.identifiers?.length" class="upstream-detail">
              (已过滤 {{ upstreamNode.config.identifiers.length }} 个属性)
            </span>
          </div>
        </el-form-item>

        <el-form-item label="选择属性">
          <el-select
            :model-value="config.identifier"
            placeholder="请先选择产品"
            filterable
            :disabled="!ruleChain.productId"
            @update:model-value="updateConfig('identifier', $event)"
          >
            <el-option
              v-for="prop in availableProperties"
              :key="prop.identifier"
              :label="`${prop.name} (${prop.identifier})`"
              :value="prop.identifier"
            />
          </el-select>
          <div v-if="!ruleChain.productId" class="form-tip">
            请先在左侧选择产品
          </div>
          <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
            属性列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
          </div>
        </el-form-item>

        <el-form-item label="操作符">
          <el-select :model-value="config.operator" @update:model-value="updateConfig('operator', $event)">
            <el-option
              v-for="op in propertyOperatorOptions"
              :key="op.value"
              :label="op.label"
              :value="op.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="阈值">
          <el-input-number :model-value="config.value" style="width: 100%" @update:model-value="updateConfig('value', $event)" />
        </el-form-item>

        <!-- 属性信息 -->
        <el-form-item v-if="selectedProperty" label="属性信息">
          <div class="property-info">
            <span>类型: {{ selectedProperty.dataType?.name }}</span>
            <span v-if="selectedProperty.unit">单位: {{ selectedProperty.unit }}</span>
            <span v-if="selectedProperty.min !== null">最小: {{ selectedProperty.min }}</span>
            <span v-if="selectedProperty.max !== null">最大: {{ selectedProperty.max }}</span>
          </div>
        </el-form-item>
      </template>

      <!-- 事件类型过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_EVENT_TYPE'">
        <!-- 上游节点信息 -->
        <el-form-item v-if="upstreamNode" label="上游节点">
          <div class="upstream-info">
            <el-tag size="small" type="success">
              {{ upstreamNode.name }}
            </el-tag>
            <span v-if="upstreamNode.config?.identifiers?.length" class="upstream-detail">
              (已过滤 {{ upstreamNode.config.identifiers.length }} 个事件)
            </span>
          </div>
        </el-form-item>

        <el-form-item label="选择事件">
          <el-select
            :model-value="config.identifier"
            placeholder="请先选择产品"
            filterable
            clearable
            :disabled="!ruleChain.productId"
            @update:model-value="onEventIdentifierChange"
          >
            <el-option
              v-for="event in availableEvents"
              :key="event.identifier"
              :label="`${event.name} (${event.identifier})`"
              :value="event.identifier"
            />
          </el-select>
          <div v-if="!ruleChain.productId" class="form-tip">
            请先在左侧选择产品
          </div>
          <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
            事件列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
          </div>
          <div v-else class="form-tip">
            不选择则放行所有事件
          </div>
        </el-form-item>

        <!-- 选中事件后，显示事件详情和参数过滤 -->
        <template v-if="selectedEvent">
          <el-form-item label="事件信息">
            <div class="property-info">
              <span>类型: {{ selectedEvent.eventType }}</span>
              <span v-if="selectedEvent.remark">描述: {{ selectedEvent.remark }}</span>
            </div>
          </el-form-item>

          <!-- 如果事件有输出参数，可以选择参数进行条件过滤 -->
          <el-form-item v-if="selectedEvent.outputParams?.length" label="过滤参数">
            <el-select
              :model-value="config.paramIdentifier"
              placeholder="选择要过滤的参数"
              filterable
              clearable
              @update:model-value="updateConfig('paramIdentifier', $event)"
            >
              <el-option
                v-for="param in selectedEvent.outputParams"
                :key="param.identifier"
                :label="`${param.name} (${param.identifier})`"
                :value="param.identifier"
              />
            </el-select>
            <div class="form-tip">
              不选择则只按事件类型过滤
            </div>
          </el-form-item>

          <!-- 如果选择了参数，显示操作符和阈值 -->
          <template v-if="config.paramIdentifier">
            <el-form-item label="操作符">
              <el-select :model-value="config.operator" @update:model-value="updateConfig('operator', $event)">
                <el-option
                  v-for="op in eventOperatorOptions"
                  :key="op.value"
                  :label="op.label"
                  :value="op.value"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="阈值">
              <el-input :model-value="config.value" placeholder="输入阈值" style="width: 100%" @update:model-value="updateConfig('value', $event)" />
            </el-form-item>
            <el-form-item v-if="selectedEventParam" label="参数信息">
              <div class="property-info">
                <span>类型: {{ selectedEventParam.dataType?.name }}</span>
                <span v-if="selectedEventParam.unit">单位: {{ selectedEventParam.unit }}</span>
              </div>
            </el-form-item>
          </template>
        </template>
      </template>

      <!-- 脚本过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_SCRIPT'">
        <el-form-item label="过滤脚本">
          <el-input
            :model-value="config.script"
            type="textarea"
            :rows="6"
            placeholder="// 返回 true 放行，false 拦截&#10;// 可用变量: msg, device, properties&#10;return msg.temperature > 30;"
            @update:model-value="updateConfig('script', $event)"
          />
          <div class="form-tip">
            JavaScript 表达式，返回 true 走 True 分支，false 走 False 分支
          </div>
        </el-form-item>
      </template>

      <!-- ==================== 告警节点配置 ==================== -->

      <!-- 创建告警节点 -->
      <template v-if="selectedNode.type === 'ALARM_CREATE'">
        <el-form-item label="告警类型">
          <el-input :model-value="config.alarmType" placeholder="high_temperature" @update:model-value="updateConfig('alarmType', $event)" />
        </el-form-item>
        <el-form-item label="告警名称">
          <el-input :model-value="config.alarmName" placeholder="高温告警-${deviceName}" @update:model-value="updateConfig('alarmName', $event)" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select :model-value="config.severity" @update:model-value="updateConfig('severity', $event)">
            <el-option label="严重" value="CRITICAL" />
            <el-option label="主要" value="MAJOR" />
            <el-option label="次要" value="MINOR" />
            <el-option label="警告" value="WARNING" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警消息">
          <el-input
            :model-value="config.message"
            type="textarea"
            :rows="3"
            placeholder="设备 ${deviceName} 温度 ${temperature} 超过阈值"
            @update:model-value="updateConfig('message', $event)"
          />
          <div class="variable-hint">
            <span class="hint-label">可用变量:</span>
            <el-tag
              v-for="v in systemVariables"
              :key="v.variable"
              size="small"
              class="variable-tag"
              @click="insertVariable(v.variable, 'message')"
            >
              {{ v.variable }}
            </el-tag>
            <template v-if="propertyVariables.length">
              <el-tag
                v-for="v in propertyVariables.slice(0, 6)"
                :key="v.variable"
                size="small"
                type="info"
                class="variable-tag"
                @click="insertVariable(v.variable, 'message')"
              >
                {{ v.variable }}
              </el-tag>
            </template>
          </div>
            @update:model-value="updateConfig('message', $event)"
          />
        </el-form-item>
      </template>

      <!-- 清除告警节点 -->
      <template v-if="selectedNode.type === 'ALARM_CLEAR'">
        <el-form-item label="告警类型">
          <el-input :model-value="config.alarmType" placeholder="high_temperature" @update:model-value="updateConfig('alarmType', $event)" />
          <div class="form-tip">
            留空则清除该设备的所有告警
          </div>
        </el-form-item>
        <el-form-item label="清除方式">
          <el-radio-group :model-value="config.clearType" @update:model-value="updateConfig('clearType', $event)">
            <el-radio label="active">
              仅清除活动告警
            </el-radio>
            <el-radio label="all">
              清除所有相关告警
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </template>

      <!-- ==================== 输出节点配置 ==================== -->

      <!-- 消息推送节点 -->
      <template v-if="selectedNode.type === 'OUTPUT_MESSAGE'">
        <el-form-item label="消息配置">
          <el-select
            :model-value="config.messageReceiveId"
            placeholder="请选择消息配置"
            filterable
            clearable
            @update:model-value="updateConfig('messageReceiveId', $event)"
          >
            <el-option
              v-for="item in messageReceiveOptions"
              :key="item.id"
              :label="`${item.name} (${item.notifyType === 1 ? '邮件' : item.notifyType === 2 ? '短信' : '电话'})`"
              :value="item.id"
            />
          </el-select>
          <div v-if="messageReceiveOptions.length === 0" class="form-tip" style="color: var(--el-color-warning);">
            暂无消息配置，请先在「消息接收」中添加配置
          </div>
        </el-form-item>

        <el-form-item label="消息标题">
          <el-input
            :model-value="config.title"
            placeholder="设备告警通知 - ${deviceName}"
            @update:model-value="updateConfig('title', $event)"
          />
          <div class="variable-hint">
            <span class="hint-label">点击插入:</span>
            <el-tag v-for="v in systemVariables" :key="v.variable" size="small" class="variable-tag" @click="insertVariable(v.variable, 'title')">
              {{ v.name }}
            </el-tag>
          </div>
        </el-form-item>

        <el-form-item label="消息内容">
          <el-input
            :model-value="config.content"
            type="textarea"
            :rows="4"
            placeholder="设备 ${deviceName} 于 ${eventTime} 触发告警，当前值: ${value}"
            @update:model-value="updateConfig('content', $event)"
          />
          <div class="form-tip">
            支持变量: ${deviceName}, ${deviceKey}, ${eventTime}, ${属性标识符}
          </div>
        </el-form-item>
      </template>

      <!-- HTTP推送节点 -->
      <template v-if="selectedNode.type === 'OUTPUT_HTTP'">
        <el-form-item label="推送配置">
          <el-select
            :model-value="config.pushConfigId"
            placeholder="请选择HTTP推送配置"
            filterable
            clearable
            @update:model-value="updateConfig('pushConfigId', $event)"
          >
            <el-option
              v-for="item in pushConfigOptions.filter(p => p.type === 'HTTP')"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <div style="display: flex; justify-content: space-between;">
                <span>{{ item.name }}</span>
                <span style="color: var(--iot-color-text-muted); font-size: 12px;">{{ item.httpUrl }}</span>
              </div>
            </el-option>
          </el-select>
          <div v-if="pushConfigOptions.filter(p => p.type === 'HTTP').length === 0" class="form-tip" style="color: var(--el-color-warning);">
            暂无HTTP推送配置，请先在「推送配置」中添加
          </div>
          <div v-else class="form-tip">
            选择预先配置好的HTTP推送参数
          </div>
        </el-form-item>

        <el-form-item label="自定义请求体">
          <el-input
            :model-value="config.customBody"
            type="textarea"
            :rows="4"
            placeholder="{&quot;device&quot;: &quot;${deviceName}&quot;, &quot;value&quot;: ${temperature}}"
            @update:model-value="updateConfig('customBody', $event)"
          />
          <div class="form-tip">
            留空使用默认请求体。支持变量: ${deviceName}, ${deviceKey}, ${eventTime}, ${属性标识符}
          </div>
        </el-form-item>
      </template>

      <!-- MQTT推送节点 -->
      <template v-if="selectedNode.type === 'OUTPUT_MQTT'">
        <el-form-item label="推送配置">
          <el-select
            :model-value="config.pushConfigId"
            placeholder="请选择MQTT推送配置"
            filterable
            clearable
            @update:model-value="updateConfig('pushConfigId', $event)"
          >
            <el-option
              v-for="item in pushConfigOptions.filter(p => p.type === 'MQTT')"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
              <div style="display: flex; justify-content: space-between;">
                <span>{{ item.name }}</span>
                <span style="color: var(--iot-color-text-muted); font-size: 12px;">{{ item.mqttTopic }}</span>
              </div>
            </el-option>
          </el-select>
          <div v-if="pushConfigOptions.filter(p => p.type === 'MQTT').length === 0" class="form-tip" style="color: var(--el-color-warning);">
            暂无MQTT推送配置，请先在「推送配置」中添加
          </div>
          <div v-else class="form-tip">
            选择预先配置好的MQTT推送参数
          </div>
        </el-form-item>

        <el-form-item label="自定义Payload">
          <el-input
            :model-value="config.customPayload"
            type="textarea"
            :rows="4"
            placeholder="{&quot;device&quot;: &quot;${deviceName}&quot;, &quot;value&quot;: ${temperature}}"
            @update:model-value="updateConfig('customPayload', $event)"
          />
          <div class="form-tip">
            留空使用默认Payload。支持变量: ${deviceName}, ${deviceKey}, ${eventTime}, ${属性标识符}
          </div>
        </el-form-item>
      </template>

      <!-- 设备指令节点 -->
      <template v-if="selectedNode.type === 'OUTPUT_COMMAND'">
        <!-- 目标设备选择 -->
        <el-form-item label="目标设备">
          <el-radio-group :model-value="config.targetMode || 'self'" @update:model-value="updateConfig('targetMode', $event)">
            <el-radio label="self">
              当前设备
            </el-radio>
            <el-radio label="other">
              其他设备
            </el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 其他设备选择 -->
        <template v-if="config.targetMode === 'other'">
          <el-form-item label="选择设备">
            <el-cascader
              :model-value="getDeviceCascadeValue()"
              :props="deviceCascadeProps"
              placeholder="选择产品类型 > 产品 > 设备"
              filterable
              clearable
              style="width: 100%"
              @update:model-value="onDeviceCascadeChange"
            />
            <div class="form-tip">
              选择要发送指令的目标设备
            </div>
          </el-form-item>
        </template>

        <el-form-item label="指令类型">
          <el-select :model-value="config.commandType" @update:model-value="updateConfig('commandType', $event)">
            <el-option label="服务调用" value="service" />
          </el-select>
          <div class="form-tip">
            通过调用设备服务实现属性设置或功能控制
          </div>
        </el-form-item>

        <el-form-item label="服务标识">
          <el-input
            :model-value="config.identifier"
            placeholder="服务identifier，如: setValue"
            @update:model-value="updateConfig('identifier', $event)"
          />
          <div class="form-tip">
            物模型中定义的服务标识符
          </div>
        </el-form-item>

        <el-form-item label="服务参数">
          <el-input
            :model-value="config.params"
            type="textarea"
            :rows="4"
            placeholder="{&quot;value&quot;: 25}"
            @update:model-value="updateConfig('params', $event)"
          />
          <div class="form-tip">
            JSON 格式的服务参数，支持变量: ${deviceName}, ${deviceKey}, ${eventTime}, ${属性标识符}
          </div>
        </el-form-item>
      </template>
    </el-form>
  </div>
</template>

<style scoped>
.node-config-panel {
  padding: 12px;
}

.upstream-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.upstream-detail {
  color: var(--iot-color-text-muted);
  font-size: 12px;
}

.property-info {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: var(--iot-color-text-secondary);
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: var(--iot-color-text-muted);
}

.variable-hint {
  margin-top: 4px;
  font-size: 12px;
  color: var(--iot-color-text-muted);
}

.variable-hint .hint-label {
  margin-right: 6px;
}

.variable-hint .variable-tag {
  margin: 2px 4px 2px 0;
  cursor: pointer;
  font-size: 11px;
}

.variable-hint .variable-tag:hover {
  opacity: 0.8;
}
</style>
