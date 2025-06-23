<script setup>
defineProps({
  label: {
    type: String,
    default: '',
  },
  value: {
    type: [String, Number],
    default: '',
  },
})
</script>

<template>
  <div class="flex flex-col gap-1">
    <span class="text-sm font-medium text-gray-600">{{ label }}</span>
    <span class="text-base text-gray-900 truncate">{{ value || '-' }}</span>
  </div>
</template>
