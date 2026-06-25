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
    label: t('tsl.property_name'),
  },
  {
    prop: 'identifier',
    label: t('tsl.property_identifier_2'),
  },
  {
    prop: 'id',
    label: t('tsl.id'),
  },
  {
    prop: 'dataTypeName',
    label: t('tsl.data_type'),
  },
  {
    prop: 'definitions',
    label: t('tsl.data_definition'),
  },
  {
    prop: 'accessMode',
    label: t('tsl.access_mode_2'),
    formatter(row) {
      return row.accessMode === 'r' ? t('tsl.property_read_only') : t('tsl.read_write')
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
  diaName: t('tsl.property'),
  defParams: {
    productTypeId: props.typeId,
    productId: props.productId,
  },
})</script>

<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable :placeholder="t('tsl.enter_property_name_identifier_search')" @keyup.enter="onSearch" />
      </div>
      <el-button type="primary" @click="onSearch">
        {{ t('common.search') }}
      </el-button>
      <el-button :icon="RefreshRight" @click="onReset">
        {{ t('common.reset') }}
      </el-button>
      <el-button v-if="showEdite" type="primary" @click="onAdd">
        {{ t('tsl.add_property') }}
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
          {{ t('tsl.edit_definition') }}
        </el-button>
        <el-button v-if="showEdite" type="danger" link :icon="Delete" @click="onDelete(row)">
          {{ t('common.delete') }}
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