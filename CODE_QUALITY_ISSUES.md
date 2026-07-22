# 代码质量问题清单

> 2026-07-22 审查 | Sprint 1 完成后  
> 179 测试通过 | ~25K LOC

---

## 🔴 P0 - 数据完整性风险（立即修复）

### 1. 影子并发写丢数据

**位置：** `DeviceShadowServiceImpl.updateDesired()`  
**问题：** version 自增但不在 WHERE，乐观锁失效
```java
shadow.setVersion(shadow.getVersion() + 1);
updateById(shadow);  // 两个并发请求都成功，后者覆盖前者
```

**修复：**
```java
boolean ok = lambdaUpdate()
    .eq(DeviceShadow::getDeviceId, deviceId)
    .eq(DeviceShadow::getVersion, oldVersion)  // ponytail: 真正的乐观锁
    .set(DeviceShadow::getDesiredState, newState)
    .set(DeviceShadow::getVersion, oldVersion + 1)
    .update();
if (!ok) throw new BusinessException("版本冲突");
```

**优先级：** P0，生产环境影子配置会丢失

---

## 🟡 P1 - 性能问题（下个 Sprint）

### 2. InfluxDB N+1 查询

**位置：** `InfluxDataProcessor.last()` 165行  
**问题：** 每个属性单独查，10个属性=10次网络
```java
for (TslProperty property : propertyList) {
    influxDBClient.queryPoints("select last_value(\"" + identifier + "\") ...");
}
```

**修复：**
```sql
-- ponytail: 一次查所有字段
SELECT last_value(*) FROM "{measurement}" WHERE "deviceKey"=$deviceKey
```

**影响：** 100个属性从 100次查询降到 1次

---

### 3. 密码校验重复编译正则

**位置：** `UserServiceImpl.validatePassword()`  
**问题：** 每次调用编译3个正则
```java
password.matches(".*[A-Z].*");  // 每次都 Pattern.compile()
password.matches(".*[a-z].*");
password.matches(".*\\d.*");
```

**修复：**
```java
// ponytail: 一个正则，编译一次
private static final Pattern PWD = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
validatePassword(String pwd) {
    if (!PWD.matcher(pwd).matches()) throw ...
}
```

---

## 🟢 P2 - 设计改善（可选）

### 4. Service 层与 ORM 耦合

**现状：** 所有 ServiceImpl 继承 `ServiceImpl<Mapper, Entity>`  
**影响：** 单测必须 Mock MyBatis，业务逻辑测试成本高

**改进方向：**
```java
// ponytail: 复杂业务单独方法，简单 CRUD 保留 MP
// 当前规模 OK，1000+ 台设备时再抽象 Repository
```

**结论：** 暂不处理，过早优化

---

### 5. TODO 注释需清理

**位置：** 7个文件  
- `MqttDriver.java`
- `InfluxDataProcessor.java` 223行（事件日志分页）
- `ServiceEndpoint.java`
- 等

**操作：** 建 GitHub issue 跟踪，或下个 Sprint 实施

---

## ✅ 已正确处理的

- ✅ 全局异常 `@RestControllerAdvice` 已有
- ✅ 空 catch 仅 2处（Thread.sleep / 枚举解析），合理
- ✅ 密码存 BCrypt，无明文
- ✅ InfluxDB client 空检查（`if (influxDBClient == null)`）
- ✅ 影子 JSON 解析异常有日志

---

## 📋 下一步

1. **立即修复：** 影子乐观锁（30min）
2. **Sprint 2：** InfluxDB 批量查询（1h）
3. **Sprint 2：** 密码正则优化（15min）
4. **backlog：** TODO → GitHub issues

---

## 🎯 总体评价

**结构：** ✅ 分层清晰，Controller/Service/Mapper 规范  
**安全：** ✅ 密码加密、异常处理、空值检查到位  
**测试：** ✅ 179 测试覆盖核心逻辑  
**性能：** ⚠️ InfluxDB N+1、正则编译可优化  
**维护：** ✅ 代码简洁，无过度抽象

**适合场景：** ✅ 1000 台以内设备  
**扩展瓶颈：** InfluxDB 查询、影子并发
