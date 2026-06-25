<script setup>
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import Property from '@/views/tslModel/widget/property.vue'
import Service from '@/views/tslModel/widget/service.vue'

defineProps(['typeId', 'productId'])
const { t } = useI18n()
const radioGroup = ref('property')
</script>

<template>
  <div class="flex flex-col gap-20px">
    <el-radio-group v-model="radioGroup">
      <el-radio-button value="property">
        {{ t('tsl.property_list') }}
      </el-radio-button>
      <el-radio-button value="service">
        {{ t('tsl.service_list') }}
      </el-radio-button>
    </el-radio-group>
    <div class="flex flex-col">
      <div class="pb-10px">
        {{ t('tsl.standard_model') }}
      </div>
      <Property v-if="radioGroup === 'property'" :key="radioGroup + typeId" :show-edite="!productId" :type-id="typeId" />
      <Service v-if="radioGroup === 'service'" :key="radioGroup + typeId" :show-edite="!productId" :type-id="typeId" />
    </div>

    <div v-if="productId" class="flex flex-col">
      <div class="pb-10px">
        {{ t('tsl.custom_model') }}
      </div>
      <Property
        v-if="radioGroup === 'property'" :key="radioGroup + typeId + productId"
        show-edite="true"
        :type-id="typeId"
        :product-id="productId"
      />
      <Service
        v-if="radioGroup === 'service'" :key="radioGroup + typeId + productId"
        :type-id="typeId"
        :product-id="productId"
        :show-edite="true"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">

</style>
