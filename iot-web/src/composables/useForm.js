import { ElMessage } from 'element-plus'
import { ref } from 'vue'

/**
 * 表单处理 composable
 * 用于编辑对话框的表单逻辑
 * 确认和取消按钮已在对话框模板中定义
 */
export function useForm(options = {}) {
  const {
    api,
    successMsg = '操作成功',
    errorMsg = '操作失败',
    callback,
    closeCallback,
    defForm = {},
  } = options

  // 表单数据
  const form = ref({ ...defForm })

  // 加载状态
  const loading = ref(false)

  // 表单引用
  const editRef = ref(null)

  // 提交表单
  const onSubmit = async () => {
    if (!editRef.value) {
      return
    }

    // 验证表单
    const valid = await editRef.value.validate().catch(() => false)
    if (!valid) {
      return
    }

    loading.value = true
    try {
      await api(form.value)
      ElMessage.success(successMsg)
      if (callback) {
        callback()
      }
      return true
    }
    catch (err) {
      ElMessage.error(err?.message || errorMsg)
      return false
    }
    finally {
      loading.value = false
    }
  }

  // 关闭对话框
  const onClose = () => {
    if (closeCallback) {
      closeCallback()
    }
  }

  // 设置表单数据
  const setForm = (data) => {
    form.value = { ...defForm, ...data }
  }

  // 重置表单
  const resetForm = () => {
    form.value = { ...defForm }
    editRef.value?.clearValidate()
  }

  return {
    form,
    onSubmit,
    editRef,
    loading,
    onClose,
    setForm,
    resetForm,
  }
}
