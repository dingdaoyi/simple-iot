<script setup>
/**
 * 设备分组管理 - 左树右列表布局
 * 左侧: 分组树（点击选中展示右侧设备列表）
 * 右侧: 已分配设备表格 + 穿梭框分配
 * 底部: 标签管理区
 */
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
  deviceTagAssignApi,
  deviceTagDelApi,
  deviceTagDevicesApi,
  deviceTagEditApi,
} from '@/api/deviceGroup'
import { deviceListApi } from '@/api/index'

const { t } = useI18n()

// === Group ===
const groupTree = ref([])
const groupLoading = ref(false)
const selectedGroupId = ref(null)
const selectedGroupNode = ref(null)

// === Device List (right panel) ===
const assignedDevices = ref([])
const assignedLoading = ref(false)

// === Transfer dialog ===
const transferVisible = ref(false)
const transferTitle = ref('')
const transferAllDevices = ref([])
const transferValue = ref([]) // selected device ids
const transferType = ref('group') // 'group' | 'tag'
const transferTargetId = ref(null)
const transferLoading = ref(false)

// === Group Dialog ===
const groupDialogVisible = ref(false)
const groupForm = ref({ name: '', parentId: -1, description: '' })
const editingGroup = ref(false)

// === Tag ===
const tags = ref([])
const tagLoading = ref(false)
const tagDialogVisible = ref(false)
const tagForm = ref({ name: '', color: '#6366f1' })
const editingTag = ref(false)
const selectedTagId = ref(null)

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

async function loadAssignedDevices() {
  if (!selectedGroupId.value) {
    assignedDevices.value = []
    return
  }
  assignedLoading.value = true
  try {
    const res = await deviceGroupDevicesApi(selectedGroupId.value)
    assignedDevices.value = res.data || []
  }
  finally {
    assignedLoading.value = false
  }
}

async function loadTagDevices(tagId) {
  selectedTagId.value = tagId
  assignedLoading.value = true
  try {
    const res = await deviceTagDevicesApi(tagId)
    assignedDevices.value = res.data || []
  }
  finally {
    assignedLoading.value = false
  }
}

function onGroupClick(node) {
  selectedGroupId.value = node.id
  selectedGroupNode.value = node
  selectedTagId.value = null
  loadAssignedDevices()
}

function onTagClick(tag) {
  selectedTagId.value = tag.id
  selectedGroupId.value = null
  selectedGroupNode.value = null
  loadTagDevices(tag.id)
}

// === Group CRUD ===
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
  if (editingGroup.value)
    await deviceGroupEditApi(groupForm.value)
  else
    await deviceGroupAddApi(groupForm.value)
  ElMessage.success(t('common.success'))
  groupDialogVisible.value = false
  loadGroups()
}

async function onDeleteGroup(node) {
  await ElMessageBox.confirm(t('deviceGroup.deleteConfirm'), t('common.confirm'), { type: 'warning' })
  await deviceGroupDelApi(node.id)
  ElMessage.success(t('common.success'))
  if (selectedGroupId.value === node.id) {
    selectedGroupId.value = null
    assignedDevices.value = []
  }
  loadGroups()
}

// === Tag CRUD ===
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
  if (editingTag.value)
    await deviceTagEditApi(tagForm.value)
  else
    await deviceTagAddApi(tagForm.value)
  ElMessage.success(t('common.success'))
  tagDialogVisible.value = false
  loadTags()
}

async function onDeleteTag(tag) {
  await ElMessageBox.confirm(t('deviceTag.deleteConfirm'), t('common.confirm'), { type: 'warning' })
  await deviceTagDelApi(tag.id)
  ElMessage.success(t('common.success'))
  if (selectedTagId.value === tag.id) {
    selectedTagId.value = null
    assignedDevices.value = []
  }
  loadTags()
}

// === Transfer (assign devices) ===
async function onAssign(type, id, name) {
  transferType.value = type
  transferTargetId.value = id
  transferTitle.value = `分配设备 - ${name}`
  transferLoading.value = true
  transferVisible.value = true

  const [allRes, assignedRes] = await Promise.all([
    deviceListApi(),
    type === 'group'
      ? deviceGroupDevicesApi(id)
      : deviceTagDevicesApi(id),
  ])
  transferAllDevices.value = (allRes.data || []).map(d => ({
    key: d.id,
    label: `${d.deviceName} (${d.deviceKey})`,
    online: d.online,
  }))
  transferValue.value = (assignedRes.data || []).map(d => d.id)
  transferLoading.value = false
}

async function onSubmitTransfer() {
  if (transferType.value === 'group')
    await deviceGroupAssignApi(transferTargetId.value, transferValue.value)
  else
    await deviceTagAssignApi(transferTargetId.value, transferValue.value)
  ElMessage.success(t('common.success'))
  transferVisible.value = false
  // refresh right panel
  if (selectedGroupId.value)
    loadAssignedDevices()
  else if (selectedTagId.value)
    loadTagDevices(selectedTagId.value)
}

function onRemoveDevice(deviceId) {
  transferValue.value = assignedDevices.value.map(d => d.id).filter(id => id !== deviceId)
  if (selectedGroupId.value)
    deviceGroupAssignApi(selectedGroupId.value, transferValue.value).then(() => loadAssignedDevices())
  else if (selectedTagId.value)
    deviceTagAssignApi(selectedTagId.value, transferValue.value).then(() => loadTagDevices(selectedTagId.value))
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

    <div class="dg-layout">
      <!-- 左侧: 分组树 + 标签 -->
      <div class="dg-left glass-card">
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
          highlight-current
          @node-click="onGroupClick"
        >
          <template #default="{ data }">
            <span class="tree-node" :class="{ active: selectedGroupId === data.id }">
              <span>{{ data.name }}</span>
              <span class="tree-actions">
                <el-button link size="small" type="primary" @click.stop="onAddGroup(data.id)">+</el-button>
                <el-button link size="small" @click.stop="onEditGroup(data)">{{ t('common.edit') }}</el-button>
                <el-button link size="small" type="danger" @click.stop="onDeleteGroup(data)">{{ t('common.delete') }}</el-button>
              </span>
            </span>
          </template>
        </el-tree>

        <el-divider />

        <div class="section-header">
          <span class="section-title">{{ t('deviceTag.tags') }}</span>
          <el-button type="primary" size="small" @click="onAddTag">
            + {{ t('common.add') }}
          </el-button>
        </div>
        <div v-loading="tagLoading" class="tag-list">
          <div
            v-for="tag in tags"
            :key="tag.id"
            class="tag-item"
            :class="{ active: selectedTagId === tag.id }"
            @click="onTagClick(tag)"
          >
            <span class="tag-dot" :style="{ background: tag.color }" />
            <span class="tag-name">{{ tag.name }}</span>
            <span class="tag-actions">
              <el-button link size="small" @click.stop="onEditTag(tag)">{{ t('common.edit') }}</el-button>
              <el-button link size="small" type="danger" @click.stop="onDeleteTag(tag)">×</el-button>
            </span>
          </div>
          <el-empty v-if="tags.length === 0" :description="t('common.empty')" :image-size="40" />
        </div>
      </div>

      <!-- 右侧: 已分配设备列表 -->
      <div class="dg-right glass-card">
        <div class="section-header">
          <span class="section-title">
            {{ selectedGroupId
              ? `${selectedGroupNode?.name || ''} - 已分配设备`
              : selectedTagId
                ? `${tags.find(t => t.id === selectedTagId)?.name || ''} - 已分配设备`
                : '请从左侧选择分组或标签' }}
          </span>
          <el-button
            v-if="selectedGroupId || selectedTagId"
            type="primary"
            size="small"
            @click="onAssign(
              selectedGroupId ? 'group' : 'tag',
              selectedGroupId || selectedTagId,
              selectedGroupNode?.name || tags.find(t => t.id === selectedTagId)?.name || '',
            )"
          >
            管理设备
          </el-button>
        </div>

        <el-table
          v-loading="assignedLoading"
          :data="assignedDevices"
          size="default"
          stripe
          style="width: 100%"
          :empty-text="selectedGroupId || selectedTagId ? '暂无已分配设备' : '请先选择分组或标签'"
        >
          <el-table-column prop="deviceName" label="设备名称" min-width="120" />
          <el-table-column prop="deviceKey" label="设备标识" min-width="140" />
          <el-table-column label="在线状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.online ? 'success' : 'info'" size="small">
                {{ row.online ? '在线' : '离线' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" align="center">
            <template #default="{ row }">
              <el-button link type="danger" size="small" @click="onRemoveDevice(row.id)">
                移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

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

    <!-- Transfer Dialog -->
    <el-dialog v-model="transferVisible" :title="transferTitle" width="700px" destroy-on-close>
      <el-transfer
        v-model="transferValue"
        v-loading="transferLoading"
        :data="transferAllDevices"
        :titles="['可选设备', '已分配']"
        filterable
        filter-placeholder="搜索设备"
        style="--el-transfer-panel-width: 300px"
      />
      <template #footer>
        <el-button @click="transferVisible = false">
          {{ t('common.cancel') }}
        </el-button>
        <el-button type="primary" @click="onSubmitTransfer">
          {{ t('common.confirm') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.dg-layout {
  display: flex;
  gap: 16px;
  min-height: 500px;
}

.dg-left {
  width: 300px;
  flex-shrink: 0;
  padding: 16px;
  max-height: 600px;
  overflow-y: auto;
}

.dg-right {
  flex: 1;
  padding: 16px;
  min-width: 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
}

.tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
  border-radius: 4px;
  padding: 2px 4px;

  &.active {
    background: var(--el-color-primary-light-9);
  }

  .tree-actions {
    display: none;
  }

  &:hover .tree-actions {
    display: inline-flex;
    gap: 2px;
  }
}

.tag-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: var(--el-fill-color-light);
  }

  &.active {
    background: var(--el-color-primary-light-9);
  }

  .tag-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    flex-shrink: 0;
  }

  .tag-name {
    flex: 1;
    font-size: 13px;
  }

  .tag-actions {
    display: none;
  }

  &:hover .tag-actions {
    display: inline-flex;
    gap: 2px;
  }
}
</style>
