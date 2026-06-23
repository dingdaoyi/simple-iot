<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <span class="locale-switcher">
      <el-icon><Grid /></el-icon>
      <span class="locale-text">{{ currentLocaleLabel }}</span>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="zh-CN" :disabled="currentLocale === 'zh-CN'">
          简体中文
        </el-dropdown-item>
        <el-dropdown-item command="en-US" :disabled="currentLocale === 'en-US'">
          English
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { setLocale } from '@/locales'
import { ElMessage } from 'element-plus'

const { locale } = useI18n()

const currentLocale = computed(() => locale.value)

const currentLocaleLabel = computed(() => {
  return currentLocale.value === 'zh-CN' ? '简体中文' : 'English'
})

function handleCommand(command) {
  setLocale(command)
  ElMessage.success({
    message: command === 'zh-CN' ? '切换到简体中文' : 'Switched to English',
    duration: 1500,
  })
  // 刷新页面以应用新语言（Element Plus 需要重新初始化）
  setTimeout(() => {
    window.location.reload()
  }, 500)
}
</script>

<style scoped>
.locale-switcher {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.2s;
}

.locale-switcher:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.locale-text {
  font-size: 14px;
  color: var(--el-text-color-regular);
}

:global(.dark) .locale-switcher:hover {
  background-color: rgba(255, 255, 255, 0.1);
}
</style>
