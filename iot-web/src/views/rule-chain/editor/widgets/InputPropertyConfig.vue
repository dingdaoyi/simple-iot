<script setup>
/**
 * 属性上报输入节点配置
 */
const props = defineProps({
  config: {
    type: Object,
    required: true,
  },
  tslProperties: {
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
  <el-form-item label="监听属性">
    <el-select
      :model-value="localIdentifiers"
      multiple
      filterable
      placeholder="留空表示监听所有属性"
      :disabled="!productId"
      @update:model-value="handleIdentifiersChange"
    >
      <el-option
        v-for="prop in tslProperties"
        :key="prop.identifier"
        :label="`${prop.name} (${prop.identifier})`"
        :value="prop.identifier"
      />
    </el-select>
    <div v-if="!productId" class="form-tip">
      请先在左侧选择产品
    </div>
    <div v-else class="form-tip">
      不选择则监听所有属性上报，下游过滤节点将只显示选中的属性
    </div>
  </el-form-item>
</template>
