# Modbus TCP 驱动

Simple IoT 内置 Modbus TCP 轮询驱动，支持按设备定时读取寄存器并自动上报为遥测数据。

## 工作原理

1. 在**Modbus 配置**页面创建配置，关联设备和寄存器映射。
2. `ModbusPollingService` 启动后按配置的间隔定时连接 Modbus 从站。
3. 读取结果通过 `messageUp` 进入标准数据管道，写入 InfluxDB + 触发规则链。

## 配置项

| 字段 | 说明 |
|---|---|
| 设备 | 关联平台中的设备 |
| Host | Modbus 从站 IP |
| Port | Modbus TCP 端口（默认 502） |
| Unit ID | Modbus 从站地址 |
| 轮询间隔 | 毫秒，默认 5000 |
| 寄存器映射 | JSON 数组，定义读取哪些寄存器 |

## 寄存器映射格式

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

| 字段 | 说明 |
|---|---|
| `function` | Modbus 功能码：1=线圈, 2=离散输入, 3=保持寄存器, 4=输入寄存器 |
| `address` | 起始寄存器地址 |
| `count` | 读取数量 |
| `identifier` | 对应物模型属性标识符 |
| `dataType` | `BOOL` / `INT` / `DOUBLE` / `FLOAT` |
| `scale` | 缩放系数，最终值 = 原始值 × scale |

## Demo 模拟器

Docker Compose 包含 `modbus-sim` 容器，模拟一个温湿度压力传感器：

- 地址：`modbus-sim:502`
- Unit ID：1
- 寄存器：temperature(FC3, addr=0), humidity(FC3, addr=2), pressure(FC3, addr=4), switch(FC1, addr=100)

默认配置已预置在种子数据中，部署后即可在仪表盘看到 `modbus-thp-001` 设备的实时数据。

## 架构说明

```
ModbusPollingService
  └─ ScheduledExecutor (4 threads)
       └─ poll(cfg)  →  Socket(host, port)
            └─ ModbusFrame.readRequest(unitId, fc, addr, count)
            └─ ModbusFrame.parseResponse(bytes)
            └─ ModbusFrame.toValue(regs, dataType, scale)
            └─ dataProcessor.messageUp(DeviceRequest)
                 └─ InfluxDataProcessor → InfluxDB
                 └─ WebSocket push → Dashboard
```

驱动不使用连接池，每次轮询开关一次连接（`ponytail:` 简化，适合低频轮询场景，高频场景可加连接池）。
