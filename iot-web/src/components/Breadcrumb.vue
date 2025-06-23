<script setup>
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

defineProps({
  breadcrumbs: {
    type: Array,
    required: true,
    default: () => [],
  },
  backIcon: {
    type: String,
    default: '/src/assets/backico.svg',
  },
})

const router = useRouter()
const separatorIcon = ArrowRight

function handleBack() {
  router.back()
}
</script>

<template>
  <div class="flex items-center justify-between">
    <!-- 面包屑导航 -->
    <el-breadcrumb :separator-icon="separatorIcon" class="text-sm">
      <el-breadcrumb-item
        v-for="(item, index) in breadcrumbs"
        :key="index"
        :to="item.path ? { path: item.path } : undefined"
        class="font-medium"
      >
        {{ item.label }}
      </el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 返回按钮 -->
    <el-button type="primary" plain size="small" @click="handleBack">
      <i class="i-ep-back mr-1" />返回
    </el-button>
  </div>
</template>


