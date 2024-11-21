<template>
  <div class="w-full">
    <div v-for="item in data" :key="item.id" class="py-6px">
      <div
        :class="['flex flex-row items-center h-40px px-8px cursor-pointer', {itemHover: item.permissionUrl !== actPath}, { act: item.permissionUrl === actPath }]"
        @click="checkChild(item)"
      >
        <div class="w-16px h-16px mr-12px">
          <img v-if="item.icon" class="wh-full" :src="item.icon">
        </div>
        <span class="flex-1 font-bold textclass text-14px">{{ item.permissionName }}</span>
        <el-icon v-if="item.children.length" :size="12" :class="['down', {downAct: item.showChild}]">
          <ArrowDownBold />
        </el-icon>
      </div>
      <div class="w-full">
        <el-collapse-transition>
          <div v-if="item.children && item.showChild">
            <menuTree :data="item.children" :actPath="actPath" />
          </div>
        </el-collapse-transition>
      </div>
    </div>
  </div>
</template>

<script lang="jsx" setup>
import { useRouter } from 'vue-router'
const router = useRouter()

const props = defineProps(['data', 'actPath'])

const checkChild = item => {
  if (item.children.length) {
    item.showChild = !item.showChild
  } else {
    router.push(`${item.permissionUrl}`)
  }
}

</script>

<style lang="scss" scoped>
.act {
  // background: var(--dw-slider-active-bg);
  background: var(--el-color-primary);
  border-radius: 4px;
  .textclass {
    color: #fff;
  }
}

.down {
  transition: all 0.3s;
  &.downAct {
    transform: rotate(180deg);
  }
}
.textclass {
  color: var(--dw-slider-color);
}
.itemHover {
  &:hover {
    background-color: var(--dw-slider-hover-bg);
    border-radius: 4px;
  }
}
</style>
