# Webhook Ingress

Simple IoT can receive telemetry from third-party platforms (ERP, MES, weather APIs, etc.) via HTTP webhook with HMAC-SHA256 signature verification.

## How it works

1. **Create a webhook config** in the console → associate it with a device.
2. The system generates a unique `token` + `secret` pair.
3. Third-party systems `POST` JSON to `/iot/webhook/{token}` with HMAC signature headers.
4. The server verifies the signature, maps the JSON keys to device properties, and writes telemetry to InfluxDB.

## Signature algorithm

```
signature = HMAC-SHA256(secret, timestamp + "." + body)
```

Headers:

| Header | Description |
|---|---|
| `X-Siot-Timestamp` | Unix millisecond timestamp (must be within 5 min of server time) |
| `X-Siot-Signature` | Hex-encoded HMAC-SHA256 |

## curl example

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

## Request format

The body is raw JSON — each top-level key maps to a device property identifier defined in the TSL model:

```json
{
  "temperature": 25.5,
  "humidity": 60
}
```

The server wraps each key-value pair into the standard protocol format and feeds it through the normal `messageUp` pipeline, so rule chains, InfluxDB persistence, and WebSocket push all work automatically.

## Error responses

| Code | Message | Cause |
|---|---|---|
| 401 | `签名验证失败` | Wrong signature or missing headers |
| 401 | `时间戳过期` | Timestamp outside 5-min window |
| 404 | `webhook不存在` | Invalid token |
| 500 | `设备未连接` | Device offline (webhook requires an online device) |

## Frontend

The **Webhook** page in the console provides full CRUD. After creating a webhook, the secret dialog shows the endpoint URL, signature algorithm, and a ready-to-copy curl command with the actual token and secret filled in.
