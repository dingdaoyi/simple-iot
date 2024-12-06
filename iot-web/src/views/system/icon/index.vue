<template>
  <div class="flex flex-col flex-1">
    <div class="flex flex-row mb-12px">
      <div class="w-200px mr-12px">
        <el-input v-model="params.model" clearable placeholder="请输入图标名称" />
      </div>
      <el-button type="primary" @click="onSearch">搜索</el-button>
      <el-button type="success" @click="onAdd">添加</el-button>
    </div>
    <DwGridList ref="dwTable"
                class="flex-1"
                :isPage="false"
                :api="iconListApi"
                :params="params"
                :minWidth="200">
      <template #default="{row}">
        <el-card class="h-full">
          <template #header>
            <div class="text-center">
              <span>{{ row.name }}</span>
            </div>
          </template>
          <div class="flex justify-center">
            <DwImage isPreview class="w-100px h-100px" :src="row.path" />
          </div>
          <template #footer>
            <div class="flex justify-end">
              <dw-button type="danger" link @click="onDelete(row)">删除</dw-button>
            </div>
          </template>
        </el-card>
      </template>
    </DwGridList>
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>
<script setup>
import { dwHooks } from 'dwyl-ui'
import { iconDeleteApi, iconListApi } from '@/api/index.js'
import EditDia from '@/views/system/icon/widget/editDia.vue'
import { useRouter } from 'vue-router'

const { useDwTable } = dwHooks

const router = useRouter()

const column = [
  {
    prop: 'name',
    label: '图标名称'
  },
  {
    prop: 'path',
    slot: 'icon',
    width: 220,
    label: '图标'
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
  onAdd,
  diaTitle,
  currentItem
} = useDwTable({
  deleteApi: iconDeleteApi,
  diaName: '图标',
  defParams: {
  }
})

function closeEdite () {
  updatePage()
}
</script>
