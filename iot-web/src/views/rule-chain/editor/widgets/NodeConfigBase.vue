<script setup>
import { computed, inject } from 'vue'

const props = defineProps({
  node: {
    type: Object,
    required: true,
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
})

const emit = defineEmits(['update:node'])

// 本地配置对象（双向绑定）
const config = computed({
  get: () => props.node.config || {},
  set: (val) => {
    emit('update:node', { ...props.node, config: val })
  },
})

// 可用的属性列表（考虑上游节点过滤）
const availableProperties = computed(() => {
  const upstream = props.upstreamNode
  if (upstream?.type === 'INPUT_PROPERTY' && upstream.config?.identifiers?.length > 0) {
    return props.tslProperties.filter(
      prop => upstream.config.identifiers.includes(prop.identifier),
    )
  }
  return props.tslProperties
})

// 可用的事件列表（考虑上游节点过滤）
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
  if (!config.value?.identifier)
    return null
  return availableProperties.value.find(
    prop => prop.identifier === config.value.identifier,
  )
})

// 选中的事件
const selectedEvent = computed(() => {
  if (!config.value?.identifier)
    return null
  return availableEvents.value.find(
    event => event.identifier === config.value.identifier,
  )
})

// 选中的事件参数
const selectedEventParam = computed(() => {
  if (!config.value?.paramIdentifier || !selectedEvent.value)
    return null
  return selectedEvent.value.outputParams?.find(
    param => param.identifier === config.value.paramIdentifier,
  )
})

// 操作符选项
const operatorOptions = [
  { label: '大于 (GT)', value: 'GT' },
  { label: '大于等于 (GE)', value: 'GE' },
  { label: '等于 (EQ)', value: 'EQ' },
  { label: '小于 (LT)', value: 'LT' },
  { label: '小于等于 (LE)', value: 'LE' },
  { label: '不等于 (NE)', value: 'NE' },
]

// 更新配置字段
function updateConfig(key, value) {
  config.value = { ...config.value, [key]: value }
}

// 清除配置字段
function clearConfig(key) {
  const newConfig = { ...config.value }
  delete newConfig[key]
  config.value = newConfig
}
</script>

<template>
  <div class="node-config-base">
    <!-- 由子组件实现具体配置 -->
    <slot
      :config="config"
      :available-properties="availableProperties"
      :available-events="availableEvents"
      :selected-property="selectedProperty"
      :selected-event="selectedEvent"
      :selected-event-param="selectedEventParam"
      :operator-options="operatorOptions"
      :update-config="updateConfig"
      :clear-config="clearConfig"
    />
  </div>
</template>
