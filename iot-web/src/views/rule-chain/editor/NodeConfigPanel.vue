<script setup>
/**
 * 规则链节点配置面板
 * 统一管理所有节点类型的配置界面
 */
import { computed, ref, watch } from 'vue'
import { deviceListApi, productListApi, ruleChainTemplateVariablesApi } from '@/api/index.js'
import VariableInserter from './VariableInserter.vue'

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

// 输入框 ref 集合（用于光标位置插入）
const alarmMessageRef = ref(null)
const outputTitleRef = ref(null)
const outputContentRef = ref(null)
const httpBodyRef = ref(null)
const mqttPayloadRef = ref(null)
const cmdParamsRef = ref(null)
const filterScriptRef = ref(null)

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

// 属性阈值是否有 min/max
const propertyMin = computed(() => {
  const v = selectedProperty.value?.min
  return v === null || v === undefined || v === '' ? null : Number(v)
})
const propertyMax = computed(() => {
  const v = selectedProperty.value?.max
  return v === null || v === undefined || v === '' ? null : Number(v)
})
const propertyStep = computed(() => {
  const t = selectedProperty.value?.dataType?.type
  return t === 'int' || t === 'INT' || t === 'long' ? 1 : 0.1
})

// ==================== 操作符选项 ====================

const propertyOperatorOptions = [
  { label: '>  大于', value: 'GT' },
  { label: '≥  大于等于', value: 'GE' },
  { label: '=  等于', value: 'EQ' },
  { label: '<  小于', value: 'LT' },
  { label: '≤  小于等于', value: 'LE' },
  { label: '≠  不等于', value: 'NE' },
]

const eventOperatorOptions = propertyOperatorOptions

// ==================== 工具函数 ====================

function updateConfig(key, value) {
  emit('update:node', {
    ...props.selectedNode,
    config: { ...config.value, [key]: value },
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
      resolve((props.productTypeOptions || []).map(pt => ({
        id: pt.id,
        name: pt.name,
        leaf: false,
      })))
    }
    else if (level === 1) {
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

function getDeviceCascadeValue() {
  const cfg = config.value
  if (cfg.targetDeviceId) {
    return [cfg.targetProductTypeId, cfg.targetProductId, cfg.targetDeviceId].filter(Boolean)
  }
  return []
}

function onDeviceCascadeChange(value) {
  if (!value || value.length < 3) {
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
            collapse-tags
            collapse-tags-tooltip
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
          <div v-if="!ruleChain.productId" class="form-tip warn">
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
            collapse-tags
            collapse-tags-tooltip
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
          <div v-if="!ruleChain.productId" class="form-tip warn">
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
          <div class="form-tip">
            监听规则链关联产品/设备的上线和下线事件
          </div>
        </el-form-item>
      </template>

      <!-- ==================== 过滤节点配置 ==================== -->

      <!-- 属性条件过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_PROPERTY'">
        <el-form-item v-if="upstreamNode" label="上游节点">
          <div class="upstream-info">
            <el-tag size="small" :type="upstreamNode.type === 'INPUT_PROPERTY' ? 'success' : 'info'">
              {{ upstreamNode.name }}
            </el-tag>
            <span v-if="upstreamNode.config?.identifiers?.length" class="upstream-detail">
              已过滤 {{ upstreamNode.config.identifiers.length }} 个属性
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
            >
              <span>{{ prop.name }}</span>
              <span class="opt-meta">{{ prop.identifier }}</span>
            </el-option>
          </el-select>
          <div v-if="!ruleChain.productId" class="form-tip warn">
            请先在左侧选择产品
          </div>
          <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
            属性列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
          </div>
        </el-form-item>

        <el-form-item label="操作符">
          <el-select :model-value="config.operator" placeholder="选择比较方式" @update:model-value="updateConfig('operator', $event)">
            <el-option
              v-for="op in propertyOperatorOptions"
              :key="op.value"
              :label="op.label"
              :value="op.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="阈值">
          <el-input-number
            :model-value="config.value"
            :min="propertyMin ?? undefined"
            :max="propertyMax ?? undefined"
            :step="propertyStep"
            :placeholder="selectedProperty?.unit ? `单位: ${selectedProperty.unit}` : '输入阈值'"
            style="width: 100%"
            @update:model-value="updateConfig('value', $event)"
          />
          <div v-if="selectedProperty && (propertyMin !== null || propertyMax !== null)" class="form-tip">
            <span v-if="propertyMin !== null">最小 {{ propertyMin }}</span>
            <span v-if="propertyMin !== null && propertyMax !== null"> · </span>
            <span v-if="propertyMax !== null">最大 {{ propertyMax }}</span>
            <span v-if="selectedProperty.unit"> · 单位 {{ selectedProperty.unit }}</span>
          </div>
        </el-form-item>

        <!-- 属性详细信息 -->
        <el-form-item v-if="selectedProperty" label="属性信息">
          <div class="property-info">
            <span class="info-chip">{{ selectedProperty.dataType?.name }}</span>
            <span v-if="selectedProperty.unit" class="info-chip">单位 {{ selectedProperty.unit }}</span>
            <span v-if="selectedProperty.accessMode" class="info-chip">{{ selectedProperty.accessMode }}</span>
          </div>
        </el-form-item>
      </template>

      <!-- 事件类型过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_EVENT_TYPE'">
        <el-form-item v-if="upstreamNode" label="上游节点">
          <div class="upstream-info">
            <el-tag size="small" type="success">
              {{ upstreamNode.name }}
            </el-tag>
            <span v-if="upstreamNode.config?.identifiers?.length" class="upstream-detail">
              已过滤 {{ upstreamNode.config.identifiers.length }} 个事件
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
          <div v-if="!ruleChain.productId" class="form-tip warn">
            请先在左侧选择产品
          </div>
          <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
            事件列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
          </div>
          <div v-else class="form-tip">
            不选择则放行所有事件
          </div>
        </el-form-item>

        <template v-if="selectedEvent">
          <el-form-item label="事件信息">
            <div class="property-info">
              <span class="info-chip">{{ selectedEvent.eventType }}</span>
              <span v-if="selectedEvent.remark" class="info-chip muted">{{ selectedEvent.remark }}</span>
            </div>
          </el-form-item>

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

          <template v-if="config.paramIdentifier">
            <el-form-item label="操作符">
              <el-select :model-value="config.operator" placeholder="选择比较方式" @update:model-value="updateConfig('operator', $event)">
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
                <span class="info-chip">{{ selectedEventParam.dataType?.name }}</span>
                <span v-if="selectedEventParam.unit" class="info-chip">单位 {{ selectedEventParam.unit }}</span>
              </div>
            </el-form-item>
          </template>
        </template>
      </template>

      <!-- 脚本过滤节点 -->
      <template v-if="selectedNode.type === 'FILTER_SCRIPT'">
        <el-form-item label="过滤脚本">
          <el-input
            ref="filterScriptRef"
            :model-value="config.script"
            type="textarea"
            :rows="8"
            placeholder="// 返回 true 走 True 分支，false 走 False 分支
// 可用变量见下方
return msg.temperature > 30;"
            @update:model-value="updateConfig('script', $event)"
          />
          <div class="form-tip">
            JavaScript 表达式，返回 true 走 <span class="branch-tag branch-true">True</span> 分支，false 走 <span class="branch-tag branch-false">False</span> 分支
          </div>
          <VariableInserter
            :target-ref="filterScriptRef"
            :value="config.script"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            label="脚本可用变量"
            @update="updateConfig('script', $event)"
          />
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
            <el-option value="CRITICAL">
              <span class="severity-dot critical" />严重 (CRITICAL)
            </el-option>
            <el-option value="MAJOR">
              <span class="severity-dot major" />主要 (MAJOR)
            </el-option>
            <el-option value="MINOR">
              <span class="severity-dot minor" />次要 (MINOR)
            </el-option>
            <el-option value="WARNING">
              <span class="severity-dot warning" />警告 (WARNING)
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="告警消息">
          <el-input
            ref="alarmMessageRef"
            :model-value="config.message"
            type="textarea"
            :rows="3"
            placeholder="设备 ${deviceName} 温度 ${temperature} 超过阈值"
            @update:model-value="updateConfig('message', $event)"
          />
          <VariableInserter
            :target-ref="alarmMessageRef"
            :value="config.message"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('message', $event)"
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
          <div v-if="messageReceiveOptions.length === 0" class="form-tip warn">
            暂无消息配置，请先在「消息接收」中添加配置
          </div>
        </el-form-item>

        <el-form-item label="消息标题">
          <el-input
            ref="outputTitleRef"
            :model-value="config.title"
            placeholder="设备告警通知 - ${deviceName}"
            @update:model-value="updateConfig('title', $event)"
          />
          <VariableInserter
            :target-ref="outputTitleRef"
            :value="config.title"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('title', $event)"
          />
        </el-form-item>

        <el-form-item label="消息内容">
          <el-input
            ref="outputContentRef"
            :model-value="config.content"
            type="textarea"
            :rows="4"
            placeholder="设备 ${deviceName} 于 ${eventTime} 触发告警，当前值: ${value}"
            @update:model-value="updateConfig('content', $event)"
          />
          <VariableInserter
            :target-ref="outputContentRef"
            :value="config.content"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('content', $event)"
          />
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
              <div class="opt-row">
                <span>{{ item.name }}</span>
                <span class="opt-meta">{{ item.httpUrl }}</span>
              </div>
            </el-option>
          </el-select>
          <div v-if="pushConfigOptions.filter(p => p.type === 'HTTP').length === 0" class="form-tip warn">
            暂无HTTP推送配置，请先在「推送配置」中添加
          </div>
          <div v-else class="form-tip">
            选择预先配置好的HTTP推送参数
          </div>
        </el-form-item>

        <el-form-item label="自定义请求体">
          <el-input
            ref="httpBodyRef"
            :model-value="config.customBody"
            type="textarea"
            :rows="5"
            placeholder="{ &quot;device&quot;: &quot;${deviceName}&quot;, &quot;value&quot;: ${temperature} }"
            @update:model-value="updateConfig('customBody', $event)"
          />
          <div class="form-tip">
            留空使用默认请求体
          </div>
          <VariableInserter
            :target-ref="httpBodyRef"
            :value="config.customBody"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('customBody', $event)"
          />
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
              <div class="opt-row">
                <span>{{ item.name }}</span>
                <span class="opt-meta">{{ item.mqttTopic }}</span>
              </div>
            </el-option>
          </el-select>
          <div v-if="pushConfigOptions.filter(p => p.type === 'MQTT').length === 0" class="form-tip warn">
            暂无MQTT推送配置，请先在「推送配置」中添加
          </div>
          <div v-else class="form-tip">
            选择预先配置好的MQTT推送参数
          </div>
        </el-form-item>

        <el-form-item label="自定义Payload">
          <el-input
            ref="mqttPayloadRef"
            :model-value="config.customPayload"
            type="textarea"
            :rows="5"
            placeholder="{ &quot;device&quot;: &quot;${deviceName}&quot;, &quot;value&quot;: ${temperature} }"
            @update:model-value="updateConfig('customPayload', $event)"
          />
          <div class="form-tip">
            留空使用默认Payload
          </div>
          <VariableInserter
            :target-ref="mqttPayloadRef"
            :value="config.customPayload"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('customPayload', $event)"
          />
        </el-form-item>
      </template>

      <!-- 设备指令节点 -->
      <template v-if="selectedNode.type === 'OUTPUT_COMMAND'">
        <el-form-item label="目标设备">
          <el-radio-group :model-value="config.targetMode || 'self'" @update:model-value="updateConfig('targetMode', $event)">
            <el-radio-button label="self">
              当前设备
            </el-radio-button>
            <el-radio-button label="other">
              其他设备
            </el-radio-button>
          </el-radio-group>
        </el-form-item>

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
            ref="cmdParamsRef"
            :model-value="config.params"
            type="textarea"
            :rows="4"
            placeholder="{ &quot;value&quot;: 25 }"
            @update:model-value="updateConfig('params', $event)"
          />
          <div class="form-tip">
            JSON 格式的服务参数
          </div>
          <VariableInserter
            :target-ref="cmdParamsRef"
            :value="config.params"
            :system-variables="systemVariables"
            :property-variables="propertyVariables"
            @update="updateConfig('params', $event)"
          />
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
  flex-wrap: wrap;
}

.upstream-detail {
  color: var(--iot-color-text-muted);
  font-size: 12px;
}

.property-info {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.info-chip {
  font-size: 11px;
  padding: 2px 8px;
  background: rgba(99, 102, 241, 0.1);
  color: #4338ca;
  border-radius: 10px;
  line-height: 1.6;

  &.muted {
    background: rgba(0, 0, 0, 0.04);
    color: var(--iot-color-text-secondary);
  }
}

.form-tip {
  margin-top: 4px;
  font-size: 12px;
  color: var(--iot-color-text-muted);
  line-height: 1.5;

  &.warn {
    color: var(--el-color-warning);
  }
}

.opt-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.opt-meta {
  color: var(--iot-color-text-muted);
  font-size: 11px;
  font-family: var(--iot-font-mono, ui-monospace, 'SF Mono', Menlo, monospace);
}

/* True/False 分支标签 */
.branch-tag {
  display: inline-block;
  padding: 1px 6px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
  margin: 0 2px;
}

.branch-true {
  background: rgba(16, 185, 129, 0.15);
  color: #047857;
}

.branch-false {
  background: rgba(239, 68, 68, 0.15);
  color: #b91c1c;
}

/* 严重程度小圆点 */
.severity-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
  vertical-align: middle;
}

.severity-dot.critical {
  background: #dc2626;
  box-shadow: 0 0 6px rgba(220, 38, 38, 0.5);
}
.severity-dot.major {
  background: #ea580c;
}
.severity-dot.minor {
  background: #ca8a04;
}
.severity-dot.warning {
  background: #2563eb;
}

:global(.dark) {
  .info-chip {
    background: rgba(99, 102, 241, 0.2);
    color: #c7d2fe;
    &.muted {
      background: rgba(255, 255, 255, 0.06);
    }
  }
  .branch-true {
    background: rgba(16, 185, 129, 0.25);
    color: #6ee7b7;
  }
  .branch-false {
    background: rgba(239, 68, 68, 0.25);
    color: #fca5a5;
  }
}
</style>
