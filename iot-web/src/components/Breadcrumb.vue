<template>
  <div class="flex items-center justify-between">
    <!-- 动态生成面包屑 -->
    <div>
      <el-breadcrumb :separator-icon="separatorIcon">
        <el-breadcrumb-item
          v-for="(item, index) in breadcrumbs"
          :key="index"
          :to="item.path ? { path: item.path } : undefined"
        >
          {{ item.label }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 返回按钮 -->
    <div>
      <el-button type="primary" plain @click="handleBack">
        <img :src="backIcon" alt="返回" class="w-4 h-4 mr-2" />
        返回
      </el-button>
    </div>
  </div>
</template>

<script lang="jsx" setup>
import { useRouter } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'

// 接收组件的 props
defineProps({
  breadcrumbs: {
    type: Array,
    required: true,
    default: () => []
  },
  backIcon: {
    type: String,
    default: '/src/assets/backico.svg' // 默认返回图标路径
  }
})

// 路由和返回功能
const router = useRouter()
const handleBack = () => {
  router.back()
}

// 图标
const separatorIcon = ArrowRight
</script>

<style scoped>

</style>
