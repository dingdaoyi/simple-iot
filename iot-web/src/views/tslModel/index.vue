<template>
  <div class="wh-full flex flex-col">
    <Breadcrumb :breadcrumbs="breadcrumbs" />
    <div class="p-20px">
      <el-tabs
        v-model="activeName"
        class="demo-tabs"
      >
        <el-tab-pane label="基础信息" name="first">User</el-tab-pane>
        <el-tab-pane label="服务定义" name="service">
          <service-config :typeId="productTypeId" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import Breadcrumb from '@/components/Breadcrumb.vue'
import { ref } from 'vue'
import { productDetailApi, productTypeDetailApi } from '@/api/index.js'
import { useRoute } from 'vue-router'
import ServiceConfig from '@/views/tslModel/widget/serviceConfig.vue'
const activeName = ref('service')
const route = useRoute()
const breadcrumbs = ref(
  [
    {
      label: '产品类型',
      path: '/productType'
    },
    {
      label: '物模型'
    },
    {
      label: '物模型'
    }
  ]
)
const productDetails = ref({})
const productTypeId = route.query.typeId
const productId = route.query.productId
const loadDetails = () => {
  if (productId) {
    productDetailApi(productId)
      .then(({ data }) => {
        // const data = res.data
        productDetails.value = data
        breadcrumbs.value[2].label = data.name
        breadcrumbs.value = [
          {
            label: data.productType.name,
            path: '/product'
          },
          {
            label: data.model
          },
          {
            label: '物模型'
          }
        ]
      })
  } else {
    productTypeDetailApi(productTypeId)
      .then(res => {
        const data = res.data
        breadcrumbs.value[2].label = data.name
      })
  }

  if (productId) {
    // 产品的物模型

  }
}
loadDetails()
</script>
<style scoped lang="scss">

</style>
