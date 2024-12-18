<script setup>
defineProps(['label', 'value'])
</script>

<template>
  <div class="flex gap-20px">
    <div class="text-gray">
      {{ label || '' }}
    </div>
    <div class="w-300px">
      {{ value || '' }}
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
