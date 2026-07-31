# Simple IoT 工作计划

> 生成时间: 2026-07-31 | 基于全局代码审查（知识图谱 + 源码扫描）
>
> 执行方式: 按梯队顺序逐项完成，每项完成后在对应 checkbox 打勾。

---

## 目录

- [第一梯队：修 Bug + 补测试](#第一梯队修-bug--补测试)
- [第二梯队：TODO 清理 + 重构](#第二梯队todo-清理--重构)
- [第三梯队：前端迁移收尾](#第三梯队前端迁移收尾)
- [第四梯队：功能扩展](#第四梯队功能扩展)
- [附录 A：复杂度热点](#附录-a复杂度热点)
- [附录 B：测试覆盖缺口](#附录-b测试覆盖缺口)
- [附录 C：配置与安全隐患](#附录-c配置与安全隐患)

---

## 第一梯队：修 Bug + 补测试

> 预计 1-2 天，优先级最高

### 1.1 修复 DeviceShadow 乐观锁（P0）✅

- [x] 完成

**位置:** `iot-server/src/main/java/com/github/dingdaoyi/service/impl/DeviceShadowServiceImpl.java` - `updateDesired()` / `updateReported()`

**状态:** 已修复。`updateDesired()` 和 `updateReported()` 均已在 WHERE 条件中带上 `eq(DeviceShadow::getVersion, oldVersion)`，更新失败抛 `BusinessException("影子版本冲突，请重试")`。

**剩余:** 无并发测试守护，见 1.5。

---

### 1.2 InfluxDB 批量查询（P1）ℹ️ 待定

- [ ] 完成

**位置:** `iot-server/src/main/java/com/github/dingdaoyi/iot/influx/InfluxDataProcessor.java` - `getLatestData()` L149

**现状:** 当前是**逐属性** `ORDER BY time DESC LIMIT 1` 查询。这是有意为之——InfluxDB v3 的 `last_value()` 返回写入顺序而非时间最新的值（已验证踩坑），所以不能用单次 `last_value(*)` 替代。

**可优化方向（低优先级）:** 如果属性数量多且成为性能瓶颈，可考虑：
1. 用 InfluxDB v3 的 `LAST(value, field)` 聚合函数配合子查询
2. 或在上层加 Caffeine 缓存（设备最新值，短 TTL 如 5s）

**注意:** 当前方案正确性 > 性能，不要为了减少查询次数回退到 `last_value(*)`。

---

### 1.3 密码正则优化（P1）✅

- [x] 完成

**位置:** `iot-server/src/main/java/com/github/dingdaoyi/service/impl/UserServiceImpl.java` - `validatePassword()`

**状态:** 已修复。已用 `private static final Pattern PWD_PATTERN = Pattern.compile(...)` 预编译，`PWD_PATTERN.matcher(password).matches()`。

---

### 1.4 关闭生产环境 SQL 日志

- [ ] 完成

**位置:** `iot-server/src/main/resources/application.yml` L74

**问题:** `log-impl: org.apache.ibatis.logging.stdout.StdOutImpl` 在生产环境打印所有 SQL 到 stdout。当前只有 `application.yml`，没有 `application-dev.yml` 做 profile 分离。

**修复方案:**

```yaml
# application.yml (默认)
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

# application-dev.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

---

### 1.5 补充核心模块测试

- [ ] 完成

当前 40 个测试文件 / 369 个主代码文件 ≈ 10.8% 文件覆盖率，160 个 `@Test` 用例。规则引擎覆盖好，但核心业务模块裸奔。

**需要补测试的模块（按优先级）:**

| 模块 | 测试重点 |
|------|----------|
| `DeviceShadowService` | 影子读写、版本冲突（守护 1.1 的修复） |
| `DeviceService` | CRUD、在线状态更新、删除校验 |
| `AlarmService` | 告警创建、确认、清除、评论 |
| `InfluxDataProcessor` | 数据写入、最新值查询、事件日志 |
| `UserService` | 登录、改密、密码策略 |
| `ProductService` | 产品 CRUD、物模型关联 |

**目标:** 每个模块至少 3-5 个核心路径测试。

---

## 第二梯队：TODO 清理 + 重构

> 预计 3-5 天

### 2.1 设备删除校验

- [ ] 完成

**位置:** `DeviceServiceImpl.removeById()` L99-103

**当前:** `// TODO 判断子设备,是否可以删除`，直接 `super.removeById(id)`。

**实现:**
1. 查询是否有子设备（`parent_id = id`），有则拒绝
2. 查询是否关联设备分组，有则提示先移除
3. 查询是否有活跃告警，可选：拒绝或级联清除
4. 删除关联的设备影子、物模型数据

---

### 2.2 指令下发日志完善

- [ ] 完成

**位置:** `ServiceEndpoint.java` L27

**当前:** `// TODO 指令下发记录日志如果错误需要知道原因`

**实现:**
1. 下发失败时记录：设备 ID、指令内容、失败原因（超时/设备离线/协议错误）
2. 在 `ServiceHandler.sendMessage()` 中捕获异常并写入日志表
3. 前端设备详情可查看指令下发历史

---

### 2.3 ModbusPollingService.poll() 拆分

- [ ] 完成

**位置:** `iot-server/src/main/java/com/github/dingdaoyi/driver/modbus/ModbusPollingService.java`

**问题:** 圈复杂度 12，认知复杂度 20，含嵌套循环 + 线性扫描。

**重构方向:**
1. 提取 `pollDevice(device)` 方法处理单设备轮询
2. 提取 `parseRegisters(response, config)` 方法处理寄存器解析
3. 提取 `buildDataPoints(parsedValues)` 方法构建数据点
4. 主循环只保留调度逻辑

---

### 2.4 产品类型树缓存

- [ ] 完成

**位置:** `ProductTypeServiceImpl.listByParentId()` L26-36

**当前:** `// TODO 换成内存方式`，每次查库。

**实现:**
1. 用 Caffeine 缓存产品类型树，key = `product_type_tree`
2. 增删改产品类型时主动失效缓存
3. 项目已有 Caffeine 依赖（`spring.cache.type=caffeine`），直接用 `@Cacheable`

---

### 2.5 InfluxDB 事件日志分页

- [ ] 完成

**位置:** `InfluxDataProcessor.eventLogs()` L235-260

**当前:** `// TODO 需要解决分页等问题`

**实现:**
1. InfluxDB 3 用 `LIMIT` + `OFFSET` 或时间游标分页
2. 返回 `PageResult<EventLog>` 格式
3. 前端事件日志表格接入分页

---

### 2.6 MQTT 设备自动注册

- [ ] 完成

**位置:** `MqttDriver.java` L69

**当前:** `// TODO, 后续做自动注册,需要放开`

**实现:**
1. 设备首次 MQTT 连接时，根据 clientId（deviceKey）查询设备
2. 如果不存在，根据配置的默认产品自动创建设备
3. 需要配置开关：`mqtt.auto-register=true/false`
4. 自动注册的设备标记 `activeStatus=false`，需手动激活

---

### 2.7 物模型结构体字段兼容

- [ ] 完成

**位置:** `ModelPropertyServiceImpl.update()` L152-164

**当前:** `// TODO 暂时未对于结构体字段的增加删除兼容`

**实现:**
1. 当属性 `dataType=STRUCT` 时，对比新旧子属性列表
2. 新增的子属性：插入
3. 删除的子属性：标记删除 + 清理 InfluxDB 对应字段
4. 修改的子属性：更新

---

### 2.8 通道激活状态校验

- [ ] 完成

**位置:** `ServiceHandler.sendMessage()` L45

**当前:** `// TODO 判断通道是否激活`

**实现:**
1. 下发指令前查询设备所属产品 → 驱动 → 通道状态
2. 通道未激活时返回明确错误：`"驱动通道未激活，无法下发指令"`
3. 在 `ResultCode` 中增加 `DRIVER_INACTIVE` 状态码

---

## 第三梯队：前端迁移收尾

> 预计 3-5 天

### 3.1 剩余页面迁移到 IotTable + useTable

- [ ] 完成

**背景:** dwyl-ui 已移除，但只有 device、protocol 等少数页面完成了迁移。

**已迁移（57 处 IotTable/useTable 引用）:**
- ✅ device/index.vue
- ✅ protocol/index.vue
- ✅ layout、dashboard

**未迁移（11 个文件用原生 el-table）:**

| 页面 | 文件 |
|------|------|
| Modbus 管理 | `views/modbus/index.vue` |
| OTA 升级 | `views/ota/index.vue` |
| Webhook | `views/webhook/index.vue` |
| 驱动管理 | `views/driver/index.vue` |
| IM 推送 | `views/im-push/index.vue` |
| 设备分组 | `views/device-group/index.vue` |
| 短信配置 | `views/system/sms/index.vue` |
| 邮件配置 | `views/system/email/index.vue` |
| 仪表盘组件 | `views/dashboard/WidgetRenderer.vue` |
| 设备详情组件 | `views/device/widget/details.vue` |
| 物模型组件 | `views/tslModel/widget/paramShow.vue` |

**迁移步骤（参考 REFACTOR_GUIDE.md）:**
1. 替换 `el-table` 为 `IotTable` 组件
2. 用 `useTable()` composable 管理表格状态
3. 用 `useForm()` composable 管理编辑对话框
4. 确保分页、搜索、删除功能正常

---

### 3.2 清理 useTable.js 兼容代码

- [ ] 完成

**位置:** `iot-web/src/composables/useTable.js`

**当前:** L13 `// 查询参数 - 完全兼容原 useDwTable 的参数名`，L148 `// dwTable - 返回表格配置对象(兼容原 useDwTable)`

**操作:** 所有页面迁移完成后，移除 dwyl-ui 兼容注释和多余的 dwTable 配置对象。

---

### 3.3 引入前端测试

- [ ] 完成

**当前:** 前端零测试。

**实现:**
1. 安装 Vitest + @vue/test-utils
2. 优先覆盖 composables：`useTable`、`useForm`、`useTheme`、`useSidebar`
3. 覆盖 utils：`request.js`（mock axios）、`date_utils.js`、`storage.js`
4. CI 中增加 `pnpm test` 步骤

---

## 第四梯队：功能扩展

> 按 ROADMAP 优先级排列，每项为独立特性

### 4.1 多租户（ROADMAP P1-1）⏭️ 跳过

- [x] 跳过

**状态:** 用户明确跳过多租户特性，当前单租户模式满足需求。

---

### 4.2 Driver SDK（ROADMAP P2-6）

- [ ] 完成

**范围:**
1. 抽象 `Driver` 接口：`connect()` / `disconnect()` / `onMessage()` / `sendCommand()`
2. Maven archetype `simple-iot-driver-archetype`
3. 示例驱动：TCP、MQTT、HTTP（从现有代码提取）
4. 驱动生命周期管理：加载、卸载、热更新
5. 贡献者文档：如何写一个驱动

---

### 4.3 移动端适配（ROADMAP P1-7）

- [ ] 完成

**范围:**
1. 响应式布局（Element Plus 的 `el-row`/`el-col` 断点）
2. 只读视图优先：首页概览、设备列表、告警列表
3. 触摸友好的操作：滑动确认、底部导航
4. PWA 已有基础（`sw.js` + `manifest.json`），完善离线缓存

---

### 4.4 Helm Chart（ROADMAP P4-1）

- [ ] 完成

**范围:**
1. `deploy/helm/simple-iot/` 目录
2. Chart.yaml + values.yaml + templates/
3. 支持 PostgreSQL、InfluxDB、RustFS 作为依赖 chart
4. 可配置资源限制、副本数、Ingress
5. `helm install simple-iot ./deploy/helm/simple-iot`

---

### 4.5 OPC UA 驱动（ROADMAP P2-2）

- [ ] 完成

**范围:**
1. 集成 Eclipse Milo 客户端
2. 节点浏览 + 订阅
3. 数据类型映射到物模型
4. 前端驱动配置页面

---

## 附录 A：复杂度热点

| 方法 | 圈复杂度 | 认知复杂度 | 文件 |
|------|----------|------------|------|
| `ModbusPollingService.poll()` | 12 | 20 | driver/modbus/ModbusPollingService.java |
| `ServiceHandler.sendMessage()` | 11 | 18 | service/ServiceHandler.java |
| `RuleChainServiceImpl.validateDraft()` | 11 | 17 | service/impl/RuleChainServiceImpl.java |
| `ScriptProtocolDecoder.parseScriptResult()` | 10 | 22 | iot/proto/script/ScriptProtocolDecoder.java |
| `AlarmCreateNode.execute()` | 9 | 13 | rule/node/AlarmCreateNode.java |
| `ScriptProtocolDecoder.decode()` | 7 | 11 | iot/proto/script/ScriptProtocolDecoder.java |
| `InfluxDataProcessor.process()` | 6 | 12 | iot/influx/InfluxDataProcessor.java |

> 认知复杂度 ≥ 15 的方法建议优先拆分。

---

## 附录 B：测试覆盖缺口

**已有测试（40 文件，160 个 `@Test`）:**
- ✅ 规则引擎 12/12 节点类型
- ✅ 协议脚本解码/编码（ProtocolControllerScriptTest, ProtocolServiceScriptTest）
- ✅ Modbus 帧解析（ModbusFrameTest）
- ✅ MQTT 认证（MqttServerAuthHandlerTest）
- ✅ 集成测试（ApplicationPostgresIntegrationTest）
- ✅ InfluxDB 空列表边界（InfluxDataProcessorTest）
- ✅ Demo 遥测端到端（DemoTelemetrySeedSmokeTest）

**无测试的高风险模块:**

| 模块 | 文件数 | 风险 |
|------|--------|------|
| DeviceController + DeviceService | 3 | 设备 CRUD 是核心路径 |
| DeviceShadowService | 1 | 有 P0 并发 bug，无测试守护 |
| AlarmController + AlarmService | 2 | 告警是核心业务 |
| UserController + UserService | 2 | 认证安全 |
| ProductService + ProductTypeService | 4 | 产品管理 |
| DashboardController | 1 | 数据聚合逻辑 |
| OtaController + OtaService | 2 | 固件升级 |
| InfluxDataProcessor（核心路径） | 1 | 仅有 1 个边界测试 |

---

## 附录 C：配置与安全隐患

| # | 问题 | 位置 | 建议 |
|---|------|------|------|
| 1 | MyBatis SQL 日志全量输出 | application.yml `log-impl: StdOutImpl` | 用 Profile 控制，生产关闭 |
| 2 | Sa-Token skip-url 过宽 | `/dict/**` 全部跳过认证 | 评估是否需要收窄 |
| 3 | MQTT 无暴力破解防护 | MqttServerAuthHandler | 增加 IP 限流或失败锁定 |
| 4 | Caffeine 缓存偏小 | `maximumSize=1000, expireAfterWrite=30s` | 设备量大时调整 |
| 5 | docker-compose 默认密码 | `postgres123` / `rustfsadmin123` | 文档强调必须修改 |
| 6 | Sa-Token token 7 天有效 | `timeout: 604800` | 评估是否需要缩短 |

---

## 执行检查清单

完成一项后打勾，方便追踪进度：

- [x] 1.1 DeviceShadow 乐观锁 ✅
- [ ] 1.2 InfluxDB 批量查询（待定，当前方案有意为之）
- [x] 1.3 密码正则优化 ✅
- [ ] 1.4 关闭生产 SQL 日志
- [ ] 1.5 核心模块测试
- [ ] 2.1 设备删除校验
- [ ] 2.2 指令下发日志
- [ ] 2.3 Modbus poll() 拆分
- [ ] 2.4 产品类型树缓存
- [ ] 2.5 事件日志分页
- [ ] 2.6 MQTT 自动注册
- [ ] 2.7 结构体字段兼容
- [ ] 2.8 通道激活校验
- [ ] 3.1 前端页面迁移
- [ ] 3.2 清理兼容代码
- [ ] 3.3 前端测试
- [x] 4.1 多租户 ⏭️ 跳过
- [ ] 4.2 Driver SDK
- [ ] 4.3 移动端适配
- [ ] 4.4 Helm Chart
- [ ] 4.5 OPC UA 驱动
