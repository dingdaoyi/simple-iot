# 脚本协议开发指南

## 概述

脚本协议功能允许使用 JavaScript、Groovy、Python 等脚本语言动态编写物联网设备协议解析器，无需重新编译部署 Java 代码。

## 支持的脚本语言

| 语言 | 引擎 | 状态 |
|------|------|------|
| JavaScript | GraalVM JS | ✅ 完全支持 |
| Groovy | Groovy JSR-223 | 🚧 开发中 |
| Python | Jython | 🚧 计划中 |
| Lua | LuaJ | 🚧 计划中 |

## 协议类型枚举

```java
public enum ProtoType {
    JAVA(1, JavaProtocolInitialize.class),       // Java 外部 JAR
    SYSTEM_DEFAULT(2, SystemProtocolInitialize.class), // 系统内置
    JAVASCRIPT(3, ScriptProtocolInitialize.class); // 脚本协议
}
```

## 数据库配置

### tb_protocol 表结构

| 字段 | 类型 | 说明 |
|------|------|------|
| id | int | 主键 |
| name | varchar | 协议名称 |
| proto_type | int | 协议类型 (1:Java, 2:系统, 3:脚本) |
| url | varchar | 脚本文件路径 (可选) |
| mark | text | 脚本内容 (优先读取) |
| proto_key | varchar | 协议唯一标识 |
| handler_class | varchar | 处理器类名 (Java 协议使用) |

### 配置示例

```sql
-- JSON 简单协议 (脚本内容存储在 mark 字段)
INSERT INTO tb_protocol (id, name, proto_type, url, mark, proto_key, handler_class)
VALUES (10, 'JSON 简单协议', 3, NULL, 'exports.decode = function(...) { ... }', 'json-simple', NULL);

-- Modbus TCP 协议 (脚本内容存储在文件)
INSERT INTO tb_protocol (id, name, proto_type, url, mark, proto_key, handler_class)
VALUES (11, 'Modbus TCP', 3, '/opt/iot/scripts/modbus-tcp.js', NULL, 'modbus-tcp', NULL);
```

## 脚本开发规范

### 必需函数

#### decode(request, tslModel)

解码设备上报的数据。

**参数:**
- `request` - 请求对象
  - `deviceKey`: 设备唯一标识
  - `messageType`: 消息类型 (PROPERTY/EVENT/SERVICE_RES)
  - `data`: 原始数据 (字符串或字节数组)
- `tslModel` - 物模型对象

**返回值:**
```javascript
{
    messageId: 123,              // 消息ID (可选)
    rawData: "原始数据",          // 原始数据 (可选)
    dataList: [                  // 解析后的数据列表
        {
            identifier: "temperature",  // 属性标识符
            type: "DOUBLE",            // 数据类型
            value: 25.5                // 属性值
        }
    ],
    eventData: { ... },          // 事件数据 (可选)
    serviceResData: { ... }      // 服务响应数据 (可选)
}
```

#### encode(message, tslModel)

编码下发给设备的命令。

**参数:**
- `message` - 命令消息
  - `identifier`: 功能/属性标识符
  - `params`: 参数对象
  - `productKey`: 产品key
  - `deviceKey`: 设备key
- `tslModel` - 物模型对象

**返回值:**
```javascript
{
    messageId: 123,
    message: "编码后的数据",      // 字符串或字节数组
    metadata: {
        topic: "/device/xxx/command"  // 可选的元数据
    }
}
```

### 数据类型

```javascript
// 支持的数据类型
'INT'      // 32位整数
'LONG'     // 64位整数
'DOUBLE'   // 双精度浮点
'FLOAT'    // 单精度浮点
'BOOLEAN'  // 布尔值
'STRING'   // 字符串
'JSON'     // JSON 对象
'ENUM'     // 枚举值
```

## 示例脚本

### 示例 1: 简单 JSON 协议

```javascript
exports.decode = function(request, tslModel) {
    const payload = JSON.parse(request.data);

    return {
        messageId: payload.msgId,
        rawData: request.data,
        dataList: Object.keys(payload.data).map(key => ({
            identifier: key,
            type: typeof payload.data[key] === 'number' ? 'DOUBLE' : 'STRING',
            value: payload.data[key]
        }))
    };
};
```

### 示例 2: 十六进制数据解析

```javascript
exports.decode = function(request, tslModel) {
    const hex = request.data; // "010302001A"
    const offset = 4; // 跳过头部

    const value = parseInt(hex.substr(offset, 4), 16);

    return {
        messageId: 0,
        dataList: [{
            identifier: "temperature",
            type: "INT",
            value: value
        }]
    };
};
```

### 示例 3: Base64 编码的二进制数据

```javascript
// Java 中需要先 Base64 解码后再传入
// 或者在脚本中使用 Java 的 Base64 工具
```

## 运行时 API

### 可用的全局对象

- `console` - 日志输出 (console.log, console.error)
- `JSON` - JSON 处理
- `Math` - 数学函数
- `Date` - 日期时间

### 与 Java 交互

```javascript
// 访问 Java 类
const ArrayList = Java.type('java.util.ArrayList');
const list = new ArrayList();

// 调用 Java 方法
const result = list.size();
```

## 调试技巧

### 1. 使用 console.log 输出

```javascript
exports.decode = function(request, tslModel) {
    console.log("收到数据:", request.data);
    console.log("消息类型:", request.messageType);

    // ...
};
```

### 2. 错误处理

```javascript
exports.decode = function(request, tslModel) {
    try {
        // 解析逻辑
    } catch (e) {
        console.error("解析失败:", e.message);
        // 返回错误信息
        return {
            messageId: 0,
            dataList: []
        };
    }
};
```

### 3. 本地测试

创建测试脚本：

```javascript
// 测试脚本
const protocol = require('./json-simple-example.js');

const mockRequest = {
    deviceKey: "test-device",
    messageType: "PROPERTY",
    data: '{"temperature": 25.5}'
};

const result = protocol.decode(mockRequest, {});
console.log(JSON.stringify(result, null, 2));
```

## 性能建议

1. **预编译**: GraalVM 会自动编译 JavaScript 为字节码，首次执行后性能会显著提升

2. **避免正则表达式**: 如果必须使用，预编译正则表达式：
   ```javascript
   const REGEX = /pattern/g;
   ```

3. **减少对象创建**: 重用对象而非每次创建新对象

4. **使用原生方法**: 优先使用 JavaScript 原生方法而非 Java API

## 热更新

协议修改后，可以调用热更新接口重新加载：

```java
// Java 代码
boolean success = ProtocolFactory.reloadProtocol("json-simple");
```

## 注意事项

1. **安全性**: 脚本可以访问 Java API，请注意权限控制

2. **资源释放**: 脚本引擎会自动管理资源，但大量协议建议设置超时

3. **类型转换**: GraalVM 会自动处理 JavaScript 与 Java 之间的类型转换

4. **异步操作**: 当前不支持异步脚本，所有操作应为同步

## 进阶示例

查看完整示例：
- `modbus-tcp-example.js` - Modbus TCP 协议实现
- `json-simple-example.js` - JSON 格式协议实现
