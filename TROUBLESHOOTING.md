# 🔧 IotTable 错误修复指南

## 问题
```
[Vue warn]: Failed to resolve component: IotTable
```

## 原因
这是 **Vite 开发服务器缓存** 导致的。虽然源代码已正确添加导入,但 Vite 可能还在使用旧的缓存版本。

## ✅ 解决方案

### 方法 1: 重启开发服务器 (推荐)

```bash
# 1. 停止当前开发服务器 (Ctrl+C)

# 2. 清理缓存
cd iot-web
rm -rf node_modules/.vite

# 3. 重新启动
pnpm dev
```

### 方法 2: 硬刷新浏览器

- **Mac**: `Cmd + Shift + R`
- **Windows/Linux**: `Ctrl + Shift + R`

### 方法 3: 清除浏览器缓存

1. 打开开发者工具 (F12)
2. 右键点击刷新按钮
3. 选择"清空缓存并硬性重新加载"

## 验证导入

所有文件已正确添加导入:

```bash
# 验证命令
grep -r "import IotTable from '@/components/IotTable.vue'" iot-web/src/views --include="*.vue"
```

应该看到 9 个文件:
- protocol/index.vue ✅
- device/index.vue ✅
- product/index.vue ✅
- rule/index.vue ✅
- system/messageReceive/index.vue ✅
- device/widget/deviceEvent.vue ✅
- productType/index.vue ✅
- tslModel/widget/service.vue ✅
- tslModel/widget/property.vue ✅

## 为什么只有驱动页面正常?

`driver/index.vue` 不使用 `IotTable`,而是直接使用 Element Plus 的 `<el-table>`,所以不受缓存影响。

## 下一步

重启开发服务器后,所有页面都应该正常显示!
