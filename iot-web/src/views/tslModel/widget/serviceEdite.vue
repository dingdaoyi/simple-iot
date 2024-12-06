<template>
  <dw-dialog
    ref="dwDialogRef"
    :title="datas?.id ? '编辑' : '新增'"
    width="1042px"
    showFooter
    :footerType="datas?.id ? 'edit' : 'add'"
    :leftLoading="loading"
    @leftBtn="onSubmit"
    @close="onClose"
    @reset="onReset">
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      :label-width="100">
      <el-form-item
        label="服务名称"
        prop="name"
        class="is-required">
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入服务名称"></el-input>
      </el-form-item>
      <el-form-item
        label="标识符"
        prop="identifier"
        class="is-required">
        <el-input
          v-model="form.identifier"
          clearable
          placeholder="请输入标识符"></el-input>
      </el-form-item>
      <el-form-item
        label="服务类型"
        prop="serviceType"
        class="is-required">
        <dw-select v-model="form.serviceType"
                   placeholder="请选择"
                   class="w-full"
                   @change="changeServiceType">
          <dw-option v-for="item in serviceTypeOpt" :key="item.value"
                     :label="item.label"
                     :value="item.value" />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="是否必选"
        class="is-required"
        prop="required">
        <el-radio-group v-model="form.required">
          <el-radio :value="true">必选</el-radio>
          <el-radio :value="false">可选</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        label="备注"
        prop="mark">
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入备注信息" />
      </el-form-item>
      <el-form-item
        v-if="currentType===1"
        label="服务时效"
        prop="async">
        <el-radio-group v-model="form.async">
          <el-radio :value="true">异步</el-radio>
          <el-radio :value="false">同步</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        v-if="currentType===1"
        label="输入参数"
        prop="inputParamIds">
        <dw-select
          v-model="form.inputParamIds"
          placeholder="属性选择"
          class="w-full"
          clearable
          multiple
        >
          <dw-option
            v-for="item in properties"
            :key="item.id"
            :label="item.name+':'+item.identifier"
            :value="item.id"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="输出参数"
        prop="outputParamIds">
        <dw-select
          v-model="form.outputParamIds"
          placeholder="参数选择"
          class="w-full"
          clearable
          multiple
        >
          <dw-option
            v-for="item in properties"
            :key="item.id"
            :label="item.name+':'+item.identifier"
            :value="item.id"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        v-if="currentType===2"
        label="事件类型"
        prop="eventType">
        <dw-select
          v-model="form.eventType"
          placeholder="事件类型选择"
          class="w-full"
          clearable
        >
          <dw-option
            v-for="item in eventTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
    </el-form>
  </dw-dialog>
</template>

<script lang="jsx" setup>
import { ref } from 'vue'
import { serviceAddApi, serviceEditeApi } from '@/api'
import { dwHooks } from 'dwyl-ui'

const { useForm } = dwHooks

const props = defineProps(['datas', 'typeId', 'properties', 'productId'])
const emits = defineEmits(['update'])

const serviceTypeOpt = [
  {
    label: '服务',
    value: 1
  },
  {
    label: '事件',
    value: 2
  }
]
const eventTypeOpt = [
  {
    label: 'INFO',
    value: 1
  },
  {
    label: 'WARN',
    value: 2
  },
  {
    label: 'FAULT',
    value: 3
  }
]
const currentType = ref(2)

const rules = ref({
  name: [{ required: true, message: '属性名称不能为空', trigger: 'blur' }],
  identifier: [{ required: true, message: '标识符不能为空', trigger: 'blur' }],
  serviceType: [{ required: true, message: '服务类型不能为空', trigger: 'change' }],
  required: [{ required: true, message: '是否必选不能为空', trigger: 'change' }]
})

const { editRef, onSubmit, form, loading, onClose, dwDialogRef, onReset } = useForm({
  defForm: {
    productTypeId: props.typeId,
    productId: props.productId,
    custom: props.productId != null,
    outputParamIds: [],
    inputParamIds: []
  },
  api: props.datas ? serviceEditeApi : serviceAddApi,
  callback: () => {
    emits('update')
  }
})

const changeServiceType = (value) => {
  currentType.value = value
}
if (props.datas) {
  form.value = props.datas
  currentType.value = props.datas.serviceType
}

</script>

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
