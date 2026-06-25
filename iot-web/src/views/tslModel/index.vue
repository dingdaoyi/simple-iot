<script setup>
import { useI18n } from 'vue-i18n'
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { productDetailApi, productTypeDetailApi } from '@/api/index.js'
import Breadcrumb from '@/components/Breadcrumb.vue'
import ProductInfo from '@/views/tslModel/widget/productInfo.vue'
import ServiceConfig from '@/views/tslModel/widget/serviceConfig.vue'
import tslexpoert from '@/views/tslModel/widget/tslexpoert.vue'

const { t } = useI18n()
const activeName = ref('service')
const route = useRoute()
const breadcrumbs = ref(
  [
    {
      label: t('auto.tslmodel_index_2db97cae'),
      path: '/productType',
    },
    {
      label: t('auto.tslmodel_index_1ff43b4a'),
    },
    {
      label: t('auto.tslmodel_index_1ff43b4a'),
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
            label: t('auto.tslmodel_index_1ff43b4a'),
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
loadDetails()</script>

<template>
  <div class="tsl-page">
    <Breadcrumb :breadcrumbs="breadcrumbs" />
    <div class="tsl-content">
      <el-tabs
        v-model="activeName"
        class="tsl-tabs"
      >
        <el-tab-pane :label="t('auto.tslmodel_index_6ea1fe6b')" name="first">
          <ProductInfo :type-id="productTypeId" :product-id="productId" />
        </el-tab-pane>
        <el-tab-pane :label="t('auto.tslmodel_index_256aa787')" name="service">
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
.tsl-page {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.tsl-content {
  flex: 1;
  padding: 16px 20px;
  overflow: auto;
  background: var(--iot-glass-bg);
  border-radius: var(--radius-lg);
  margin: 0 var(--space-md) var(--space-md);
}

/* Tabs 样式 */
.tsl-tabs {
  :deep(.el-tabs__header) {
    margin-bottom: 16px;
  }
}
</style>