<script setup>
/**
 * 事件上报输入节点配置
 */
const props = defineProps({
  config: {
    type: Object,
    required: true,
  },
  tslEvents: {
    type: Array,
    default: () => [],
  },
  productId: {
    type: [Number, String],
    default: null,
  },
})

const emit = defineEmits(['update:config'])

// 本地绑定的标识符列表
const localIdentifiers = defineModel('identifiers', { type: Array, default: () => [] })

function handleIdentifiersChange(val) {
  localIdentifiers.value = val
  emit('update:config', { identifiers: val })
}
</script>

<template>
  <el-form-item label="监听事件">
    <el-select
      :model-value="localIdentifiers"
      multiple
      filterable
      placeholder="留空表示监听所有事件"
      :disabled="!productId"
      @update:model-value="handleIdentifiersChange"
    >
      <el-option
        v-for="event in tslEvents"
        :key="event.identifier"
        :label="`${event.name} (${event.identifier})`"
        :value="event.identifier"
      />
    </el-select>
    <div v-if="!productId" class="form-tip">
      请先在左侧选择产品
    </div>
    <div v-else class="form-tip">
      不选择则监听所有事件上报
    </div>
  </el-form-item>
</template>
