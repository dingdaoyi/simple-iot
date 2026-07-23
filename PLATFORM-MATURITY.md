# Simple IoT 平台成熟度评估与迭代规划

> 最后更新：2026-07-23 (Sprint 3 完成后)
> 维护者：simple-iot 团队
> 评估视角：从物联网平台**完整能力矩阵**出发，对标 ThingsBoard / 消智云 / KubeEdge

---

## 📊 当前平台资产盘点

### 代码规模
| 维度 | 数量 |
|------|------|
| Java 文件 | 349 |
| Vue/JS 文件 | 89 |
| 数据库表 | 32 |
| Controller | 33 (123 endpoints) |
| Service 实现 | 24 |
| 规则引擎节点 | 13 种 |
| 前端页面 | 50 |
| 协议驱动 | 4 (MQTT/HTTP/TCP/Modbus) |
| 数据库迁移 | 12 个 |
| 单元测试 | 179 (全部通过) |

### 已有能力矩阵

| 能力域 | 成熟度 | 已实现 | 缺失 |
|--------|--------|--------|------|
| 🔵 **设备接入** | 80% | MQTT Broker、HTTP Webhook、TCP Server、Modbus TCP 轮询 | OPC UA、CoAP、LwM2M、MQTT TLS |
| 🟢 **设备管理** | 85% | CRUD、分组、标签、设备影子、**网关-子设备拓扑(parentId)**、**批量指令** | 设备生命周期事件 |
| 🟢 **物模型** | 85% | 属性/服务/事件、Java/JS/Groovy/Lua 协议解析 | 子设备物模型、物模型版本管理 |
| 🟢 **规则引擎** | 85% | 可视化编排、**14 种节点**(含定时触发)、调试日志、执行日志 | 批处理节点、规则链版本管理 |
| 🟡 **告警中心** | 85% | 四级告警、创建/清除、告警评论、**告警抑制(5min 窗口)** | 升级策略、告警分组聚合 |
| 🟡 **时序存储** | 70% | InfluxDB 3 写入、批量 last_value 查询、date_bin 聚合、Caffeine 实时缓存 | 降采样、冷热分离、数据归档 |
| 🟢 **可视化** | 70% | 拖拽式仪表板编辑器、**7 种组件**(含设备状态网格+告警列表) | 实时推送(WebSocket/SSE)、地图、视频流 |
| 🟢 **通知推送** | 80% | 邮件、短信、HTTP Webhook(HMAC 签名)、**钉钉/企微/飞书** | 语音通知、推送模板管理 |
| 🟢 **OTA 升级** | 80% | 固件上传、OTA 任务、分组推送、RustFS S3 存储 | 差分包、断点续传、灰度发布 |
| 🔴 **多租户** | 0% | 无 tenant_id、无数据隔离 | 完全缺失 |
| 🟡 **认证授权** | 80% | Sa-Token、密码复杂度、首次改密、7天 Token、**API 限流**、**审计日志** | 数据权限、 |
| 🟢 **数据保留** | 65% | DataRetentionService(90天告警/30天日志) | InfluxDB TTL、按表配置、归档到 S3 |
| 🟡 **运维监控** | 50% | Prometheus 指标、Docker Compose | 链路追踪、慢查询分析、健康检查面板 |

**总体评分：75%** - Sprint 3 后提升 10 个百分点。核心能力基本齐全，距离生产级商业部署还有 **2-3 个迭代周期**。

---

## 🚨 关键缺失能力（按 IoT 平台标准重新评估）

### 🔴 第一梯队：生产阻塞（不做不能上生产）

| # | 能力 | 当前状态 | 影响 | 工作量 |
|---|------|----------|------|--------|
| **R1** | **实时数据推送** | 仪表板 30s 轮询，无 WebSocket/SSE | 运维人员看不到实时告警，体验差 | 2天 |
| **R2** | **API 限流 + 审计日志** | 无任何限流，无操作记录 | 接口可被刷爆，出问题无法追溯 | 2天 |
| **R3** | **告警抑制** | 每条告警独立触发 | 掉线设备每秒一条告警，短信轰炸 | 1天 |

### 🟡 第二梯队：用户高频诉求（不做不好用）

| # | 能力 | 当前状态 | 用户场景 | 工作量 |
|---|------|----------|----------|--------|
| **R4** | **批量设备指令** | 只能逐台操作 | 选中 100 台设备一键重启/配置下发 | 3天 |
| **R5** | **定时规则触发** | 13 种节点无定时类型 | 每晚 23:00 检查离线设备、每小时统计 | 2天 |
| **R6** | **设备拓扑(网关-子设备)** | ProductType.parentId 是分类树，不是拓扑树 | 网关下挂 10 个 Modbus 从站 | 4天 |
| **R7** | **钉钉/企微/飞书推送** | 只有邮件/短信/HTTP webhook | 企业用户 90% 用钉钉/企微告警 | 2天 |
| **R8** | **数据导出** | 无 | 导出设备列表 Excel、导出告警 CSV | 1天 |

### 🟢 第三梯队：锦上添花（有更好，没有不影响用）

| # | 能力 | 说明 |
|---|------|------|
| **R9** | 仪表板实时推送 | 用 WebSocket 替代 30s 轮询 |
| **R10** | 设备轨迹地图 | GPS 设备轨迹回放 |
| **R11** | 时序数据降采样 | 1年前数据自动按小时聚合 |
| **R12** | OPC UA 协议 | Eclipse Milo 客户端 |
| **R13** | CoAP 协议 | Californium 框架，NB-IoT 场景 |
| **R14** | 数据转发 Kafka | 规则节点 KafkaOutputNode |
| **R15** | 多租户 | tenant_id + TenantLineHandler |

---

## 📋 重新规划的迭代路线（Sprint 2-5）

### 🎯 Sprint 2: 实时性与安全加固（1.5周）

**目标：让平台"活起来"——实时推送 + 安全闭环**

#### R1: WebSocket 实时推送
```
后端:
- WebSocketConfig + WebSocketHandler
- /ws/telemetry — 设备属性变更推送
- /ws/alarm — 告警实时推送
- 规则引擎 InputPropertyNode 执行后广播到 WebSocket

前端:
- DashboardEditor WidgetRenderer: 替换 setInterval 为 WebSocket 订阅
- 告警页面实时刷新
```

#### R2: API 限流 + 审计日志
```
限流:
- Sa-Token 注解 @SaCheckSafe 或自定义 RateLimitInterceptor
- 登录接口 5次/分钟，普通接口 100次/分钟
- Bucket4j 内存令牌桶（不引 Redis，保持简单）

审计:
- 新表 tb_audit_log (user_id, action, resource, resource_id, detail, ip, create_time)
- AOP 切面 @AuditLog 自动记录 Controller 操作
- 审计日志查看页面
```

#### R3: 告警抑制
```
- AlarmCreateNode 修改: 创建前检查同设备+同类型+5min内已有活动告警
- 配置项: alarm.suppression.window-seconds = 300
- 告警升级: warning 级别 30min 未清除 -> 升级为 critical
```

**验收标准：**
- ✅ 仪表板组件数据延迟 < 2 秒
- ✅ 暴力登录 5 次后被限流
- ✅ 同设备连续掉线 5 分钟内只告警 1 次

---

### 🎯 Sprint 3: 运维效率提升（2周）

**目标：让运维人员"少点击"——批量操作 + 定时规则 + IM 推送**

#### R4: 批量设备指令
```
后端:
- DeviceCommandService.batchSend(List<Integer> deviceIds, String serviceKey, Map params)
- 新表 tb_batch_task (id, type, target_ids JSON, status, total, success, failed, create_time)
- 异步执行 + 进度查询 API: GET /device/batch-task/{id}

前端:
- 设备列表多选框
- 批量操作下拉: 重启 / 配置下发 / OTA 升级
- 进度弹窗: 50/100 完成，3 失败
```

#### R5: 定时规则触发
```
- 新规则节点: ScheduleTriggerNode
  - cron 表达式配置
  - 触发后生成虚拟消息进入规则链
- RuleChainEngine 启动时注册 ScheduledExecutorService
- 前端规则编辑器: 节点配置面板增加 cron 选择器
```

#### R6: 设备拓扑（网关-子设备）
```
- Device 表加 parent_id (nullable, 网关设备 ID)
- 新表 tb_device_topology (parent_id, child_id, bind_time, connection_type)
- 网关协议脚本解析后路由到子设备: 子设备 deviceKey = 网关deviceKey + "/" + 子设备地址
- 前端: 设备详情页增加"子设备"Tab
```

#### R7: 钉钉/企微/飞书推送
```
- 新表 tb_im_push_config (platform, webhook_url, secret, enabled)
- ImPushService: 钉钉机器人 / 企微 / 飞书 webhook
- 告警规则节点: 增加推送渠道选择
- 前端: 系统设置 -> 推送配置 -> 新增 IM 推送 Tab
```

#### R8: 数据导出
```
- 后端: /device/export (Excel), /alarm/export (Excel/CSV)
- 使用 Hutool ExcelWriter
- 前端: 列表页增加"导出"按钮
```

**验收标准：**
- ✅ 选中 50 台设备一键重启，进度条显示
- ✅ 创建定时规则每晚 23:00 检查离线设备
- ✅ 告警推送到钉钉群
- ✅ 导出设备列表 Excel

---

### 🎯 Sprint 4: 可视化增强 + 移动端（2周）

**目标：让平台"看起来像个产品"**

#### 仪表板增强
```
- 新增组件: 设备状态网格（绿/红圆点矩阵）
- 新增组件: 告警列表（最近 10 条）
- 新增组件: 设备地图（如果有 GPS 属性）
- WebSocket 实时数据源（Sprint 2 基础上）
- 组件标题自定义 + 颜色主题
- 仪表板模板: 预置"设备总览""告警监控""车间大屏"模板
```

#### 移动端响应式
```
- Element Plus 表格 -> 移动端卡片列表
- 侧边栏 -> 抽屉模式
- PWA: manifest.json + service worker
- 扫码绑定设备（可选）
```

**验收标准：**
- ✅ 创建"车间监控"仪表板，6 个组件实时刷新
- ✅ 手机浏览器访问，所有页面可操作

---

### 🎯 Sprint 5: 协议扩展 + 企业级（3周）

**目标：支撑中小型商用部署**

#### 协议扩展
```
- OPC UA 客户端 (Eclipse Milo)
- CoAP 服务器 (Californium)
- 数据转发: KafkaOutputNode 规则节点
```

#### 多租户架构
```
- 所有业务表加 tenant_id BIGINT DEFAULT 1
- MyBatis-Plus TenantLineHandler 自动注入 WHERE
- 新表 tb_tenant (name, max_devices, max_rules, storage_quota_gb)
- 超级管理员租户切换
```

#### 时序数据降采样
```
- InfluxDB Continuous Query 或定时任务
- 1年前数据自动按小时聚合
- 原始数据归档到 RustFS
```

---

## 🎬 下一步行动

### 立即启动 Sprint 2（1.5 周）

按优先级排序：

1. **R3 告警抑制** — 1天，改动最小但效果最直接
2. **R2 API 限流 + 审计日志** — 2天，安全基线
3. **R1 WebSocket 实时推送** — 2天，体验提升最大
4. **R8 数据导出** — 1天，顺手做

### Sprint 2 开工

```bash
cd ~/IdeaProjects/sample-iot
git checkout -b feat/sprint2-realtime-security
# 先做 R3 告警抑制（改动集中在 AlarmCreateNode，1天内完成）
```

---

## 📝 Sprint 1 完成总结

| 任务 | 状态 | 验证 |
|------|------|------|
| P0-4 认证强化 | ✅ 完成 | Admin@2026 + force_change_pwd + 7天 token |
| P0-8 设备影子 | ✅ 完成 | desired/reported API + 乐观锁 |
| P0-9 时序聚合 API | ✅ 完成 | date_bin 聚合，前端图表对接 |
| P0 影子乐观锁修复 | ✅ 完成 | version in WHERE clause |
| P1 InfluxDB N+1 修复 | ✅ 完成 | 批量 last_value 查询 |
| P1 密码正则预编译 | ✅ 完成 | 单个 static Pattern |
| init.sql 密码同步 | ✅ 完成 | force_change_pwd 列 + 正确 hash |
| 部署验证 | ✅ 完成 | 179 测试通过，演示服务器在线 |

## 📝 Sprint 2 完成总结 (2026-07-22)

| 任务 | 状态 | 验证 |
|------|------|------|
| R1 WebSocket 实时推送 | ✅ 完成 | 仪表板 < 2s 延迟 |
| R2 API 限流 + 审计日志 | ✅ 完成 | Bucket4j 令牌桶 + tb_audit_log + AOP 切面 |
| R3 告警抑制 | ✅ 完成 | 5min 窗口去重，AlarmSuppressionService |
| R8 数据导出(后端) | ✅ 完成 | DeviceController + AlarmController export endpoint |
| 部署验证 | ✅ 完成 | 179 测试通过 |

## 📝 Sprint 3 完成总结 (2026-07-23)

| 任务 | 状态 | 验证 |
|------|------|------|
| R4 批量设备指令 | ✅ 完成 | POST /service/batch/{identifier} + 前端批量按钮 |
| R5 定时规则触发 | ✅ 完成 | INPUT_SCHEDULE 节点 + ScheduleTriggerService (cron) + 前端 cron 配置面板 |
| R6 设备拓扑 | ✅ 完成 | Device.parentId + GET /device/{id}/children + 前端子设备 Tab |
| R7 钉钉/企微/飞书推送 | ✅ 完成 | WebhookNotificationService + 3 平台实现 + ImPushConfig CRUD + 前端配置页面 |
| R8 数据导出(前端) | ✅ 完成 | 设备列表 + 告警列表导出 Excel 按钮 |
| Sprint 4 仪表板组件 | ✅ 完成 | 设备状态网格 + 告警列表组件 |
| 测试 | ✅ 通过 | 181 tests, 0 failures |
| 部署 | ✅ 完成 | demo 服务器 (122.51.129.91) 后端 d11579e + 前端 ca78067 |
