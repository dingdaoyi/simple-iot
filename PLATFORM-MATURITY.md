# Simple IoT 平台成熟度评估与迭代规划

> 最后更新：2026-07-22  
> 维护者：simple-iot 团队  
> 评估视角：从物联网平台**完整能力矩阵**出发，不仅看已有 ROADMAP，而是对标成熟 IoT 平台（ThingsBoard / 消智云）的核心能力

---

## 📊 成熟度总览

| 能力域 | 成熟度 | 说明 |
|--------|--------|------|
| 🟢 **设备接入** | 80% | MQTT/HTTP/Modbus TCP 已有，OPC UA/CoAP/LwM2M 缺失 |
| 🟡 **设备管理** | 60% | 基础 CRUD + 分组/标签，缺**影子**、**拓扑**、**批量指令** |
| 🟢 **物模型** | 85% | 属性/服务/事件完整，缺**子设备物模型**、**动态扩展** |
| 🟢 **协议解析** | 90% | Java/JS/Groovy/Lua 热加载，缺**可视化脚本调试器** |
| 🟡 **规则引擎** | 70% | 可视化编排 + 12 种节点，缺**定时触发**、**批处理节点** |
| 🟢 **告警中心** | 75% | 四级告警 + 清除机制，缺**告警抑制**、**升级策略** |
| 🟡 **时序存储** | 65% | InfluxDB 3 写入 + Caffeine 缓存，缺**聚合查询 API**、**降采样** |
| 🔴 **可视化** | 30% | 仅统计卡片，缺**自定义仪表板**、**设备轨迹地图** |
| 🟡 **通知推送** | 60% | 邮件/短信/HTTP，缺**钉钉/企微/飞书**、**语音通知** |
| 🔴 **多租户** | 0% | 全局单租户，无 tenant_id / 配额隔离 |
| 🟡 **权限控制** | 55% | Sa-Token RBAC，缺**数据权限**、**API 级限流** |
| 🟢 **OTA升级** | 80% | 固件上传 + 分组推送，缺**差分包**、**断点续传** |
| 🟡 **数据存储** | 60% | 时序 + RustFS S3，缺**冷热分离**、**自动归档** |
| 🔴 **边缘计算** | 0% | 无边缘侧 agent / 本地规则引擎 |
| 🟡 **运维监控** | 50% | Prometheus 指标，缺**链路追踪**、**慢查询分析** |

**总体评分：62%** — 已具备**轻量级 IoT 平台核心框架**，距离**生产级商业平台**还有 4-5 个迭代周期。

---

## 🚨 关键缺失能力（按优先级）

### 🔴 P0 - 生产就绪阻塞项

| # | 能力 | 当前状态 | 影响 | 目标状态 |
|---|------|----------|------|----------|
| **P0-4** | 认证强化 | admin/123456 明文，无密码策略 | ⚠️ 生产部署必被入侵 | JWT 过期 + 首次登录强制改密 + 复杂度校验 |
| **P0-8** | 设备影子 | 无 | 离线设备状态无法保持，移动网络场景不可用 | 设备期望状态 + 上报状态 + 差量下发 |
| **P0-9** | 数据聚合 API | InfluxDB 只有原始写入，无查询封装 | 前端无法绘制趋势图 | `/device/{id}/telemetry/agg?interval=1h&fn=avg` |

### 🟡 P1 - 用户高频诉求

| # | 能力 | 原 ROADMAP | 补充需求 |
|---|------|-----------|----------|
| **P1-1** | 多租户 | ✅ 已列入 | ⚠️ 需**数据权限引擎**（租户隔离 + 部门隔离） |
| **P1-5** | 自定义仪表板 | ✅ 已列入 | 需**组件库**：折线图/仪表盘/地图/表格/视频流 |
| **P1-7** | 移动端 UI | ✅ 已列入 | 需**响应式布局** + **PWA 离线** + **扫码绑定** |
| **P1-9** | 批量指令 | ❌ 缺失 | 选中 100 台设备 → 一键重启/升级/配置下发 |
| **P1-10** | 设备拓扑 | ❌ 缺失 | 网关-子设备树形关系，子设备通过网关通信 |
| **P1-11** | 定时规则 | ❌ 缺失 | 定时触发规则链（如每晚 23:00 检查告警） |

### 🟢 P2 - 协议与集成

| # | 能力 | 原 ROADMAP | 补充需求 |
|---|------|-----------|----------|
| **P2-1** | Modbus TCP | ✅ 已实现 | ✅ 已验证可用 |
| **P2-2** | OPC UA | ✅ 已列入 | Eclipse Milo 客户端 + 订阅机制 |
| **P2-4** | HTTP Webhook | ✅ 已实现 | ✅ HMAC 签名验证已有 |
| **P2-7** | 第三方推送 | ❌ 缺失 | 钉钉机器人 / 企业微信 / 飞书 webhook |
| **P2-8** | 数据转发 | ❌ 缺失 | Kafka / MQTT Bridge / HTTP 批量推送 |

### 🔵 P3 - 高级特性

| # | 能力 | 说明 |
|---|------|------|
| **P3-1** | 影子设备差量下发 | 只推送变更字段，节省流量 |
| **P3-2** | 时序数据降采样 | 1年前数据自动按小时聚合 |
| **P3-3** | 告警抑制规则 | 同设备同类型告警 5min 内只发一次 |
| **P3-4** | 规则链性能分析 | 每个节点执行耗时统计 + 瓶颈标红 |
| **P3-5** | 设备轨迹回放 | GPS 设备历史轨迹地图动画 |
| **P3-6** | 边缘计算网关 | Go 写的轻量 agent，本地执行规则链 |

---

## 📋 重新规划的迭代路线

### 🎯 Sprint 1: 生产就绪补完（2周）
**目标：让平台可以安全上生产**

- [ ] **P0-4 认证强化**
  - JWT refresh token + 7天过期
  - 首次登录强制改密（User 表加 `force_change_pwd` 字段）
  - 密码复杂度校验（8位+大小写+数字）
  
- [ ] **P0-8 设备影子基础版**
  - 新表 `tb_device_shadow`（device_id, desired_state JSONB, reported_state JSONB, version, updated_at）
  - API: `POST /device/{id}/shadow/desired` + `GET /device/{id}/shadow`
  - MQTT topic: `shadow/{productKey}/{deviceKey}/update`
  
- [ ] **P0-9 时序数据聚合 API**
  - `DeviceDataController.queryAggregated(deviceId, propertyKey, start, end, interval, function)`
  - InfluxDB Flux 查询封装：`aggregateWindow(every: 1h, fn: mean)`
  - 前端折线图组件对接

**验收标准：**
- ✅ 演示服务器改掉默认密码 admin/Admin@2026
- ✅ 离线设备上线后拉取影子配置生效
- ✅ 仪表板绘制温度 24h 趋势图

---

### 🎯 Sprint 2: 核心交互增强（2周）
**目标：提升运维效率**

- [ ] **P1-9 批量设备指令**
  - 设备列表多选 → "批量操作" 下拉菜单
  - 后端 `DeviceCommandService.batchSendCommand(List<Integer> deviceIds, String serviceKey, Map<String, Object> params)`
  - 进度追踪表 `tb_batch_task`（状态：pending/running/success/failed）
  
- [ ] **P1-10 设备拓扑（网关-子设备）**
  - Device 表加 `parent_id`（网关 ID）
  - 新表 `tb_device_topology`（parent_id, child_id, bind_time）
  - 子设备数据上报路径：网关设备协议脚本解析 → 路由到子设备
  
- [ ] **P1-11 定时规则触发**
  - 新节点类型 `ScheduleTriggerNode`（cron 表达式）
  - RuleChainEngine 启动时注册 ScheduledExecutorService
  - 定时触发生成虚拟消息进入规则链

**验收标准：**
- ✅ 选中 50 台设备一键重启，进度条显示完成度
- ✅ 一个网关下挂 10 个 Modbus 从站，数据正常上报
- ✅ 每晚 23:00 自动检查所有设备离线状态并告警

---

### 🎯 Sprint 3: 可视化与移动端（3周）
**目标：让平台"看起来像个平台"**

- [ ] **P1-5 自定义仪表板 v1**
  - 新表 `tb_dashboard`（name, layout JSON, widgets JSON）
  - 组件库（前端 Vue）：
    - 数值卡片（实时值 + 对比昨日）
    - 折线图（ECharts + 时序聚合 API）
    - 仪表盘（温度/压力/转速）
    - 表格（设备列表 + 状态）
  - 拖拽布局（grid-layout）
  
- [ ] **P1-7 移动端响应式**
  - Element Plus 表格改为移动端卡片列表
  - 侧边栏改抽屉
  - 添加 PWA manifest.json + service worker
  
- [ ] **P3-5 设备轨迹地图（如果有 GPS 设备）**
  - 高德地图 / Mapbox GL JS
  - 播放控件：播放/暂停/速度调节

**验收标准：**
- ✅ 创建"车间监控"仪表板，6 个组件实时刷新
- ✅ 手机浏览器访问，所有页面可操作
- ✅ GPS 设备轨迹地图回放（如无 GPS 设备，降为 P4）

---

### 🎯 Sprint 4: 企业级特性（3周）
**目标：支撑中小型商用部署**

- [ ] **P1-1 多租户架构**
  - 所有业务表加 `tenant_id BIGINT`
  - MyBatis-Plus TenantLineHandler 自动注入 WHERE 条件
  - 新表 `tb_tenant`（配额：max_devices, max_rules, storage_quota_gb）
  - 超级管理员租户切换功能
  
- [ ] **P2-7 第三方推送集成**
  - 钉钉机器人 / 企业微信 / 飞书 webhook
  - 告警推送选择通道
  
- [ ] **P3-3 告警抑制**
  - 同设备 + 同类型 + 5min 内 → 只发一次
  - 告警升级：warning → 30min 未清除 → critical

**验收标准：**
- ✅ 租户 A 看不到租户 B 的设备
- ✅ 告警推送到钉钉群
- ✅ 同设备连续掉线不会短信轰炸

---

### 🎯 Sprint 5: 协议与驱动扩展（2周）

- [ ] **P2-2 OPC UA 客户端**
  - Eclipse Milo 集成
  - OPC UA 连接配置页面（endpoint / security policy）
  - 节点订阅 → 数据上报到设备
  
- [ ] **P2-3 CoAP 服务器**
  - Californium 框架
  - CoAP POST `/telemetry` 接收数据
  
- [ ] **P2-8 数据转发 Kafka**
  - 新规则节点 `KafkaOutputNode`
  - 配置页面：bootstrap.servers / topic / 序列化格式

**验收标准：**
- ✅ 连接 KEPServerEX，读取 PLC 数据
- ✅ NB-IoT 模组通过 CoAP 上报
- ✅ 所有设备数据实时转发到 Kafka

---

### 🎯 Sprint 6+: 长期演进

- **边缘计算**：Go 写的边缘 agent，拉取规则链本地执行
- **AI 集成**：异常检测模型（Prophet / LSTM）+ 预测性维护
- **数据分析**：Spark / Flink 离线 / 实时分析任务
- **SaaS 化**：付费套餐 + Stripe 集成 + 用量计费

---

## 🎬 下一步行动

**立即启动 Sprint 1，目标 2 周内完成：**

1. **P0-4 认证强化** — 今天开始，预计 3 天
2. **P0-8 设备影子** — 第 4-8 天，核心功能
3. **P0-9 时序聚合 API** — 第 9-12 天，前端对接
4. 全部完成后 → 部署到演示服务器 → 写 CHANGELOG v0.2.0

**开工顺序：**
```bash
cd ~/IdeaProjects/sample-iot
git checkout -b feat/sprint1-production-ready
# 先做 P0-4 认证强化（影响最小，快速见效）
```

需要我立即开始执行吗？
