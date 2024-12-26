<script lang="jsx" setup>
import {iconAddApi, iconEditeApi, messageReceiveAddApi, messageReceiveEditApi} from '@/api'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['datas'])

const emits = defineEmits(['update'])
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
const { useForm } = dwHooks

const rules = ref({
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
})

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? messageReceiveEditApi : messageReceiveAddApi,
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
        <dw-select
          v-model="form.notifyType"
          placeholder="请选择"
          class="w-full"
        >
          <dw-option
            v-for="item in notifyTypeOpt" :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
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
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
