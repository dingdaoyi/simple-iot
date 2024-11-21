import { defineStore } from 'pinia'
import { getDictionaryApi, getProductType, producerCurrent, getProvinceCityArea } from '@/api'

export const useDictionary = defineStore({
  id: 'dictionary',
  state: () => ({
    productTypeList: null,
    producer: null
  }),
  getters: {},
  actions: {
    async getDictionary (code) {
      if (!this[code]) {
        const { data } = await getDictionaryApi({ code })
        data.forEach((item) => {
          item.label = item.dictValue
          item.value = item.dictKey
        })
        this[code] = data
        return data
      } else {
        return this[code]
      }
    },
    /* 获取产品类型 */
    async getProductTypeDic () {
      if (!this.productTypeList) {
        const { data } = await getProductType()
        data.forEach((item) => {
          item.label = item.name
          item.value = item.id
        })
        this.productTypeList = data
        return data
      } else {
        return this.productTypeList
      }
    },
    async getMyProducer () {
      if (!this.producer) {
        const { data } = await producerCurrent()
        this.producer = data
        return data
      } else {
        return this.producer
      }
    },
    /* 获取省市区 */
    async getProvinceCityArea (pid, code) {
      if (!this[code]) {
        const { data } = await getProvinceCityArea({ pid }, code)
        data.forEach((item) => {
          item.label = item.name
          item.value = item.id
        })
        this[code] = data
        return data
      } else {
        return this[code]
      }
    }
  }
})
