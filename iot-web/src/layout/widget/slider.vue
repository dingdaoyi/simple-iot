<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSidebar } from '@/composables/useSidebar.js'

const route = useRoute()
const router = useRouter()
const { isCollapsed } = useSidebar()

const menuTree = computed(() => {
  const allRoutes = router.options.routes
  const loyoutChild = allRoutes.find(item => item.name === 'layout')
  return loyoutChild.children
})

function handleMenuItem(path) {
  router.push(path)
}

const currRoute = computed(() => route.path)
</script>

<template>
  <div class="sidebar-container" :class="{ collapsed: isCollapsed }">
    <el-scrollbar>
      <el-menu
        :default-active="currRoute"
        :default-openeds="['/productType', '/system']"
        :collapse="isCollapsed"
        :collapse-transition="false"
        class="sidebar-menu"
      >
        <template v-for="item in menuTree" :key="item.path">
          <!-- 有子菜单 -->
          <el-sub-menu v-if="item.children && !item.meta?.hidden" :index="item.path">
            <template #title>
              <div class="menu-item-title">
                <div v-if="item.meta?.icon" class="menu-icon">
                  <el-icon :size="22">
                    <component :is="item.meta.icon" />
                  </el-icon>
                </div>
                <span class="menu-text">{{ item.meta?.title || item.name }}</span>
              </div>
            </template>
            <template v-for="child in item.children" :key="child.path">
              <el-menu-item
                v-if="!child.meta?.hidden"
                :index="child.path"
                @click="handleMenuItem(child.path)"
              >
                <div class="menu-item-content">
                  <div v-if="child.meta?.icon" class="menu-icon">
                    <el-icon :size="22">
                      <component :is="child.meta.icon" />
                    </el-icon>
                  </div>
                  <span class="menu-text">{{ child.meta?.title || child.name }}</span>
                </div>
              </el-menu-item>
            </template>
          </el-sub-menu>

          <!-- 无子菜单 -->
          <el-menu-item
            v-else-if="!item.meta?.hidden"
            :index="item.path"
            @click="handleMenuItem(item.path)"
          >
            <div class="menu-item-content">
              <div v-if="item.meta?.icon" class="menu-icon">
                <el-icon :size="22">
                  <component :is="item.meta.icon" />
                </el-icon>
              </div>
              <span class="menu-text">{{ item.meta?.title || item.name }}</span>
            </div>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<style lang="scss" scoped>
.sidebar-container {
  width: 240px;
  height: 100%;
  background: var(--iot-glass-bg-sidebar);
  border-right: 1px solid var(--iot-glass-border);
  display: flex;
  flex-direction: column;
  transition:
    width var(--transition-base),
    background var(--transition-fast),
    border-color var(--transition-fast);

  &.collapsed {
    width: 64px;
  }
}

.sidebar-menu {
  border-right: none;
  padding: var(--space-md) 0;

  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    color: var(--iot-color-text-secondary);
    transition: all var(--transition-fast);
    height: 44px;
    line-height: 44px;
    padding: 0 var(--space-lg);
    margin: 2px var(--space-md);
    border-radius: var(--radius-sm);

    &:hover {
      background-color: var(--iot-glass-bg-hover);
      color: var(--iot-color-primary);
    }
  }

  :deep(.el-menu-item.is-active) {
    background: linear-gradient(90deg, rgba(99, 102, 241, 0.15), rgba(99, 102, 241, 0.05));
    color: var(--iot-color-primary);
    font-weight: 600;
    border-left: 3px solid var(--iot-color-primary);
    padding-left: calc(var(--space-lg) - 3px);
  }

  :deep(.el-sub-menu.is-active > .el-sub-menu__title) {
    color: var(--iot-color-primary);
  }

  :deep(.el-sub-menu .el-menu-item) {
    padding-left: calc(var(--space-lg) + var(--space-md));
    margin-left: var(--space-lg);
  }
}

.menu-item-title,
.menu-item-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.menu-text {
  transition: opacity var(--transition-fast);
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  flex-shrink: 0;
  color: var(--iot-color-primary);
  transition: all var(--transition-fast);

  :deep(.el-icon) {
    width: 24px !important;
    height: 24px !important;
    color: currentColor !important;
  }

  :deep(svg) {
    width: 24px !important;
    height: 24px !important;
  }
}

:deep(.el-menu-item.is-active .menu-icon),
:deep(.el-sub-menu.is-active > .el-sub-menu__title .menu-icon) {
  color: var(--iot-color-primary);
  opacity: 1;
}

:deep(.el-menu-item:hover .menu-icon),
:deep(.el-sub-menu__title:hover .menu-icon) {
  opacity: 1;
}

// 子菜单箭头
:deep(.el-sub-menu__icon-arrow) {
  color: var(--iot-color-text-muted);
  transition: transform var(--transition-fast);
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  transform: rotate(90deg);
}

// 折叠时的样式
:deep(.el-menu--collapse) {
  :deep(.el-menu-item),
  :deep(.el-sub-menu__title) {
    padding: 0;
    justify-content: center;

    .menu-item-title,
    .menu-item-content {
      justify-content: center;
    }
  }

  :deep(.el-menu-item.is-active) {
    padding-left: 0;
    border-left: none;
    background: rgba(99, 102, 241, 0.15);
  }

  :deep(.el-sub-menu .el-menu-item) {
    padding-left: 0;
    margin-left: 0;
  }

  // 折叠时隐藏子菜单箭头
  :deep(.el-sub-menu__icon-arrow) {
    display: none;
  }

  // 折叠时隐藏文字
  :deep(.menu-text) {
    display: none;
  }
}

// 响应式
@media (max-width: 768px) {
  .sidebar-container {
    width: 200px;

    &.collapsed {
      width: 56px;
    }
  }

  .sidebar-menu {
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      padding: 0 var(--space-md);
      margin: 2px var(--space-sm);
    }

    :deep(.el-sub-menu .el-menu-item) {
      padding-left: calc(var(--space-md) + var(--space-sm));
    }
  }
}
</style>
