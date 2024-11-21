<template>
  <DwDialog
    title="中国传统颜色"
    width="660px"
  >
    <el-tabs v-model="activeName" class="demo-tabs">
      <el-tab-pane v-for="(item, index) in colorList" :key="item.label" :label="item.label" :name="index">
        <div style="width: 100%; display: grid; grid-template-columns: repeat(8, minmax(0px, 1fr)); gap: 16px;">
          <div v-for="child in item.data" :key="child.color" class="cursor-pointer flex-col-center" @click="handleColor(child.color)">
            <div
              class="w-full h-36px border-rounded"
              :style="{backgroundColor: child.color}"
            ></div>
            <div class="pt-5px">{{ child.label }}</div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

  </DwDialog>
</template>

<script setup>
import { ref } from 'vue'
import { dwHooks } from 'dwyl-ui'
import colorList from './color.json'

const emits = defineEmits(['change'])

const activeName = ref(0)

const handleColor = (val) => {
  emits('change', val)
}
</script>

<style lang="scss" scoped></style>
