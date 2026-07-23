<script setup>
import { Delete, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { imPushAddApi, imPushDeleteApi, imPushListApi, imPushUpdateApi } from '@/api/index.js'
import PageHeader from '@/components/PageHeader.vue'
import { ChatDotRound } from '@element-plus/icons-vue'

const { t } = useI18n()
const loading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const editing = ref(false)
const form = ref({ name: '', platform: 'DINGTALK', webhookUrl: '', enabled: true })

const platformOptions = [
  { label: '钉钉', value: 'DINGTALK' },
  { label: '企业微信', value: 'WECOM' },
  { label: '飞书', value: 'FEISHU' },
]

async function loadList() {
  loading.value = true
  try {
    const { data } = await imPushListApi()
    list.value = data || []
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

function onAdd() {
  editing.value = false
  form.value = { name: '', platform: 'DINGTALK', webhookUrl: '', enabled: true }
  dialogVisible.value = true
}

function onEdit(row) {
  editing.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function onSave() {
  if (!form.value.name || !form.value.webhookUrl) {
    ElMessage.warning('名称和 Webhook URL 不能为空')
    return
  }
  try {
    if (editing.value) {
      await imPushUpdateApi(form.value)
    } else {
      await imPushAddApi(form.value)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadList()
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确认删除「${row.name}」？`, '提示', { type: 'warning' })
  await imPushDeleteApi(row.id)
  ElMessage.success('已删除')
  loadList()
}

onMounted(loadList)
</script>

<template>
  <div class="im-push-page">
    <PageHeader title="IM 推送配置" subtitle="钉钉 / 企业微信 / 飞书 Webhook 推送" :icon="ChatDotRound" />
    <div class="table-wrapper glass-card">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="onAdd">新增配置</el-button>
      </div>
      <el-table :data="list" v-loading="loading" style="width: 100%">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="platform" label="平台" width="120">
          <template #default="{ row }">
            <el-tag>{{ platformOptions.find(p => p.value === row.platform)?.label || row.platform }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="webhookUrl" label="Webhook URL" show-overflow-tooltip />
        <el-table-column prop="enabled" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="onEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="editing ? '编辑配置' : '新增配置'" width="520px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="名称">
          <el-input v-model="form.name" placeholder="如：运维告警群" />
        </el-form-item>
        <el-form-item label="平台">
          <el-select v-model="form.platform" style="width: 100%">
            <el-option v-for="p in platformOptions" :key="p.value" :label="p.label" :value="p.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="Webhook URL">
          <el-input v-model="form.webhookUrl" placeholder="https://oapi.dingtalk.com/robot/send?access_token=..." />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.enabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.im-push-page {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
  padding: var(--space-xl);
  min-height: 100vh;
}
.table-toolbar {
  margin-bottom: var(--space-md);
}
</style>
