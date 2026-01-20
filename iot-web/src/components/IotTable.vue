<script setup>
import { computed, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const props = defineProps({
  // 数据
  data: {
    type: Array,
    default: () => [],
  },
  // 列配置
  columns: {
    type: Array,
    default: () => [],
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
})

const emit = defineEmits(['page-change', 'size-change', 'selection-change'])

const innerPage = ref(props.currentPage)
const innerSize = ref(props.pageSize)

const pageChange = (page) => {
  innerPage.value = page
  emit('page-change', page)
}

const sizeChange = (size) => {
  innerSize.value = size
  emit('size-change', size)
}

const selectionChange = (selection) => {
  emit('selection-change', selection)
}

// 格式化列配置
const formatColumns = computed(() => {
  return props.columns.map(col => {
    const column = { ...col }

    // 如果有render函数,使用slot
    if (column.render && typeof column.render === 'function') {
      column.slotName = column.prop
    }

    return column
  })
})

// 渲染函数
const renderCell = (row, column, renderFn) => {
  if (typeof renderFn === 'function') {
    return renderFn({ row, column })
  }
  return row[column.prop]
}
</script>

<template>
  <div class="iot-table-wrapper">
    <el-table
      :data="data"
      :loading="loading"
      stripe
      style="width: 100%"
      @selection-change="selectionChange"
    >
      <el-table-column
        v-if="columns.some(col => col.type === 'selection')"
        type="selection"
        width="55"
      />
      <el-table-column
        v-for="column in formatColumns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
        :min-width="column.minWidth"
        :fixed="column.fixed"
        :align="column.align || 'left'"
      >
        <template #default="{ row, $index }">
          <slot
            v-if="column.slotName"
            :name="column.slotName"
            :row="row"
            :column="column"
            :index="$index"
          >
            {{ renderCell(row, column, column.render) }}
          </slot>
          <span v-else>{{ row[column.prop] }}</span>
        </template>
      </el-table-column>
      <slot name="operations" />
    </el-table>

    <div v-if="total > 0" class="iot-table-pagination">
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
  </div>
</template>

<style lang="scss" scoped>
.iot-table-wrapper {
  background: white;
  border-radius: 12px;
  padding: var(--space-lg);
  box-shadow: var(--shadow-md);
}

:deep(.el-table) {
  font-size: 14px;

  .el-table__header-wrapper {
    th {
      background: #F8FAFC;
      color: var(--iot-color-text);
      font-weight: 600;
      font-family: 'Fira Code', monospace;
    }
  }

  .el-table__body-wrapper {
    .el-table__row {
      &:hover {
        background: #F8FAFC;
      }
    }
  }
}

.iot-table-pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--space-lg);
  padding-top: var(--space-md);
  border-top: 1px solid #E2E8F0;
}

:deep(.el-pagination) {
  .el-pagination__total {
    color: #64748B;
  }

  .btn-prev,
  .btn-next,
  .el-pager li {
    background: white;
    border: 1px solid #E2E8F0;
    border-radius: 6px;
    margin: 0 2px;

    &.active {
      background: var(--iot-color-primary);
      border-color: var(--iot-color-primary);
      color: white;
    }
  }
}
</style>
