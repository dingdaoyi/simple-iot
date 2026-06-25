<script lang="jsx" setup>
import { useI18n } from 'vue-i18n'
import { ref } from 'vue'
import { productDetailApi, productTypeDetailApi } from '@/api/index.js'

const { t } = useI18n()
const props = defineProps(['typeId', 'productId'])
const product = ref({})
const productType = ref({})
if (props.productId) {
  productDetailApi(props.productId)
    .then(({ data }) => {
      product.value = data
      productType.value = data.productType
    })
}
else {
  productTypeDetailApi(props.typeId)
    .then(({ data }) => {
      productType.value = data
    })
}</script>

<template>
  <div class="wh-full flex flex-col gap-20px p-20px">
    <div class="flex gap-20px ">
      <div class="flex">
        <div class="w-120px">
          {{ t('tsl.product_type_name') }}
        </div>
        <div class="text-gray">
          {{ productType.name || '' }}
        </div>
      </div>
    </div>
    <el-divider />
    <div v-if="productId" class="flex gap-20px ">
      <div class="flex w-200px gap-20px">
        <div>
          {{ t('tsl.info_manufacturer') }}
        </div>
        <div class="text-gray">
          {{ product.manufacturer || '' }}
        </div>
      </div>
      <div class="flex w-200px gap-20px">
        <div class="">
          {{ t('tsl.product_model') }}
        </div>
        <div class="text-gray">
          {{ product.model || '' }}
        </div>
      </div>
      <div class="flex w-300px gap-20px">
        <div class="">
          {{ t('tsl.product_access_key') }}
        </div>
        <div class="text-gray">
          {{ product.productKey || '' }}
        </div>
      </div>
    </div>
    <div class="flex  gap-20px">
      <div class="">
        {{ t('tsl.product_description') }}
      </div>
      <div class="text-gray">
        {{ product.mark || '' }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
