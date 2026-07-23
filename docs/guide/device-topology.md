# Device Topology (Gateway-Subdevice)

Simple IoT supports parent-child device topology for gateway-to-subdevice scenarios (e.g., Modbus gateway + multiple slaves).

## Data Model

The `tb_device` table has a `parent_id` field pointing to the parent device ID. One device has one parent, but a parent can have multiple children.

## API

### List child devices

```
GET /device/{parentId}/children
```

**Response:**

```json
{
  "code": 200,
  "data": [
    {
      "id": 2,
      "deviceName": "Modbus Slave 1",
      "deviceKey": "modbus-slave-01",
      "parentId": 1
    }
  ]
}
```

## Frontend

1. Go to Device Management
2. Click a gateway device's "Details"
3. Switch to the "Child Devices" tab

## Typical Scenarios

- **Modbus Gateway**: gateway polls multiple Modbus slaves, each slave is a child device
- **LoRa Gateway**: gateway receives data from multiple LoRa nodes
- **Edge Gateway**: edge gateway proxies multiple local devices
