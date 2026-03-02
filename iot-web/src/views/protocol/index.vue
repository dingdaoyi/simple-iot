<script setup>
import { h } from 'vue'
import { ElTag, ElMessage, ElMessageBox } from 'element-plus'
import { onMounted } from 'vue'
import { protocolDeleteApi, protocolListApi, protocolSetStatusApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import { useTable } from '@/composables/useTable.js'
import EditDia from '@/views/protocol/widget/editDia.vue'

const protocolTypeOpt = [
  {
    label: 'JAVA',
    value: 1,
    color: 'warning',
  },
  {
    label: '系统默认',
    value: 2,
    color: 'info',
  },
  {
    label: 'JAVASCRIPT',
    value: 3,
    color: 'success',
  },
]

const scriptLangOpt = {
  javascript: { label: 'JavaScript', icon: '📜' },
  python: { label: 'Python', icon: '🐍' },
  groovy: { label: 'Groovy', icon: '☕' },
  lua: { label: 'Lua', icon: '🌙' },
}

const column = [
  {
    prop: 'name',
    label: '协议名称',
    width: 180,
    fixed: 'left',
  },
  {
    prop: 'status',
    label: '状态',
    width: 100,
    render({ row }) {
      return h(ElTag, { type: row.status === 1 ? 'success' : 'info' }, () => row.status === 1 ? '已启用' : '已禁用')
    },
  },
  {
    prop: 'protoType',
    label: '协议类型',
    width: 120,
    render({ row }) {
      const opt = protocolTypeOpt.find(item => item.value === row.protoType)
      return opt ? h(ElTag, { type: opt.color }, () => opt.label) : '-'
    },
  },
  {
    prop: 'protoKey',
    label: '协议 Key',
    width: 160,
  },
  {
    prop: 'scriptLang',
    label: '脚本语言',
    width: 120,
    render({ row }) {
      if (row.protoType !== 3) return '-'
      const lang = scriptLangOpt[row.scriptLang]
      return lang ? `${lang.icon} ${lang.label}` : '-'
    },
  },
  {
    prop: 'mark',
    label: '备注说明',
    minWidth: 150,
    showOverflowTooltip: true,
  },
  {
    prop: 'cz',
    slot: 'cz',
    width: 200,
    fixed: 'right',
    label: '操作',
  },
]

const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  tableData,
  total,
  loading,
  onDelete,
  onAdd,
  diaTitle,
  currentItem,
  onPageChange,
  onSizeChange,
} = useTable({
  deleteApi: protocolDeleteApi,
  fetchApi: protocolListApi,
  diaName: '协议',
  defParams: {},
})

function closeEdite() {
  updatePage()
}

function onEdit(row) {
  currentItem.value = row
  dialogVisible.value = true
}

async function toggleStatus(row) {
  const newStatus = row.status !== 1
  const action = newStatus ? '启用' : '禁用'

  try {
    await ElMessageBox.confirm(
      `确认要${action}协议 "${row.name}" 吗？${newStatus ? '启用后协议将被加载到内存中。' : '禁用后协议将从内存中卸载。'}`,
      '操作确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      },
    )
    await protocolSetStatusApi(row.id, newStatus)
    row.status = newStatus ? 1 : 2
    ElMessage.success(`协议已${action}`)
  }
  catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`操作失败: ${error.message || '未知错误'}`)
    }
  }
}

onMounted(() => {
  updatePage()
})
</script>

<template>
  <div class="protocol-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <span class="title-icon">◊</span>
          协议管理
        </h1>
        <p class="page-subtitle">
          管理设备通信协议，支持 Java、JavaScript 等多种协议类型
        </p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar glass-card">
      <el-form :inline="false" class="search-form">
        <div class="form-row">
          <el-form-item label="协议类型">
            <el-select
              v-model="params.protoType"
              placeholder="选择协议类型"
              filterable
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="item in protocolTypeOpt"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="脚本语言">
            <el-select
              v-model="params.scriptLang"
              placeholder="选择脚本语言"
              filterable
              clearable
              style="width: 150px"
            >
              <el-option
                v-for="(lang, key) in scriptLangOpt"
                :key="key"
                :label="lang.label"
                :value="key"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="协议名称">
            <el-input
              v-model="params.name"
              clearable
              placeholder="输入协议名称"
              style="width: 200px"
            />
          </el-form-item>

          <el-form-item label="协议 Key">
            <el-input
              v-model="params.protoKey"
              clearable
              placeholder="输入协议 Key"
              style="width: 180px"
            />
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" @click="onSearch">
            <span class="btn-icon">⌕</span>
            搜索
          </el-button>
          <el-button type="success" @click="onAdd">
            <span class="btn-icon">+</span>
            添加协议
          </el-button>
        </div>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper glass-card">
      <IotTable
        :columns="column"
        :data="tableData"
        :total="total"
        :current-page="params.page"
        :page-size="params.size"
        :loading="loading"
        @page-change="onPageChange"
        @size-change="onSizeChange"
      >
        <template #cz="{ row }">
          <el-button
            :type="row.status === 1 ? 'warning' : 'success'"
            link
            @click="toggleStatus(row)"
          >
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
          <el-button type="primary" link @click="onEdit(row)">
            编辑
          </el-button>
          <el-button
            v-if="row.protoType !== 2"
            type="danger"
            link
            @click="onDelete(row)"
          >
            删除
          </el-button>
        </template>
      </IotTable>
    </div>

    <!-- 编辑对话框 -->
    <EditDia
      v-if="dialogVisible"
      v-model="dialogVisible"
      :title="diaTitle"
      :datas="currentItem"
      @update="closeEdite"
    />
  </div>
</template>

<style scoped lang="scss">
.protocol-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

/* 页面标题 */
.page-header {
  .header-content {
    background: var(--iot-glass-bg-dark);
    backdrop-filter: blur(20px);
    border: 1px solid var(--iot-glass-border);
    border-radius: var(--radius-lg);
    padding: var(--space-xl) var(--space-2xl);
    box-shadow: var(--shadow-md);
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 4px;
      background: linear-gradient(90deg, var(--iot-color-primary), var(--iot-color-accent));
    }
  }

  .page-title {
    font-size: 24px;
    font-weight: 700;
    margin: 0;
    display: flex;
    align-items: center;
    gap: var(--space-sm);
    color: var(--iot-color-text-primary);

    .title-icon {
      font-size: 28px;
      background: linear-gradient(135deg, var(--iot-color-primary), var(--iot-color-accent));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-subtitle {
    margin: var(--space-sm) 0 0 0;
    font-size: 14px;
    color: var(--iot-color-text-secondary);
  }
}

/* 搜索栏 */
.search-bar {
  .search-form {
    display: flex;
    flex-direction: column;
    gap: var(--space-md);
  }

  .form-row {
    display: flex;
    flex-wrap: wrap;
    gap: var(--space-md);

    .el-form-item {
      margin-bottom: 0;
    }
  }

  .form-actions {
    display: flex;
    gap: var(--space-sm);
    justify-content: flex-end;
  }

  .btn-icon {
    margin-right: var(--space-xs);
    font-size: 16px;
  }
}

/* 表格容器 */
.table-wrapper {
  flex: 1;
}

/* 响应式 */
@media (max-width: 768px) {
  .protocol-page {
    padding: var(--space-md);
  }

  .form-row {
    flex-direction: column;

    .el-form-item {
      width: 100%;

      :deep(.el-select),
      :deep(.el-input) {
        width: 100% !important;
      }
    }
  }

  .form-actions {
    width: 100%;
    flex-direction: column;

    .el-button {
      width: 100%;
    }
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>
