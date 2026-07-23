<div align="center">

<img src="doc/brand/banner.png" alt="Simple IoT — The Minimal IoT Platform" width="100%"/>

# Simple IoT

**The Minimal IoT Platform — lightweight, single-binary, ready in 60 seconds.**

[![CI](https://github.com/dingdaoyi/simple-iot/actions/workflows/ci.yml/badge.svg)](https://github.com/dingdaoyi/simple-iot/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/dingdaoyi/simple-iot?color=blue)](LICENSE)
[![Tests](https://img.shields.io/badge/tests-190%20passing-brightgreen)](https://github.com/dingdaoyi/simple-iot)
[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-42b883?logo=vue.js)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](docker-compose.yml)
[![GitHub Stars](https://img.shields.io/github/stars/dingdaoyi/simple-iot?style=social)](https://github.com/dingdaoyi/simple-iot/stargazers)

**English** · [简体中文](README.zh-CN.md) · [Live Demo](https://dingdaoyi.github.io/simple-iot/guide/demo) · [Documentation](https://dingdaoyi.github.io/simple-iot/) · [Report Bug](https://github.com/dingdaoyi/simple-iot/issues)

</div>

---

## ✨ What is Simple IoT?

**Simple IoT** is a self-hosted IoT platform for teams that want to connect devices, model telemetry, build rule flows and operate alarms without assembling a large distributed stack first.

The project keeps the runtime intentionally compact: one Spring Boot application, one Vue 3 console, PostgreSQL for business data, InfluxDB for telemetry and Docker Compose for deployment. It is easy to read, fork, run on a small server and adapt to real projects.

## 🌟 Release Highlights

### v0.2.0 - Real-time + Operations (2026-07-23)

| Area | What's new |
|---|---|
| **Real-time dashboard** | WebSocket push for telemetry & alarm channels, 2 new widget types (device status grid, alarm list). |
| **Schedule triggers** | Cron-based rule chain execution via `INPUT_SCHEDULE` node. |
| **IM push** | DingTalk, WeCom, Feishu webhook notifications with CRUD config UI. |
| **Batch operations** | Multi-device command dispatch with per-device result tracking. |
| **Device topology** | Parent-child device relationships for gateway-subdevice scenarios. |
| **Data retention** | InfluxDB telemetry auto-purge (configurable, default 180 days). |
| **Security** | API rate limiting (Bucket4j), audit logging (AOP), alarm suppression (5-min dedup). |
| **Docs** | 8 new VitePress guide pages (zh + en). 190 unit tests. Platform maturity 75%. |

### v0.1.1 - Usability (2026-06-25)

| Area | What's new |
|---|---|
| **International UI** | Full `zh-CN` / `en-US` frontend i18n, semantic translation keys, language switcher and Element Plus locale sync. |
| **Rule-chain authoring** | Improved editor usability with canvas zoom/pan, keyboard shortcuts, node search, validation and debug path highlighting. |
| **Scriptable automation** | Script filter nodes and safer protocol/script test handling: user script mistakes now return readable validation results instead of noisy server errors. |
| **Integration reliability** | Push configuration validation, signed push testing and better delivery-setting checks. |
| **Demo & release quality** | Hardened GitHub Actions, frontend lint in CI, stable demo deployment and public smoke verification. |

> **In short** — Simple IoT is a practical, lightweight IoT console you can run yourself, inspect end-to-end and evolve with your product.

---

## 🚀 Quick Start (60 seconds with Docker)

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
chmod +x deploy.sh
./deploy.sh deploy
```

Then open:

| Service | URL | Default credentials |
|---------|-----|--------------------|
| Web UI | http://localhost | `admin` / `123456` |
| API docs | http://localhost:5010/iot/doc.html | — |
| MQTT broker | `mqtt://localhost:1883` | — |
| MQTT WebSocket | `ws://localhost:8083/mqtt` | — |

The Docker stack seeds a demo sensor by default (`demo-sensor-001` / `demo-secret`) so the dashboard and MQTT smoke path are useful immediately. See [MQTT Quick Test](docs/guide/mqtt-test.md) for a copy-paste telemetry publish command.

That's it. PostgreSQL, RustFS (S3-compatible), the backend and the frontend all spin up together.

> Want to develop locally? See the [development guide](#-development).

---

## 🎯 Core Features

| Module | What it does |
|--------|--------------|
| **Device Management** | Registration, online/offline tracking, batch operations, command dispatch |
| **Product & Thing Model** | Product types, properties, services and events for structured device capabilities |
| **Protocol Engine** | Hot-loaded scripts in **Java / JavaScript / Groovy / Lua**, no restart needed |
| **Visual Rule Engine** | Drag-and-drop chain editor with validation, debug paths, script filters and reusable nodes |
| **Alarm Center** | Severity levels (info / warning / critical / urgent), active / cleared lifecycle |
| **Data Ingestion** | InfluxDB 3 time-series storage, Caffeine in-process cache |
| **Notifications** | Email & SMS push, HTTP callbacks, MQTT forward, device commands |
| **Dashboard** | Device totals, online stats, system metrics (CPU / memory / disk), live alarms |
| **Auth & Permissions** | Sa-Token based, fine-grained, role/menu/button level |
| **International UI** | Modern Vue 3 console with `zh-CN` / `en-US`, light/dark/auto theme and responsive layouts |

---

## 🖼️ Screenshots

<div align="center">

### Dashboard
<img src="doc/screenshots/dashboard.png" alt="Dashboard" width="80%"/>

### Visual Rule Engine
<img src="doc/screenshots/rule-chain-editor.png" alt="Rule Engine Editor" width="80%"/>

### Device Management
<img src="doc/screenshots/device.png" alt="Device" width="80%"/>

### Protocol Scripts
<img src="doc/screenshots/protocol.png" alt="Protocol" width="80%"/>

### Alarms
<img src="doc/screenshots/alarm.png" alt="Alarm" width="80%"/>

</div>

---

## 🏗️ Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                          Simple IoT                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Vue 3 + Vite + Element Plus  (Minimal UI)                │
│           │                                                      │
│           ▼  REST / WebSocket                                    │
│   ┌──────────────────────────────────────────────────────────┐   │
│   │   Spring Boot 4 (single JVM, single binary)              │   │
│   │                                                          │   │
│   │   • Sa-Token auth         • MyBatis-Plus                 │   │
│   │   • Caffeine local cache  • Knife4j OpenAPI              │   │
│   │   • Visual rule engine    • Hot-loaded protocol scripts  │   │
│   │   • mica-mqtt broker (1883 / 8083 ws)                    │   │
│   └──────────────────────────────────────────────────────────┘   │
│           │                          │                           │
│           ▼                          ▼                           │
│   ┌──────────────┐          ┌────────────────┐                   │
│   │ PostgreSQL   │          │ InfluxDB 3     │                   │
│   │ (business)   │          │ (telemetry)    │                   │
│   └──────────────┘          └────────────────┘                   │
│                                                                  │
│   ┌──────────────────────────────────────────────────────────┐   │
│   │  Devices  →  MQTT / TCP / HTTP  →  Protocol scripts      │   │
│   └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### Stack

**Backend** — Java 25 · Spring Boot 4.0.2 · Sa-Token · MyBatis-Plus · PostgreSQL · InfluxDB 3 · mica-mqtt · Caffeine · Hutool · AWS S3 SDK
**Frontend** — Vue 3 · Vite · Element Plus · Pinia · Vue Router · Axios · ECharts
**Infra** — Docker Compose · RustFS (S3-compatible) · GitHub Actions

---

## 📦 Project Layout

```
simple-iot/
├── iot-server/         # Main Spring Boot service (REST + MQTT broker + rule engine)
├── iot-common/         # Shared base classes (BaseEntity, ResultCode, paging)
├── iot-driver/         # System-supplied protocol drivers
├── iot-web/            # Vue 3 admin frontend
├── doc/                # Docs, SQL schema, screenshots, brand assets
├── docker-compose.yml  # Stack: postgres + rustfs + iot-server + iot-web
├── deploy.sh           # One-command deploy / start / stop / logs
└── pom.xml             # Maven multi-module build
```

---

## 🛠️ Development

### Prerequisites
- JDK 25+
- Node.js 18+
- pnpm 8+
- PostgreSQL 14+
- Docker & Docker Compose (optional, recommended)

### Backend

```bash
# 1. Start PostgreSQL + RustFS only (skip backend/frontend)
docker compose up -d postgres rustfs

# 2. Run the server in your IDE / via Maven
cd iot-server
mvn spring-boot:run
```

API ready at `http://localhost:5010/iot/`, OpenAPI at `http://localhost:5010/iot/doc.html`.

### Frontend

```bash
cd iot-web
pnpm install
pnpm dev
```

Web ready at `http://localhost:5173`. Vite proxies `/iot` → `http://localhost:5010` automatically.

> See [`AGENTS.md`](AGENTS.md) for the full coding conventions used by this project (component patterns, design tokens, naming rules).

---

## 🗺️ Roadmap

- [x] **v0.1** — Stable single-node release, bilingual docs, CI, public demo
- [x] **v0.2** — `zh-CN` / `en-US` UI, rule-chain editor polish, demo deploy hardening
- [ ] **v0.3** — thing-model import/export, device groups
- [ ] **v0.4** — Custom data dashboards (drag-and-drop widgets)
- [ ] **v0.5** — OTA upgrade flow, edge gateway packaging
- [ ] **v0.6** — Plugin system (protocol packs as standalone JARs)
- [ ] **v1.0** — Production hardening, performance benchmarks, helm chart

Open an issue or [Discussion](https://github.com/dingdaoyi/simple-iot/discussions) if there's a feature you want to see prioritised.

---

## 🤝 Contributing

Contributions are very welcome. Whether it's a typo fix, a new protocol script, a UI tweak, or a translation — every PR helps.

1. Read [CONTRIBUTING.md](CONTRIBUTING.md)
2. Fork → branch → commit (use [Conventional Commits](https://www.conventionalcommits.org/): `feat:`, `fix:`, `docs:`, ...)
3. Open a Pull Request

Found a bug or have a question? [Open an issue](https://github.com/dingdaoyi/simple-iot/issues/new/choose) or join the [Discussions](https://github.com/dingdaoyi/simple-iot/discussions).

---

## 🛡️ Security

If you discover a security vulnerability, please **do not** open a public issue. See [SECURITY.md](SECURITY.md) for the responsible-disclosure process.

---

## ⭐ Star History

<a href="https://star-history.com/#dingdaoyi/simple-iot&Date">
  <img src="https://api.star-history.com/svg?repos=dingdaoyi/simple-iot&type=Date" alt="Star History" width="600"/>
</a>

If this project helps you, please **drop a star ⭐** — it's the easiest way to support the work and helps others discover it.

---

## 📄 License

[Apache License 2.0](LICENSE) © dingdaoyi & contributors

---

<div align="center">

<sub>Built with ❤️ for makers, integrators and small teams shipping practical IoT products.</sub>

</div>
