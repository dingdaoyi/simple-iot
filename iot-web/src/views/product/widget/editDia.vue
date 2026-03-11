<script lang="jsx" setup>
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, watch } from 'vue'
import { productAddApi, productEditeApi, uploadMoreApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'productTypeList', 'modelValue'])

const emits = defineEmits(['update', 'update:modelValue'])

const rules = ref({
  productTypeId: [{ required: true, message: '产品类型不能为空', trigger: 'change' }],
  model: [{ required: true, message: '产品型号不能为空', trigger: 'blur' }],
})

const { form, onSubmit: handleSubmit, editRef, loading, onClose } = useForm({
  api: props.datas ? productEditeApi : productAddApi,
  callback: () => {
    emits('update')
    emits('update:modelValue', false)
  },
  closeCallback: () => {
    emits('update:modelValue', false)
  },
})

const uploadLoading = ref(false)

function onSubmit() {
  handleSubmit()
}

// 图片上传处理
async function handleUpload(options) {
  const { file } = options
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return
  }

  try {
    uploadLoading.value = true
    const formData = new FormData()
    formData.append('file', file)
    const res = await uploadMoreApi(formData)
    // 后端返回的是数组格式 ["/iot/file/xxx.png"]
    const url = res.data?.url || (Array.isArray(res.data) ? res.data[0] : null)
    if (url) {
      form.value.icon = url
      ElMessage.success('上传成功')
    }
    else {
      ElMessage.error('上传失败：未返回文件地址')
    }
  }
  catch (e) {
    ElMessage.error('上传失败')
  }
  finally {
    uploadLoading.value = false
  }
}

// 删除图片
function removeIcon() {
  form.value.icon = ''
}

watch(() => props.datas, (val) => {
  if (val) {
    form.value = { ...val }
  }
}, { immediate: true })
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    :title="datas?.id ? '编辑' : '新增'"
    width="600px"
    @update:model-value="$emit('update:modelValue', $event)"
    @close="onClose"
  >
    <el-form
      ref="editRef"
      :rules="rules"
      :model="form"
      label-width="100px"
    >
      <el-form-item
        label="产品类型"
        prop="productTypeId"
      >
        <el-select
          v-model="form.productTypeId"
          placeholder="请选择产品类型"
          style="width: 100%"
          filterable
          clearable
        >
          <el-option
            v-for="item in productTypeList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item
        label="产品图标"
        prop="icon"
      >
        <div class="icon-upload-wrapper">
          <!-- 已上传图片预览 -->
          <div v-if="form.icon" class="icon-preview">
            <el-image
              :src="form.icon"
              fit="cover"
              class="preview-image"
              :preview-src-list="[form.icon]"
            />
            <div class="preview-actions">
              <el-button type="danger" size="small" @click="removeIcon">
                删除
              </el-button>
            </div>
          </div>

          <!-- 上传按钮 -->
          <el-upload
            v-else
            class="icon-uploader"
            :show-file-list="false"
            :http-request="handleUpload"
            accept="image/*"
          >
            <div class="upload-trigger" :class="{ 'is-loading': uploadLoading }">
              <el-icon v-if="!uploadLoading" class="upload-icon">
                <Plus />
              </el-icon>
              <el-icon v-else class="is-loading">
                <Loading />
              </el-icon>
              <span class="upload-text">{{ uploadLoading ? '上传中...' : '上传图标' }}</span>
            </div>
          </el-upload>

          <div class="upload-tip">
            建议尺寸: 128x128px，支持 PNG、JPG、SVG，不超过 2MB
          </div>
        </div>
      </el-form-item>

      <el-form-item
        label="型号"
        prop="model"
      >
        <el-input
          v-model="form.model"
          clearable
          placeholder="请输入产品型号"
        />
      </el-form-item>

      <el-form-item
        label="厂家"
        prop="manufacturer"
      >
        <el-input
          v-model="form.manufacturer"
          clearable
          placeholder="请输入设备厂家"
        />
      </el-form-item>

      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          placeholder="请输入备注信息"
          type="textarea"
          :rows="2"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onClose">
        取消
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
.icon-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.icon-preview {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--iot-color-border);

  .preview-image {
    width: 100%;
    height: 100%;
  }

  .preview-actions {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    padding: var(--space-xs);
    opacity: 0;
    transition: opacity var(--transition-base);
  }

  &:hover .preview-actions {
    opacity: 1;
  }
}

.icon-uploader {
  :deep(.el-upload) {
    width: 100px;
    height: 100px;
  }
}

.upload-trigger {
  width: 100px;
  height: 100px;
  border: 2px dashed var(--iot-color-border);
  border-radius: var(--radius-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-xs);
  cursor: pointer;
  transition: all var(--transition-base);
  background: var(--iot-glass-bg);

  &:hover {
    border-color: var(--iot-color-primary);
    background: var(--iot-glass-bg-hover);
  }

  &.is-loading {
    border-color: var(--iot-color-primary);
    pointer-events: none;
  }

  .upload-icon {
    font-size: 28px;
    color: var(--iot-color-text-muted);
  }

  .upload-text {
    font-size: 12px;
    color: var(--iot-color-text-secondary);
  }
}

.upload-tip {
  font-size: 12px;
  color: var(--iot-color-text-muted);
}
</style>
