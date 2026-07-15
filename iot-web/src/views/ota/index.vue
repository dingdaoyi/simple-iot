<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref, watch } from 'vue'
import { deviceGroupTreeApi } from '@/api/deviceGroup'
import { deviceListApi, productListApi } from '@/api/index'
import { firmwareDeleteApi, firmwareListApi, firmwarePublishApi, firmwareUploadApi, otaTaskCreateApi, otaTaskGetApi, otaTaskListApi } from '@/api/ota'

const activeTab = ref('firmware')
const loading = ref(false)

// === Firmware ===
const firmwareList = ref([])
const uploadVisible = ref(false)
const uploadForm = ref({ productId: null, name: '', version: '', description: '', file: null })
const products = ref([])

// === Tasks ===
const taskList = ref([])
const taskDialogVisible = ref(false)
const taskForm = ref({ firmwareId: null, groupId: null, deviceIds: [] })
const taskDetailVisible = ref(false)
const taskDetail = ref(null)
const groups = ref([])
const devices = ref([])

async function loadProducts() {
  const res = await productListApi({})
  products.value = res.data || []
}

async function loadFirmware() {
  loading.value = true
  const res = await firmwareListApi()
  firmwareList.value = res.data || []
  loading.value = false
}

async function loadGroups() {
  const res = await deviceGroupTreeApi()
  groups.value = res.data || []
}

async function loadDevices(productId) {
  if (!productId) {
    devices.value = []
    return
  }
  const res = await deviceListApi({ productId })
  devices.value = res.data || []
}

// ponytail: load devices for task dialog when firmware product changes
const selectedFirmware = computed(() => firmwareList.value.find(f => f.id === taskForm.value.firmwareId))
watch(() => taskForm.value.firmwareId, () => {
  if (selectedFirmware.value)
    loadDevices(selectedFirmware.value.productId)
})

function onUploadFile(file) {
  uploadForm.value.file = file.raw
  return false
}

async function onSubmitUpload() {
  if (!uploadForm.value.file || !uploadForm.value.productId || !uploadForm.value.name || !uploadForm.value.version) {
    ElMessage.warning('请填写完整信息并选择文件')
    return
  }
  const fd = new FormData()
  fd.append('file', uploadForm.value.file)
  fd.append('productId', uploadForm.value.productId)
  fd.append('name', uploadForm.value.name)
  fd.append('version', uploadForm.value.version)
  fd.append('description', uploadForm.value.description || '')
  await firmwareUploadApi(fd)
  ElMessage.success('上传成功')
  uploadVisible.value = false
  uploadForm.value = { productId: null, name: '', version: '', description: '', file: null }
  loadFirmware()
}

async function onPublish(id) {
  await ElMessageBox.confirm('确认发布该固件?', '提示', { type: 'warning' })
  await firmwarePublishApi(id)
  ElMessage.success('已发布')
  loadFirmware()
}

async function onDelete(id) {
  await ElMessageBox.confirm('确认删除该固件?', '提示', { type: 'warning' })
  await firmwareDeleteApi(id)
  ElMessage.success('已删除')
  loadFirmware()
}

// === Tasks ===
async function loadTasks() {
  loading.value = true
  const res = await otaTaskListApi()
  taskList.value = res.data || []
  loading.value = false
}

function openCreateTask(firmwareId) {
  taskForm.value = { firmwareId, groupId: null, deviceIds: [] }
  taskDialogVisible.value = true
}

async function onSubmitTask() {
  const params = { firmwareId: taskForm.value.firmwareId }
  if (taskForm.value.groupId)
    params.groupId = taskForm.value.groupId
  if (taskForm.value.deviceIds.length)
    params.deviceIds = taskForm.value.deviceIds
  await otaTaskCreateApi(params)
  ElMessage.success('升级任务已创建')
  taskDialogVisible.value = false
  loadTasks()
}

async function onTaskDetail(id) {
  const res = await otaTaskGetApi(id)
  taskDetail.value = res.data
  taskDetailVisible.value = true
}

const firmwareName = computed(() => id => firmwareList.value.find(f => f.id === id)?.name || `#${id}`)

onMounted(() => {
  loadProducts()
  loadFirmware()
  loadTasks()
  loadGroups()
})
</script>

<template>
  <div class="ota-page">
    <el-tabs v-model="activeTab" class="glass-card">
      <!-- Firmware Tab -->
      <el-tab-pane label="固件管理" name="firmware">
        <div style="margin-bottom: 12px; display: flex; justify-content: space-between; align-items: center;">
          <span style="font-size: 14px; color: var(--iot-color-text-muted);">固件包列表</span>
          <el-button type="primary" size="small" @click="uploadVisible = true">
            上传固件
          </el-button>
        </div>
        <el-table v-loading="loading" :data="firmwareList" stripe>
          <el-table-column prop="name" label="名称" min-width="120" />
          <el-table-column prop="version" label="版本" width="80" />
          <el-table-column label="产品" width="120">
            <template #default="{ row }">
              {{ products.find(p => p.id === row.productId)?.model || row.productId }}
            </template>
          </el-table-column>
          <el-table-column prop="fileSize" label="大小" width="80">
            <template #default="{ row }">
              {{ row.fileSize ? `${(row.fileSize / 1024).toFixed(0)}KB` : '-' }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status !== 'PUBLISHED'" link size="small" type="primary" @click="onPublish(row.id)">
                发布
              </el-button>
              <el-button v-if="row.status === 'PUBLISHED'" link size="small" type="success" @click="openCreateTask(row.id)">
                推送升级
              </el-button>
              <el-button link size="small" type="danger" @click="onDelete(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <!-- Task Tab -->
      <el-tab-pane label="升级任务" name="task">
        <el-table v-loading="loading" :data="taskList" stripe>
          <el-table-column label="固件" width="120">
            <template #default="{ row }">
              {{ firmwareName(row.firmwareId) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'COMPLETED' ? 'success' : row.status === 'RUNNING' ? 'warning' : 'info'" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="total" label="总数" width="60" />
          <el-table-column prop="success" label="成功" width="60" />
          <el-table-column prop="fail" label="失败" width="60" />
          <el-table-column prop="createTime" label="创建时间" width="160" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button link size="small" type="primary" @click="onTaskDetail(row.id)">
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- Upload Dialog -->
    <el-dialog v-model="uploadVisible" title="上传固件" width="500px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="产品">
          <el-select v-model="uploadForm.productId" placeholder="选择产品" filterable style="width: 100%">
            <el-option v-for="p in products" :key="p.id" :label="p.model" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="uploadForm.name" placeholder="如: 传感器固件 v2" />
        </el-form-item>
        <el-form-item label="版本">
          <el-input v-model="uploadForm.version" placeholder="如: 1.2.0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="uploadForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="固件文件">
          <el-upload :auto-upload="false" :on-change="onUploadFile" :limit="1">
            <el-button type="primary" size="small">
              选择文件
            </el-button>
            <template #tip>
              <div style="font-size: 12px; color: var(--iot-color-text-muted);">
                支持 .bin .hex .img 等格式
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="onSubmitUpload">
          确认上传
        </el-button>
      </template>
    </el-dialog>

    <!-- Create Task Dialog -->
    <el-dialog v-model="taskDialogVisible" title="推送升级" width="500px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="固件">
          <span>{{ firmwareName(taskForm.firmwareId) }}</span>
        </el-form-item>
        <el-form-item label="目标分组">
          <el-select v-model="taskForm.groupId" placeholder="选择分组(可选)" clearable style="width: 100%">
            <el-option v-for="g in groups" :key="g.id" :label="g.name" :value="g.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="指定设备">
          <el-select v-model="taskForm.deviceIds" placeholder="选择设备" multiple filterable style="width: 100%" :disabled="!!taskForm.groupId">
            <el-option v-for="d in devices" :key="d.id" :label="`${d.deviceName} (${d.deviceKey})`" :value="d.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="onSubmitTask">
          确认推送
        </el-button>
      </template>
    </el-dialog>

    <!-- Task Detail Dialog -->
    <el-dialog v-model="taskDetailVisible" title="升级任务详情" width="600px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="固件">
          {{ firmwareName(taskDetail?.firmwareId) }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          {{ taskDetail?.status }}
        </el-descriptions-item>
        <el-descriptions-item label="总数">
          {{ taskDetail?.total }}
        </el-descriptions-item>
        <el-descriptions-item label="成功">
          {{ taskDetail?.success }}
        </el-descriptions-item>
        <el-descriptions-item label="失败">
          {{ taskDetail?.fail }}
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ taskDetail?.createTime }}
        </el-descriptions-item>
      </el-descriptions>
      <div v-if="taskDetail?.progress" style="margin-top: 12px;">
        <div style="font-size: 13px; margin-bottom: 8px;">
          设备进度
        </div>
        <el-table :data="Object.entries(taskDetail.progress).map(([k, v]) => ({ deviceId: k, status: v }))" stripe size="small">
          <el-table-column prop="deviceId" label="设备ID" width="100" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="row.status === 'SUCCESS' ? 'success' : row.status === 'FAIL' ? 'danger' : 'warning'" size="small">
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.ota-page {
  padding: 8px;
  height: 100%;
}
</style>
