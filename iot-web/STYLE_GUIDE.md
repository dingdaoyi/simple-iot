# 前端代码风格指南

## 🎯 总体原则

基于 **Antfu ESLint Config** + **UnoCSS** + **dwyl-ui** 的现代化前端开发规范。

## 📝 Vue 组件规范

### 1. Script 部分

```vue
<script setup>
// ✅ 推荐：使用 setup 语法糖
import { computed, ref } from 'vue'

// Props 定义 - 使用对象形式，包含类型和默认值
const props = defineProps({
  deviceDetail: {
    type: Object,
    required: true,
  },
  title: {
    type: String,
    default: '',
  },
})

// 响应式数据
const loading = ref(false)
const activeTab = ref('tab1')

// 计算属性
const serviceList = computed(() => {
  return props.deviceDetail?.tslModel?.services || []
})

// 方法定义 - 使用 function 声明
function handleClick() {
  // 逻辑处理
}

// 异步方法
async function loadData() {
  try {
    loading.value = true
    // 异步逻辑
  }
  finally {
    loading.value = false
  }
}
</script>
```

### 2. Template 部分

```vue
<template>
  <!-- 使用语义化的容器结构 -->
  <div class="wh-full flex flex-col gap-5 p-6 bg-gray-50">
    <!-- 头部区域 -->
    <div class="bg-white rounded-lg p-4 shadow-sm">
      <h2 class="text-lg font-semibold text-gray-800 mb-4">
        标题
      </h2>
    </div>

    <!-- 内容区域 -->
    <div class="bg-white rounded-lg p-6 shadow-sm flex-1">
      <!-- 网格布局 -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="item in list" :key="item.id" class="p-4 border rounded-lg">
          {{ item.name }}
        </div>
      </div>
    </div>
  </div>
</template>
```

## 🎨 样式规范

### 1. UnoCSS 类名使用

```vue
<!-- ✅ 推荐：使用 UnoCSS 原子类 -->
<div class="flex items-center justify-between p-4 bg-white rounded-lg shadow-sm">
  <span class="text-lg font-semibold text-gray-800">标题</span>
  <el-button type="primary" size="small">操作</el-button>
</div>

<!-- ❌ 避免：内联样式 -->
<div style="display: flex; padding: 16px;">
```

### 2. 常用布局模式

```css
/* 全屏容器 */
.wh-full /* width: 100%; height: 100% */

/* Flex 布局 */
.flex-center /* display: flex; justify-content: center; align-items: center */
.flex-col-center /* flex-center + flex-direction: column */

/* 网格布局 */
.grid.grid-cols-1.md:grid-cols-2.lg:grid-cols-3

/* 间距 */
.gap-4 /* gap: 1rem */
.p-4 /* padding: 1rem */
.m-4 /* margin: 1rem */

/* 颜色 */
.bg-white .bg-gray-50 .bg-blue-50
.text-gray-800 .text-gray-600 .text-blue-600

/* 圆角和阴影 */
.rounded-lg .shadow-sm .shadow-lg
```

### 3. 响应式设计

```vue
<div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
  <!-- 小屏1列，中屏2列，大屏3列，超大屏4列 -->
</div>
```

## 🧩 组件设计原则

### 1. 组件结构

```vue
<!-- ✅ 推荐：清晰的层次结构 -->
<template>
  <div class="component-container">
    <!-- 头部 -->
    <header class="component-header">
      <h2>标题</h2>
      <div class="actions">
        <el-button>操作</el-button>
      </div>
    </header>

    <!-- 内容 -->
    <main class="component-content">
      <!-- 内容区域 -->
    </main>

    <!-- 底部 -->
    <footer class="component-footer">
      <!-- 底部内容 -->
    </footer>
  </div>
</template>
```

### 2. Props 验证

```javascript
// ✅ 推荐：完整的 Props 定义
defineProps({
  title: {
    type: String,
    required: true,
  },
  items: {
    type: Array,
    default: () => [],
  },
  config: {
    type: Object,
    default: () => ({}),
  },
})
```

### 3. 事件处理

```javascript
// ✅ 推荐：使用 emit 传递事件
const emit = defineEmits(['update', 'delete', 'change'])

function handleUpdate(data) {
  emit('update', data)
}
```

## 📦 dwyl-ui 组件使用

```vue
<template>
  <!-- 使用 dwyl-ui 封装的组件 -->
  <DwTable
    ref="tableRef"
    :column="columns"
    :params="params"
    :api="apiFunction"
    row-key="id"
  />

  <dw-select
    v-model="selectedValue"
    placeholder="请选择"
    filterable
    clearable
  >
    <dw-option
      v-for="item in options"
      :key="item.value"
      :label="item.label"
      :value="item.value"
    />
  </dw-select>
</template>
```

## 🔧 代码组织

### 1. 文件结构

```
src/
├── components/          # 通用组件
│   ├── Breadcrumb.vue
│   └── IconInput.vue
├── views/              # 页面组件
│   └── device/
│       ├── index.vue   # 主页面
│       └── widget/     # 子组件
│           ├── details.vue
│           ├── deviceEvent.vue
│           └── deviceService.vue
└── utils/              # 工具函数
    ├── request.js
    └── date_utils.js
```

### 2. 导入顺序

```javascript
import { dwHooks } from 'dwyl-ui'
// 2. 第三方库
import { ElMessage } from 'element-plus'

// 1. Vue 相关
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 3. 项目内部
import { apiFunction } from '@/api/index.js'
import ComponentName from '@/components/ComponentName.vue'
```

## ✨ 最佳实践

### 1. 性能优化

```javascript
// ✅ 使用 computed 而不是 methods
const filteredList = computed(() => {
  return list.value.filter(item => item.active)
})

// ✅ 使用 v-show 而不是 v-if（频繁切换）
<div v-show="isVisible">内容</div>
```

### 2. 错误处理

```javascript
async function loadData() {
  try {
    loading.value = true
    const { data } = await apiCall()
    // 处理数据
  }
  catch (error) {
    ElMessage.error(`加载失败: ${error.message}`)
  }
  finally {
    loading.value = false
  }
}
```

### 3. 类型安全

```javascript
// ✅ 使用可选链和空值合并
const userName = user?.profile?.name ?? '未知用户'
const itemCount = items?.length || 0
```

## 🎯 代码检查

使用 ESLint 自动格式化：

```bash
# 检查代码
npm run lint

# 自动修复
npm run lint:fix
```

## 📋 检查清单

- [ ] 使用 `<script setup>` 语法
- [ ] Props 包含类型定义
- [ ] 使用 UnoCSS 原子类而非内联样式
- [ ] 移除空的 `<style>` 标签
- [ ] 组件名使用 PascalCase
- [ ] 事件处理函数以 `handle` 或 `on` 开头
- [ ] 异步函数包含错误处理
- [ ] 响应式布局适配多端
- [ ] 代码通过 ESLint 检查

遵循这些规范，确保代码的一致性、可维护性和现代化。
