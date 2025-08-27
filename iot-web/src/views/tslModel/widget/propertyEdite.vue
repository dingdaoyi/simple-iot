<script lang="jsx" setup>
import { customPropertyAddApi, dictListApi, standardPropertyAddApi, standardPropertyEditApi } from '@/api'
import IconInput from '@/components/IconInput.vue'
import { dwHooks } from 'dwyl-ui'
import { ElMessage } from 'element-plus'
import { ref } from 'vue'

const props = defineProps(['datas', 'typeId', 'productId'])

const emits = defineEmits(['update'])

const { useForm } = dwHooks

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
})
const unitListOpt = ref([])
const { editRef, loading, onClose, dwDialogRef, onReset } = useForm({})
async function onSubmit() {
  await editRef.value.validate((valid) => {
    if (valid) {
      if (loading.value) {
        return
      }
      loading.value = true
      const func = props.datas
        ? standardPropertyEditApi
        : props.productId
          ? customPropertyAddApi
          : standardPropertyAddApi
      func(form.value).then(() => {
        loading.value = false
        ElMessage({
          message: '操作成功',
          type: 'success',
        })
        emits('update')
      }).catch(() => {
        loading.value = false
      })
    }
  })
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
  // TODO 暂时不做
  // {
  //   label: '日期',
  //   value: 7,
  //   type: 'date'
  // },
  // {
  //   label: '结构体',
  //   value: 8,
  //   type: 'struct'
  // }
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
  form.value = props.datas
  currentType.value = dataTypeOpt.find(item => item.value === props.datas.dataType).type
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
        label="属性名称"
        prop="name"
        class="is-required"
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
        class="is-required"
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
        class="is-required"
      >
        <dw-select
          v-model="form.dataType"
          placeholder="请选择"
          class="w-full"
          @change="changeDataType"
        >
          <dw-option
            v-for="item in dataTypeOpt" :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="访问权限"
        class="is-required"
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
          class="wh-full"
          min="1"
          clearable
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
          class="w-full"
          :step="form.step"
          clearable
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
          class="w-full"
          :step="form.step"
          clearable
          placeholder="请输入最小值"
        />
      </el-form-item>
      <el-form-item
        v-if="currentType === 'number'"
        label="单位"
        prop="unit"
        class="is-required"
      >
        <dw-select
          v-model="form.unit"
          placeholder="请选择"
          class="w-full"
          @change="changeUnit"
        >
          <dw-option
            v-for="item in unitListOpt" :key="item.value"
            :label="`${item.label}:${item.value}`"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
      <el-form-item v-if="currentType === 'enum'" label="枚举值" prop="enums">
        <div class="flex flex-col">
          <div v-for="(item, index) in form.enums" :key="index" class="flex gap-20px pt-10px">
            <el-input-number
              v-model="item.key"
              placeholder="键名"
              style="width: 40%; margin-right: 10px"
            />
            <el-input
              v-model="item.value"
              placeholder="值"
              style="width: 40%; margin-right: 10px"
            />
            <el-button :disabled="index === 0" type="danger" icon="el-icon-delete" @click="removeEnumValue(index)">
              删除
            </el-button>
          </div>
          <div class="pt-10px self-center">
            <dw-button type="primary" plain @click="addEnumValue">
              添加枚举值
            </dw-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item
        v-if="currentType === 'text'"
        label="字符串长度"
        prop="length"
      >
        <el-input-number
          v-model="form.length"
          class="w-full"
          clearable
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
        prop="bool0"
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
      <IconInput v-model:value="form.iconId" />
    </el-form>
  </dw-dialog>
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
