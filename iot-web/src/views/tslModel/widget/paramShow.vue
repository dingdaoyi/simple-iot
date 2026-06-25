<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { ref } from 'vue'

const { t } = useI18n()
const props = defineProps(['datas', 'properties'])

const inputParam = ref([])
const outPutParam = ref([])
function findItem(id) {
  return props.properties
    .find(item => item.id === id)
}
inputParam.value = props.datas?.inputParamIds?.map(id => findItem(id)) || []
outPutParam.value = props.datas?.outputParamIds?.map(id => findItem(id)) || []</script>

<template>
  <el-dialog
    :title="t('auto.tslmodel_paramshow_cba87444')"
    width="620px"
  >
    <div class="flex flex-col gap-40px">
      <div v-if="outPutParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          {{ t('auto.tslmodel_paramshow_283598a6') }}
        </div>
        <el-divider />
        <div>
          <el-table :data="outPutParam" style="width: 100%">
            <el-table-column prop="name" :label="t('auto.tslmodel_paramshow_d7ec2d3f')" />
            <el-table-column prop="identifier" :label="t('auto.tslmodel_paramshow_f3c00c7e')" />
          </el-table>
        </div>
      </div>
      <div v-if="inputParam.length > 0" class="flex flex-col w-full">
        <div class="text-16px">
          {{ t('auto.tslmodel_paramshow_6e21a018') }}
        </div>
        <el-divider />
        <div>
          <el-table :data="inputParam" style="width: 100%">
            <el-table-column prop="name" :label="t('auto.tslmodel_paramshow_d7ec2d3f')" />
            <el-table-column prop="identifier" :label="t('auto.tslmodel_paramshow_f3c00c7e')" />
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
