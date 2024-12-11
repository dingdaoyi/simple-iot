<script setup>
import { productDetailApi, productTypeDetailApi } from '@/api/index.js'
import Breadcrumb from '@/components/Breadcrumb.vue'
import ProductInfo from '@/views/tslModel/widget/productInfo.vue'
import ServiceConfig from '@/views/tslModel/widget/serviceConfig.vue'
import tslexpoert from '@/views/tslModel/widget/tslexpoert.vue'
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const activeName = ref('service')
const route = useRoute()
const breadcrumbs = ref(
  [
    {
      label: '产品类型',
      path: '/productType',
    },
    {
      label: '物模型',
    },
    {
      label: '物模型',
    },
  ],
)
const productDetails = ref({})
const productTypeId = route.query.typeId
const productId = route.query.productId
function loadDetails() {
  if (productId) {
    productDetailApi(productId)
      .then(({ data }) => {
        // const data = res.data
        productDetails.value = data
        breadcrumbs.value[2].label = data.name
        breadcrumbs.value = [
          {
            label: data.productType.name,
            path: '/product',
          },
          {
            label: data.model,
          },
          {
            label: '物模型',
          },
        ]
      })
  }
  else {
    productTypeDetailApi(productTypeId)
      .then((res) => {
        const data = res.data
        breadcrumbs.value[2].label = data.name
      })
  }
}
loadDetails()
</script>

<template>
  <div class="wh-full flex flex-col">
    <Breadcrumb :breadcrumbs="breadcrumbs" />
    <div class="p-20px flex-1">
      <el-tabs
        v-model="activeName"
        class="demo-tabs"
      >
        <el-tab-pane label="基础信息" name="first">
          <ProductInfo :type-id="productTypeId" :product-id="productId" />
        </el-tab-pane>
        <el-tab-pane label="服务定义" name="service">
          <ServiceConfig :type-id="productTypeId" :product-id="productId" />
        </el-tab-pane>
        <el-tab-pane v-if="productId" label="TSL" name="tsl">
          <tslexpoert v-if="activeName === 'tsl'" :type-id="productTypeId" :product-id="productId" />
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
