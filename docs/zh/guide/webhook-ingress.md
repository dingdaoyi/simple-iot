# Webhook 数据接入

Simple IoT 支持通过 HTTP Webhook 接收第三方平台（ERP、MES、天气 API 等）推送的遥测数据，使用 HMAC-SHA256 签名验证。

## 工作原理

1. 在控制台**创建 Webhook 接入**，关联一个设备。
2. 系统生成唯一的 `token` + `secret` 密钥对。
3. 第三方系统带 HMAC 签名头 `POST` JSON 到 `/iot/webhook/{token}`。
4. 服务端验签后将 JSON 键值映射为设备属性，写入 InfluxDB。

## 签名算法

```
signature = HMAC-SHA256(secret, timestamp + "." + body)
```

请求头：

| Header | 说明 |
|---|---|
| `X-Siot-Timestamp` | Unix 毫秒时间戳（与服务端时间差不超过 5 分钟） |
| `X-Siot-Signature` | HMAC-SHA256 的十六进制字符串 |

## curl 示例

```bash
TOKEN="your-webhook-token"
SECRET="your-webhook-secret"
BODY='{"temperature":25.5,"humidity":60}'
TS=$(date +%s%3N)
SIG=$(printf '%s.%s' "$TS" "$BODY" | openssl dgst -sha256 -hmac "$SECRET" | awk '{print $NF}')

curl -X POST "http://localhost:5010/iot/webhook/$TOKEN" \
  -H "Content-Type: application/json" \
  -H "X-Siot-Timestamp: $TS" \
  -H "X-Siot-Signature: $SIG" \
  -d "$BODY"
```

## 请求格式

Body 是裸 JSON，每个顶层 key 对应物模型中定义的属性标识符：

```json
{
  "temperature": 25.5,
  "humidity": 60
}
```

服务端将每个键值对包装成标准协议格式，走正常的 `messageUp` 管道，规则链、InfluxDB 持久化、WebSocket 推送全部自动生效。

## 错误响应

| Code | Message | 原因 |
|---|---|---|
| 401 | `签名验证失败` | 签名错误或缺少请求头 |
| 401 | `时间戳过期` | 时间戳超出 5 分钟窗口 |
| 404 | `webhook不存在` | token 无效 |
| 500 | `设备未连接` | 设备离线（webhook 需要在线设备） |

## 前端管理

控制台的 **Webhook** 页面提供完整增删改查。创建接入后，密钥弹窗会展示端点 URL、签名算法和可直接复制的 curl 命令（已填入实际 token 和 secret）。
