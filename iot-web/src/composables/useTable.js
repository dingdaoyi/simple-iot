import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

export function useTable(options = {}) {
  const {
    deleteApi = null,
    defParams = {},
    pageSize = 20,
  } = options

  // 查询参数
  const params = reactive({
    ...defParams,
    page: 1,
    size: pageSize,
  })

  // 表格数据
  const tableData = ref([])
  const total = ref(0)
  const loading = ref(false)

  // 对话框相关
  const dialogVisible = ref(false)
  const currentItem = ref(null)
  const diaTitle = ref('')

  // 更新页面数据
  const updatePage = async () => {
    loading.value = true
    try {
      // 这里需要传入获取数据的API函数
      if (options.fetchApi) {
        const res = await options.fetchApi(params)
        if (res && res.data) {
          tableData.value = res.data.records || res.data.data || res.data
          total.value = res.data.total || 0
        }
      }
    }
    catch (error) {
      console.error('获取数据失败:', error)
      ElMessage.error('获取数据失败')
    }
    finally {
      loading.value = false
    }
  }

  // 搜索
  const onSearch = () => {
    params.page = 1
    updatePage()
  }

  // 重置搜索
  const onReset = () => {
    Object.assign(params, {
      ...defParams,
      page: 1,
      size: pageSize,
    })
    updatePage()
  }

  // 新增
  const onAdd = (title = '新增') => {
    diaTitle.value = title
    currentItem.value = null
    dialogVisible.value = true
  }

  // 编辑
  const onEdit = (row, title = '编辑') => {
    diaTitle.value = title
    currentItem.value = { ...row }
    dialogVisible.value = true
  }

  // 删除
  const onDelete = async (row, title = '该项') => {
    try {
      await ElMessageBox.confirm(
        `确定要删除${title}吗?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        },
      )

      if (deleteApi) {
        await deleteApi(row.id || row)
        ElMessage.success('删除成功')
        updatePage()
      }
    }
    catch (error) {
      if (error !== 'cancel') {
        console.error('删除失败:', error)
        ElMessage.error('删除失败')
      }
    }
  }

  // 分页改变
  const onPageChange = (page) => {
    params.page = page
    updatePage()
  }

  // 每页条数改变
  const onSizeChange = (size) => {
    params.size = size
    params.page = 1
    updatePage()
  }

  // 关闭对话框
  const closeDialog = () => {
    dialogVisible.value = false
    currentItem.value = null
  }

  return {
    // 数据
    params,
    tableData,
    total,
    loading,
    dialogVisible,
    currentItem,
    diaTitle,

    // 方法
    updatePage,
    onSearch,
    onReset,
    onAdd,
    onEdit,
    onDelete,
    onPageChange,
    onSizeChange,
    closeDialog,
  }
}
