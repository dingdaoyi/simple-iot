<script lang="jsx" setup>
import { ref, watch } from 'vue'
import { iconAddApi, iconEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  name: [{ required: true, message: '图标名称不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? iconEditeApi : iconAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

function onSubmit() {
  handleSubmit()
}

function onCancel() {
  emits('update:modelValue', false)
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑' : '新增'"
    width="500px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
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
    <template #footer>
      <el-button @click="onCancel">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>

</style>
