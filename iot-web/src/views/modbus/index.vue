<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { deviceListApi, productListApi } from '@/api/index'
import { modbusAddApi, modbusDeleteApi, modbusListApi, modbusTestApi, modbusUpdateApi } from '@/api/modbus'

const loading = ref(false)
const list = ref([])
const products = ref([])
const devices = ref([])
const dialogVisible = ref(false)
const editing = ref(false)

const form = reactive({
  id: null,
  deviceId: null,
  host: '',
  port: 502,
  unitId: 1,
  intervalMs: 5000,
  enabled: true,
  registerMapText: '[]',
})

async function loadData() {
  loading.value = true
  const [cfgRes, prodRes] = await Promise.all([
    modbusListApi({}),
    productListApi({}),
  ])
  list.value = (cfgRes.data || []).map(c => ({
    ...c,
    registerMap: typeof c.registerMap === 'string' ? JSON.parse(c.registerMap || '[]') : (c.registerMap || []),
  }))
  products.value = prodRes.data || []
  loading.value = false
}

async function loadDevices(productId) {
  if (productId) {
    const res = await deviceListApi({ productId })
    devices.value = res.data || []
  } else {
    // ponytail: no productId filter = load all, so the dropdown isn't empty on add
    const res = await deviceListApi({})
    devices.value = res.data || []
  }
}

function getDeviceName(deviceId) {
  const d = devices.value.find(d => d.id === deviceId)
  return d ? `${d.deviceName} (${d.deviceKey})` : deviceId
}

function onAdd() {
  editing.value = false
  Object.assign(form, {
    id: null,
    deviceId: null,
    host: '',
    port: 502,
    unitId: 1,
    intervalMs: 5000,
    enabled: true,
    registerMapText: '[]',
  })
  loadDevices()
  dialogVisible.value = true
}

function onEdit(row) {
  editing.value = true
  Object.assign(form, {
    id: row.id,
    deviceId: row.deviceId,
    host: row.host,
    port: row.port,
    unitId: row.unitId,
    intervalMs: row.intervalMs,
    enabled: row.enabled,
    registerMapText: JSON.stringify(row.registerMap || [], null, 2),
  })
  loadDevices()
  dialogVisible.value = true
}

async function onSubmit() {
  let parsedMap
  try {
    parsedMap = JSON.parse(form.registerMapText)
  }
  catch {
    ElMessage.error('寄存器映射 JSON 格式错误')
    return
  }
  const payload = {
    id: form.id,
    deviceId: form.deviceId,
    host: form.host,
    port: form.port,
    unitId: form.unitId,
    intervalMs: form.intervalMs,
    enabled: form.enabled,
    registerMap: JSON.stringify(parsedMap),
  }
  if (editing.value) {
    await modbusUpdateApi(payload)
  }
  else {
    await modbusAddApi(payload)
  }
  ElMessage.success(editing.value ? '修改成功' : '新增成功')
  dialogVisible.value = false
  loadData()
}

async function onDelete(id) {
  await ElMessageBox.confirm('确认删除此配置?', '提示', { type: 'warning' })
  await modbusDeleteApi(id)
  ElMessage.success('删除成功')
  loadData()
}

async function onTest(id) {
  await modbusTestApi(id)
  ElMessage.success('已触发轮询')
}

onMounted(loadData)
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          Modbus TCP
        </h1>
        <p class="page-subtitle">
          主从轮询：平台做 Master，定时读取寄存器映射到物模型属性
        </p>
      </div>
    </div>

    <div class="action-bar">
      <el-button type="primary" @click="onAdd">
        + 新增配置
      </el-button>
    </div>

    <div class="table-wrapper glass-card">
      <el-table v-loading="loading" :data="list" border>
        <el-table-column label="设备" min-width="140">
          <template #default="{ row }">
            {{ getDeviceName(row.deviceId) }}
          </template>
        </el-table-column>
        <el-table-column prop="host" label="主机" width="140" />
        <el-table-column prop="port" label="端口" width="70" />
        <el-table-column prop="unitId" label="单元ID" width="70" />
        <el-table-column prop="intervalMs" label="间隔(ms)" width="90" />
        <el-table-column label="映射数" width="70">
          <template #default="{ row }">
            {{ (row.registerMap || []).length }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" size="small">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" type="primary" @click="onEdit(row)">
              编辑
            </el-button>
            <el-button link size="small" type="success" @click="onTest(row.id)">
              测试
            </el-button>
            <el-button link size="small" type="danger" @click="onDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑配置' : '新增配置'" width="700px" destroy-on-close>
      <el-form label-width="100px">
        <el-form-item label="设备">
          <el-select v-model="form.deviceId" placeholder="选择设备" filterable style="width: 100%">
            <el-option v-for="d in devices" :key="d.id" :label="`${d.deviceName} (${d.deviceKey})`" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="主机地址">
          <el-input v-model="form.host" placeholder="如 192.168.1.100" />
        </el-form-item>
        <el-form-item label="端口">
          <el-input-number v-model="form.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="单元ID">
          <el-input-number v-model="form.unitId" :min="1" :max="247" />
        </el-form-item>
        <el-form-item label="轮询间隔(ms)">
          <el-input-number v-model="form.intervalMs" :min="500" :step="1000" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="寄存器映射">
          <el-input
            v-model="form.registerMapText"
            type="textarea"
            :rows="10"
            placeholder="[{&quot;identifier&quot;:&quot;temperature&quot;,&quot;function&quot;:3,&quot;address&quot;:0,&quot;count&quot;:1,&quot;dataType&quot;:&quot;int16&quot;,&quot;scale&quot;:0.1}]"
          />
          <div style="margin-top: 4px; font-size: 12px; color: #909399;">
            JSON 数组，每项: identifier(物模型标识符), function(3=保持/4=输入), address(起始地址), count(寄存器数), dataType(int16/int32/float32/float64/bool), scale(缩放系数)
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="onSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
