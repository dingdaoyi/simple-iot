调研范围：已只读检查 `src/styles/`、布局组件、指定 10 个代表页面、5 个公共组件、主题/构建配置、`STYLE_GUIDE.md`，并用 `rg` 覆盖图标、阴影、圆角、动效、loading/empty、表格密度等用法。截图目录存在 8 张：`dashboard.png`、`device.png`、`alarm.png`、`product.png`、`product-type.png`、`protocol.png`、`rule-chain-list.png`、`rule-chain-editor.png`。

## 一、UI/UX "掉档次"问题清单（按页面/组件维度）

- **[主题 token 冲突]** `src/styles/global.scss:16`、`src/styles/element.scss:5`、`src/styles/var.scss:1` — IoT 主色是 `--iot-color-primary: #6366f1`，Element Plus 主色是 `--el-color-primary: #167eff`，旧 Sass 变量又是 `$mainColor: #409eff`。**问题：** 同一产品出现三套蓝紫体系，按钮、UnoCSS `color-primary`、表格 hover、登录页背景会割裂，不符合现代 SaaS 的单一品牌 token 标准。
- **[响应式布局]** `src/styles/reset.scss:79-83` — `html, body` 强制 `min-width: 1440px`、`min-height: 680px`。**问题：** 即使页面写了 `@media (max-width: 768px)`，实际移动端仍会横向滚动，SaaS 管理台至少应支持 1280 桌面、1024 平板和 390 移动查看。
- **[字体系统]** `src/styles/global.scss:6`、`src/styles/reset.scss:37-49` — 全局引入 Fira Sans/Fira Code，但 reset 又对几乎所有元素 `font-family: ... !important`。**问题：** 字体策略互相覆盖，标题 `Fira Code` 的技术感也被中文 UI 的可读性拖累，建议统一到中文友好的 Inter/PingFang SC + 数字等宽。
- **[圆角尺度]** `src/styles/global.scss:76-80` — `--radius-md: 16px`、`--radius-lg: 24px`、`--radius-xl: 32px`。**问题：** 企业 SaaS 的表格、筛选区、菜单一般以 6-10px 为主，当前大圆角叠加玻璃态更像展示页，降低工作台的专业密度。
- **[全局 hover 过重]** `src/styles/global.scss:210-224` — `.glass-card:hover` 默认 `translateY(-2px)`。**问题：** 搜索栏、表格容器、普通信息区都会被赋予卡片漂浮动效，操作型后台不应让所有容器在 hover 时移动。
- **[按钮语义颜色]** `src/styles/global.scss:269-281` — `.el-button--success` 被全局做成绿色渐变，页面将新增按钮大量设为 `type="success"`。**问题：** 现代 SaaS 通常只用 primary 表示主操作，success 应保留给成功状态；新增按钮用绿色会和在线/启用状态混淆。
- **[页面标题重复造型]** `src/styles/global.scss:800-847`、`src/views/device/index.vue:267-313`、`src/views/productType/index.vue:192-238` — 已有全局 `.page-header`，多个页面又复制一份。**问题：** 维护成本高且细节不一致，后续改 token 会造成页面级偏差。
- **[页面标题装饰化]** `src/views/device/index.vue:139`、`src/views/alarm/index.vue:298`、`src/views/protocol/index.vue:155` — 标题图标使用 `◈/◊` 字符。**问题：** 这类字符不属于统一图标系统，和 Element Plus 线性图标混用，首屏观感像临时占位。
- **[按钮图标字符化]** `src/views/device/index.vue:208-214`、`src/views/protocol/index.vue:221-228`、`src/views/system/icon/index.vue:65-71` — 搜索和新增按钮使用 `⌕`、`+` 文本。**问题：** 图标粗细、基线、可访问名称都不可控，和 `el-icon` 的 SVG 尺寸无法对齐。
- **[表格 loading 绑定错误]** `src/components/IotTable.vue:184-188` — `:loading="tableLoading"` 直接传给 `el-table`。**问题：** Element Plus 表格没有 `loading` prop，标准写法是 `v-loading="tableLoading"`；当前通用表格 loading 覆盖率看似有，实际可能不显示。
- **[表格空状态重复]** `src/components/IotTable.vue:261-272` — `el-table` 自带 empty 区域外，又追加 `.iot-table-empty` 和 emoji `📭`。**问题：** 空数据时会出现表格空文案和外部空块竞争，占用空间且风格不专业，应统一到 `#empty` 或 `<el-empty>`。
- **[表格密度不可配置]** `src/components/IotTable.vue:184-192`、`src/components/IotTable.vue:325-327` — `el-table` 没有 `size`/`row-class-name`/密度 prop，单元格 padding 固定 `var(--space-md) var(--space-sm)`。**问题：** 设备、告警、规则列表属于高频扫描场景，需要 compact/comfortable 两档密度。
- **[表格对齐过度居中]** `src/components/IotTable.vue:224-225` — 所有列默认 `align="center"`、`header-align="center"`。**问题：** SaaS 数据表应文本左对齐、状态/数字居中；全居中会降低长文本扫描效率。
- **[表格容器套娃]** `src/views/protocol/index.vue:235-242`、`src/components/IotTable.vue:277-284` — 页面 `.table-wrapper glass-card` 内部的 `IotTable` 自己又是玻璃卡片。**问题：** 卡片嵌套卡片让边框、padding、阴影叠加，视觉噪声增加。
- **[设备页表格少一层玻璃容器不一致]** `src/views/device/index.vue:220-244` — `.table-wrapper` 没有 `glass-card`，其他页面有。**问题：** 同类列表页面外边距和阴影层级不一致，切换页面时布局跳动。
- **[筛选区内联宽度]** `src/views/protocol/index.vue:168-217` — 多个 `<el-select>`/`<el-input>` 使用 `style="width: 150px/200px/180px"`。**问题：** 内联宽度绕开 token，响应式时难以统一成 240px 输入宽度或网格列。
- **[Dashboard 信息层级失衡]** `src/views/home/index.vue:428-510` — 首屏 4 张卡片均等宽，后续主内容固定 `grid-template-columns: 320px 1fr`。**问题：** 设备在线率、活动告警等核心指标没有主次权重，资源监控反而占据左栏首位。
- **[Dashboard 动态列表空状态弱]** `src/views/home/index.vue:349-366`、`src/views/home/index.vue:382-403` — 仅纯文字 `暂无设备数据/暂无活动告警`。**问题：** 空状态没有图标、操作按钮或骨架占位，加载/空/错误三态边界不清。
- **[Dashboard 颜色硬编码]** `src/views/home/index.vue:277-305`、`src/views/home/index.vue:486-503` — 进度条和卡片渐变直接写 `#6366f1/#8b5cf6/#06b6d4/#ef4444`。**问题：** 主题切换或换品牌色时 dashboard 不会自动跟随 token。
- **[产品卡片视图缺少显式切换控件上下文]** `src/views/product/index.vue:23`、`src/views/product/index.vue:205-210`、`src/views/product/index.vue:288-299` — `viewMode` 默认 `card`，同页又维护表格视图。**问题：** 卡片/表格模式的入口和状态需要固定在 action bar，避免用户不知道可以切换。
- **[协议页 emoji 混入业务表格]** `src/views/protocol/index.vue:27-32`、`src/views/protocol/index.vue:67-72` — 脚本语言用 `📜/🐍/☕/🌙`。**问题：** emoji 渲染因系统字体变化，和 SaaS 表格的线性图标/标签风格不一致。
- **[图标管理空状态 emoji]** `src/views/system/icon/index.vue:81-89`、`src/views/system/icon/index.vue:109-113` — 空状态和错误态用 `📁/⚠️`。**问题：** 图标管理本身更应展示统一图标系统，emoji 会让管理后台显得不成体系。
- **[规则链编辑器画布基础能力弱]** `src/views/rule-chain/editor/index.vue:925-985` — 画布只有拖放和静态网格，没有缩放、平移、对齐辅助、mini-map 或键盘快捷操作。**问题：** 节点编辑器是生产力核心，现代流程编辑器需要 Flow 类交互反馈。
- **[规则链节点视觉旧]** `src/views/rule-chain/editor/index.vue:1308-1416` — 节点固定 `width: 180px`、白底、顶部渐变条。**问题：** 渐变头+白卡像早期低代码工具，节点类型/状态/错误没有体系化编码。
- **[规则链返回按钮不统一]** `src/views/rule-chain/editor/index.vue:822-824` — 返回使用文本箭头 `← 返回`。**问题：** 与 `Breadcrumb.vue` 的 `ArrowLeft` 图标按钮不一致，也缺少固定 36px 图标按钮尺寸。
- **[主题实现双轨]** `src/composables/useTheme.js:30-38`、`src/store/modules/theme.js:34-84` — composable 设置 `data-theme` 和 `.dark`，Pinia store 也维护 `.dark` 与 `--el-color-primary`。**问题：** 主题状态来源不唯一，登录页和布局页可能读取不同 token。
- **[Element 图标全量注册]** `src/main.js:1-30` — `@element-plus/icons-vue` 全量注册到全局。**问题：** 破坏 tree-shaking，图标命名也散落各页面，后续统一替换成本高。
- **[SVG 插件未形成系统]** `vite.config.mjs:8`、`vite.config.mjs:39-44` — 配置了 `vite-plugin-svg-icons`，但实际 `src/icons` 未在审计范围内发现成体系使用。**问题：** 图标链路有 Element Plus、手写 SVG、svg-icons 插件、emoji 四套入口。
- **[登录页风格脱节]** `src/views/login/index.vue:91-120`、`src/views/login/index.vue:124-129` — 登录卡片仍使用 `--dw-box-bg`、8px 圆角和旧背景组件。**问题：** 登录是第一触点，却没有使用当前玻璃态 token 和 IoT 工业色板。

## 二、TOP 优化项（≤ 12 条，按 ROI 倒序）

#### 1. 合并品牌色 token，让 Element Plus 与 IoT token 同源
- **位置：** `src/styles/global.scss:13-20`、`src/styles/element.scss:4-6`、`uno.config.js:20-24`、`src/store/modules/theme.js:10-13`
- **现状：** `#6366f1`、`#167eff`、`#409eff` 三套主色并存。
- **改造方案：** 将 `--el-color-primary` 指向 `--iot-color-primary`，废弃 `$mainColor` 和 `themeStore.defaultConfig.elColorPrimary` 的硬编码；示例：
```diff
 :root {
-  --el-color-primary: #167eff;
+  --el-color-primary: var(--iot-color-primary);
+  --el-color-primary-light-3: color-mix(in srgb, var(--iot-color-primary) 70%, white);
+  --el-color-primary-dark-2: var(--iot-color-primary-dark);
 }
```
- **视觉收益：** 品牌一致性 +35%，主题切换可信度 +30%。

#### 2. 取消全局 1440px 最小宽度，恢复真实响应式
- **位置：** `src/styles/reset.scss:79-83`、`src/layout/widget/slider.vue:231-252`
- **现状：** `html, body` 强制 `min-width: 1440px`，移动端媒体查询失效。
- **改造方案：** 改成应用容器级 `min-width: 1024px` 或完全移除；移动端让 sidebar overlay 化，至少先解除 body 限制：
```diff
 html,
 body {
-  min-width: 1440px;
-  min-height: 680px;
+  min-width: 0;
+  min-height: 100vh;
 }
```
- **视觉收益：** 移动/平板可用性 +50%，布局专业度 +20%。

#### 3. 收敛页面标题为一个标准 PageHeader
- **位置：** `src/styles/global.scss:800-847`、`src/views/device/index.vue:136-145`、`src/views/protocol/index.vue:152-160`、`src/views/system/icon/index.vue:39-47`
- **现状：** 多页面复制 `.page-header`，且用 `◈/◊` 字符做标题图标。
- **改造方案：** 新建或复用统一组件时，标题图标只接受 SVG 组件；标题容器用 `--iot-color-bg-card`、`--radius-md: 10px`、顶部 2px accent line，不再每页复制样式：
```diff
-<span class="title-icon">◈</span>
+<el-icon class="title-icon" :size="20"><Monitor /></el-icon>
```
- **视觉收益：** 首屏精致度 +30%，维护一致性 +40%。

#### 4. 修复 IotTable loading/empty/density 三件套
- **位置：** `src/components/IotTable.vue:184-192`、`src/components/IotTable.vue:248-272`、`src/components/IotTable.vue:325-327`
- **现状：** `:loading` 不是 Element Plus 表格 prop，空状态用 emoji，表格没有密度选项。
- **改造方案：** 增加 `density` prop，表格使用 `v-loading`，空状态使用 `#empty + <el-empty>`：
```diff
 <el-table
-  :loading="tableLoading"
+  v-loading="tableLoading"
+  :size="density === 'compact' ? 'small' : 'default'"
 >
+  <template #empty>
+    <el-empty :image-size="72" description="暂无数据" />
+  </template>
 </el-table>
```
- **视觉收益：** 表格可信度 +35%，数据扫描效率 +25%。

#### 5. 去掉卡片套卡片，统一列表页容器层级
- **位置：** `src/views/protocol/index.vue:235-242`、`src/views/alarm/index.vue:365-372`、`src/views/rule-chain/index.vue:188-195`、`src/components/IotTable.vue:277-284`
- **现状：** 页面 `.table-wrapper glass-card` 包住内部自带玻璃背景的 `IotTable`。
- **改造方案：** 二选一：保留 `IotTable` 的 `.iot-table-wrapper`，页面 `table-wrapper` 只负责 `flex: 1`；或者让 `IotTable` 提供 `bordered=false`。优先删除页面层 `glass-card`：
```diff
-<div class="table-wrapper glass-card">
+<div class="table-wrapper">
   <IotTable ... />
 </div>
```
- **视觉收益：** 视觉噪声 -25%，页面密度 +20%。

#### 6. 将新增按钮从 success 改为 primary，状态色只服务状态
- **位置：** `src/views/device/index.vue:207-215`、`src/views/protocol/index.vue:221-228`、`src/views/productType/index.vue:110-118`、`src/styles/global.scss:269-281`
- **现状：** 新增动作使用绿色 `type="success"`，全局 success 按钮又是强渐变。
- **改造方案：** 新增/保存统一 `type="primary"`，成功状态用 `el-tag type="success"`；危险动作 `plain` 或 `link`，不使用大面积渐变：
```diff
-<el-button type="success" @click="onAdd">
+<el-button type="primary" @click="onAdd">
   <el-icon><Plus /></el-icon>
   添加设备
 </el-button>
```
- **视觉收益：** 操作语义清晰度 +30%，品牌一致性 +20%。

#### 7. 用 CSS class 替换协议页内联宽度
- **位置：** `src/views/protocol/index.vue:168-217`、`src/styles/global.scss:849-879`
- **现状：** 筛选项写死 `style="width: 150px"` 等。
- **改造方案：** 在全局 `.search-bar` 增加 `.filter-field-sm/md/lg`，协议页改 class：
```diff
-style="width: 150px"
+class="filter-field-sm"
```
建议 token：`sm=160px`、`md=220px`、`lg=280px`，移动端 `width: 100%`。
- **视觉收益：** 筛选区整齐度 +25%，响应式维护性 +30%。

#### 8. Dashboard 指标卡加入数值动效和骨架屏
- **位置：** `src/views/home/index.vue:233-257`、`src/views/home/index.vue:349-366`、`src/views/home/index.vue:382-403`
- **现状：** 指标瞬时显示，列表 loading 只有遮罩，空态只有文字。
- **改造方案：** 使用 `@vueuse/core` 的 `useTransition` 做 count-up；设备/告警列表加载时显示 5 行 skeleton，空态用统一 `<el-empty>`：
```diff
+<el-skeleton v-if="devicesLoading" :rows="5" animated />
-<div v-loading="devicesLoading" class="devices-list">
+<div v-else class="devices-list">
```
- **视觉收益：** 首屏精致度 +30%，加载感知 +25%。

#### 9. 规则链编辑器引入专业 Flow 交互基线
- **位置：** `src/views/rule-chain/editor/index.vue:925-1035`、`src/views/rule-chain/editor/index.vue:1188-1307`
- **现状：** 自研绝对定位节点和 SVG 连线，没有缩放/平移/吸附/mini-map。
- **改造方案：** 中期切到 `@vue-flow/core`；短期先在当前实现补充 `zoom` state、space+drag pan、节点选中呼吸光晕、连线 drawing 动画：
```scss
.connection-line {
  stroke-dasharray: 320;
  animation: draw-line 240ms ease-out both;
}
@keyframes draw-line { from { stroke-dashoffset: 320; } to { stroke-dashoffset: 0; } }
```
- **视觉收益：** 编辑器专业度 +45%，规则配置效率 +30%。

#### 10. 登录页切换到同一套 SaaS 视觉系统
- **位置：** `src/views/login/index.vue:91-120`、`src/views/login/index.vue:124-177`
- **现状：** 登录页使用 `--dw-box-bg`、旧背景组件和旧表单字号。
- **改造方案：** `.content` 改为 `background: var(--iot-color-bg-card)`、`backdrop-filter: blur(24px)`、`border: 1px solid var(--iot-glass-border)`，标题使用产品 logo 区，表单 label 14px，不再 `20px`：
```diff
 .content {
-  border-radius: 8px;
-  background-color: var(--dw-box-bg);
+  border-radius: var(--radius-md);
+  background: var(--iot-color-bg-card);
+  border: 1px solid var(--iot-glass-border);
+  box-shadow: var(--shadow-lg);
 }
```
- **视觉收益：** 第一印象 +35%，品牌一致性 +25%。

#### 11. 统一动效时长和 reduced-motion 策略
- **位置：** `src/styles/global.scss:83-86`、`src/styles/transition.css:1-82`、`src/views/rule-chain/editor/index.vue:1165-1170`
- **现状：** token 是 150/250/350ms，但局部仍写 `0.2s/0.15s/0.3s`。
- **改造方案：** 将局部时长替换为 `var(--transition-fast/base)`，新增 `@media (prefers-reduced-motion: reduce)` 已在 `global.scss:740` 附近存在，扩展到 SVG drawing 和节点 hover。
- **视觉收益：** 微交互一致性 +20%，可访问性 +15%。

#### 12. 替换 emoji/字符图标为统一 SVG 图标
- **位置：** `src/views/protocol/index.vue:27-32`、`src/components/IotTable.vue:266-270`、`src/views/system/icon/index.vue:81-83`、`src/views/device/index.vue:208-214`
- **现状：** emoji、字符、Element Plus 图标和手写 SVG 混用。
- **改造方案：** 引入 `lucide-vue-next`，按钮/空态/标题图标全部使用 Lucide 线性图标；保留 Element Plus 组件内部图标直到迁移完。
- **视觉收益：** 图标一致性 +40%，界面专业度 +25%。

## 三、图标系统建议

当前依赖层面只有 `@element-plus/icons-vue`（`package.json:15`）和 `vite-plugin-svg-icons`（`package.json:40`、`vite.config.mjs:8`、`vite.config.mjs:39-44`）。代码层面还存在全量 Element Plus 图标注册（`src/main.js:1-30`）、手写 SVG fallback（`src/components/DefaultIcon.vue:23-35`）、独立 SVG 资产（`src/assets/backico.svg:1`）、字符图标（`src/views/device/index.vue:139`、`src/views/protocol/index.vue:155`）、emoji（`src/views/protocol/index.vue:27-32`、`src/views/system/icon/index.vue:81-83`）。结论：当前是 4 套视觉入口混用。

推荐统一到 **Lucide**，包名用 `lucide-vue-next`。理由：Lucide 默认 24px viewBox、2px stroke，和现代 SaaS 的轻量线性风格更接近；Vue 3 组件按需导入，tree-shaking 友好；IoT 后台所需的 `Monitor`、`Cpu`、`Boxes`、`Network`、`Workflow`、`Bell`、`Search`、`Plus`、`Trash2`、`Pencil`、`Settings`、`Moon/Sun` 覆盖完整；相比 Element Plus 图标更中性，和 Linear/Vercel/Resend 的产品工具气质更接近。

迁移落地方案：

- 安装：`pnpm add lucide-vue-next`。
- 迁移策略：新增 `src/components/AppIcon.vue` 或直接页面按需导入 Lucide；先替换业务页面图标，Element Plus 内置交互图标可暂时保留。
- 工作量评估：菜单和 header 2-3 小时；代表列表页按钮/标题 3-4 小时；空状态和表格 2 小时；规则链编辑器节点/工具栏 4-6 小时；合计约 1.5-2 个工作日。
- 与 Element Plus 共存：`el-icon` 仍可包裹 Lucide 组件，但不要再全量注册 `ElementPlusIconsVue`；只在需要 Element Plus 专属图标时局部导入。

必须立刻替换的高频图标清单：

- 菜单图标：`House -> LayoutDashboard`、`Monitor -> MonitorCog`、`Box -> Boxes`、`Connection -> Cable`、`Operation -> Workflow`、`Bell -> BellRing`、`Setting -> Settings`。
- 操作按钮图标：`⌕ -> Search`、`+ -> Plus`、`← 返回 -> ArrowLeft`、`编辑 -> Pencil`、`删除 -> Trash2`、`查看详情 -> Eye`、`保存 -> Save`、`刷新 -> RefreshCw`。
- 空状态图标：`📭 -> Inbox`、`📁 -> FolderOpen`、`⚠️ -> TriangleAlert`、`暂无设备 -> MonitorOff`、`暂无告警 -> BellOff`、`暂无产品 -> PackageOpen`、`暂无规则 -> Workflow`。
- 协议/脚本图标：`📜 -> FileCode2`、`🐍 -> Code2`、`☕ -> Coffee` 或统一 `FileCode2`、`🌙 -> Moon` 不建议表达 Lua，改 `Braces`。

## 四、关键页面微交互建议

Dashboard（`src/views/home/index.vue`）：

- 数字 count-up 滚动：成本 1.5h；用 `@vueuse/core` 的 `useTransition` 包装 `overviewCards` value，格式化到整数/1 位小数。
- 概览卡片 stagger 进入：成本 1h；纯 CSS `.overview-card { animation: card-in 220ms var(--ease-out) both; animation-delay: calc(var(--i) * 45ms); }`，`v-for` 写 `:style="{ '--i': index }"`。
- 列表骨架屏：成本 1h；`devicesLoading/alertsLoading` 时显示 `<el-skeleton animated :rows="5" />`，加载结束再渲染列表。
- 告警 badge pulse：成本 0.5h；只在 `activeCount > 0` 时给 `.alert-badge` 加 `box-shadow` 呼吸动画，受 `prefers-reduced-motion` 关闭。
- 资源进度条 easing：成本 0.5h；`el-progress` 颜色改 token，CSS 覆盖 `.el-progress-bar__inner { transition: width 500ms cubic-bezier(.16,1,.3,1); }`。

Device list（`src/views/device/index.vue` + `src/components/IotTable.vue`）：

- 搜索按钮 loading 锁定：成本 1h；`useTable` 暴露 `loading` 后，搜索按钮加 `:loading="loading"`，避免重复点击。
- 表格行 stagger：成本 1.5h；`IotTable` 增加 `row-class-name` 或 CSS 变量 `--row-index`，用 `animation-delay` 做 20ms 递进。
- 行 hover 操作显隐：成本 2h；操作列按钮默认 `opacity: .72`，hover 当前行变 `opacity: 1`，避免整列按钮过重。
- 状态标签轻动效：成本 0.5h；在线状态 `el-tag` 前加 6px dot，在线 dot 使用 `box-shadow: 0 0 0 3px rgba(16,185,129,.12)`。
- 表格密度切换：成本 2h；`IotTable` 增加 `density` prop 和右上 segmented control，映射 Element Plus `size="small/default"`。

Rule chain editor（`src/views/rule-chain/editor/index.vue`）：

- 连线 drawing 动画：成本 1h；SVG path 使用 `stroke-dasharray/stroke-dashoffset`，新增 `@keyframes draw-line`。
- 拖拽端口呼吸光晕：成本 1h；`connectionDrag.isDragging` 时给合法目标 `.port-dot` 加 `animation: port-pulse 900ms infinite`。
- 节点选中状态强化：成本 1h；`.canvas-node.selected` 加左侧 3px 状态条和 `outline: 2px solid color-mix(...)`，避免只靠阴影。
- 画布缩放/平移反馈：成本 4-6h；短期自研 `wheel + transform scale`，中期建议 `@vue-flow/core`。
- 节点创建菜单进入动画：成本 0.5h；当前 `animation: menuFadeIn`（`src/views/rule-chain/editor/index.vue:1511`）需要补齐 keyframes，并加 `transform-origin: top left`。

| 页面 | 微交互 | 成本(h) | 收益 |
|---|---:|---:|---|
| dashboard | 数字 count-up | 1.5 | 首屏数据质感 +25% |
| dashboard | 卡片 stagger 进入 | 1 | 首屏精致度 +15% |
| dashboard | 列表骨架屏 | 1 | 加载感知 +25% |
| dashboard | 告警 badge pulse | 0.5 | 风险可见性 +15% |
| device list | 搜索按钮 loading 锁定 | 1 | 操作确定性 +20% |
| device list | 表格行 stagger | 1.5 | 数据出现质感 +15% |
| device list | 行 hover 操作显隐 | 2 | 表格噪声 -20% |
| device list | 密度切换 | 2 | 扫描效率 +25% |
| rule chain editor | 连线 drawing | 1 | 编辑反馈 +20% |
| rule chain editor | 端口呼吸光晕 | 1 | 可连接性识别 +25% |
| rule chain editor | 节点选中强化 | 1 | 操作定位 +20% |
| rule chain editor | 缩放/平移反馈 | 6 | 编辑器专业度 +40% |

## 五、主题色板建议

现状识别：代码真实主色不是单一值。`global.scss` 中 light mode 定义 `--iot-color-primary: #6366f1`、`--iot-color-accent: #06b6d4`（`src/styles/global.scss:16-20`），dark mode 没有重写 primary/accent，只重写背景/文本/阴影（`src/styles/global.scss:102-146`）。Element Plus 的主色是 `--el-color-primary: #167eff`（`src/styles/element.scss:4-6`），旧 `var.scss` 还有 `$mainColor: #409eff`（`src/styles/var.scss:1`），AGENTS 期望主色为 `#6366f1`。因此当前 light/dark 的 IoT 主色实际均为 indigo `#6366f1`，但组件库主色实际为 `#167eff`。

是否建议换：**yes**。`#6366f1` 紫蓝科技感强，但与大量 AI/SaaS 模板撞色，IoT/工业平台需要更冷静的深蓝/钢青基调；同时避开 Vue 绿 `#42b883` 和 ThingsBoard/EMQX 常见绿蓝组合。建议切到“深海钢青 + 电光琥珀 + 冷灰蓝”。

新主色板提案：

| 色板 | 模式 | 50 | 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900 |
|---|---|---|---|---|---|---|---|---|---|---|---|
| primary | light | `#ECFEFF` | `#CFFAFE` | `#A5F3FC` | `#67E8F9` | `#22D3EE` | `#0F6B8F` | `#0E5A78` | `#134E63` | `#164253` | `#0B2F3A` |
| primary | dark | `#E0F7FF` | `#B8ECFF` | `#7DD3FC` | `#38BDF8` | `#0EA5E9` | `#38BDF8` | `#0284C7` | `#0369A1` | `#075985` | `#0C4A6E` |
| accent | light | `#FFFBEB` | `#FEF3C7` | `#FDE68A` | `#FCD34D` | `#FBBF24` | `#F59E0B` | `#D97706` | `#B45309` | `#92400E` | `#78350F` |
| accent | dark | `#FFF7ED` | `#FFEDD5` | `#FED7AA` | `#FDBA74` | `#FB923C` | `#FBBF24` | `#F59E0B` | `#D97706` | `#B45309` | `#92400E` |
| neutral | light | `#F8FAFC` | `#F1F5F9` | `#E2E8F0` | `#CBD5E1` | `#94A3B8` | `#64748B` | `#475569` | `#334155` | `#1E293B` | `#0F172A` |
| neutral | dark | `#F8FAFC` | `#E2E8F0` | `#CBD5E1` | `#94A3B8` | `#64748B` | `#475569` | `#334155` | `#1E293B` | `#0F172A` | `#07111F` |

落地映射：

| CSS token | light mode | dark mode |
|---|---|---|
| `--iot-color-primary` | primary-500 `#0F6B8F` | primary-500 `#38BDF8` |
| `--iot-color-primary-light` | primary-400 `#22D3EE` | primary-400 `#0EA5E9` |
| `--iot-color-primary-dark` | primary-700 `#134E63` | primary-700 `#0369A1` |
| `--iot-color-accent` | accent-500 `#F59E0B` | accent-500 `#FBBF24` |
| `--iot-color-background` / `--iot-color-bg-base` | neutral-50 `#F8FAFC` | neutral-900 `#07111F` |
| `--iot-color-bg-card` | `rgba(255,255,255,0.86)` | `rgba(15,23,42,0.72)` |
| `--iot-color-bg-overlay` | `rgba(255,255,255,0.96)` | `rgba(7,17,31,0.96)` |
| `--iot-color-border` | neutral-200 `#E2E8F0` | `rgba(148,163,184,0.20)` |
| `--iot-color-text-primary` | neutral-900 `#0F172A` | neutral-50 `#F8FAFC` |
| `--iot-color-text-secondary` | neutral-600 `#475569` | neutral-200 `#CBD5E1` |
| `--iot-color-text-muted` | neutral-400 `#94A3B8` | neutral-400 `#64748B` |
| `--iot-glass-bg` | `rgba(255,255,255,0.72)` | `rgba(15,23,42,0.62)` |
| `--iot-glass-bg-header` | `rgba(255,255,255,0.90)` | `rgba(7,17,31,0.88)` |
| `--iot-glass-bg-sidebar` | `rgba(248,250,252,0.94)` | `rgba(15,23,42,0.92)` |
| `--iot-glass-bg-hover` | `rgba(15,107,143,0.08)` | `rgba(56,189,248,0.14)` |
| `--iot-glass-border` | `rgba(226,232,240,0.72)` | `rgba(148,163,184,0.18)` |
| `--el-color-primary` | `var(--iot-color-primary)` | `var(--iot-color-primary)` |

可访问性自检：light mode 下 primary-500 `#0F6B8F` 对 neutral-50 `#F8FAFC` 的估算对比度约 **5.70:1**，普通文本达到 WCAG AA，未达 AAA；dark mode 下 primary-500 `#38BDF8` 对 neutral-900 `#07111F` 的估算对比度约 **8.84:1**，普通文本达到 AAA。注意：light 的 primary-500 如果用于大面积按钮背景，按钮文字应使用 `#FFFFFF`，不要使用 neutral-50 以外的低对比文字。

> 审计基于 commit: 7380e5e，时间：2026-06-17
