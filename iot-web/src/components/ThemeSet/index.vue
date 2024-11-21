<template>
  <el-drawer
    title="主题配置"
    size="330px"
  >
    <div>
      <el-divider>主题色</el-divider>
      <div class="flex flex-wrap gap-16px justify-between">
        <div
          v-for="item in themeColorList"
          :key="item"
          class="cursor-pointer w-20px h-20px border-rounded"
          :style="{backgroundColor: item}"
          @click="handleColor(item)"
        ></div>
      </div>
      <div class="py-20px">
        <el-color-picker v-model="store.themeJson.elColorPrimary" @change="changeTheme" />
        <el-button class="mt-10px w-full" type="primary" plain @click="showMore = true">更多颜色</el-button>
      </div>
    </div>
    <!-- <div>
      <el-divider>侧边栏</el-divider>
      <div>
        <el-color-picker v-model="store.themeJson.dwSliderActiveBg" @change="changeTheme2" />
      </div>
    </div> -->
    <MoreColor v-model="showMore" @change="handleColor" />

  </el-drawer>
</template>
<script setup>
import { ref } from 'vue'
import { themeColorList } from './set'
import MoreColor from './MoreColor'
import { useThemeStore } from '@/store'
const store = useThemeStore()

const showMore = ref(false)

const changeTheme = (colorVal) => {
  store.changeTheme(colorVal)
}

const handleColor = (val) => {
  changeTheme(val)
}

const changeTheme2 = (colorVal) => {
  store.changeSliderTheme(colorVal)
}

</script>

<style lang="scss" scoped>
:deep(.el-color-picker) {
  width: 100%;
  .el-color-picker__trigger {
    width: 100%;
  }
}
</style>
