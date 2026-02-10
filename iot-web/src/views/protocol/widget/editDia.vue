<script lang="jsx" setup>
import { ref, watch } from 'vue'
import { protocolAddApi, protocolEditApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const protocolTypeOpt = [
  {
    label: 'JAVA',
    value: 1,
  },
  {
    label: '系统默认',
    value: 2,
  },
  {
    label: 'JAVASCRIPT',
    value: 3,
  },
]

const rules = ref({
  name: [{ required: true, message: '协议名称不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? protocolEditApi : protocolAddApi,
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
    width="600px"
    @update:model-value="$emit('update:modelValue', $event)"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
    >
      <el-form-item
        label="协议名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入协议名称"
        />
      </el-form-item>
      <el-form-item
        label="协议类型"
        prop="protoType"
      >
        <el-select
          v-model="form.protoType"
          placeholder="请选择协议类型"
          style="width: 100%"
          filterable
          clearable
        >
          <el-option
            v-for="item in protocolTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>

      <el-form-item
        label="协议protoKey"
        prop="protoKey"
      >
        <el-input
          v-model="form.protoKey"
          clearable
          placeholder="请输入协议protoKey"
        />
      </el-form-item>
      <el-form-item
        v-if="form.protoType && form.protoType !== 2"
        label="协议包上传"
        prop="url"
      >
        <el-input
          v-model="form.url"
          clearable
          placeholder="协议包上传"
        />
      </el-form-item>

      <el-form-item
        label="备注"
        prop="mark"
      >
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入备注信息"
        />
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
