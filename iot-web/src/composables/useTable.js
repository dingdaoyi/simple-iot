import { ElMessage, ElMessageBox } from 'element-plus'
import { reactive, ref } from 'vue'

export function useTable(options = {}) {
  const {
    deleteApi = null,
    fetchApi = null,
    defParams = {},
    pageSize = 20,
    diaName = '项',
  } = options

  // 查询参数 - 完全兼容原 useDwTable 的参数名
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
    if (!fetchApi) {
      console.warn('useTable: 请传入 fetchApi 函数')
      return
    }

    loading.value = true
    try {
      // 添加时间戳防止 GET 请求缓存
      const queryParams = { ...params, _t: Date.now() }
      const res = await fetchApi(queryParams)
      if (res && res.data) {
        tableData.value = res.data.records || res.data.data || res.data
        total.value = res.data.total || 0
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
    // 通过添加时间戳强制触发 params 变化，让外部组件（如 IotTable）能监听到
    params._t = Date.now()
    params.page = 1
    // 如果有 fetchApi 则调用
    if (fetchApi) {
      updatePage()
    }
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
  const onAdd = (title) => {
    // 如果第一个参数是事件对象，忽略它
    if (title && typeof title === 'object' && title.type) {
      title = null
    }
    diaTitle.value = title || `新增${diaName}`
    currentItem.value = null
    dialogVisible.value = true
  }

  // 编辑
  const onEdit = (row, title) => {
    // 如果第二个参数是事件对象，忽略它
    if (title && typeof title === 'object' && title.type) {
      title = null
    }
    diaTitle.value = title || `编辑${diaName}`
    currentItem.value = { ...row }
    dialogVisible.value = true
  }

  // 删除
  const onDelete = async (row) => {
    if (!deleteApi) {
      console.warn('useTable: 请传入 deleteApi 函数')
      return
    }

    try {
      await ElMessageBox.confirm(
        `确定要删除${diaName}吗?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning',
        },
      )

      await deleteApi(row.id || row)
      ElMessage.success('删除成功')
      updatePage()
    }
    catch (error) {
      if (error !== 'cancel') {
        console.error('删除失败:', error)
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

  // dwTable - 返回表格配置对象(兼容原 useDwTable)
  const dwTable = ref({
    data: tableData,
    total,
    loading,
    currentPage: params.page,
    pageSize: params.size,
  })

  return {
    // 数据
    params,
    dialogVisible,
    currentItem,
    diaTitle,
    tableData,
    total,
    loading,
    dwTable,

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
