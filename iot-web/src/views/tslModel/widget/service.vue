<template>
  <div class="wh-full">
    <div class="flex flex-row mb-12px">
      <div class="w-240px mr-12px">
        <el-input v-model="params.search" clearable placeholder="请输入服务名称或标识符搜索" />
      </div>
      <el-button type="primary" @click="onSearch">搜索</el-button>
      <el-button type="success" @click="onAdd">添加</el-button>
    </div>
    <DwTable
      ref="dwTable"
      row-key="id"
      :column="column"
      :params="params"
      :isPage="false"
      :api="standardServiceListApi"
    >
      <template #cz="{ row }">
        <dw-button type="danger" link @click="onDelete(row)">删除</dw-button>
        <dw-button type="primary" link @click="onEdit(row)">编辑定义</dw-button>
      </template>
    </DwTable>
    <service-edite
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :type-id="typeId"
      :datas="currentItem"
      :properties="propertiesDct"
      @update="updatePage"
    />
    <param-show
      v-if="dialogVisibleParams"
      v-model="dialogVisibleParams"
      :properties="propertiesDct"
      :datas="showDetailRow"
    />

  </div>
</template>
<script lang="jsx" setup>
import { productTypeDelApi, propertyListApi, standardServiceListApi } from '@/api/index.js'
import { dwHooks } from 'dwyl-ui'
import ServiceEdite from '@/views/tslModel/widget/serviceEdite.vue'
import { ElButton } from 'element-plus'
import { ZoomIn } from '@element-plus/icons-vue'
import { h, ref } from 'vue'
import ParamShow from '@/views/tslModel/widget/paramShow.vue' // 引入图标

const { useDwTable } = dwHooks

const props = defineProps(['typeId'])
const propertiesDct = ref([])
const dialogVisibleParams = ref(false)
const showDetailRow = ref()
const handleShowParams = (row) => {
  showDetailRow.value = row
  dialogVisibleParams.value = true
}
const column = [
  {
    prop: 'id',
    label: '服务ID'
  },
  {
    prop: 'name',
    label: '服务名称'
  },
  {
    prop: 'identifier',
    label: '服务标识'
  },

  {
    prop: 'serviceType',
    label: '服务类型',
    formatter (row) {
      return row.serviceType === 1 ? '服务' : '事件'
    }
  },
  {
    label: '参数列表',
    render ({ row }) {
      return h(
        ElButton,
        {
          type: 'primary',
          link: true,
          onClick: () => handleShowParams(row)
        },
        () => '查看参数'
      )
    }
  },
  {
    prop: 'required',
    label: '是否必选',
    formatter (row) {
      return row.required === true ? '必选' : '可选'
    }
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 220,
    label: '操作'
  }
]
const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  dwTable,
  onDelete,
  onEdit,
  onAdd,
  diaTitle,
  currentItem
} = useDwTable({
  deleteApi: productTypeDelApi,
  diaName: '服务',
  defParams: {
    productTypeId: props.typeId
  }
})

const loadPropertiesDict = () => {
  propertyListApi({
    productTypeId: props.typeId
  })
    .then(({ data }) => {
      propertiesDct.value = data
    })
}
loadPropertiesDict()
</script>

<style scoped lang="scss">

</style>
