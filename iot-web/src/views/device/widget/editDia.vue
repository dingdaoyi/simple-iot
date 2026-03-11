<script lang="jsx" setup>
import { ref, watch } from 'vue'
import { deviceAddApi, deviceEditeApi, manufacturerListApi, productListApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'productTypeList', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const manufacturerListOpt = ref([])
const productListOpt = ref([])

const rules = ref({
  productTypeId: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
  manufacturer: [{ required: true, message: '厂家不能为空', trigger: 'change' }],
  productId: [{ required: true, message: '产品型号不能为空', trigger: 'change' }],
  deviceKey: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading } = useForm({
  api: props.datas ? deviceEditeApi : deviceAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
})

async function changeProductType() {
  if (form.value.productTypeId) {
    const { data } = await manufacturerListApi({
      productTypeId: form.value.productTypeId,
    })
    manufacturerListOpt.value = data
  }
  else {
    manufacturerListOpt.value = []
  }
}

function changeManufacturer() {
  if (!form.value.manufacturer) {
    productListOpt.value = []
    return
  }
  productListApi({
    productTypeId: form.value.productTypeId,
    manufacturer: form.value.manufacturer,
  })
    .then(({ data }) => {
      productListOpt.value = data
    })
}

function onSubmit() {
  handleSubmit()
}

function onCancel() {
  emits('update:modelValue', false)
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = { ...val }
    changeProductType()
      .then(() => changeManufacturer())
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
        label="产品类型"
        prop="productTypeId"
      >
        <el-select
          v-model="form.productTypeId"
          placeholder="请选择产品类型"
          style="width: 100%"
          filterable
          clearable
          @change="changeProductType"
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
        label="厂家"
        prop="manufacturer"
      >
        <el-select
          v-model="form.manufacturer"
          placeholder="请选择厂家"
          style="width: 100%"
          filterable
          clearable
          @change="changeManufacturer"
        >
          <el-option
            v-for="item in manufacturerListOpt"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="型号"
        prop="productId"
      >
        <el-select
          v-model="form.productId"
          placeholder="请选择型号"
          style="width: 100%"
          filterable
          clearable
          @change="changeManufacturer"
        >
          <el-option
            v-for="item in productListOpt"
            :key="item.id"
            :label="item.model"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="设备编号"
        prop="deviceKey"
      >
        <el-input
          v-model="form.deviceKey"
          clearable
          placeholder="请输入设备编号"
        />
      </el-form-item>
      <el-form-item
        label="设备名称"
        prop="deviceName"
      >
        <el-input
          v-model="form.deviceName"
          clearable
          placeholder="请输入设备名称"
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
