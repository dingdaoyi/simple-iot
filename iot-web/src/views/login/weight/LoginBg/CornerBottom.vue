<script setup>
const props = defineProps({
  startColor: {
    type: String,
    default: '#28aff0',
  },
  endColor: {
    type: String,
    default: '#120fc4',
  },
})
</script>

<template>
  <svg
    version="1.1"
    xmlns="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    height="896"
    width="967.8852157128662"
  >
    <defs>
      <path
        id="path-2"
        opacity="1"
        fill-rule="evenodd"
      />
      <linearGradient id="linearGradient-3" x1="0.5" y1="0" x2="0.5" y2="1">
        <stop offset="0" :stop-color="startColor" stop-opacity="1" />
        <stop offset="1" :stop-color="endColor" stop-opacity="1" />
      </linearGradient>
    </defs>
    <g opacity="1">
      <use xlink:href="#path-2" fill="url(#linearGradient-3)" fill-opacity="1" />
    </g>
  </svg>
</template>

<style scoped></style>
