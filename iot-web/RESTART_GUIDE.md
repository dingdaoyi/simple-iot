# 🔄 IotTable 错误 - 完整解决步骤

## 问题
```
[Vue warn]: Failed to resolve component: IotTable
```

## ✅ 已验证
- ✅ 所有导入已正确添加
- ✅ IotTable.vue 组件文件存在且正确
- ✅ 0360de 分支已合并最新更改

## 🔧 解决步骤

### 步骤 1: 完全停止开发服务器
```bash
# 在运行 pnpm dev 的终端按 Ctrl+C
```

### 步骤 2: 清理所有缓存
```bash
cd iot-web

# 清理 Vite 缓存
rm -rf node_modules/.vite
rm -rf .vite
rm -rf dist

# 清理浏览器缓存后重启
```

### 步骤 3: 重新启动开发服务器
```bash
pnpm dev
```

### 步骤 4: 清除浏览器缓存并刷新

**方法 1 - 硬刷新:**
- Mac: `Cmd + Shift + R`
- Windows: `Ctrl + Shift + R`

**方法 2 - 开发者工具:**
1. 按 F12 打开开发者工具
2. 右键点击刷新按钮
3. 选择"清空缓存并硬性重新加载"

**方法 3 - 清除站点数据:**
1. 打开开发者工具 (F12)
2. 切换到 "Application" 或 "应用程序" 标签
3. 左侧点击 "Storage" 或 "存储"
4. 点击 "Clear site data" 或 "清除站点数据"

## 🎯 为什么需要这样?

这是 **Vite HMR(热模块替换)** 的已知问题。当新添加组件导入时,有时 Vite 不会立即更新所有模块的依赖关系。

## ✅ 验证修复

重启后,访问以下页面应该都正常:
- ✅ /device - 设备管理
- ✅ /protocol - 协议管理
- ✅ /product - 产品管理
- ✅ /rule - 规则引擎
- ✅ /system/messageReceive - 消息通知
- ✅ /system/icon - 图标管理

## 📝 技术说明

所有文件已正确添加导入:
```javascript
import IotTable from '@/components/IotTable.vue'
```

组件定义正确:
```javascript
// iot-web/src/components/IotTable.vue
<script setup>
// ... 组件代码
</script>
```

## 如果还不行?

如果重启后还有问题,请检查:

1. Node 版本是否兼容
```bash
node -v  # 应该是 v16+
```

2. 依赖是否完整
```bash
pnpm install
```

3. 是否有端口冲突
```bash
lsof -i :5173  # 检查默认端口
```

---

**重启开发服务器是解决此问题的关键!**
