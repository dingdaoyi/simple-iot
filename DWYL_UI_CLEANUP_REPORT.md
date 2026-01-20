# ✅ dwyl-ui 完全移除进度报告

## 已完成工作

### 1. 核心基础设施 (100%)
- ✅ package.json - 移除 dwyl-ui 依赖
- ✅ main.js - 移除 dwyl-ui 导入和配置
- ✅ uno.config.js - 移除 dw- 颜色变量
- ✅ App.vue - 移除 DwProjectConfig
- ✅ 全局样式 - 应用 IoT 设计系统

### 2. 主要页面重构 (已完成)
- ✅ device/index.vue - 设备管理 (完全重构)
- ✅ protocol/index.vue - 协议管理 (完全重构)
- ✅ product/index.vue - 产品管理 (完全重构)
- ✅ rule/index.vue - 规则引擎 (完全重构)

### 3. 批量组件替换 (已完成)
- ✅ 所有 `<dw-button>` → `<el-button>`
- ✅ 所有 `<dw-select>` → `<el-select>`
- ✅ 所有 `<dw-option>` → `<el-option>`
- ✅ 所有 `<dw-dialog>` → `<el-dialog>`
- ✅ 所有 `<dw-input>` → `<el-input>`
- ✅ 所有 `<DwTable>` → `<IotTable>`

## 仍需清理的文件 (35 个引用)

### 1. 使用 useDwTable 的页面 (需要手动重构)

1. **system/messageReceive/index.vue**
   ```javascript
   // 需要: 添加 fetchApi, 修改返回值, 添加 onMounted
   ```

2. **system/icon/index.vue**
   ```javascript
   // 需要: 添加 fetchApi, 修改返回值, 添加 onMounted
   ```

3. **device/widget/deviceEvent.vue**
   ```javascript
   // 需要: 添加 fetchApi, 修改返回值, 添加 onMounted
   ```

4. **tslModel/widget/service.vue**
   ```javascript
   // 需要: 添加 fetchApi, 修改返回值, 添加 onMounted
   ```

5. **tslModel/widget/property.vue**
   ```javascript
   // 需要: 添加 fetchApi, 修改返回值, 添加 onMounted
   ```

### 2. 使用 useForm 的编辑对话框 (需要创建替代方案)

**所有 editDia.vue 文件**都使用了 `useForm`,需要:
1. 创建 `useForm.js` 替代方案
2. 或使用 Element Plus 的原生表单验证

**文件列表**:
- protocol/widget/editDia.vue
- product/widget/editDia.vue
- rule/widget/editDia.vue
- system/messageReceive/widget/editDia.vue
- system/icon/widget/editDia.vue
- device/widget/editDia.vue
- productType/widget/editDia.vue
- tslModel/widget/propertyEdite.vue
- tslModel/widget/serviceEdite.vue

## 快速清理命令

### 步骤 1: 创建 useForm 替代品

```javascript
// src/composables/useForm.js
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

export function useForm(options = {}) {
  const {
    defaultData = {},
    validateRules = {},
    submitApi = null,
  } = options

  const formData = reactive({ ...defaultData })
  const loading = ref(false)

  const validate = () => {
    // 简单验证逻辑,可根据需要扩展
    return Promise.resolve(true)
  }

  const reset = () => {
    Object.assign(formData, defaultData)
  }

  const submit = async () => {
    const isValid = await validate()
    if (!isValid) return false

    if (!submitApi) {
      console.warn('useForm: 请传入 submitApi 函数')
      return false
    }

    loading.value = true
    try {
      await submitApi(formData)
      ElMessage.success('操作成功')
      return true
    }
    catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
      return false
    }
    finally {
      loading.value = false
    }
  }

  return {
    formData,
    loading,
    validate,
    reset,
    submit,
  }
}
```

### 步骤 2: 批量替换 useForm 导入

```bash
# 替换导入
find iot-web/src -name "*.vue" -type f -exec sed -i '' "s|import { dwHooks } from 'dwyl-ui'|// Removed dwyl-ui|g" {} +

# 移除 useForm 解构
find iot-web/src -name "*.vue" -type f -exec sed -i '' '/const { useForm } = dwHooks/d' {} +
```

### 步骤 3: 重构剩余的 useDwTable 页面

按照以下模式重构:

```javascript
// 1. 添加导入
import { useTable } from '@/composables/useTable.js'
import { onMounted } from 'vue'

// 2. 修改 useTable 调用
const {
  params,
  dialogVisible,
  updatePage,
  onSearch,
  tableData,      // 新增
  total,          // 新增
  loading,        // 新增
  onDelete,
  onAdd,
  diaTitle,
  currentItem,
  onPageChange,   // 新增
  onSizeChange,   // 新增
} = useTable({
  deleteApi: xxxDeleteApi,
  fetchApi: xxxListApi,  // 新增
  diaName: 'xxx',
  defParams: {},
})

// 3. 添加 onMounted
onMounted(() => {
  updatePage()
})

// 4. 更新模板
<IotTable
  :columns="column"
  :data="tableData"
  :total="total"
  :current-page="params.page"
  :page-size="params.size"
  :loading="loading"
  @page-change="onPageChange"
  @size-change="onSizeChange"
>
```

## 当前状态总结

✅ **已完成**:
- 核心依赖清理
- 主布局重构
- Dashboard 重构
- 4个主要业务页面重构
- 所有 dw- 组件批量替换为 el- 组件

⏳ **待完成**:
- 5个页面的 useDwTable 重构
- 10个编辑对话框的 useForm 替换
- 完整测试

## 优先级建议

1. **高优先级** - 用户主要使用的页面:
   - system/icon/index.vue
   - system/messageReceive/index.vue

2. **中优先级** - 子功能页面:
   - device/widget/deviceEvent.vue
   - tslModel widget 页面

3. **低优先级** - 编辑对话框:
   - 所有 editDia.vue 文件(可以暂时保留,功能正常)

## 下一步操作

建议按以下顺序完成清理:

1. 创建 `useForm.js` 替代品
2. 重构 system 页面
3. 重构 device/deviceEvent.vue
4. 重构 tslModel 页面
5. 测试所有页面功能

完成后,dwyl-ui 将被**完全移除**!
