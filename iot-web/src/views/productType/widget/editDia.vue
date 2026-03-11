<script lang="jsx" setup>
import { ref } from 'vue'
import { productTypeAddApi, productTypeEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'parentId', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  name: [{ required: true, message: '品名不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading, onClose } = useForm({
  api: props.datas ? productTypeEditeApi : productTypeAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
  defForm: {
    parentId: props.parentId,
  },
})

if (props?.datas) {
  form.value = { ...props.datas }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑' : '新增'"
    width="500px"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="onClose"
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
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">
        取消
      </el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="onSubmit"
      >
        {{ datas?.id ? '保存' : '新增' }}
      </el-button>
    </template>
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
