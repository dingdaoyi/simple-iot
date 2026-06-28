# MQTT 快速测试

本页给出一条可以直接跑通的 Simple IoT 内置 MQTT Broker 遥测链路。

## 1. 使用预置演示设备

Docker Compose 默认通过下面的环境变量开启可选演示遥测种子数据：

```bash
SAMPLE_IOT_DEMO_SEED_ENABLED=true
```

后端启动时会幂等创建一组演示数据：

| 项目 | 值 |
|---|---|
| 产品 | `Demo Smart Sensor` |
| Product key | `demo-smart-sensor` |
| 设备 | `Demo Sensor 001` |
| Device key / MQTT username | `demo-sensor-001` |
| Device secret / MQTT password | `demo-secret` |
| 协议 | `demo-json-telemetry` |
| 告警规则 | `temperature > 60` 时创建 `demo_high_temperature` 告警 |

如果要模拟更接近生产的空环境，在 `.env` 中设置 `SAMPLE_IOT_DEMO_SEED_ENABLED=false`，然后重建/重启 `iot-server`。

## 2. 发布遥测数据

Simple IoT 默认 MQTT 驱动使用以下格式：

| 字段 | 默认格式 |
|---|---|
| Client ID | `sample_{deviceKey}` |
| Username | `{deviceKey}` |
| Password | 设备密钥 |
| 属性上报 Topic | `sampleiot/pro/{productKey}` |
| 事件上报 Topic | `sampleiot/ev/{productKey}` |
| 服务应答 Topic | `sampleiot/cam_res/{productKey}` |

向预置设备发布一条高温样例：

```bash
mosquitto_pub -h localhost -p 1883 \
  -i sample_demo-sensor-001 \
  -u demo-sensor-001 \
  -P demo-secret \
  -t sampleiot/pro/demo-smart-sensor \
  -m '{"temperature":72.5,"humidity":43,"voltage":220.8,"online":true,"mode":"auto"}'
```

预期结果：

1. `demo-json-telemetry` JavaScript 协议脚本解码该 JSON。
2. 平台写入 `temperature`、`humidity`、`voltage`、`online`、`mode` 等属性。
3. 由于 `temperature` 高于 `60`，演示规则链会创建一条活动的 `demo_high_temperature` 告警。

## 3. 用模拟器生成 payload

编译后端后，可以用内置小工具打印连接参数和 JSON payload：

```bash
./mvnw -pl iot-server -am -DskipTests compile
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator
```

手动冒烟时可以覆盖数值：

```bash
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator \
  temperature=55.2 humidity=48 voltage=219.6 mode=manual
```

如果你修改了 `application.yml` 里的 MQTT 驱动前缀，需要给模拟器传入同样的前缀：

```bash
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator \
  topicPrefix=custom-prefix clientIdPrefix=device_
```

## Topic 速查

| 方向 | Topic | 用途 |
|---|---|---|
| 设备 → 平台 | `sampleiot/pro/{productKey}` | 属性上报 |
| 设备 → 平台 | `sampleiot/ev/{productKey}` | 事件上报 |
| 平台 → 设备 | `sampleiot/cmd/{productKey}` | 服务调用 |
| 设备 → 平台 | `sampleiot/cam_res/{productKey}` | 服务调用应答 |
