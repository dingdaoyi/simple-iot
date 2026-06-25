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
    { required: true, message: t('driver.driver_page'), trigger: 'blur' },
  ],
  type: [
    { required: true, message: t('driver.select_driver_type'), trigger: 'change' },
  ],
  connectionType: [
    { required: true, message: t('driver.select_connection_type'), trigger: 'change' },
  ],
})
const driverTypeOptions = ref([])
const connectionTypeOptions = ref([])
const formRef = ref()

function fetchList() {
  getDriverList().then((res) => {
    driverList.value = res.data
  }).catch(() => {
    ElMessage.error(t('driver.failed_fetch_driver_list'))
  })
}

function handleAdd() {
  dialogTitle.value = t('driver.add_driver')
  form.value = { status: 1, connectionType: 'DEFAULT', port: null }
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = t('driver.edit_driver')
  form.value = { ...row, port: row.port || null }
  dialogVisible.value = true
}

function handleSave() {
  formRef.value.validate((valid) => {
    if (!valid)
      return

    // 验证端口号
    if (form.value.port && (form.value.port < 1 || form.value.port > 65535)) {
      ElMessage.error(t('driver.text'))
      return
    }

    // 验证TCP/UDP驱动必须配置端口
    if ((form.value.type === 'TCP' || form.value.type === 'UDP') && !form.value.port) {
      ElMessage.error(t('driver.tcp_udp'))
      return
    }

    if (form.value.driverId) {
      updateDriver(form.value).then(() => {
        ElMessage.success(t('driver.update_success'))
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error(t('driver.update_failed'))
      })
    }
    else {
      addDriver(form.value).then(() => {
        ElMessage.success(t('driver.create_success'))
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error(t('driver.create_failed'))
      })
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(t('driver.you_sure_you_want_delete_this_driver'), t('common.tip'), {
    type: 'warning',
  }).then(() => {
    deleteDriver(row.driverId).then(() => {
      ElMessage.success(t('driver.delete_success'))
      fetchList()
    }).catch(() => {
      ElMessage.error(t('driver.delete_failed'))
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
          {{ t('driver.driver') }}
        </h1>
        <p class="page-subtitle">
          {{ t('driver.manage_configure_device_communication_drivers') }}
        </p>
      </div>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <span class="btn-icon">+</span>
        {{ t('driver.add_driver') }}
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper glass-card">
      <el-table :data="driverList" style="width: 100%">
        <el-table-column prop="driverId" label="ID" width="80" />
        <el-table-column prop="name" :label="t('driver.driver_name')" />
        <el-table-column prop="type" :label="t('common.type')">
          <template #default="{ row }">
            <el-tag :type="row.type === 'TCP' ? 'primary' : row.type === 'UDP' ? 'success' : 'warning'">
              {{ row.type }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="connectionType" :label="t('driver.connection_type')" />
        <el-table-column prop="port" :label="t('common.port')" width="100">
          <template #default="{ row }">
            {{ row.port || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="description" :label="t('common.description')" />
        <el-table-column prop="status" :label="t('common.status')" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? t('common.enable') : t('driver.disable') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('common.operation')" width="200">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="handleEdit(row)">
              {{ t('common.edit') }}
            </el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row)">
              {{ t('common.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item :label="t('driver.driver_name')" prop="name">
          <el-input v-model="form.name" :placeholder="t('driver.driver_page')" />
        </el-form-item>
        <el-form-item :label="t('driver.driver_type')" prop="type">
          <el-select v-model="form.type" :placeholder="t('driver.select_driver_type')" style="width: 100%">
            <el-option v-for="item in driverTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('driver.connection_type')" prop="connectionType">
          <el-select v-model="form.connectionType" :placeholder="t('driver.select_connection_type')" style="width: 100%">
            <el-option v-for="item in connectionTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'TCP' || form.type === 'UDP'" :label="t('common.port')" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" :placeholder="t('driver.enter_port')" style="width: 100%" />
          <div class="form-tip">
            {{ t('driver.tcp_udp_drivers_require_listening_port_1_65535') }}
          </div>
        </el-form-item>
        <el-form-item :label="t('common.description')">
          <el-input v-model="form.description" type="textarea" :rows="3" :placeholder="t('driver.enter_driver_description')" />
        </el-form-item>
        <el-form-item v-if="form.connectionType === 'CUSTOM'" :label="t('driver.jar_path')">
          <el-input v-model="form.jarPath" :placeholder="t('driver.enter_jar_file_path')" />
        </el-form-item>
        <el-form-item :label="t('common.status')">
          <el-switch
            v-model="form.status"
            :active-value="1"
            :inactive-value="0"
            :active-text="t('common.enable')"
            :inactive-text="t('driver.disable')"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="handleSave">
          {{ t('common.save') }}
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