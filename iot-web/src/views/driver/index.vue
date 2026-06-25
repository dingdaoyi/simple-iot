<script setup>
import { useI18n } from 'vue-i18n'
import { Lightning } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref, watch } from 'vue'
import { getConnectionTypeEnum, getDriverTypeEnum } from '@/api/dict'
import { addDriver, deleteDriver, getDriverList, updateDriver } from '@/api/driver'

const { t } = useI18n()
const driverList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({})
const formRules = ref({
  name: [
    { required: true, message: t('auto.driver_index_19355234'), trigger: 'blur' },
  ],
  type: [
    { required: true, message: t('auto.driver_index_5b734c4d'), trigger: 'change' },
  ],
  connectionType: [
    { required: true, message: t('auto.driver_index_3854d851'), trigger: 'change' },
  ],
})
const driverTypeOptions = ref([])
const connectionTypeOptions = ref([])
const formRef = ref()

function fetchList() {
  getDriverList().then((res) => {
    driverList.value = res.data
  }).catch(() => {
    ElMessage.error(t('auto.driver_index_d132a3f0'))
  })
}

function handleAdd() {
  dialogTitle.value = t('auto.driver_index_58d6a32e')
  form.value = { status: 1, connectionType: 'DEFAULT', port: null }
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = t('auto.driver_index_c64a1c74')
  form.value = { ...row, port: row.port || null }
  dialogVisible.value = true
}

function handleSave() {
  formRef.value.validate((valid) => {
    if (!valid)
      return

    // 验证端口号
    if (form.value.port && (form.value.port < 1 || form.value.port > 65535)) {
      ElMessage.error(t('auto.driver_index_040f01f9'))
      return
    }

    // 验证TCP/UDP驱动必须配置端口
    if ((form.value.type === 'TCP' || form.value.type === 'UDP') && !form.value.port) {
      ElMessage.error(t('auto.driver_index_1017fe6e'))
      return
    }

    if (form.value.driverId) {
      updateDriver(form.value).then(() => {
        ElMessage.success(t('auto.driver_index_69be6717'))
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error(t('auto.driver_index_5badb3ba'))
      })
    }
    else {
      addDriver(form.value).then(() => {
        ElMessage.success(t('auto.driver_index_a5bfd70d'))
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error(t('auto.driver_index_bac372f6'))
      })
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(t('auto.driver_index_c8979982'), t('auto.driver_index_02d9819d'), {
    type: 'warning',
  }).then(() => {
    deleteDriver(row.driverId).then(() => {
      ElMessage.success(t('auto.driver_index_0007d170'))
      fetchList()
    }).catch(() => {
      ElMessage.error(t('auto.driver_index_acf0664a'))
    })
  })
}

onMounted(() => {
  getDriverTypeEnum().then((res) => {
    driverTypeOptions.value = res.data
  })
  getConnectionTypeEnum().then((res) => {
    connectionTypeOptions.value = res.data
  })
  fetchList()
})

// 监听驱动类型变化
watch(() => form.value.type, (newType) => {
  if (newType !== 'TCP' && newType !== 'UDP') {
    form.value.port = null
  }
})

// 监听对话框关闭
watch(dialogVisible, (visible) => {
  if (!visible) {
    form.value = {}
    formRef.value?.clearValidate()
  }
})</script>

<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          <el-icon :size="24" class="title-icon">
            <Lightning />
          </el-icon>
          {{ t('auto.driver_index_a7f61b5e') }}
        </h1>
        <p class="page-subtitle">
          {{ t('auto.driver_index_9ffc10bc') }}
        </p>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <span class="btn-icon">+</span>
        {{ t('auto.driver_index_58d6a32e') }}
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper glass-card">
      <el-table :data="driverList" style="width: 100%">
        <el-table-column prop="driverId" label="ID" width="80" />
        <el-table-column prop="name" :label="t('auto.driver_index_6a0c8d88')" />
        <el-table-column prop="type" :label="t('auto.driver_index_226b0912')">
          <template #default="{ row }">
            <el-tag :type="row.type === 'TCP' ? 'primary' : row.type === 'UDP' ? 'success' : 'warning'">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="connectionType" :label="t('auto.driver_index_d9e99fc2')" />
        <el-table-column prop="port" :label="t('auto.driver_index_c76cfefe')" width="100">
          <template #default="{ row }">
            {{ row.port || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="t('auto.driver_index_3bdd08ad')" />
        <el-table-column prop="status" :label="t('auto.driver_index_3fea7ca7')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? t('auto.driver_index_7854b52a') : t('auto.driver_index_710ad08b') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('auto.driver_index_2b6bc0f2')" width="200">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="handleEdit(row)">
              {{ t('auto.driver_index_95b351c8') }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">
              {{ t('auto.driver_index_2f4aaddd') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item :label="t('auto.driver_index_6a0c8d88')" prop="name">
          <el-input v-model="form.name" :placeholder="t('auto.driver_index_19355234')" />
        </el-form-item>
        <el-form-item :label="t('auto.driver_index_f985a467')" prop="type">
          <el-select v-model="form.type" :placeholder="t('auto.driver_index_5b734c4d')" style="width: 100%">
            <el-option v-for="item in driverTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('auto.driver_index_d9e99fc2')" prop="connectionType">
          <el-select v-model="form.connectionType" :placeholder="t('auto.driver_index_3854d851')" style="width: 100%">
            <el-option v-for="item in connectionTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'TCP' || form.type === 'UDP'" :label="t('auto.driver_index_c76cfefe')" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" :placeholder="t('auto.driver_index_8d0fa2ee')" style="width: 100%" />
          <div class="form-tip">
            {{ t('auto.driver_tcp_udp_port_tip') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('auto.driver_index_3bdd08ad')">
          <el-input v-model="form.description" type="textarea" :rows="3" :placeholder="t('auto.driver_index_a83eff2e')" />
        </el-form-item>
        <el-form-item v-if="form.connectionType === 'CUSTOM'" :label="t('auto.driver_index_60fa7788')">
          <el-input v-model="form.jarPath" :placeholder="t('auto.driver_index_e57a3204')" />
        </el-form-item>
        <el-form-item :label="t('auto.driver_index_3fea7ca7')">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            :active-text="t('auto.driver_index_7854b52a')"
            :inactive-text="t('auto.driver_index_710ad08b')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          {{ t('auto.driver_index_625fb26b') }}
        </el-button>
        <el-button type="primary" @click="handleSave">
          {{ t('auto.driver_index_be5fbbe3') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.page-container {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
}

.table-wrapper {
  flex: 1;

  .el-table {
    background: transparent;

    :deep(.el-table__header-wrapper) {
      th {
        background: rgba(99, 102, 241, 0.08);
        color: var(--iot-color-text-primary);
        font-weight: 600;
        font-family: 'Fira Code', monospace;
        border-bottom: 1px solid rgba(99, 102, 241, 0.15);
      }
    }

    :deep(.el-table__body-wrapper) {
      .el-table__row {
        transition: all var(--transition-fast);

        &:hover {
          background: rgba(99, 102, 241, 0.06);
        }
      }
    }
  }
}

.form-tip {
  font-size: 12px;
  color: var(--iot-color-text-muted);
  margin-top: var(--space-xs);
  line-height: 1.4;
}

.title-icon {
  color: var(--iot-color-primary);
  margin-right: var(--space-sm);
}

/* 响应式 */
@media (max-width: 768px) {
  .page-container {
    padding: var(--space-md);
  }

  .page-title {
    font-size: 20px !important;
  }
}
</style>