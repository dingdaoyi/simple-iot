<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { ref, watch } from 'vue'
import { messageReceiveAddApi, messageReceiveEditApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const { t } = useI18n()
const props = defineProps(['datas', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const notifyTypeOpt = [
  {
    label: t('system.email'),
    value: 1,
  },
  {
    label: t('system.sms'),
    value: 2,
  },
  {
    label: t('system.phone'),
    value: 3,
  },
]

const rules = ref({
  name: [{ required: true, message: t('system.name_required'), trigger: 'blur' }],
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
}, { immediate: true })</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? t('system.edit_notification') : t('system.add_notification')"
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
        :label="t('system.messagereceive_dialog_system_messagereceive_dialog')"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          :placeholder="t('system.system_messagereceive_dialog')"
        />
      </el-form-item>
      <el-form-item
        :label="t('system.message_type')"
        prop="notifyType"
      >
        <el-select
          v-model="form.notifyType"
          :placeholder="t('common.please_select')"
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
        :label="t('system.receiver')"
        prop="receiver"
      >
        <el-input
          v-model="form.receiver"
          clearable
          :placeholder="t('system.enter_phone_email')"
        />
      </el-form-item>
      <el-form-item
        :label="t('common.remark')"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          :placeholder="t('system.enter_remarks')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">
        {{ t('common.cancel') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('common.confirm') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>

</style>
