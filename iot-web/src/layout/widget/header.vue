<template>
  <div class="header-box flex flex-row items-center bg-dwboxbg">
    <div class="flex items-center flex-1">
      <!-- <img class="w-95px h-20px" src="@/assets/309455747895656448.png"> -->
      <div class="pl-20px text-22px title color-primary">消防大数据管理系统</div>
    </div>
    <div class="flex-center h-full">
      <el-tooltip
        effect="dark"
        content="主题模式"
        placement="bottom"
      >
        <div class="flex-center cursor-pointer px-10px h-full mr-10px lightDark" @click="themeStore.toogleDarkModel">
          <el-icon :size="20">
            <Sunny v-if="themeStore.isDark" />
            <Moon v-else />
          </el-icon>
        </div>
      </el-tooltip>
      <el-dropdown>
        <span class="el-dropdown-link cursor-pointer">
          {{ userName }}
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="outLogin">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAccountStore, useThemeStore } from '@/store'
import { signout } from '@/api'
const router = useRouter()
const store = useAccountStore()
const themeStore = useThemeStore()
const userName = computed(() => store?.userinfo?.username)

const outLogin = () => {
  signout().then(() => {
    store.clearToken()
    router.push('/login')
  })
}

const isProd = computed(() => import.meta.env.MODE === 'production') // 是生产环境

</script>

<style lang="scss" scoped>
.header-box{height: 60px; padding: 0 20px; position: relative; z-index: 9;
  box-shadow: 0px 4px 10px 0px rgba(78, 89, 105, 0.15),0px 1px 0px 0px var(--dw-border-color);}
.title {
  font-family: PangMenZhengDao;
  letter-spacing: 2px;
}
.lightDark {
  // color: var;
  &:hover{
    background-color: var(--dw-hover-color);
  }
}
</style>
