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
        label="产品类型"
        prop="productTypeId"
        class="is-required">
        <dw-select
          v-model="form.productTypeId"
          placeholder="请选择产品类型"
          class="w-full"
          filterable
          clearable
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
        label="型号"
        prop="model">
        <el-input
          v-model="form.model"
          clearable
          placeholder="请输入产品型号" />
      </el-form-item>
      <el-form-item
        label="厂家"
        prop="manufacturer">
        <el-input
          v-model="form.manufacturer"
          clearable
          placeholder="请输入设备厂家" />
      </el-form-item>
      <el-form-item
        label="备注"
        prop="mark">
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入户主地址" />
      </el-form-item>
    </el-form>
  </dw-dialog>
</template>

<script lang="jsx" setup>
import { ref } from 'vue'
import { productAddApi, productEditeApi } from '@/api'
import { dwHooks } from 'dwyl-ui'

const { useForm } = dwHooks

const props = defineProps(['datas', 'productTypeList'])
const emits = defineEmits(['update'])

const rules = ref({
  productTypeId: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
  model: [{ required: true, message: '产品型号不能为空', trigger: 'blur' }]
})

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? productEditeApi : productAddApi,
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
