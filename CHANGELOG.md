# Changelog

All notable changes to **Simple IoT** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed
- **Driver page dropdowns** — added two missing dictionary endpoints (`GET /dict/driver-type`, `GET /dict/connection-type`) on the backend and the corresponding `getDriverTypeEnum()` / `getConnectionTypeEnum()` exports in `iot-web/src/api/dict.js`. The driver list page now loads its protocol-type and connection-type filters correctly. ([#0fa7f53](https://github.com/dingdaoyi/simple-iot/commit/0fa7f53))

## [0.1.0] — 2026-06-17

First public release. The platform has been used internally for over a year and is now opened up with proper docs, branding and community infrastructure.

### Added
- **Bilingual README** (English + 简体中文) with badges, comparison table, architecture diagram and screenshots.
- Brand assets: logo and banner SVG/PNG.
- Project metadata: GitHub Topics, Star History, license.
- Community files: `CONTRIBUTING.md`, `CODE_OF_CONDUCT.md`, `SECURITY.md`, issue and pull-request templates.
- Enhanced GitHub Actions: backend Maven build, frontend Vite build, Docker build sanity check.

### Core platform features (carried over from internal versions)
- **Device management** — registration, online/offline tracking, batch operations, command dispatch.
- **Product & thing model** — product types, properties, services, events.
- **Visual rule engine** — drag-and-drop chain editor with input / filter / transform / action / alarm nodes; template variables (`${deviceName}`, `${eventTime}`, ...).
- **Hot-loaded protocol scripts** in Java / JavaScript / Groovy / Lua.
- **Built-in MQTT broker** via `mica-mqtt` (TCP 1883, WebSocket 8083).
- **Time-series storage** on InfluxDB 3.
- **Notifications** — email, SMS, HTTP callback, MQTT forward, device commands.
- **Alarm center** with severity levels and active / cleared lifecycle.
- **Minimal UI** with light / dark / auto themes.
- **One-command deploy** — `./deploy.sh deploy` brings up the whole stack via Docker Compose.

### Stack
- Backend: Java 25 · Spring Boot 4.0.2 · Sa-Token · MyBatis-Plus · PostgreSQL · InfluxDB 3 · Caffeine · Hutool.
- Frontend: Vue 3 · Vite · Element Plus · Pinia · Vue Router · Axios · ECharts.
- Infra: Docker Compose · RustFS (S3-compatible) · GitHub Actions.

[Unreleased]: https://github.com/dingdaoyi/simple-iot/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/dingdaoyi/simple-iot/releases/tag/v0.1.0
