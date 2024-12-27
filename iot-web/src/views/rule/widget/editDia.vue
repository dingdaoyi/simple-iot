<script lang="jsx" setup>
import { deviceListApi, productListApi, productTypeListApi, protocolAddApi, protocolEditApi } from '@/api'
import { dwHooks } from 'dwyl-ui'
import { ref } from 'vue'

const props = defineProps(['datas'])

const emits = defineEmits(['update'])

const { useForm } = dwHooks

const ruleTypeOpt = [
  {
    label: '数据判断',
    value: 1,
  },
  {
    label: '数值转换',
    value: 2,
  },
]
const inputTypeOpt = [
  {
    label: '属性',
    value: 1,
  },
  {
    label: '事件',
    value: 2,
  },
  {
    label: '在离线',
    value: 3,
  },
]

const deviceList = ref([])
const productTypeListOpt = ref([])
const productListOpt = ref([])
const searchDeviceLoading = ref(false)

const rules = ref({
  name: [{ required: true, message: '协议名称不能为空', trigger: 'blur' }],
})

const sourceTypeOpt = [
  {
    label: '产品',
    value: 1,
  },
  {
    label: '设备分组',
    value: 2,
  },
  {
    label: '具体设备',
    value: 3,
  },
]

const { form, onSubmit, editRef, loading, onClose, dwDialogRef, onReset } = useForm({
  api: props.datas ? protocolEditApi : protocolAddApi,
  callback: () => {
    emits('update')
  },
})
function changeProduct(productId) {
  if (form.value.sourceType === 1) {
    form.sourceId = productId
  }
}
function changeProductType() {
  productListApi({ productTypeId: form.value.productTypeId })
    .then(({ data }) => {
      productListOpt.value = data
    })
}

async function searchDevice(query) {
  if (query) {
    searchDeviceLoading.value = true
    try {
      const res = await deviceListApi({
        deviceKey: query,
        productTypeId: form.value.productTypeId,
        productId: form.value.productId,
      })
      deviceList.value = res.data
    }
    finally {
      searchDeviceLoading.value = false
    }
  }
}
if (props?.datas) {
  form.value = props.datas
}
productTypeListApi()
  .then(({ data }) => {
    productTypeListOpt.value = data
  })
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
        label="规则名称"
        prop="name"
      >
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入规则名称"
        />
      </el-form-item>
      <el-form-item
        label="输入类型"
        prop="inputType"
        class="is-required"
      >
        <dw-select
          v-model="form.inputType"
          placeholder="请选择输入类型"
          class="w-full"
          filterable
          clearable
        >
          <dw-option
            v-for="item in inputTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        v-if="form.inputType === 1"
        label="处理类型"
        prop="ruleType"
        class="is-required"
      >
        <dw-select
          v-model="form.ruleType"
          placeholder="处理类型"
          class="w-full"
          filterable
          clearable
        >
          <dw-option
            v-for="item in ruleTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="数据源类型"
        prop="sourceType"
        class="is-required"
      >
        <dw-select
          v-model="form.sourceType"
          placeholder="请选择数据源类型"
          class="w-full"
          filterable
          clearable
        >
          <dw-option
            v-for="item in sourceTypeOpt"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </dw-select>
      </el-form-item>
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
            v-for="item in productTypeListOpt"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        label="产品"
        prop="productId"
        class="is-required"
      >
        <dw-select
          v-model="form.productId"
          placeholder="请选择产品"
          class="w-full"
          filterable
          clearable
        >
          <dw-option
            v-for="item in productListOpt"
            :key="item.id"
            :label="`${item.model}(${item.manufacturer})`"
            :value="item.id"
            @change="changeProduct(item.id)"
          />
        </dw-select>
      </el-form-item>
      <el-form-item
        v-if="form.sourceType === 3"
        label="设备"
        prop="sourceId"
        class="is-required"
      >
        <el-select
          v-model="form.sourceId"
          placeholder="请输入设备编号或名称"
          class="w-full"
          filterable
          remote
          :loading="searchDeviceLoading"
          :remote-method="searchDevice"
          clearable
        >
          <el-option
            v-for="item in deviceList"
            :key="item.id"
            :label="`${item.deviceKey}(${item.deviceName})`"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item
        label="备注"
        prop="mark"
      >
        <el-input
          v-model="form.mark"
          clearable
          placeholder="请输入备注信息"
        />
      </el-form-item>
    </el-form>
  </dw-dialog>
</template>

<style lang="scss" scoped>

</style>
