# MQTT 快速测试

本页给出 Simple IoT 内置 MQTT broker 的报文格式样例，方便你接入第一台设备。

> **前提**：设备已经在控制台「产品 → 设备」处创建，下面用占位 `device-id` 表示设备号 `B29Gx0OpapnOtPrQXtb4`。

## 1. 上报属性数据

**Topic**：`simple/iot/pro/{device-id}`

**Payload**：

```json
{
  "header": {
    "msgId": 1,
    "identifier": "pressure"
  },
  "body": {
    "value": "20"
  }
}
```

发布后在控制台「设备 → 遥测」即可看到该属性。

## 2. 上报事件

**Topic**：`simple/iot/ev/{device-id}`

**Payload**：

```json
{
  "header": {
    "msgId": 1,
    "identifier": "alarm"
  },
  "body": {
    "data": {
      "temper": "1"
    }
  }
}
```

事件会进入「告警中心」，可被规则引擎消费。

## 3. 服务调用（下发指令）

**HTTP 调用**（平台侧）：

```http
POST http://127.0.0.1:5010/iot/service/{device-id}/customservice
Content-Type: application/json

{
  "levele": 12
}
```

**响应**：

```json
{
  "code": 1,
  "success": true,
  "msg": "操作成功",
  "data": {
    "levele": 12
  }
}
```

**设备侧 MQTT 应答**：

- Topic：`simple/iot/cam_res/{device-id}`，QoS 1
- Payload：

```json
{
  "header": {
    "msgId": 0,
    "identifier": "customservice"
  },
  "body": {
    "data": {
      "levele": 12
    }
  }
}
```

## Topic 速查

| 方向 | Topic | 用途 |
|---|---|---|
| 设备 → 平台 | `simple/iot/pro/{device-id}` | 属性上报 |
| 设备 → 平台 | `simple/iot/ev/{device-id}` | 事件上报 |
| 平台 → 设备 | `simple/iot/cmd/{device-id}` | 服务调用 |
| 设备 → 平台 | `simple/iot/cam_res/{device-id}` | 服务调用应答 |
