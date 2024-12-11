<script lang="jsx" setup>
import { productDetailApi, productTypeDetailApi } from '@/api/index.js'
import { ref } from 'vue'

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
}
</script>

<template>
  <div class="wh-full flex flex-col gap-20px p-20px">
    <div class="flex gap-20px ">
      <div class="flex">
        <div class="w-120px">
          产品类型名称
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
          产品厂家:
        </div>
        <div class="text-gray">
          {{ product.manufacturer || '' }}
        </div>
      </div>
      <div class="flex w-200px gap-20px">
        <div class="">
          产品型号:
        </div>
        <div class="text-gray">
          {{ product.model || '' }}
        </div>
      </div>
      <div class="flex w-300px gap-20px">
        <div class="">
          产品接入密钥:
        </div>
        <div class="text-gray">
          {{ product.productKey || '' }}
        </div>
      </div>
    </div>
    <div class="flex  gap-20px">
      <div class="">
        产品描述:
      </div>
      <div class="text-gray">
        {{ product.mark || '' }}
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
