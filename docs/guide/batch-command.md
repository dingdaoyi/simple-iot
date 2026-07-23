# Batch Device Commands

Simple IoT supports sending service commands to multiple devices in a single request (e.g., batch reboot, batch config push).

## API

### Send batch command

```
POST /service/batch/{identifier}
```

**Path parameter:**
- `identifier` - service identifier (defined in the thing model)

**Request body:**

```json
{
  "deviceKeys": ["device-001", "device-002", "device-003"],
  "params": {
    "action": "reboot"
  }
}
```

**Response:**

```json
{
  "code": 200,
  "data": {
    "total": 3,
    "success": 2,
    "failed": 1,
    "details": [
      { "deviceKey": "device-001", "status": "SUCCESS" },
      { "deviceKey": "device-002", "status": "SUCCESS" },
      { "deviceKey": "device-003", "status": "FAILED", "error": "Device offline" }
    ]
  }
}
```

## Frontend

1. Go to Device Management
2. Select devices via checkbox
3. Click "Batch Command" button
4. Enter service identifier and params (JSON)
5. Click "Execute" to see per-device results

## Notes

- Commands execute synchronously; large device counts will take longer
- Offline devices return FAILED without blocking others
- Each device's execution is independent
