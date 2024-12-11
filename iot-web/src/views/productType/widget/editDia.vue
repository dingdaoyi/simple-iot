<script lang="jsx" setup>
import { productTypeAddApi, productTypeEditeApi } from '@/api'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['datas', 'parentId'])

const emits = defineEmits(['update'])

const { useForm } = dwHooks

const rules = ref({
  name: [{ required: true, message: '品名不能为空', trigger: 'blur' }],
})

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? productTypeEditeApi : productTypeAddApi,
  callback: () => {
    emits('update')
  },
  defForm: {
    parentId: props.parentId,
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
    width="1042px"
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
        label="品类名称"
        prop="name"
        class="is-required"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入品类名称"
        />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="mark"
      >
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入户主地址"
        />
      </el-form-item>
    </el-form>
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
