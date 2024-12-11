<script lang="jsx" setup>
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
  <svg height="1337" width="1337">
    <defs>
      <path
        id="path-1"
        opacity="1"
        fill-rule="evenodd"
      />
      <linearGradient id="linearGradient-2" x1="0.79" y1="0.62" x2="0.21" y2="0.86">
        <stop offset="0" :stop-color="startColor" stop-opacity="1" />
        <stop offset="1" :stop-color="endColor" stop-opacity="1" />
      </linearGradient>
    </defs>
    <g opacity="1">
      <use xlink:href="#path-1" fill="url(#linearGradient-2)" fill-opacity="1" />
    </g>
  </svg>
</template>

<style scoped></style>
