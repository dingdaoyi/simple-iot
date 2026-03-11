<script setup>
/**
 * 事件类型过滤节点配置
 */
import { computed } from 'vue'

const props = defineProps({
  config: {
    type: Object,
    required: true,
  },
  availableEvents: {
    type: Array,
    default: () => [],
  },
  upstreamNode: {
    type: Object,
    default: null,
  },
  productId: {
    type: [Number, String],
    default: null,
  },
})

const emit = defineEmits(['update:config'])

// 本地配置
const localConfig = computed(() => props.config || {})

// 选中的事件
const selectedEvent = computed(() => {
  if (!localConfig.value.identifier)
    return null
  return props.availableEvents.find(
    event => event.identifier === localConfig.value.identifier,
  )
})

// 选中的事件参数
const selectedEventParam = computed(() => {
  if (!localConfig.value.paramIdentifier || !selectedEvent.value)
    return null
  return selectedEvent.value.outputParams?.find(
    param => param.identifier === localConfig.value.paramIdentifier,
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

function updateConfig(key, value) {
  const newConfig = { ...localConfig.value, [key]: value }
  // 切换事件时清除参数选择
  if (key === 'identifier') {
    newConfig.paramIdentifier = null
    newConfig.operator = 'GT'
    newConfig.value = null
  }
  // 清除参数选择时清除操作符和阈值
  if (key === 'paramIdentifier' && !value) {
    newConfig.operator = 'GT'
    newConfig.value = null
  }
  emit('update:config', newConfig)
}
</script>

<template>
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
      :model-value="localConfig.identifier"
      placeholder="请先选择产品"
      filterable
      clearable
      :disabled="!productId"
      @update:model-value="updateConfig('identifier', $event)"
    >
      <el-option
        v-for="event in availableEvents"
        :key="event.identifier"
        :label="`${event.name} (${event.identifier})`"
        :value="event.identifier"
      />
    </el-select>
    <div v-if="!productId" class="form-tip">
      请先在左侧选择产品
    </div>
    <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
      事件列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
    </div>
    <div v-else class="form-tip">
      不选择则放行所有事件
    </div>
  </el-form-item>

  <!-- 选中事件后，显示事件的输出参数过滤配置 -->
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
        :model-value="localConfig.paramIdentifier"
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
    <template v-if="localConfig.paramIdentifier">
      <el-form-item label="操作符">
        <el-select
          :model-value="localConfig.operator"
          @update:model-value="updateConfig('operator', $event)"
        >
          <el-option
            v-for="op in operatorOptions"
            :key="op.value"
            :label="op.label"
            :value="op.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="阈值">
        <el-input
          :model-value="localConfig.value"
          placeholder="输入阈值"
          style="width: 100%"
          @update:model-value="updateConfig('value', $event)"
        />
      </el-form-item>
      <!-- 显示选中参数的信息 -->
      <el-form-item v-if="selectedEventParam" label="参数信息">
        <div class="property-info">
          <span>类型: {{ selectedEventParam.dataType?.name }}</span>
          <span v-if="selectedEventParam.unit">单位: {{ selectedEventParam.unit }}</span>
        </div>
      </el-form-item>
    </template>
  </template>
</template>

<style scoped>
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
</style>
