<script lang="jsx" setup>
import { ref, watch } from 'vue'
import { messageReceiveAddApi, messageReceiveEditApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const notifyTypeOpt = [
  {
    label: '邮件',
    value: 1,
  },
  {
    label: '短信',
    value: 2,
  },
  {
    label: '电话',
    value: 3,
  },
]

const rules = ref({
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? messageReceiveEditApi : messageReceiveAddApi,
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
        label="接收名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输接收名称"
        />
      </el-form-item>
      <el-form-item
        label="消息类型"
        prop="notifyType"
      >
        <el-select
          v-model="form.notifyType"
          placeholder="请选择"
          style="width: 100%"
        >
          <el-option
            v-for="item in notifyTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="接收者"
        prop="receiver"
      >
        <el-input
          v-model="form.receiver"
          clearable
          placeholder="请输电话或邮箱"
        />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          placeholder="输入备注信息"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">
        取消
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>

</style>
