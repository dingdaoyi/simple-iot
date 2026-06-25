<script lang="jsx" setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const props = defineProps(['datas', 'properties'])
const { t } = useI18n()
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
  <el-dialog
    :title="t('tsl.parameter_list')"
    width="620px"
  >
    <div class="flex flex-col gap-40px">
      <div v-if="outPutParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          {{ t('tsl.output_params_3') }}
        </div>
        <el-divider />
        <div>
          <el-table :data="outPutParam" style="width: 100%">
            <el-table-column prop="name" :label="t('common.name')" />
            <el-table-column prop="identifier" :label="t('tsl.identifier')" />
          </el-table>
        </div>
      </div>
      <div v-if="inputParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          {{ t('tsl.input_params_2') }}
        </div>
        <el-divider />
        <div>
          <el-table :data="inputParam" style="width: 100%">
            <el-table-column prop="name" :label="t('common.name')" />
            <el-table-column prop="identifier" :label="t('tsl.identifier')" />
          </el-table>
        </div>
      </div>
    </div>
  </el-dialog>
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
