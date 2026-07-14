<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  deviceGroupAddApi,
  deviceGroupAssignApi,
  deviceGroupDelApi,
  deviceGroupDevicesApi,
  deviceGroupEditApi,
  deviceGroupTreeApi,
  deviceTagAddApi,
  deviceTagAllApi,
  deviceTagDelApi,
  deviceTagEditApi,
} from '@/api/deviceGroup'
import { deviceListApi } from '@/api/index'

const { t } = useI18n()

// === Group ===
const groupTree = ref([])
const groupLoading = ref(false)
const groupDialogVisible = ref(false)
const groupForm = ref({ name: '', parentId: -1, description: '' })
const editingGroup = ref(false)

// === Tag ===
const tags = ref([])
const tagLoading = ref(false)
const tagDialogVisible = ref(false)
const tagForm = ref({ name: '', color: '#6366f1' })
const editingTag = ref(false)

// === Assign ===
const assignDialogVisible = ref(false)
const assignType = ref('group') // 'group' | 'tag'
const assignTargetId = ref(null)
const assignDeviceIds = ref([])
const deviceOptions = ref([])
const assignedDeviceIds = ref([])

async function loadGroups() {
  groupLoading.value = true
  try {
    const res = await deviceGroupTreeApi()
    groupTree.value = res.data || []
  }
  finally {
    groupLoading.value = false
  }
}

async function loadTags() {
  tagLoading.value = true
  try {
    const res = await deviceTagAllApi()
    tags.value = res.data || []
  }
  finally {
    tagLoading.value = false
  }
}

function onAddGroup(parentId = -1) {
  editingGroup.value = false
  groupForm.value = { name: '', parentId, description: '' }
  groupDialogVisible.value = true
}

function onEditGroup(node) {
  editingGroup.value = true
  groupForm.value = { ...node }
  groupDialogVisible.value = true
}

async function onSubmitGroup() {
  if (!groupForm.value.name) {
    ElMessage.warning(t('validate.nameRequired'))
    return
  }
  if (editingGroup.value) {
    await deviceGroupEditApi(groupForm.value)
  }
  else {
    await deviceGroupAddApi(groupForm.value)
  }
  ElMessage.success(t('common.success'))
  groupDialogVisible.value = false
  loadGroups()
}

async function onDeleteGroup(node) {
  await ElMessageBox.confirm(t('deviceGroup.deleteConfirm'), t('common.confirm'), { type: 'warning' })
  await deviceGroupDelApi(node.id)
  ElMessage.success(t('common.success'))
  loadGroups()
}

function onAddTag() {
  editingTag.value = false
  tagForm.value = { name: '', color: '#6366f1' }
  tagDialogVisible.value = true
}

function onEditTag(tag) {
  editingTag.value = true
  tagForm.value = { ...tag }
  tagDialogVisible.value = true
}

async function onSubmitTag() {
  if (!tagForm.value.name) {
    ElMessage.warning(t('validate.nameRequired'))
    return
  }
  if (editingTag.value) {
    await deviceTagEditApi(tagForm.value)
  }
  else {
    await deviceTagAddApi(tagForm.value)
  }
  ElMessage.success(t('common.success'))
  tagDialogVisible.value = false
  loadTags()
}

async function onDeleteTag(tag) {
  await ElMessageBox.confirm(t('deviceTag.deleteConfirm'), t('common.confirm'), { type: 'warning' })
  await deviceTagDelApi(tag.id)
  ElMessage.success(t('common.success'))
  loadTags()
}

async function onAssign(type, id) {
  assignType.value = type
  assignTargetId.value = id
  assignDeviceIds.value = []
  const [devices, assigned] = await Promise.all([
    deviceListApi(),
    type === 'group'
      ? deviceGroupDevicesApi(id)
      : (await import('@/api/deviceGroup')).deviceTagsByDeviceApi ? [] : [],
  ])
  deviceOptions.value = devices.data || []
  if (type === 'group') {
    assignedDeviceIds.value = assigned.data || []
  }
  assignDeviceIds.value = [...assignedDeviceIds.value]
  assignDialogVisible.value = true
}

async function onSubmitAssign() {
  const fn = assignType.value === 'group' ? deviceGroupAssignApi : null
  if (fn) {
    await fn(assignTargetId.value, assignDeviceIds.value)
  }
  ElMessage.success(t('common.success'))
  assignDialogVisible.value = false
}

onMounted(() => {
  loadGroups()
  loadTags()
})
</script>

<template>
  <div class="page-container">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">
          {{ t('deviceGroup.title') }}
        </h1>
        <p class="page-subtitle">
          {{ t('deviceGroup.subtitle') }}
        </p>
      </div>
    </div>

    <el-row :gutter="16">
      <!-- Group Tree -->
      <el-col :span="12">
        <div class="glass-card section-card">
          <div class="section-header">
            <span class="section-title">{{ t('deviceGroup.groups') }}</span>
            <el-button type="primary" size="small" @click="onAddGroup(-1)">
              + {{ t('common.add') }}
            </el-button>
          </div>
          <el-tree
            v-loading="groupLoading"
            :data="groupTree"
            node-key="id"
            :props="{ label: 'name', children: 'children' }"
            default-expand-all
          >
            <template #default="{ data }">
              <span class="tree-node">
                <span>{{ data.name }}</span>
                <span class="tree-actions">
                  <el-button link size="small" @click.stop="onAssign('group', data.id)">{{ t('deviceGroup.assignDevices') }}</el-button>
                  <el-button link size="small" type="primary" @click.stop="onAddGroup(data.id)">+</el-button>
                  <el-button link size="small" @click.stop="onEditGroup(data)">{{ t('common.edit') }}</el-button>
                  <el-button link size="small" type="danger" @click.stop="onDeleteGroup(data)">{{ t('common.delete') }}</el-button>
                </span>
              </span>
            </template>
          </el-tree>
        </div>
      </el-col>

      <!-- Tags -->
      <el-col :span="12">
        <div class="glass-card section-card">
          <div class="section-header">
            <span class="section-title">{{ t('deviceTag.tags') }}</span>
            <el-button type="primary" size="small" @click="onAddTag">
              + {{ t('common.add') }}
            </el-button>
          </div>
          <div v-loading="tagLoading" class="tag-list">
            <el-tag
              v-for="tag in tags"
              :key="tag.id"
              :color="tag.color"
              closable
              :style="{ color: '#fff', marginRight: '8px', marginBottom: '8px' }"
              @close="onDeleteTag(tag)"
              @click="onEditTag(tag)"
            >
              {{ tag.name }}
            </el-tag>
            <el-empty v-if="tags.length === 0" :description="t('common.empty')" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Group Dialog -->
    <el-dialog v-model="groupDialogVisible" :title="editingGroup ? t('common.edit') : t('common.add')" width="500px">
      <el-form :model="groupForm" label-width="100px">
        <el-form-item :label="t('deviceGroup.name')">
          <el-input v-model="groupForm.name" />
        </el-form-item>
        <el-form-item :label="t('deviceGroup.description')">
          <el-input v-model="groupForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="groupDialogVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="onSubmitGroup">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- Tag Dialog -->
    <el-dialog v-model="tagDialogVisible" :title="editingTag ? t('common.edit') : t('common.add')" width="400px">
      <el-form :model="tagForm" label-width="100px">
        <el-form-item :label="t('deviceTag.name')">
          <el-input v-model="tagForm.name" />
        </el-form-item>
        <el-form-item :label="t('deviceTag.color')">
          <el-color-picker v-model="tagForm.color" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="tagDialogVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="onSubmitTag">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>

    <!-- Assign Devices Dialog -->
    <el-dialog v-model="assignDialogVisible" :title="t('deviceGroup.assignDevices')" width="600px">
      <el-select v-model="assignDeviceIds" multiple filterable style="width: 100%">
        <el-option
          v-for="d in deviceOptions"
          :key="d.id"
          :label="`${d.deviceName} (${d.deviceKey})`"
          :value="d.id"
        />
      </el-select>
      <template #footer>
        <el-button @click="assignDialogVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="onSubmitAssign">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.section-card {
  padding: 16px;
  min-height: 400px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.section-title {
  font-size: 16px;
  font-weight: 600;
}
.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}
.tree-actions {
  display: none;
}
.tree-node:hover .tree-actions {
  display: inline-flex;
  gap: 4px;
}
.tag-list {
  min-height: 200px;
}
</style>
