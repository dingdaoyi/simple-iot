<script setup>
import { getConnectionTypeEnum, getDriverTypeEnum } from '@/api/dict'
import { addDriver, deleteDriver, getDriverList, updateDriver } from '@/api/driver'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref, watch } from 'vue'

const driverList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = ref({})
const formRules = ref({
  name: [
    { required: true, message: '请输入驱动名称', trigger: 'blur' },
  ],
  type: [
    { required: true, message: '请选择驱动类型', trigger: 'change' },
  ],
  connectionType: [
    { required: true, message: '请选择连接类型', trigger: 'change' },
  ],
})
const driverTypeOptions = ref([])
const connectionTypeOptions = ref([])
const formRef = ref()

function fetchList() {
  getDriverList().then((res) => {
    driverList.value = res.data
  }).catch(() => {
    ElMessage.error('获取驱动列表失败')
  })
}

function handleAdd() {
  dialogTitle.value = '新增驱动'
  form.value = { status: 1, connectionType: 'DEFAULT', port: null }
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑驱动'
  form.value = { ...row, port: row.port || null }
  dialogVisible.value = true
}

function handleSave() {
  formRef.value.validate((valid) => {
    if (!valid) {
      return
    }

    // 验证端口号
    if (form.value.port && (form.value.port < 1 || form.value.port > 65535)) {
      ElMessage.error('端口号必须在1-65535之间')
      return
    }

    // 验证TCP/UDP驱动必须配置端口
    if ((form.value.type === 'TCP' || form.value.type === 'UDP') && !form.value.port) {
      ElMessage.error('TCP/UDP驱动必须配置端口')
      return
    }

    if (form.value.driverId) {
      updateDriver(form.value).then(() => {
        ElMessage.success('修改成功')
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error('修改失败')
      })
    }
    else {
      addDriver(form.value).then(() => {
        ElMessage.success('新增成功')
        dialogVisible.value = false
        form.value = {}
        fetchList()
      }).catch(() => {
        ElMessage.error('新增失败')
      })
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该驱动吗？', '提示', {
    type: 'warning',
  }).then(() => {
    deleteDriver(row.driverId).then(() => {
      ElMessage.success('删除成功')
      fetchList()
    }).catch(() => {
      ElMessage.error('删除失败')
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
})

fetchList()
</script>

<template>
  <div class="app-container">
    <el-button type="primary" @click="handleAdd">
      新增驱动
    </el-button>
    <el-table :data="driverList" style="width: 100%; margin-top: 20px;">
      <el-table-column prop="driverId" label="ID" width="80" />
      <el-table-column prop="name" label="驱动名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="connectionType" label="连接类型" />
      <el-table-column prop="port" label="端口" width="100">
        <template #default="scope">
          {{ scope.row.port || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" />
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">
            编辑
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" :title="dialogTitle">
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px">
        <el-form-item label="驱动名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="驱动类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择">
            <el-option v-for="item in driverTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="连接类型" prop="connectionType">
          <el-select v-model="form.connectionType" placeholder="请选择">
            <el-option v-for="item in connectionTypeOptions" :key="item.code" :label="item.desc" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.type === 'TCP' || form.type === 'UDP'" label="端口">
          <el-input-number v-model="form.port" :min="1" :max="65535" placeholder="请输入端口号" />
          <div class="el-form-item__tip">
            TCP/UDP驱动需要配置监听端口
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" />
        </el-form-item>
        <el-form-item v-if="form.connectionType === 'CUSTOM'" label="JAR路径">
          <el-input v-model="form.jarPath" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="handleSave">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.el-form-item__tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}
</style>
