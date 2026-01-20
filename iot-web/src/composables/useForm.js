import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useForm(options = {}) {
  const {
    defaultData = {},
  } = options

  const formData = reactive({ ...defaultData })
  const loading = ref(false)

  const reset = () => {
    Object.assign(formData, defaultData)
  }

  const setData = (data) => {
    Object.assign(formData, data)
  }

  return {
    formData,
    loading,
    reset,
    setData,
  }
}
