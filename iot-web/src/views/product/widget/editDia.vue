<script lang="jsx" setup>
import { ref, watch } from 'vue'
import { productAddApi, productEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'productTypeList', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  productTypeId: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
  model: [{ required: true, message: '产品型号不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading, onClose } = useForm({
  api: props.datas ? productEditeApi : productAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
})

function onSubmit() {
  handleSubmit()
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
    @close="onClose"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
    >
      <el-form-item
        label="产品类型"
        prop="productTypeId"
      >
        <el-select
          v-model="form.productTypeId"
          placeholder="请选择产品类型"
          style="width: 100%"
          filterable
          clearable
        >
          <el-option
            v-for="item in productTypeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="型号"
        prop="model"
      >
        <el-input
          v-model="form.model"
          clearable
          placeholder="请输入产品型号"
        />
      </el-form-item>
      <el-form-item
        label="厂家"
        prop="manufacturer"
      >
        <el-input
          v-model="form.manufacturer"
          clearable
          placeholder="请输入设备厂家"
        />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          placeholder="请输入户主地址"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>

</style>
