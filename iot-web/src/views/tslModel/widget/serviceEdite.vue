<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { serviceAddApi, serviceEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const { t } = useI18n()
const props = defineProps(['datas', 'typeId', 'properties', 'productId', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const serviceTypeOpt = [
  {
    label: t('auto.tslmodel_serviceedite_47d68cd0'),
    value: 1,
  },
  {
    label: t('auto.tslmodel_serviceedite_10b2761d'),
    value: 2,
  },
]
const eventTypeOpt = [
  {
    label: 'INFO',
    value: 1,
  },
  {
    label: 'WARN',
    value: 2,
  },
  {
    label: 'FAULT',
    value: 3,
  },
]
const currentType = ref(2)

const rules = ref({
  name: [{ required: true, message: t('auto.tslmodel_serviceedite_9dd30989'), trigger: 'blur' }],
  identifier: [{ required: true, message: t('auto.tslmodel_serviceedite_ac228e59'), trigger: 'blur' }],
  serviceType: [{ required: true, message: t('auto.tslmodel_serviceedite_c31fa69e'), trigger: 'change' }],
  required: [{ required: true, message: t('auto.tslmodel_serviceedite_0b83c7c6'), trigger: 'change' }],
})

const { form, editRef, loading, onClose } = useForm({
  defForm: {
    productTypeId: props.typeId,
    productId: props.productId,
    custom: props.productId != null,
    outputParamIds: [],
    inputParamIds: [],
    serviceType: 1,
  },
  api: props.datas ? serviceEditeApi : serviceAddApi,
  callback: () => {
    emits('update')
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
})

async function onSubmit() {
  if (!editRef.value)
    return

  const valid = await editRef.value.validate().catch(() => false)
  if (!valid)
    return

  if (loading.value)
    return

  loading.value = true
  try {
    const func = props.datas ? serviceEditeApi : serviceAddApi
    await func(form.value)
    ElMessage.success(t('auto.tslmodel_serviceedite_33130f5c'))
    emits('update')
    emits('update:modelValue', false)
  }
  catch {
    ElMessage.error(t('auto.tslmodel_serviceedite_5fa802be'))
  }
  finally {
    loading.value = false
  }
}

function changeServiceType(value) {
  currentType.value = value
}

if (props.datas) {
  form.value = { ...form.value, ...props.datas }
  currentType.value = props.datas.serviceType
}</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="t('auto.tslmodel_serviceedite_edc3dbb0')"
    width="600px"
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
        :label="t('auto.tslmodel_serviceedite_8f3747c0')"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          :placeholder="t('auto.tslmodel_serviceedite_d314caa1')"
        />
      </el-form-item>
      <el-form-item
        :label="t('auto.tslmodel_serviceedite_f3c00c7e')"
        prop="identifier"
      >
        <el-input
          v-model="form.identifier"
          clearable
          :placeholder="t('auto.tslmodel_serviceedite_59a3f583')"
        />
      </el-form-item>
      <el-form-item
        :label="t('auto.tslmodel_serviceedite_924f67de')"
        prop="serviceType"
      >
        <el-select
          v-model="form.serviceType"
          :placeholder="t('auto.tslmodel_serviceedite_708c9d6d')"
          style="width: 100%"
          @change="changeServiceType"
        >
          <el-option
            v-for="item in serviceTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        :label="t('auto.tslmodel_serviceedite_d10a7280')"
        prop="required"
      >
        <el-radio-group v-model="form.required">
          <el-radio :value="true">
            {{ t('auto.tslmodel_serviceedite_417973c1') }}
          </el-radio>
          <el-radio :value="false">
            {{ t('auto.tslmodel_serviceedite_c20cba89') }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        :label="t('auto.tslmodel_serviceedite_2432b575')"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          :placeholder="t('auto.tslmodel_serviceedite_245475e1')"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 1"
        :label="t('auto.tslmodel_serviceedite_36e07d42')"
        prop="async"
      >
        <el-radio-group v-model="form.async">
          <el-radio :value="true">
            {{ t('auto.tslmodel_serviceedite_8b5a247d') }}
          </el-radio>
          <el-radio :value="false">
            {{ t('auto.tslmodel_serviceedite_6a620e3c') }}
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        v-if="currentType === 1"
        :label="t('auto.tslmodel_serviceedite_6e21a018')"
        prop="inputParamIds"
      >
        <el-select
          v-model="form.inputParamIds"
          :placeholder="t('auto.tslmodel_serviceedite_2c539e00')"
          style="width: 100%"
          clearable
          multiple
        >
          <el-option
            v-for="item in properties"
            :key="item.id"
            :label="`${item.name}:${item.identifier}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        :label="t('auto.tslmodel_serviceedite_283598a6')"
        prop="outputParamIds"
      >
        <el-select
          v-model="form.outputParamIds"
          :placeholder="t('auto.tslmodel_serviceedite_894e9552')"
          style="width: 100%"
          clearable
          multiple
        >
          <el-option
            v-for="item in properties"
            :key="item.id"
            :label="`${item.name}:${item.identifier}`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        v-if="currentType === 2"
        :label="t('auto.tslmodel_serviceedite_f974c846')"
        prop="eventType"
      >
        <el-select
          v-model="form.eventType"
          :placeholder="t('auto.tslmodel_serviceedite_d3b3c4e5')"
          style="width: 100%"
          clearable
        >
          <el-option
            v-for="item in eventTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button class="glass-btn glass-btn-secondary" @click="onClose">
        {{ t('auto.tslmodel_serviceedite_625fb26b') }}
      </el-button>
      <el-button type="primary" :loading="loading" class="glass-btn glass-btn-primary" @click="onSubmit">
        {{ datas?.id ? t('auto.tslmodel_widget_serviceedite_be5fbbe3') : t('auto.tslmodel_widget_serviceedite_66ab5e9f') }}
      </el-button>
    </template>
  </el-dialog>
</template>