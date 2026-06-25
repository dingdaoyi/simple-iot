<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, RefreshRight } from '@element-plus/icons-vue'
import { ElButton, ElMessage, ElMessageBox } from 'element-plus'
import { h, ref } from 'vue'
import {
  customServiceListApi,
  propertyListApi,
  serviceDeleteApi,
  standardServiceListApi,
} from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import ParamShow from '@/views/tslModel/widget/paramShow.vue' // 引入图标
import ServiceEdite from '@/views/tslModel/widget/serviceEdite.vue'

const { t } = useI18n()
const props = defineProps(['typeId', 'productId', 'showEdite'])

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
    await ElMessageBox.confirm(t('auto.tslmodel_widget_service_8c4a16b0'), t('auto.tslmodel_widget_service_02d9819d'), {
      confirmButtonText: t('auto.tslmodel_service_38cf16f2'),
      cancelButtonText: t('auto.tslmodel_service_625fb26b'),
      type: 'warning',
    })
    await serviceDeleteApi(row.id)
    ElMessage.success(t('auto.tslmodel_service_0007d170'))
    refreshTable()
  }
  catch (error) {
    if (error !== 'cancel') {
      console.error(t('auto.tslmodel_widget_service_b42909e2'), error)
    }
  }
}

const column = [
  {
    prop: 'id',
    label: t('auto.tslmodel_service_b7ec1d09'),
  },
  {
    prop: 'name',
    label: t('auto.tslmodel_service_8f3747c0'),
  },
  {
    prop: 'identifier',
    label: t('auto.tslmodel_service_fda8afde'),
  },

  {
    prop: 'serviceType',
    label: t('auto.tslmodel_service_924f67de'),
    formatter(row) {
      return row.serviceType === 1 ? t('auto.tslmodel_widget_service_47d68cd0') : t('auto.tslmodel_widget_service_10b2761d')
    },
  },
  {
    label: t('auto.tslmodel_service_cba87444'),
    render({ row }) {
      return h(
        ElButton,
        {
          type: 'primary',
          link: true,
          onClick: () => handleShowParams(row),
        },
        () => t('auto.tslmodel_widget_service_6fa6500b'),
      )
    },
  },
  {
    prop: 'required',
    label: t('auto.tslmodel_service_d10a7280'),
    formatter(row) {
      return row.required === true ? t('auto.tslmodel_widget_service_417973c1') : t('auto.tslmodel_widget_service_c20cba89')
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('auto.tslmodel_service_2b6bc0f2'),
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
  diaName: t('auto.tslmodel_widget_service_47d68cd0'),
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
loadPropertiesDict()</script>

<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable :placeholder="t('auto.tslmodel_service_c6d56424')" @keyup.enter="onSearch" />
      </div>
      <ElButton type="primary" @click="onSearch">
        {{ t('auto.tslmodel_service_e5f71fc3') }}
      </ElButton>
      <ElButton :icon="RefreshRight" @click="onReset">
        {{ t('auto.tslmodel_service_4b9c3271') }}
      </ElButton>
      <ElButton v-if="showEdite" type="primary" @click="onAdd">
        {{ t('auto.tslmodel_service_b58c7549') }}
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
          {{ t('auto.tslmodel_service_09296297') }}
        </ElButton>
        <ElButton v-if="showEdite" type="danger" link :icon="Delete" @click="handleDelete(row)">
          {{ t('auto.tslmodel_service_2f4aaddd') }}
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