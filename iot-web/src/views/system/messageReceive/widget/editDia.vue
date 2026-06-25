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
    label: t('auto.system_messagereceive_editdia_e9e8054f'),
    value: 1,
  },
  {
    label: t('auto.system_messagereceive_editdia_485c3abb'),
    value: 2,
  },
  {
    label: t('auto.system_messagereceive_editdia_5a93d3f7'),
    value: 3,
  },
]

const rules = ref({
  name: [{ required: true, message: t('auto.system_messagereceive_editdia_df09fff7'), trigger: 'blur' }],
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
    :title="t('auto.system_messagereceive_editdia_5d8b3184')"
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
        :label="t('auto.system_messagereceive_editdia_26bba1d9')"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          :placeholder="t('auto.system_messagereceive_editdia_030ecec0')"
        />
      </el-form-item>
      <el-form-item
        :label="t('auto.system_messagereceive_editdia_6d00710a')"
        prop="notifyType"
      >
        <el-select
          v-model="form.notifyType"
          :placeholder="t('auto.system_messagereceive_editdia_708c9d6d')"
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
        :label="t('auto.system_messagereceive_editdia_88340ead')"
        prop="receiver"
      >
        <el-input
          v-model="form.receiver"
          clearable
          :placeholder="t('auto.system_messagereceive_editdia_bd664852')"
        />
      </el-form-item>
      <el-form-item
        :label="t('auto.system_messagereceive_editdia_2432b575')"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          :placeholder="t('auto.system_messagereceive_editdia_e54550e0')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onCancel">
        {{ t('auto.system_messagereceive_editdia_625fb26b') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('auto.system_messagereceive_editdia_38cf16f2') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>

</style>
