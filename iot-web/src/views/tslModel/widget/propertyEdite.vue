<script lang="jsx" setup>
import { ElMessage } from 'element-plus'
import { ref } from 'vue'
import { customPropertyAddApi, dictListApi, standardPropertyAddApi, standardPropertyEditApi } from '@/api'
import IconInput from '@/components/IconInput.vue'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'typeId', 'productId', 'modelValue', 'title'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  name: [{ required: true, message: '属性名称不能为空', trigger: 'blur' }],
  identifier: [{ required: true, message: '标识符不能为空', trigger: 'blur' }],
  accessMode: [{ required: true, message: '读写类型不能为空', trigger: 'change' }],
  dataType: [{ required: true, message: '数据类型不能为空', trigger: 'change' }],
})
const form = ref({
  productTypeId: props.typeId,
  productId: props.productId,
  enums: [{
    key: 1,
    value: '',
  }],
  step: 1,
})
const unitListOpt = ref([])
const { editRef, loading, onClose } = useForm({
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
    const func = props.datas
      ? standardPropertyEditApi
      : props.productId
        ? customPropertyAddApi
        : standardPropertyAddApi
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

const dataTypeOpt = [
  {
    label: 'INT:整型',
    value: 1,
    type: 'number',
  },
  {
    label: 'float浮点型',
    value: 2,
    type: 'number',
  },
  {
    label: 'double浮点型',
    value: 3,
    type: 'number',
  },
  {
    label: '枚举型',
    value: 4,
    type: 'enum',
  },
  {
    label: '字符串',
    value: 5,
    type: 'text',
  },
  {
    label: '布尔',
    value: 6,
    type: 'bool',
  },
]
const currentType = ref(1)

function changeDataType(value) {
  currentType.value = dataTypeOpt.find(item => item.value === value).type
}

function addEnumValue() {
  const enums = form.value.enums
  if (!enums) {
    form.value.enums = [{
      key: 1,
      value: '',
    }]
  }

  const key = enums.length === 0 ? 1 : enums[enums.length - 1].key + 1
  form.value.enums.push({ key, value: '' })
}

function removeEnumValue(index) {
  form.value.enums.splice(index, 1)
}

if (props?.datas) {
  form.value = { ...form.value, ...props.datas }
  currentType.value = dataTypeOpt.find(item => item.value === props.datas.dataType)?.type || 1
}

dictListApi('analog_quantity')
  .then(({ data }) => {
    unitListOpt.value = data
  })

function changeUnit(value) {
  form.value.unitName = unitListOpt.value.find(item => item.value === value)?.label || ''
}
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑属性' : '新增属性'"
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
        label="属性名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入属性名称"
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
        label="数据类型"
        prop="dataType"
      >
        <el-select
          v-model="form.dataType"
          placeholder="请选择"
          style="width: 100%"
          @change="changeDataType"
        >
          <el-option
            v-for="item in dataTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="访问权限"
        prop="accessMode"
      >
        <el-radio-group v-model="form.accessMode">
          <el-radio value="r">
            只读
          </el-radio>
          <el-radio value="rw">
            读写
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        v-if="currentType === 'number'"
        label="步进"
        prop="step"
      >
        <el-input-number
          v-model="form.step"
          :min="1"
          style="width: 100%"
          placeholder="请输入步进参数"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'number'"
        label="最大值"
        prop="max"
      >
        <el-input-number
          v-model="form.max"
          :step="form.step"
          style="width: 100%"
          placeholder="请输入最大值"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'number'"
        label="最小值"
        prop="min"
      >
        <el-input-number
          v-model="form.min"
          :step="form.step"
          style="width: 100%"
          placeholder="请输入最小值"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'number'"
        label="单位"
        prop="unit"
      >
        <el-select
          v-model="form.unit"
          placeholder="请选择"
          style="width: 100%"
          @change="changeUnit"
        >
          <el-option
            v-for="item in unitListOpt"
            :key="item.value"
            :label="`${item.label}:${item.value}`"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-if="currentType === 'enum'" label="枚举值" prop="enums">
        <div class="enum-inputs">
          <div
            v-for="(item, index) in form.enums"
            :key="index"
            class="enum-row"
          >
            <el-input-number
              v-model="item.key"
              placeholder="键名"
              style="width: 40%"
            />
            <el-input
              v-model="item.value"
              placeholder="值"
              style="width: 40%"
            />
            <el-button
              :disabled="index === 0"
              class="enum-delete-btn"
              @click="removeEnumValue(index)"
            >
              删除
            </el-button>
          </div>
          <el-button class="enum-add-btn" @click="addEnumValue">
            + 添加枚举值
          </el-button>
        </div>
      </el-form-item>
      <el-form-item
        v-if="currentType === 'text'"
        label="字符串长度"
        prop="length"
      >
        <el-input-number
          v-model="form.length"
          :min="1"
          style="width: 100%"
          placeholder="请输入字符串长度"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'bool'"
        label="false值"
        prop="bool0"
      >
        <el-input
          v-model="form.bool0"
          clearable
          placeholder="请输入false代表信息"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'bool'"
        label="true值"
        prop="bool1"
      >
        <el-input
          v-model="form.bool1"
          clearable
          placeholder="请输入true代表信息"
        />
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
      <el-form-item label="图标" prop="iconId">
        <IconInput v-model:value="form.iconId" />
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

<style lang="scss" scoped>
// 枚举值输入区域样式
.enum-inputs {
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: var(--space-sm);

  .enum-row {
    display: flex;
    align-items: center;
    gap: var(--space-sm);
  }
}

// 添加枚举值按钮 - 玻璃态主题
.enum-add-btn {
  width: 100%;
  padding: var(--space-sm) var(--space-md);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  background: var(--iot-glass-bg);
  color: var(--iot-color-primary);
  font-size: 14px;
  transition: all var(--transition-fast);

  &:hover {
    background: var(--iot-glass-bg-hover);
    border-color: var(--iot-color-primary);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
}

// 删除按钮样式
.enum-delete-btn {
  min-width: 60px;
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-sm);
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #ef4444;
  transition: all var(--transition-fast);

  &:hover:not(:disabled) {
    background: rgba(239, 68, 68, 0.2);
    border-color: #ef4444;
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}
</style>
