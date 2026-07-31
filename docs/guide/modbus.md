# Modbus TCP Driver

Simple IoT includes a built-in Modbus TCP polling driver that reads registers on a schedule and reports them as telemetry.

## How it works

1. Create a config on the **Modbus** page - associate a device and define register mappings.
2. `ModbusPollingService` connects to the Modbus slave at the configured interval.
3. Results go through `messageUp` into the standard pipeline: InfluxDB + rule chains + WebSocket.

## Configuration

| Field | Description |
|---|---|
| Device | Associated platform device |
| Host | Modbus slave IP |
| Port | Modbus TCP port (default 502) |
| Unit ID | Modbus slave address |
| Interval | Poll interval in ms (default 5000) |
| Register Map | JSON array defining which registers to read |

## Register mapping format

```json
[
  {
    "function": 3,
    "address": 0,
    "count": 2,
    "identifier": "temperature",
    "dataType": "DOUBLE",
    "scale": 0.01
  },
  {
    "function": 1,
    "address": 100,
    "count": 1,
    "identifier": "switch",
    "dataType": "BOOL"
  }
]
```

| Field | Description |
|---|---|
| `function` | Modbus function code: 1=coil, 2=discrete input, 3=holding register, 4=input register |
| `address` | Start register address |
| `count` | Number of registers |
| `identifier` | TSL model property identifier |
| `dataType` | `BOOL` / `INT` / `DOUBLE` / `FLOAT` |
| `scale` | Scale factor, final = raw × scale |

## Demo simulator

Docker Compose includes a `modbus-sim` container simulating a temp/humidity/pressure sensor:

- Address: `modbus-sim:502`
- Unit ID: 1
- Registers: temperature(FC3, addr=0), humidity(FC3, addr=2), pressure(FC3, addr=4), switch(FC1, addr=100)

Seed data is pre-configured - after deploy you can see `modbus-thp-001` telemetry on the dashboard immediately.

## Architecture

```
ModbusPollingService
  └─ ScheduledExecutor (4 threads)
       └─ poll(cfg)  ->  Socket(host, port)
            └─ ModbusFrame.readRequest(unitId, fc, addr, count)
            └─ ModbusFrame.parseResponse(bytes)
            └─ ModbusFrame.toValue(regs, dataType, scale)
            └─ dataProcessor.messageUp(DeviceRequest)
                 └─ InfluxDataProcessor -> InfluxDB
                 └─ WebSocket push -> Dashboard
```

No connection pooling - each poll opens and closes a socket (`ponytail:` simplification for low-frequency polling; add a pool for high-frequency scenarios).
