# MQTT Quick Test

This page gives you a ready-to-run MQTT telemetry path for Simple IoT's built-in broker.

## 1. Use the seeded demo device

Docker Compose enables the optional demo telemetry seed by default with:

```bash
SIMPLE_IOT_DEMO_SEED_ENABLED=true
```

On startup the backend creates an idempotent demo dataset:

| Item | Value |
|---|---|
| Product | `Demo Smart Sensor` |
| Product key | `demo-smart-sensor` |
| Device | `Demo Sensor 001` |
| Device key / MQTT username | `demo-sensor-001` |
| Device secret / MQTT password | `demo-secret` |
| Protocol | `demo-json-telemetry` |
| Alarm rule | `temperature > 60` creates `demo_high_temperature` |

To disable the seed for a production-like deployment, set `SIMPLE_IOT_DEMO_SEED_ENABLED=false` in `.env` and recreate `iot-server`.

## 2. Publish telemetry

Simple IoT's default MQTT driver uses:

| Field | Default format |
|---|---|
| Client ID | `simple_{deviceKey}` |
| Username | `{deviceKey}` |
| Password | device secret |
| Property topic | `simpleiot/pro/{productKey}` |
| Event topic | `simpleiot/ev/{productKey}` |
| Service reply topic | `simpleiot/cam_res/{productKey}` |

Publish a high-temperature sample to the seeded device:

```bash
mosquitto_pub -h localhost -p 1883 \
  -i simple_demo-sensor-001 \
  -u demo-sensor-001 \
  -P demo-secret \
  -t simpleiot/pro/demo-smart-sensor \
  -m '{"temperature":72.5,"humidity":43,"voltage":220.8,"online":true,"mode":"auto"}'
```

Expected result:

1. The payload is decoded by the `demo-json-telemetry` JavaScript protocol.
2. Telemetry is written as properties: `temperature`, `humidity`, `voltage`, `online`, `mode`.
3. Because `temperature` is above `60`, the demo rule chain creates an active `demo_high_temperature` alarm.

## 3. Generate a payload from the simulator

After compiling the backend, the small CLI helper prints the exact connection details and JSON payload:

```bash
./mvnw -pl iot-server -am -DskipTests compile
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator
```

Override values for manual smoke runs:

```bash
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator \
  temperature=55.2 humidity=48 voltage=219.6 mode=manual
```

If you changed the MQTT driver prefixes in `application.yml`, pass matching simulator overrides:

```bash
java -cp iot-server/target/classes com.github.dingdaoyi.demo.DemoTelemetrySimulator \
  topicPrefix=custom-prefix clientIdPrefix=device_
```

## Topic cheat sheet

| Direction | Topic | Purpose |
|---|---|---|
| Device → Platform | `simpleiot/pro/{productKey}` | Property report |
| Device → Platform | `simpleiot/ev/{productKey}` | Event report |
| Platform → Device | `simpleiot/cmd/{productKey}` | Service invocation |
| Device → Platform | `simpleiot/cam_res/{productKey}` | Service reply |
