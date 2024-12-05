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
        label="协议名称"
        prop="name">
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入协议名称" />
      </el-form-item>
      <el-form-item
        label="协议类型"
        prop="protoType"
        class="is-required">
        <dw-select
          v-model="form.protoType"
          placeholder="请选择协议类型"
          class="w-full"
          filterable
          clearable
        >
          <dw-option
            v-for="item in protocolTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>

      <el-form-item
        label="协议protoKey"
        prop="protoKey">
        <el-input
          v-model="form.protoKey"
          clearable
          placeholder="请输入协议protoKey" />
      </el-form-item>
      <el-form-item
        v-if="form.protoType &&form.protoType!==2"
        label="协议包上传"
        prop="url">
        <el-input
          v-model="form.url"
          clearable
          placeholder="协议包上传" />
      </el-form-item>

      <el-form-item
        label="备注"
        prop="mark">
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入备注信息" />
      </el-form-item>
    </el-form>
  </dw-dialog>
</template>

<script lang="jsx" setup>
import { ref } from 'vue'
import { protocolAddApi, protocolEditApi } from '@/api'
import { dwHooks } from 'dwyl-ui'

const { useForm } = dwHooks

const props = defineProps(['datas'])
const emits = defineEmits(['update'])

const protocolTypeOpt = [
  {
    label: 'JAVA',
    value: 1
  },
  {
    label: '系统默认',
    value: 2
  },
  {
    label: 'JAVASCRIPT',
    value: 3
  }
]

const rules = ref({
  name: [{ required: true, message: '协议名称不能为空', trigger: 'blur' }]
})

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? protocolEditApi : protocolAddApi,
  callback: () => {
    emits('update')
  }
})

if (props?.datas) {
  form.value = props.datas
}
</script>

<style lang="scss" scoped>

</style>
