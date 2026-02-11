<script setup>
import { ArrowDown, Check, Expand, Fold, Lightning, Monitor, Moon, Sunny, SwitchButton, User } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { useTheme } from '@/composables/useTheme.js'
import { useAccountStore } from '@/store'
import { useSidebar } from '@/composables/useSidebar.js'

const router = useRouter()
const store = useAccountStore()
const userName = '管理员'
const { theme, setTheme, resolvedTheme } = useTheme()
const { isCollapsed, toggle } = useSidebar()

function outLogin() {
  store.clearToken()
  router.push('/login')
}

// 主题选项
const themeOptions = [
  { value: 'auto', label: '跟随系统', icon: Monitor },
  { value: 'light', label: '亮色模式', icon: Sunny },
  { value: 'dark', label: '暗色模式', icon: Moon },
]

function getThemeLabel() {
  return themeOptions.find(t => t.value === theme.value)?.label || '跟随系统'
}

function getThemeIcon() {
  if (theme.value === 'auto')
    return resolvedTheme.value === 'dark' ? Moon : Sunny
  return theme.value === 'dark' ? Moon : Sunny
}
</script>

<template>
  <div class="header-box">
    <div class="header-left">
      <!-- 折叠按钮 -->
      <div class="collapse-btn" @click="toggle">
        <el-icon :size="20">
          <Expand v-if="isCollapsed" />
          <Fold v-else />
        </el-icon>
      </div>
      <div class="logo">
        <el-icon :size="24" class="logo-icon">
          <Lightning />
        </el-icon>
        <span class="logo-text">简单 IoT</span>
      </div>
    </div>
    <div class="header-right">
      <!-- 主题切换下拉菜单 -->
      <el-dropdown trigger="click" @command="setTheme">
        <div class="theme-dropdown">
          <el-icon :size="20" class="theme-icon">
            <component :is="getThemeIcon()" />
          </el-icon>
          <span class="theme-label">{{ getThemeLabel() }}</span>
          <el-icon class="dropdown-icon" :size="14">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="theme-dropdown-menu">
            <el-dropdown-item
              v-for="option in themeOptions"
              :key="option.value"
              :command="option.value"
              :class="{ 'is-active': theme.value === option.value }"
            >
              <el-icon :size="18">
                <component :is="option.icon" />
              </el-icon>
              <span>{{ option.label }}</span>
              <el-icon v-if="theme.value === option.value" class="check-icon" :size="16">
                <Check />
              </el-icon>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 用户菜单 -->
      <el-dropdown trigger="click">
        <div class="user-dropdown">
          <div class="user-icon">
            <el-icon :size="18">
              <User />
            </el-icon>
          </div>
          <span class="user-name">{{ userName }}</span>
          <el-icon class="dropdown-icon" :size="14">
            <ArrowDown />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="outLogin">
              <el-icon>
                <SwitchButton />
              </el-icon>
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
  height: 60px;
  padding: 0 var(--space-xl);
  background: var(--iot-glass-bg-header);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--iot-glass-border);
  box-shadow: var(--shadow-sm);
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 9;
  transition:
    background var(--transition-fast),
    border-color var(--transition-fast);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  color: var(--iot-color-text-secondary);

  &:hover {
    background: var(--iot-glass-bg-hover);
    color: var(--iot-color-primary);
  }
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.logo-icon {
  color: var(--iot-color-primary);
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  font-family: 'Fira Code', monospace;
  color: var(--iot-color-text-primary);
  letter-spacing: 1px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.theme-dropdown {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;

  &:hover {
    background: var(--iot-glass-bg-hover);
    border-color: var(--iot-glass-border);
  }

  .theme-icon {
    color: var(--iot-color-text-secondary);
  }

  .theme-label {
    color: var(--iot-color-text-primary);
    font-size: 14px;
  }
}

// 下拉菜单样式
:deep(.el-dropdown-menu) {
  background: var(--iot-glass-bg-dark);
  backdrop-filter: blur(20px);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  padding: var(--space-xs);
  box-shadow: var(--shadow-lg);
}

:deep(.theme-dropdown-menu) {
  min-width: 140px;

  .el-dropdown-item {
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    padding: var(--space-sm) var(--space-md);
    border-radius: var(--radius-sm);
    color: var(--iot-color-text-secondary);
    transition: all var(--transition-fast);

    .el-icon {
      color: var(--iot-color-text-muted);
    }

    &.is-active {
      background: rgba(99, 102, 241, 0.1);
      color: var(--iot-color-primary);
      font-weight: 500;

      .el-icon {
        color: var(--iot-color-primary);
      }
    }

    &:hover {
      background: var(--iot-glass-bg-hover);
      color: var(--iot-color-primary);

      .el-icon {
        color: var(--iot-color-primary);
      }
    }

    .check-icon {
      margin-left: auto;
      color: var(--iot-color-primary);
    }
  }
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-fast);
  border: 1px solid transparent;

  &:hover {
    background: var(--iot-glass-bg-hover);
    border-color: var(--iot-glass-border);
  }
}

.user-icon {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, var(--iot-color-primary), var(--iot-color-accent));
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.user-name {
  color: var(--iot-color-text-primary);
  font-size: 14px;
  font-weight: 500;
}

.dropdown-icon {
  color: var(--iot-color-text-muted);
  transition: transform var(--transition-fast);
}

.user-dropdown:hover .dropdown-icon {
  transform: rotate(180deg);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  border-radius: var(--radius-sm);
  color: var(--iot-color-text-secondary);
  transition: all var(--transition-fast);

  .el-icon {
    color: var(--iot-color-text-muted);
  }

  &:hover {
    background: var(--iot-glass-bg-hover);
    color: var(--iot-color-primary);

    .el-icon {
      color: var(--iot-color-primary);
    }
  }
}
</style>
