<script setup>
import { useI18n } from 'vue-i18n'
import { Delete, Edit, RefreshRight } from '@element-plus/icons-vue'
import { propertyDeleteApi, propertyListApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import PropertyEdite from '@/views/tslModel/widget/propertyEdite.vue'

const { t } = useI18n()
const props = defineProps(['typeId', 'productId', 'showEdite'])

const column = [

  {
    prop: 'name',
    label: t('auto.tslmodel_property_45fc3131'),
  },
  {
    prop: 'identifier',
    label: t('auto.tslmodel_property_8be288ea'),
  },
  {
    prop: 'id',
    label: t('auto.tslmodel_property_3404df91'),
  },
  {
    prop: 'dataTypeName',
    label: t('auto.tslmodel_property_185f7bf6'),
  },
  {
    prop: 'definitions',
    label: t('auto.tslmodel_property_a32474fd'),
  },
  {
    prop: 'accessMode',
    label: t('auto.tslmodel_property_1fc8d87b'),
    formatter(row) {
      return row.accessMode === 'r' ? t('auto.tslmodel_widget_property_85541bd9') : t('auto.tslmodel_widget_property_2300ad28')
    },
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: t('auto.tslmodel_property_2b6bc0f2'),
  },
]
const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  onReset,
  dwTable,
  onDelete,
  onEdit,
  onAdd,
  diaTitle,
  currentItem,
} = useTable({
  deleteApi: propertyDeleteApi,
  diaName: t('auto.tslmodel_widget_property_24d67862'),
  defParams: {
    productTypeId: props.typeId,
    productId: props.productId,
  },
})</script>

<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable :placeholder="t('auto.tslmodel_property_2773a445')" @keyup.enter="onSearch" />
      </div>
      <el-button type="primary" @click="onSearch">
        {{ t('auto.tslmodel_property_e5f71fc3') }}
      </el-button>
      <el-button :icon="RefreshRight" @click="onReset">
        {{ t('auto.tslmodel_property_4b9c3271') }}
      </el-button>
      <el-button v-if="showEdite" type="primary" @click="onAdd">
        {{ t('auto.tslmodel_property_b58c7549') }}
      </el-button>
    </div>
    <IotTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :is-page="false"
      :api="propertyListApi"
    >
      <template #cz="{ row }">
        <el-button v-if="showEdite" type="primary" link :icon="Edit" @click="onEdit(row)">
          {{ t('auto.tslmodel_property_09296297') }}
        </el-button>
        <el-button v-if="showEdite" type="danger" link :icon="Delete" @click="onDelete(row)">
          {{ t('auto.tslmodel_property_2f4aaddd') }}
        </el-button>
      </template>
    </IotTable>
    <PropertyEdite
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :type-id="typeId"
      :product-id="productId"
      :datas="currentItem"
      @update="updatePage"
    />
  </div>
</template>

<style scoped lang="scss">

</style>