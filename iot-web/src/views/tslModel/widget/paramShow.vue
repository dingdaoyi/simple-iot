<script lang="jsx" setup>
import { ref } from 'vue'

const props = defineProps(['datas', 'properties'])

const inputParam = ref([])
const outPutParam = ref([])
function findItem(id) {
  return props.properties
    .find(item => item.id === id)
}
inputParam.value = props.datas?.inputParamIds?.map(id => findItem(id)) || []
outPutParam.value = props.datas?.outputParamIds?.map(id => findItem(id)) || []
</script>

<template>
  <dw-dialog
    title="参数列表"
    width="620px"
  >
    <div class="flex flex-col gap-40px">
      <div v-if="outPutParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          输出参数
        </div>
        <el-divider />
        <div>
          <el-table :data="outPutParam" style="width: 100%">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="identifier" label="标识符" />
          </el-table>
        </div>
      </div>
      <div v-if="inputParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          输入参数
        </div>
        <el-divider />
        <div>
          <el-table :data="inputParam" style="width: 100%">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="identifier" label="标识符" />
          </el-table>
        </div>
      </div>
    </div>
  </dw-dialog>
</template>

<style lang="scss" scoped>
.check-item {
  ::v-deep(.el-form-item__content) {
    width: 100%;
  }
}

:deep(.jtlc) {
  .el-form-item__content {
    display: block;
  }
}
</style>
