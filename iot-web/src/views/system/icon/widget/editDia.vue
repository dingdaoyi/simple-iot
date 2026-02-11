<script lang="jsx" setup>
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { computed, ref, watch } from 'vue'
import { iconAddApi, iconEditeApi } from '@/api'
import { useForm } from '@/composables/useForm.js'
import service from '@/utils/request.js'

const props = defineProps(['datas', 'modelValue'])
const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  name: [{ required: true, message: '图标名称不能为空', trigger: 'blur' }],
})

const uploadRef = ref(null)
const uploading = ref(false)
const fileList = ref([])

const { form, onSubmit: handleSubmit, editRef, loading, onClose, resetForm } = useForm({
  api: props.datas ? iconEditeApi : iconAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
  defForm: {
    name: '',
    path: '',
  },
})

const isEdit = computed(() => !!props.datas?.id)

function onSubmit() {
  // 上传中不允许提交
  if (uploading.value) {
    ElMessage.warning('文件上传中，请稍候...')
    return
  }
  // 新增时必须上传图片
  if (!isEdit.value && !form.value.path) {
    ElMessage.warning('请上传图标')
    return
  }
  handleSubmit()
}

// 上传前校验
function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 自定义上传
function handleUpload(uploadFile) {
  // el-upload 的 on-change 传入的是 UploadFile 对象，真正的文件在 raw 中
  const file = uploadFile.raw
  if (!file) {
    return
  }

  // 先检查文件类型
  if (!beforeUpload(file)) {
    return
  }

  uploading.value = true

  const formData = new FormData()
  formData.append('file', file)

  service.post('/file/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
    .then((result) => {
      // axios 拦截器已经返回了 response.data，result 就是 {code: 200, data: [...]}
      if (result.code === 200 && result.data && result.data.length > 0) {
        // 后端返回的是数组，取第一个元素
        const filePath = result.data[0]
        form.value.path = filePath
        // 只有上传成功才更新文件列表显示
        fileList.value = [{
          name: file.name,
          url: filePath,
          status: 'success',
          uid: uploadFile.uid,
        }]
        ElMessage.success('上传成功')
      }
      else {
        ElMessage.error(result.msg || '上传失败')
        fileList.value = []
      }
    })
    .catch((err) => {
      console.error('上传错误:', err)
      ElMessage.error('上传失败')
      fileList.value = []
    })
    .finally(() => {
      uploading.value = false
    })
}

// 文件移除
function handleRemove() {
  form.value.path = ''
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = {
      id: val.id,
      name: val.name,
      path: val.path,
    }
    // 编辑时设置文件列表
    if (val.path) {
      fileList.value = [{
        name: val.name || 'icon.png',
        url: val.path,
        status: 'success',
      }]
    }
  }
  else {
    resetForm()
    fileList.value = []
  }
}, { immediate: true })

// 监听对话框关闭，重置状态
watch(() => props.modelValue, (newVal) => {
  if (!newVal) {
    resetForm()
    fileList.value = []
    uploading.value = false
  }
})
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑图标' : '新增图标'"
    width="500px"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="onClose"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
    >
      <el-form-item label="图标名称" prop="name">
        <el-input
          v-model="form.name"
          clearable
          placeholder="请输入图标名称"
        />
      </el-form-item>

      <!-- 上传组件 -->
      <el-form-item label="图标" prop="path">
        <el-upload
          ref="uploadRef"
          :file-list="fileList"
          :auto-upload="false"
          :limit="1"
          :on-change="handleUpload"
          :before-upload="beforeUpload"
          :on-remove="handleRemove"
          accept="image/*"
          list-type="picture-card"
          class="icon-upload"
        >
          <el-icon><Plus /></el-icon>
        </el-upload>
        <div class="upload-tip">
          <el-text type="info" size="small">
            支持 jpg、png 格式，大小不超过 2MB
          </el-text>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="onClose">
        取消
      </el-button>
      <el-button type="primary" :loading="loading || uploading" @click="onSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.upload-tip {
  margin-top: var(--space-xs);
}

:deep(.icon-upload) {
  .el-upload {
    width: 120px;
    height: 120px;
    border: 2px dashed var(--iot-color-border);
    border-radius: var(--radius-lg);
    background: var(--iot-color-background);
    cursor: pointer;
    transition: all var(--transition-base) ease;

    &:hover {
      border-color: var(--iot-color-primary);
      background: var(--iot-glass-bg-hover);
    }

    .el-icon {
      font-size: 28px;
      color: var(--iot-color-text-muted);
    }
  }

  .el-upload-list__item {
    width: 120px;
    height: 120px;
    border-radius: var(--radius-lg);
  }

  .el-upload-list__item-actions {
    font-size: 18px;

    .el-upload-list__item-preview,
    .el-upload-list__item-delete {
      &:hover {
        color: var(--iot-color-primary);
      }
    }
  }
}
</style>
