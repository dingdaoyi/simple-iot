<script setup>
import { computed, onMounted, ref, watch } from 'vue'

const props = defineProps({
  // 新风格：数据数组
  data: {
    type: Array,
    default: () => [],
  },
  // 新风格：列配置（复数）
  columns: {
    type: Array,
    default: () => [],
  },
  // 旧风格：列配置（单数）- 兼容旧代码
  column: {
    type: Array,
    default: () => [],
  },
  // 旧风格：API 函数
  api: {
    type: Function,
    default: null,
  },
  // 旧风格：请求参数
  params: {
    type: Object,
    default: () => ({}),
  },
  // 旧风格：是否分页
  isPage: {
    type: Boolean,
    default: true,
  },
  // 旧风格：row-key
  rowKey: {
    type: String,
    default: '',
  },
  // 总数
  total: {
    type: Number,
    default: 0,
  },
  // 当前页
  currentPage: {
    type: Number,
    default: 1,
  },
  // 每页条数
  pageSize: {
    type: Number,
    default: 20,
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false,
  },
  // 是否自适应列宽（铺满表格）
  autoFit: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['pageChange', 'sizeChange', 'selectionChange'])

const innerPage = ref(props.currentPage)
const innerSize = ref(props.pageSize)
const innerData = ref(props.data || [])
const innerLoading = ref(false)
const dwTable = ref()

// 兼容：优先使用 columns，否则使用 column
const finalColumns = computed(() => {
  const cols = (props.columns && props.columns.length > 0) ? props.columns : (props.column || [])
  return cols.map((col) => {
    const column = { ...col }

    // 处理 formatter 函数 - 转换为 render 函数
    if (column.formatter && typeof column.formatter === 'function') {
      const formatterFn = column.formatter
      column.render = ({ row }) => formatterFn(row, column, row[column.prop])
    }

    // 处理 slot: 'expand' - 转换为 type: 'expand'
    if (column.slot === 'expand') {
      column.type = 'expand'
      column.prop = `expand_${Date.now()}_${Math.random()}`
    }

    return column
  }).filter(col => col.type !== 'expand')
})

// 获取 expand 列配置
const expandColumn = computed(() => {
  const cols = props.columns || props.column || []
  return cols.find(col => col.slot === 'expand')
})

// 是否使用旧风格（API 模式）
const isApiMode = computed(() => !!props.api)

// 实际使用的数据
const tableData = computed(() => isApiMode.value ? innerData.value : props.data)

// 实际使用的加载状态
const tableLoading = computed(() => isApiMode.value ? innerLoading.value : props.loading)

function pageChange(page) {
  innerPage.value = page
  emit('pageChange', page)
  if (isApiMode.value) {
    fetchData()
  }
}

function sizeChange(size) {
  innerSize.value = size
  emit('sizeChange', size)
  if (isApiMode.value) {
    fetchData()
  }
}

function selectionChange(selection) {
  emit('selectionChange', selection)
}

// 判断是否为 VNode
function isVNode(value) {
  return value && typeof value === 'object' && ('__v_isVNode' in value || '$el' in value || 'type' in value)
}

// 旧风格：通过 API 获取数据
async function fetchData() {
  if (!props.api)
    return

  innerLoading.value = true
  try {
    const res = await props.api({
      ...props.params,
      page: innerPage.value,
      size: innerSize.value,
    })
    innerData.value = res.data || res.list || []
  }
  catch (e) {
    console.error('IotTable fetchData error:', e)
    innerData.value = []
  }
  finally {
    innerLoading.value = false
  }
}

// 监听 params 变化重新获取数据
watch(() => props.params, () => {
  if (isApiMode.value) {
    fetchData()
  }
}, { deep: true })

// 初始化
onMounted(() => {
  if (isApiMode.value) {
    fetchData()
  }
})

// 暴露方法给外部
defineExpose({
  dwTable,
  fetchData,
  refresh: fetchData,
})
</script>

<template>
  <div class="iot-table-wrapper">
    <el-table
      ref="dwTable"
      :data="tableData"
      :loading="tableLoading"
      :row-key="rowKey"
      :table-layout="autoFit ? 'auto' : 'fixed'"
      stripe
      style="width: 100%"
      @selection-change="selectionChange"
    >
      <!-- Expand 列 -->
      <el-table-column
        v-if="expandColumn"
        type="expand"
        :width="expandColumn.width"
      >
        <template #default="{ row, $index }">
          <slot
            name="expand"
            :row="row"
            :column="expandColumn"
            :index="$index"
          />
        </template>
      </el-table-column>

      <!-- Selection 列 -->
      <el-table-column
        v-if="finalColumns.some(col => col.type === 'selection')"
        type="selection"
        width="55"
      />
      <el-table-column
        v-for="col in finalColumns"
        :key="col.prop"
        :prop="col.prop"
        :label="col.label"
        :width="col.width"
        :min-width="col.minWidth || (autoFit && !col.width ? 120 : undefined)"
        :fixed="col.fixed"
        :align="col.align || 'center'"
        header-align="center"
      >
        <template #default="{ row, $index }">
          <slot
            v-if="col.slot"
            :name="col.slot"
            :row="row"
            :column="col"
            :index="$index"
          />
          <!-- VNode 渲染 -->
          <component
            :is="col.render({ row, column: col })"
            v-else-if="col.render && isVNode(col.render({ row, column: col }))"
          />
          <!-- 字符串/普通值渲染 -->
          <span v-else-if="col.render">{{ col.render({ row, column: col }) ?? row[col.prop] ?? '-' }}</span>
          <span v-else>{{ row[col.prop] ?? '-' }}</span>
        </template>
      </el-table-column>
      <slot name="operations" />
    </el-table>

    <div v-if="isPage && total > 0" class="iot-table-pagination">
      <el-pagination
        :current-page="innerPage"
        :page-size="innerSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="pageChange"
        @size-change="sizeChange"
      />
    </div>

    <!-- 空状态提示 -->
    <div
      v-if="!tableLoading && tableData.length === 0"
      class="iot-table-empty"
    >
      <div class="empty-icon">
        📭
      </div>
      <div class="empty-text">
        暂无数据
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.iot-table-wrapper {
  background: var(--iot-glass-bg-dark);
  backdrop-filter: blur(20px) saturate(180%);
  -webkit-backdrop-filter: blur(20px) saturate(180%);
  border: 1px solid var(--iot-glass-border);
  border-radius: var(--radius-md);
  padding: var(--space-lg);
  box-shadow: var(--shadow-md);
  transition: all var(--transition-base);
  position: relative;
}

:deep(.el-table) {
  font-size: 14px;
  background: transparent;

  &::before {
    background: transparent;
  }

  .el-table__inner-wrapper {
    border-radius: var(--radius-sm);
    overflow: hidden;
  }

  .el-table__header-wrapper {
    th {
      background: rgba(99, 102, 241, 0.08);
      color: var(--iot-color-text-primary);
      font-weight: 600;
      font-family: 'Fira Code', monospace;
      border-bottom: 1px solid rgba(99, 102, 241, 0.15);
      padding: var(--space-md) var(--space-sm);

      .cell {
        padding: 0 var(--space-sm);
      }
    }
  }

  .el-table__body-wrapper {
    .el-table__row {
      transition: all var(--transition-fast);

      &:hover {
        background: rgba(99, 102, 241, 0.06);
      }

      td {
        border-bottom: 1px solid rgba(99, 102, 241, 0.08);
        padding: var(--space-md) var(--space-sm);

        .cell {
          padding: 0 var(--space-sm);
        }
      }
    }

    .el-table__empty-block {
      background: transparent;
    }

    .el-table__empty-text {
      color: var(--iot-color-text-muted);
    }
  }
}

:deep(.el-loading-mask) {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border-radius: var(--radius-md);
}

.iot-table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--space-xl);
  padding-top: var(--space-lg);
  border-top: 1px solid rgba(99, 102, 241, 0.15);
}

:deep(.el-pagination) {
  .el-pagination__total {
    color: var(--iot-color-text-secondary);
    font-weight: 500;
  }

  .btn-prev,
  .btn-next,
  .el-pager li {
    background: var(--iot-glass-bg);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-sm);
    margin: 0 4px;
    font-weight: 500;
    transition: all var(--transition-fast);

    &:hover {
      background: rgba(99, 102, 241, 0.1);
      border-color: var(--iot-color-primary);
    }

    &.active {
      background: linear-gradient(135deg, var(--iot-color-primary) 0%, var(--iot-color-primary-dark) 100%);
      border-color: var(--iot-color-primary);
      color: white;
      box-shadow: 0 2px 8px rgba(99, 102, 241, 0.4);
    }
  }

  .el-select {
    .el-input {
      .el-input__wrapper {
        background: var(--iot-glass-bg);
        border: 1px solid var(--iot-glass-border);
        border-radius: var(--radius-sm);
        box-shadow: none;
        transition: all var(--transition-fast);

        &:hover {
          border-color: rgba(99, 102, 241, 0.3);
        }

        &.is-focus {
          border-color: var(--iot-color-primary);
          box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1);
        }
      }
    }
  }
}

// 空状态
.iot-table-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-2xl) var(--space-xl);
  min-height: 200px;

  .empty-icon {
    font-size: 48px;
    margin-bottom: var(--space-md);
    opacity: 0.5;
  }

  .empty-text {
    color: var(--iot-color-text-muted);
    font-size: 14px;
  }
}
</style>
