<template>
  <div class="header-box flex flex-row items-center bg-dwboxbg">
    <div class="flex items-center flex-1">
      <div class="pl-20px text-22px title color-primary">物联网管理平台</div>
    </div>
    <div class="flex-center h-full">
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
import { useRouter } from 'vue-router'
import { useAccountStore } from '@/store'
const router = useRouter()
const store = useAccountStore()
const userName = '管理员'

const outLogin = () => {
  store.clearToken()
  router.push('/login')
}

</script>

<style lang="scss" scoped>
.header-box{height: 60px; padding: 0 20px; position: relative; z-index: 9;
  box-shadow: 0px 4px 10px 0px rgba(78, 89, 105, 0.15),0px 1px 0px 0px var(--dw-border-color);}
.title {
  font-family: PangMenZhengDao,serif;
  letter-spacing: 2px;
}
.lightDark {
  // color: var;
  &:hover{
    background-color: var(--dw-hover-color);
  }
}
</style>
