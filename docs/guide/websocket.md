# WebSocket Real-time Push

Simple IoT pushes real-time data to the frontend via WebSocket, eliminating polling.

## Endpoint

```
ws://{host}/iot/ws/iot?token={token}
```

> Use `wss://` in production (HTTPS).

## Authentication

Pass the Sa-Token via the `token` query parameter during the WebSocket handshake. The backend `WebSocketAuthInterceptor` validates it.

## Message Format

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

### Channels

| Channel | Trigger | Data |
|---------|---------|------|
| `telemetry` | Device property report | `deviceKey` + `properties[]` |
| `alarm` | Rule engine creates alarm | Alarm object |

## Frontend Integration

### Dashboard

WidgetRenderer auto-subscribes to WebSocket. Updates value cards on `telemetry`, refreshes alarm list on `alarm`. 30s polling as fallback.

### Device Details

Subscribes to `telemetry`, filters by `deviceKey`, updates property values in real-time.

### Alarm Page

Subscribes to `alarm`, debounces 500ms, then refreshes list and statistics.

## nginx Configuration

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
