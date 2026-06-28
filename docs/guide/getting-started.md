# Getting Started

This guide walks you through running **Simple IoT** locally in under 5 minutes.

## Prerequisites

- Docker 24+ with Docker Compose v2
- 2 GB free RAM
- Ports `5010`, `1883`, `8083`, `9999`, `5432`, `8181` available

## 1. Clone the repository

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
```

## 2. Start the stack

```bash
cp .env.example .env       # adjust passwords if you want
./deploy.sh deploy
```

This brings up the full stack via Docker Compose:

| Service | Port | Purpose |
|---|---|---|
| `iot-server` | 5010 | Spring Boot REST + MQTT broker |
| `iot-web` | 80 | Vue 3 admin UI (Nginx) |
| `iot-postgres` | 5432 | PostgreSQL 16 (metadata) |
| `influxdb` | 8181 | InfluxDB 3 (telemetry) |
| `rustfs` | 9000 | S3-compatible storage |

## 3. Log in

Open <http://localhost> and use the default account:

```
username: admin
password: 123456
```

> **Change this password before exposing the stack to a public network.**

## 4. Try live telemetry

The Docker stack seeds a demo sensor by default. You can publish one sample immediately:

```bash
mosquitto_pub -h localhost -p 1883 \
  -i sample_demo-sensor-001 \
  -u demo-sensor-001 \
  -P demo-secret \
  -t sampleiot/pro/demo-smart-sensor \
  -m '{"temperature":72.5,"humidity":43,"voltage":220.8,"online":true,"mode":"auto"}'
```

This goes through the demo JavaScript protocol, stores telemetry and triggers the seeded high-temperature alarm rule.
For more details, see [MQTT Quick Test](./mqtt-test).

## 5. Onboard your first device

1. Go to **Products** → click **+ New** → choose `MQTT` as protocol.
2. Add a **device** under that product. Note its `clientId` and `secret`.
3. Connect with any MQTT client to `tcp://<host>:1883` using those credentials.
4. Watch live telemetry stream into the **Devices → Telemetry** tab.

## 6. Build a rule

1. Go to **Rule Engine** → **+ New rule chain**.
2. Drag an **Input** node, connect it to a **Filter**, then to an **Action**.
3. Pick "send email" / "MQTT forward" / "device command" as the action.

Done — your first rule is live the moment you save it.

## Local development

If you want to hack on the code instead of running the prebuilt images, see the [Architecture](./architecture) page for the dev-mode workflow (Maven + pnpm).
