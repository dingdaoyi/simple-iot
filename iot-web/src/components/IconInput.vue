<script setup>
import { iconListApi } from '@/api/index.js'
import { ref } from 'vue'

const props = defineProps({
  value: String,
})
const emit = defineEmits(['update:value'])
const list = ref([])
const actIcon = ref(null)
iconListApi().then((rs) => {
  list.value = rs.data
  rs.data.forEach((item) => {
    if (item.id === props.value) {
      actIcon.value = item
    }
  })
})
function change(e) {
  actIcon.value = e
  emit('update:value', e.id)
}
</script>

<template>
  <div class="inline-block">
    <el-form-item label="图标" prop="icon">
      <el-dropdown max-height="300px" @command="change">
        <div class="el-dropdown-link">
          <div v-if="actIcon" class="inline-block">
            <img class="w-30px h-30px mr-8px" :src="actIcon.path">
            <span class="mr-8px">{{ actIcon.name }}</span>
          </div>

          <span v-else>请选择图标</span>
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-for="item in list" :key="item.id" class="list-item" :command="item">
              <img class="w-30px h-30px mr-12px" :src="item.path">
              {{ item.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-form-item>
  </div>
</template>

<style lang="scss" scoped>
::v-deep(.el-dropdown-menu__item:hover) {
  background: #eee;
}
.el-dropdown-link {
  cursor: pointer;
  line-height: 32px;
}
</style>
