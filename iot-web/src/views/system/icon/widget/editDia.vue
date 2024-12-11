<script lang="jsx" setup>
import { iconAddApi, iconEditeApi } from '@/api'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['datas'])

const emits = defineEmits(['update'])

const { useForm } = dwHooks

const rules = ref({
  name: [{ required: true, message: '协议名称不能为空', trigger: 'blur' }],
})

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? iconEditeApi : iconAddApi,
  callback: () => {
    emits('update')
  },
})

if (props?.datas) {
  form.value = props.datas
}
</script>

<template>
  <dw-dialog
    ref="dwDialogRef"
    :title="datas?.id ? '编辑' : '新增'"
    width="440px"
    show-footer
    :footer-type="datas?.id ? 'edit' : 'add'"
    :left-loading="loading"
    @left-btn="onSubmit"
    @close="onClose"
    @reset="onReset"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      :label-width="100"
    >
      <el-form-item
        label="图标名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入图标名称"
        />
      </el-form-item>
      <el-form-item
        label="上传图标"
        prop="icon"
      >
        <DwUpload v-model="form.path" :limit="1" />
      </el-form-item>
    </el-form>
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
