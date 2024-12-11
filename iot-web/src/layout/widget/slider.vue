<template>
  <div>
    <el-scrollbar class="sidebar-container ">
      <el-menu
        :default-active="activeMenu"
        class="el-menu-class"
        active-text-color="#ffffff"
        router
        @select="handleSelect"
      >
        <template v-for="menu in menus" :key="menu.index">
          <el-sub-menu v-if="menu.children" :index="menu.index">
            <template #title>
              <el-icon>
                <component :is="menu.icon" />
              </el-icon>
              <span>{{ menu.title }}</span>
            </template>
            <el-menu-item
              v-for="subItem in menu.children"
              :key="subItem.index"
              :index="subItem.index"
            >
              {{ subItem.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="menu.index">
            <el-icon>
              <component :is="menu.icon" />
            </el-icon>
            <span>{{ menu.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="jsx" setup>
import { ref, markRaw } from 'vue'
import { Location } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
// 路由对象
const route = useRoute()
const router = useRouter()
// 菜单数据
const menus = ref([
  {
    index: '/home', // 修改为字符串
    title: '主页',
    icon: markRaw(Location),
    path: '/home'
  },
  {
    index: '/productIndex',
    title: '产品管理',
    icon: markRaw(Location),
    children: [
      {
        index: '/productType',
        title: '产品类型管理',
        path: '/productType'
      },
      {
        index: '/protocol',
        title: '协议管理',
        path: '/protocol'
      },
      {
        index: '/product',
        title: '产品',
        path: '/product'
      },
      {
        index: '/device',
        title: '设备管理',
        path: '/device'
      }
    ]
  },
  {
    index: '/system',
    title: '系统管理',
    icon: markRaw(Location),
    children: [
      {
        index: '/icon',
        title: '图标管理',
        path: '/icon'
      }
    ]
  }
])

const activeMenu = ref('home')

const handleSelect = (index) => {
  activeMenu.value = index
  const selectedMenu = menus.value
    .flatMap((menu) => [menu, ...(menu.children || [])])
    .find((item) => item.index === index)
  if (selectedMenu && selectedMenu.path && route.path !== selectedMenu.path) {
    router.push({
      path: selectedMenu.path
    })
  }
}
</script>

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
