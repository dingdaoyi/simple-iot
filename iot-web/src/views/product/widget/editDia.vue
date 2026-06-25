<script lang="jsx" setup>
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { productAddApi, productEditeApi, uploadMoreApi } from '@/api'
import { useForm } from '@/composables/useForm.js'

const props = defineProps(['datas', 'productTypeList', 'modelValue'])
const emits = defineEmits(['update', 'update:modelValue'])
const { t } = useI18n()
const rules = ref({
  productTypeId: [{ required: true, message: t('product.product_type_required'), trigger: 'change' }],
  model: [{ required: true, message: t('product.product_model_required'), trigger: 'blur' }],
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
    ElMessage.error(t('product.only_image_files_can_uploaded'))
    return
  }
  if (!isLt2M) {
    ElMessage.error(t('product.text_2mb'))
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
      ElMessage.success(t('product.upload_success'))
    }
    else {
      ElMessage.error(t('product.upload_failed_no_file_url_returned'))
    }
  }
  catch {
    ElMessage.error(t('product.upload_failed'))
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
    :title="datas?.id ? t('common.edit') : t('common.add')"
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
        :label="t('menu.productType')"
        prop="productTypeId"
      >
        <el-select
          v-model="form.productTypeId"
          :placeholder="t('product.select_product_type')"
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
        :label="t('product.product_icon')"
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
                {{ t('common.delete') }}
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
              <span class="upload-text">{{ uploadLoading ? t('product.uploading') : t('system.upload_icon') }}</span>
            </div>
          </el-upload>

          <div class="upload-tip">
            {{ t('product.text_128x128px_png_jpg_svg_2mb') }}
          </div>
        </div>
      </el-form-item>

      <el-form-item
        :label="t('product.model')"
        prop="model"
      >
        <el-input
          v-model="form.model"
          clearable
          :placeholder="t('product.product_dialog')"
        />
      </el-form-item>

      <el-form-item
        :label="t('product.manufacturer_2')"
        prop="manufacturer"
      >
        <el-input
          v-model="form.manufacturer"
          clearable
          :placeholder="t('product.enter_manufacturer')"
        />
      </el-form-item>

      <el-form-item
        :label="t('common.remark')"
        prop="remark"
      >
        <el-input
          v-model="form.remark"
          clearable
          :placeholder="t('product.dialog_product_dialog')"
          type="textarea"
          :rows="2"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="onClose">
        {{ t('common.cancel') }}
      </el-button>
      <el-button type="primary" :loading="loading" @click="onSubmit">
        {{ t('common.confirm') }}
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
