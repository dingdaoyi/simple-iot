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
            class: 'flex-y-center gap-1',
          }, [
            h(
              <el-icon size="20">
                <Location />
              </el-icon>,
              // <SvgIcon icon={item.icon} size="6" color={setStore.themeColor} />,
            ),
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
  <div>
    <el-scrollbar class="sidebar-container ">
      <el-menu
        :default-active="currRoute"
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
  width: 200px;
  background-color: var(--el-bg-color);
  padding: 30px 10px;
  margin: 0 0;
  overflow: auto;
}

.el-menu-class:not(.el-menu--collapse) {
  width: 100%;
  min-height: 400px;
}
// 设置选中项的背景色
.el-menu-class .el-menu-item.is-active {
  background-color: var(--el-color-primary) !important; // 使用主题色变量
  color: #fff !important; // 设置文字颜色
  border-radius: 4px; // 圆角效果（可选）
}

.el-menu-class .el-sub-menu.is-active > .el-menu__title {
  background-color: var(--el-color-primary) !important;
  color: #fff !important;
  border-radius: 4px; // 圆角效果（可选）
}
</style>
