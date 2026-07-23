# WebSocket 实时推送

Simple IoT 通过 WebSocket 向前端推送实时数据，无需轮询。

## 端点

```
ws://{host}/iot/ws/iot?token={token}
```

> 生产环境使用 `wss://`（HTTPS）。

## 认证

WebSocket 握手时通过 query 参数 `token` 传递 Sa-Token，后端 `WebSocketAuthInterceptor` 校验。

## 消息格式

```json
{
  "channel": "telemetry",
  "data": {
    "deviceKey": "demo-sensor-001",
    "properties": [
      { "identifier": "temperature", "value": 25.6 },
      { "identifier": "humidity", "value": 40 }
    ]
  }
}
```

### 频道

| Channel | 触发 | 数据 |
|---------|------|------|
| `telemetry` | 设备上报属性 | `deviceKey` + `properties[]` |
| `alarm` | 规则引擎创建告警 | 告警对象 |

## 前端接入

### 仪表盘

仪表盘组件（WidgetRenderer）自动订阅 WebSocket，收到 `telemetry` 时更新数值卡，收到 `alarm` 时刷新告警列表。30 秒轮询作为降级。

### 设备详情页

设备详情页订阅 `telemetry` 频道，按 `deviceKey` 过滤，实时更新属性值。

### 告警页面

告警列表页订阅 `alarm` 频道，500ms 防抖后刷新列表和统计。

## nginx 配置

```nginx
location /iot/ws/ {
    proxy_pass http://iot-server:5010;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";
    proxy_read_timeout 3600s;
}
```

> ponytail: in-memory session set, no cluster broadcast. Redis pub/sub for multi-node.
