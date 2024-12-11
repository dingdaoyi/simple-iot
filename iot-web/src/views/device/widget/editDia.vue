<script lang="jsx" setup>
import { deviceAddApi, deviceEditeApi, manufacturerListApi, productListApi } from '@/api'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['datas', 'productTypeList'])

const emits = defineEmits(['update'])

const { useForm } = dwHooks

const manufacturerListOpt = ref([])
const productListOpt = ref([])

const rules = ref({
  productTypeId: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
  manufacturer: [{ required: true, message: '厂家不能为空', trigger: 'change' }],
  productId: [{ required: true, message: '产品型号不能为空', trigger: 'change' }],
  deviceKey: [{ required: true, message: '设备编号不能为空', trigger: 'blur' }],
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

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? deviceEditeApi : deviceAddApi,
  callback: () => {
    emits('update')
  },
})

if (props?.datas) {
  form.value = props.datas
  changeProductType()
    .then(() => changeManufacturer())
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
        label="产品类型"
        prop="productTypeId"
        class="is-required"
      >
        <dw-select
          v-model="form.productTypeId"
          placeholder="请选择产品类型"
          class="w-full"
          filterable
          clearable
          @change="changeProductType"
        >
          <dw-option
            v-for="item in productTypeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="厂家"
        prop="manufacturer"
      >
        <dw-select
          v-model="form.manufacturer"
          placeholder="请选择厂家"
          class="w-full"
          filterable
          clearable
          @change="changeManufacturer"
        >
          <dw-option
            v-for="item in manufacturerListOpt"
            :key="item"
            :label="item"
            :value="item"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="型号"
        prop="productId"
      >
        <dw-select
          v-model="form.productId"
          placeholder="请选择型号"
          class="w-full"
          filterable
          clearable
          @change="changeManufacturer"
        >
          <dw-option
            v-for="item in productListOpt"
            :key="item.id"
            :label="item.model"
            :value="item.id"
          />
        </dw-select>
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
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
