<script setup>
defineProps({
  tslProp: {
    type: Object,
    required: true,
  },
  value: {
    type: [String, Number],
    default: '-',
  },
})
</script>

<template>
  <div class="flex items-center gap-3 p-2 rounded-lg hover:bg-blue-50 transition-colors">
    <div class="flex-shrink-0">
      <el-image
        v-if="tslProp.icon"
        class="w-12 h-12 rounded-lg"
        :src="tslProp.icon"
        fit="cover"
      />
      <div v-else class="w-12 h-12 bg-blue-100 rounded-lg flex-center">
        <i class="i-ep-data-analysis text-blue-500 text-xl" />
      </div>
    </div>
    <div class="flex-1 min-w-0">
      <div class="text-lg font-semibold text-gray-900 truncate">
        {{ value }}{{ tslProp.unit || '' }}
      </div>
      <div class="text-sm text-gray-600 truncate">
        {{ tslProp.name }}
      </div>
    </div>
  </div>
</template>
