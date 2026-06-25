<script lang="jsx" setup>
import { Delete, Edit, RefreshRight } from '@element-plus/icons-vue'
import { ElButton, ElMessage, ElMessageBox } from 'element-plus'
import { h, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  customServiceListApi,
  propertyListApi,
  serviceDeleteApi,
  standardServiceListApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import ParamShow from '@/views/tslModel/widget/paramShow.vue' // Import icon display
import ServiceEdite from '@/views/tslModel/widget/serviceEdite.vue'

const props = defineProps(['typeId', 'productId', 'showEdite'])
const { t } = useI18n()
const propertiesDct = ref([])
const dialogVisibleParams = ref(false)
const showDetailRow = ref()
const iotTableRef = ref()

function handleShowParams(row) {
  showDetailRow.value = row
  dialogVisibleParams.value = true
}

// 刷新表格数据
function refreshTable() {
  iotTableRef.value?.refresh()
}

// 自定义删除方法
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(t('tsl.you_sure_you_want_delete_this_service'), t('common.tip'), {
      confirmButtonText: t('common.confirm'),
      cancelButtonText: t('common.cancel'),
      type: 'warning',
    })
    await serviceDeleteApi(row.id)
    ElMessage.success(t('tsl.delete_success'))
    refreshTable()
  }
  catch (error) {
    if (error !== 'cancel') {
      console.error(t('tsl.service'), error)
    }
  }
}

const column = [
  {
    prop: 'id',
    label: t('tsl.service_id'),
  },
  {
    prop: 'name',
    label: t('tsl.service_name'),
  },
  {
    prop: 'identifier',
    label: t('tsl.service_identifier_2'),
  },

  {
    prop: 'serviceType',
    label: t('tsl.service_type'),
    formatter(row) {
      return row.serviceType === 1 ? t('device.service') : t('tsl.event')
    },
  },
  {
    label: t('tsl.parameter_list_2'),
    render({ row }) {
      return h(
        ElButton,
        {
          type: 'primary',
          link: true,
          onClick: () => handleShowParams(row),
        },
        () => t('tsl.view_parameters'),
      )
    },
  },
  {
    prop: 'required',
    label: t('tsl.required_2'),
    formatter(row) {
      return row.required === true ? t('tsl.required') : t('tsl.param_optional_text')
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('common.operation'),
  },
]
const {
  params,
  dialogVisible,
  onSearch,
  onReset,
  onEdit,
  onAdd,
  diaTitle,
  currentItem,
} = useTable({
  diaName: t('device.service'),
  defParams: {
    productTypeId: props.typeId,
    productId: props.productId,
  },
})

function loadPropertiesDict() {
  propertyListApi({
    productTypeId: props.typeId,
    productId: props.productId,
    all: props.productId != null,
  })
    .then(({ data }) => {
      propertiesDct.value = data
    })
}
loadPropertiesDict()
</script>

<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable :placeholder="t('tsl.enter_service_name_identifier_search')" @keyup.enter="onSearch" />
      </div>
      <ElButton type="primary" @click="onSearch">
        {{ t('common.search') }}
      </ElButton>
      <ElButton :icon="RefreshRight" @click="onReset">
        {{ t('common.reset') }}
      </ElButton>
      <ElButton v-if="showEdite" type="primary" @click="onAdd">
        {{ t('tsl.add_service') }}
      </ElButton>
    </div>
    <IotTable
      ref="iotTableRef"
      row-key="id"
      :column="column"
      :params="params"
      :is-page="false"
      :api="productId ? customServiceListApi : standardServiceListApi"
    >
      <template #cz="{ row }">
        <ElButton v-if="showEdite" type="primary" link :icon="Edit" @click="onEdit(row)">
          {{ t('tsl.edit_definition_2') }}
        </ElButton>
        <ElButton v-if="showEdite" type="danger" link :icon="Delete" @click="handleDelete(row)">
          {{ t('common.delete') }}
        </ElButton>
      </template>
    </IotTable>
    <ServiceEdite
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :type-id="typeId"
      :product-id="productId"
      :datas="currentItem"
      :properties="propertiesDct"
      @update="refreshTable"
    />
    <ParamShow
      v-if="dialogVisibleParams"
      v-model="dialogVisibleParams"
      :properties="propertiesDct"
      :datas="showDetailRow"
    />
  </div>
</template>

<style scoped lang="scss">

</style>
