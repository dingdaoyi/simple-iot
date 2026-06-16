# Architecture

## High-level diagram

```text
                 ┌───────────────────────┐
                 │      Vue 3 Web UI     │  iot-web (Nginx :80)
                 └────────────┬──────────┘
                              │ /iot/*  (REST + WebSocket)
                              ▼
   ┌───────────────────────────────────────────────────────────┐
   │                   iot-server (Spring Boot :5010)          │
   │ ┌──────────┐ ┌────────────┐ ┌─────────────┐ ┌───────────┐ │
   │ │ REST API │ │ Sa-Token   │ │ Rule engine │ │ MQTT      │ │
   │ │ (mvc)    │ │ auth       │ │ (chain DAG) │ │ broker    │ │
   │ └────┬─────┘ └──────┬─────┘ └──────┬──────┘ └─────┬─────┘ │
   │      │              │              │              │       │
   │      └──────────────┴──────┬───────┴──────────────┘       │
   │                            │                              │
   └────────────────────────────┼──────────────────────────────┘
                                │
        ┌───────────────────────┼─────────────────────────────┐
        ▼                       ▼                             ▼
  ┌──────────┐           ┌─────────────┐              ┌──────────────┐
  │PostgreSQL│           │ InfluxDB 3  │              │ RustFS (S3)  │
  │ metadata │           │ telemetry   │              │ files / FW   │
  └──────────┘           └─────────────┘              └──────────────┘

         ▲
         │ MQTT 1883 / WS 8083
   ┌─────┴──────┐
   │  Devices   │
   └────────────┘
```

## Components

### Backend — `iot-server`

Single Spring Boot 4 jar exposing:

- **REST API** under `/iot/**` (controllers in `com.github.dingdaoyi.controller`).
- **Sa-Token** session-cookie / token auth.
- **MyBatis-Plus** repositories on PostgreSQL.
- **mica-mqtt** built-in broker on TCP 1883 / WebSocket 8083.
- **Rule engine** consuming MQTT + REST events, dispatching to actions.
- **Protocol factory** that hot-loads decoders (Java / JS / Groovy / Lua).
- **InfluxDB 3** client for telemetry writes & queries.

### Frontend — `iot-web`

Vue 3 + Vite + Element Plus + Pinia.

- Source under `iot-web/src/`.
- Dev server: `pnpm dev` on port `9999`, proxies `/iot` to backend `:5010`.
- Build: `pnpm build` produces a static bundle served by Nginx in production.

### Storage

| Store | Role |
|---|---|
| **PostgreSQL 16** | Devices, products, rules, users, configs, alarms metadata. |
| **InfluxDB 3** | Time-series telemetry (`tb_telemetry` measurements). |
| **RustFS** | S3-compatible object store for firmware / icons / file uploads. |

## Local dev workflow

```bash
# 1) start data layer only
docker compose up -d postgres        # influx + rustfs optional during dev

# 2) backend (auto-reload not enabled; rebuild jar)
mvn -B -ntp -DskipTests clean install -pl iot-server -am
java -jar iot-server/target/iot-server-0.0.1-SNAPSHOT.jar

# 3) frontend
cd iot-web
pnpm install
pnpm dev    # http://localhost:9999  → proxies /iot to backend
```

## Module layout

```
simple-iot/
├── iot-common/         # shared DTOs / utils
├── iot-driver/         # protocol drivers (MQTT/TCP/HTTP/UDP)
├── iot-rule/           # rule engine nodes
├── iot-server/         # Spring Boot main module
├── iot-web/            # Vue 3 admin
├── doc/                # raw notes & screenshots
├── docs/               # this VitePress site
└── deploy.sh           # one-shot Docker Compose deploy
```
