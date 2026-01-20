<script setup>
import { useAccountStore } from '@/store'
import { useRouter } from 'vue-router'

const router = useRouter()
const store = useAccountStore()
const userName = '管理员'

function outLogin() {
  store.clearToken()
  router.push('/login')
}
</script>

<template>
  <div class="header-box flex items-center">
    <div class="flex items-center flex-1">
      <div class="title">
        物联网管理平台
      </div>
    </div>
    <div class="flex items-center h-full gap-4">
      <el-dropdown>
        <span class="el-dropdown-link">
          {{ userName }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="outLogin">
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.header-box {
  height: 64px;
  padding: 0 var(--space-xl);
  background: white;
  box-shadow: var(--shadow-md);
  position: relative;
  z-index: 9;
}

.title {
  font-size: 24px;
  font-weight: 700;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-primary);
  letter-spacing: 2px;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  padding: var(--space-sm) var(--space-md);
  border-radius: 8px;
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    background-color: var(--iot-color-background);
  }
}
</style>
