<template>
  <el-scrollbar class="!w-200px bg-dwboxbg py-30px px-10px overflow-auto">
    <menuTree :data="menuData" :actPath="path" />
  </el-scrollbar>
</template>

<script lang="jsx" setup>
import { computed, watchEffect, ref } from 'vue'
import menuTree from '@/components/selectTree/menuTree'
import { useAccountStore } from '@/store'
import { useRoute } from 'vue-router'

const route = useRoute()
const path = computed(() => route.path)
const { menuData } = useAccountStore()

watchEffect(() => {
  const getTree2 = (data, id) => {
    data.forEach(item => {
      if (item.id === id) {
        item.showChild = true
        if (item.parentId) {
          getTree2(menuData, item.parentId)
        }
      } else {
        item.children && getTree2(item.children, id)
      }
    })
  }
  const getTree = (data) => {
    data.forEach(item => {
      if (item.permissionUrl === path.value) {
        getTree2(menuData, item.parentId)
      } else {
        item.children && getTree(item.children)
      }
    })
  }
  getTree(menuData)
})

</script>

<style lang="scss" scoped>
</style>
