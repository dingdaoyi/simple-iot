<script setup>
import { Index } from '@/api'
import { useAccountStore, useThemeStore } from '@/store'
import { getCurrentInstance, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import LoginBg from './weight/LoginBg'

const { proxy: ctx } = getCurrentInstance() // 可以把ctx当成vue2中的this
const store = useAccountStore()
const themeStore = useThemeStore()
const router = useRouter()
const route = useRoute()
const state = reactive({
  loading: false,
  psdType: 'password',
})
const form = ref({
  username: '',
  password: '',
  source: 1,
})
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    // {
    //   min: 6,
    //   max: 12,
    //   message: '长度在 6 到 12 个字符',
    //   trigger: 'blur'
    // }
  ],
}
const loginForm = ref(null)
function onSubmit() {
  if (state.loading) {
    return
  }
  loginForm.value.validate(async (valid) => {
    if (valid) {
      state.loading = true
      // const { code, data, msg } = await Index(form.value)
      Index(form.value).then((rs) => {
        const { code, data } = rs
        if (code === 200) {
          ctx.$message.success({
            message: '登录成功',
            duration: 1000,
          })

          const redirectPath = route.query.redirect
          if (redirectPath && redirectPath !== 'undefined') {
            const targetPath = decodeURIComponent(redirectPath)
            if (targetPath.startsWith('http')) {
              // 如果是一个url地址
              window.location.href = targetPath
            }
            else if (targetPath.startsWith('/')) {
              // 如果是内部路由地址
              router.push(targetPath)
            }
            else {
              router.push('/home')
            }
          }
          else {
            router.push('/home')
          }

          store.setToken(data)
        }
      }).finally(() => {
        state.loading = false
      })
    }
  })
}
function showPass(e) {
  state.psdType = e
}

// const bgColor = computed(() => {
//   const COLOR_WHITE = '#ffffff'
//   const ratio = themeStore.isDark ? 0.5 : 0.2
//   return mixColor(COLOR_WHITE, themeStore.themeJson.elColorPrimary, ratio)
// })
</script>

<template>
  <div class="relative flex flex-col wh-full page-main">
    <div class="flex-1 flex items-center justify-center flex-col min-h-750px z-4">
      <div class="content flex flex-row">
        <div class="form-box flex-1 relative">
          <div class="account">
            <div class="text-center  text-28px mb-12px color-primary lh-40px">
              简单的物联网平台
            </div>
            <el-form ref="loginForm" :model="form" :rules="rules" label-position="top">
              <el-form-item label="账号" prop="username">
                <el-input v-model="form.username" placeholder="请输入账号" />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <div class="w-full relative">
                  <el-input v-model="form.password" :type="state.psdType" placeholder="请输入密码" />
                  <div v-if="state.psdType === 'password'" class="close" @click="showPass('text')" />
                  <div v-else class="open" @click="showPass('password')" />
                </div>
              </el-form-item>
              <el-button class="w-full sub-btn" type="primary" :loading="state.loading" @click="onSubmit">
                登录
              </el-button>
            </el-form>
          </div>
        </div>
      </div>
    </div>
    <LoginBg :theme-color="themeStore.themeJson.elColorPrimary" />
  </div>
</template>

<style lang="scss" scoped>
.content {
  width: 424px;
  overflow: hidden;
  border-radius: 8px;
  background-color: var(--dw-box-bg);
}

.account {
  width: 340px;
  margin: 40px auto;

  .input {
    line-height: 40px;
  }

  .sub-btn {
    height: 40px;
    line-height: 40px;
    margin-top: 20px;
  }

  .close {
    width: 40px;
    height: 40px;
    position: absolute;
    right: 0px;
    top: 0px;
    background-size: auto auto;
    background-position: center center;
    z-index: 10;
  }

  .open {
    @extend .close;
    background-size: auto auto;
    background-position: center center;
  }
}

::v-deep(.el-form-item__label) {
  font-size: 20px;
  line-height: 28px !important;
  margin-bottom: 7px !important;
}

::v-deep(.el-input__inner) {
  height: 40px;
  line-height: 40px;
}

:deep(.el-form-item__label:before) {
  content: '' !important;
}
</style>
