<script lang="jsx" setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { productTypeAddApi, productTypeEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const { t } = useI18n()

const props = defineProps(['datas', 'parentId', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  name: [{ required: true, message: t('auto.producttype_editdia_003b0ed3'), trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading, onClose } = useForm({
  api: props.datas ? productTypeEditeApi : productTypeAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
  defForm: {
    parentId: props.parentId,
  },
})

if (props?.datas) {
  form.value = { ...props.datas }
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="t('auto.producttype_editdia_5d8b3184')"
    width="500px"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="onClose"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      :label-width="100"
    >
      <el-form-item
        :label="t('auto.producttype_editdia_11382905')"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          :placeholder="t('auto.producttype_editdia_d1ac18cc')"
        />
      </el-form-item>
      <el-form-item
        :label="t('auto.producttype_editdia_2432b575')"
        prop="mark"
      >
        <el-input
          v-model="form.mark"
          clearable
          :placeholder="t('auto.producttype_editdia_3cac6342')"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">
        {{ t('auto.producttype_editdia_625fb26b') }}
      </el-button>
      <el-button
        type="primary"
        :loading="loading"
        @click="handleSubmit"
      >
        {{ datas?.id ? t('common.save') : t('common.add') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.check-item {
  ::v-deep(.el-form-item__content) {
    width: 100%;
  }
}

:deep(.jtlc) {
  .el-form-item__content {
    display: block;
  }
}
</style>
