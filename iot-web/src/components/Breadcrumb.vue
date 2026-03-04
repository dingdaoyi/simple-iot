<script setup>
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
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
  <div class="breadcrumb-container">
    <!-- 面包屑导航 -->
    <el-breadcrumb :separator-icon="separatorIcon" class="text-sm breadcrumb">
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
    <el-button class="glass-btn glass-btn-secondary" size="small" @click="handleBack">
      <el-icon :size="14" class="mr-1">
        <ArrowLeft />
      </el-icon>
      返回
    </el-button>
  </div>
</template>

<style lang="scss" scoped>
.breadcrumb-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-md);
}

.breadcrumb {
  :deep(.el-breadcrumb__item) {
    .el-breadcrumb__inner {
      color: var(--iot-color-text-secondary);
      transition: color var(--transition-fast);

      &:hover {
        color: var(--iot-color-primary);
      }
    }

    &:last-child .el-breadcrumb__inner {
      color: var(--iot-color-text-primary);
      font-weight: 500;
    }
  }

  :deep(.el-breadcrumb__separator) {
    color: var(--iot-color-text-muted);
  }
}

.glass-btn {
  // 覆盖全局样式，添加 flex 布局
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  min-width: 70px;
  padding: var(--space-xs) var(--space-md);

  &.glass-btn-secondary:hover {
    border-color: var(--iot-color-primary);
    color: var(--iot-color-primary);
  }
}
</style>
