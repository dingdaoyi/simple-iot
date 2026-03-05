<script lang="jsx" setup>
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { serviceAddApi, serviceEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'typeId', 'properties', 'productId', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const serviceTypeOpt = [
  {
    label: '服务',
    value: 1,
  },
  {
    label: '事件',
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
  name: [{ required: true, message: '属性名称不能为空', trigger: 'blur' }],
  identifier: [{ required: true, message: '标识符不能为空', trigger: 'blur' }],
  serviceType: [{ required: true, message: '服务类型不能为空', trigger: 'change' }],
  required: [{ required: true, message: '是否必选不能为空', trigger: 'change' }],
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
    ElMessage.success('操作成功')
    emits('update')
    emits('update:modelValue', false)
  }
  catch {
    ElMessage.error('操作失败')
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
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑服务' : '新增服务'"
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
        label="服务名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入服务名称"
        />
      </el-form-item>
      <el-form-item
        label="标识符"
        prop="identifier"
      >
        <el-input
          v-model="form.identifier"
          clearable
          placeholder="请输入标识符"
        />
      </el-form-item>
      <el-form-item
        label="服务类型"
        prop="serviceType"
      >
        <el-select
          v-model="form.serviceType"
          placeholder="请选择"
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
        label="是否必选"
        prop="required"
      >
        <el-radio-group v-model="form.required">
          <el-radio :value="true">
            必选
          </el-radio>
          <el-radio :value="false">
            可选
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          placeholder="请输入备注信息"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 1"
        label="服务时效"
        prop="async"
      >
        <el-radio-group v-model="form.async">
          <el-radio :value="true">
            异步
          </el-radio>
          <el-radio :value="false">
            同步
          </el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item
        v-if="currentType === 1"
        label="输入参数"
        prop="inputParamIds"
      >
        <el-select
          v-model="form.inputParamIds"
          placeholder="属性选择"
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
        label="输出参数"
        prop="outputParamIds"
      >
        <el-select
          v-model="form.outputParamIds"
          placeholder="参数选择"
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
        label="事件类型"
        prop="eventType"
      >
        <el-select
          v-model="form.eventType"
          placeholder="事件类型选择"
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
        取消
      </el-button>
      <el-button type="primary" :loading="loading" class="glass-btn glass-btn-primary" @click="onSubmit">
        {{ datas?.id ? '保存' : '新增' }}
      </el-button>
    </template>
  </el-dialog>
</template>
