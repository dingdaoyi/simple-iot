# MQTT Quick Test

This page shows the wire format for Simple IoT's built-in MQTT broker so you can onboard your first device.

> **Prerequisite**: the device must already exist under **Products → Devices** in the admin UI. Below we use the placeholder `device-id` standing for the device number `B29Gx0OpapnOtPrQXtb4`.

## 1. Publish telemetry (property)

**Topic**: `simple/iot/pro/{device-id}`

**Payload**:

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

Once published the value shows up under **Devices → Telemetry**.

## 2. Publish an event

**Topic**: `simple/iot/ev/{device-id}`

**Payload**:

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

Events flow into the **Alarm Center** and can be consumed by the rule engine.

## 3. Service invocation (downstream command)

**HTTP call** (platform side):

```http
POST http://127.0.0.1:5010/iot/service/{device-id}/customservice
Content-Type: application/json

{
  "levele": 12
}
```

**Response**:

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

**Device-side MQTT reply**:

- Topic: `simple/iot/cam_res/{device-id}`, QoS 1
- Payload:

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

## Topic cheat sheet

| Direction | Topic | Purpose |
|---|---|---|
| Device → Platform | `simple/iot/pro/{device-id}` | Property report |
| Device → Platform | `simple/iot/ev/{device-id}` | Event report |
| Platform → Device | `simple/iot/cmd/{device-id}` | Service invocation |
| Device → Platform | `simple/iot/cam_res/{device-id}` | Service reply |
