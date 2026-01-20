<script lang="jsx" setup>
import { Location } from '@element-plus/icons-vue'
import { ElMenuItem, ElSubMenu } from 'element-plus'
import { computed, h } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由对象
const route = useRoute()
const router = useRouter()

const menuTree = computed(() => {
  const allRoutes = router.options.routes
  const loyoutChild = allRoutes.find(item => item.name === 'layout')
  return loyoutChild.children
})

function handleMenuItem(item) {
  router.push({
    path: item.index,
  })
}

const currRoute = computed(() => route.path)

function renderMenu(item, index) {
  if (item.children && item.children.length > 0) {
    return !item.meta.hidden && h(
      ElSubMenu,
      { index },
      {
        title: () =>
          h('div', {
            class: 'menu-item-title',
          }, [
            h('div', { class: 'menu-icon' }, [
              h(
                <el-icon size="20">
                  <Location />
                </el-icon>,
              ),
            ]),
            h('span', null, item.meta.title),
          ]),
        default: () => {
          return item.children.reduce((res, child) => {
            if (!child.meta.hidden) {
              res.push(renderMenu(child, String(child.path)))
            }
            return res
          }, [])
        },
      },
    )
  }
  else {
    return h(
      ElMenuItem,
      {
        index,
        onClick: handleMenuItem,
      },
      { default: () => item.name },
    )
  }
}
</script>

<template>
  <div class="sidebar-container">
    <el-scrollbar>
      <el-menu
        :default-active="currRoute"
        class="sidebar-menu"
      >
        <template v-for="item in menuTree" :key="item.path">
          <component :is="renderMenu(item, String(item.path))" />
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<style lang="scss" scoped>
.sidebar-container {
  width: 240px;
  height: 100%;
  background: white;
  border-right: 1px solid #E2E8F0;
}

.sidebar-menu {
  border-right: none;
}

.menu-item-title {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.menu-icon {
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
