<script setup>
import router from '@/router/index.js'
import { useAccountStore } from '@/store/index.js'

const store = useAccountStore()
if (!store.authorization) {
  router.replace('/login')
}
</script>

<template>
  66
</template>
