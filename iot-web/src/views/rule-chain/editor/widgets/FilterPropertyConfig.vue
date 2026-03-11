<script setup>
/**
 * 属性条件过滤节点配置
 */
import { computed } from 'vue'

const props = defineProps({
  config: {
    type: Object,
    required: true,
  },
  availableProperties: {
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

// 选中的属性
const selectedProperty = computed(() => {
  if (!localConfig.value.identifier)
    return null
  return props.availableProperties.find(
    prop => prop.identifier === localConfig.value.identifier,
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
  emit('update:config', { ...localConfig.value, [key]: value })
}
</script>

<template>
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
      :model-value="localConfig.identifier"
      placeholder="请先选择产品"
      filterable
      :disabled="!productId"
      @update:model-value="updateConfig('identifier', $event)"
    >
      <el-option
        v-for="prop in availableProperties"
        :key="prop.identifier"
        :label="`${prop.name} (${prop.identifier})`"
        :value="prop.identifier"
      />
    </el-select>
    <div v-if="!productId" class="form-tip">
      请先在左侧选择产品
    </div>
    <div v-else-if="upstreamNode?.config?.identifiers?.length" class="form-tip">
      属性列表来自上游节点「{{ upstreamNode.name }}」的过滤结果
    </div>
  </el-form-item>

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
    <el-input-number
      :model-value="localConfig.value"
      style="width: 100%"
      @update:model-value="updateConfig('value', $event)"
    />
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
