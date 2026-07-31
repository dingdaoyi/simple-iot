# Changelog

All notable changes to **Simple IoT** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.3.0] - 2026-07-31

### Added
- **Webhook Ingress** (`P2-4`) - HMAC-SHA256 signed HTTP ingress for third-party platforms. Per-webhook token + secret, timestamp window replay protection, CRUD config API and frontend management page with curl usage guide. Raw JSON `{"temp":25.5}` auto-wrapped to standard protocol format per-property.
- **Modbus TCP driver** (`P2-1`) - `ModbusPollingService` with per-device scheduled polling, register mapping config, multi-function-code read (FC 1/2/3/4), scale + data-type conversion. `modbus-tcp` protocol decoder registered. Frontend config page. Demo `modbus-sim` container included.
- **System resource monitoring** - Dashboard now shows real CPU%, memory%, disk% via `OperatingSystemMXBean` (replaces placeholder zeros).
- **UI component audit** (`P3`) - 8 pages unified to `PageHeader` + `glass-card` + `el-empty` empty states, inline styles removed, 40 lines of duplicate CSS deleted.

### Fixed
- **Device latest data API** - `ORDER BY time DESC LIMIT 1` per-property replaces `last_value()` (InfluxDB v3 returns write-order, not time-order, for `last_value()`).
- **Webhook NPE** - `DefaultProtocolDecoder.responseError` now null-checks `DeviceConnection` (webhook pushes have no connection).
- **MQTT msgId NPE** - property reports without `msgId` no longer trigger NPE in `responseRegister`.

### Changed
- **Platform maturity** - raised to 80%.
- **ROADMAP** - P2-1 Modbus and P2-4 Webhook marked complete.

## [0.2.0] - 2026-07-23

### Added
- **WebSocket real-time push** - dashboard widgets now subscribe to WebSocket telemetry/alarm channels for instant updates (30s poll kept as fallback).
- **Schedule trigger node** (`INPUT_SCHEDULE`) - cron-based rule chain execution with `ScheduleTriggerService`, auto-registers on rule chain CRUD.
- **IM push notifications** - DingTalk, WeCom (WeChat Work), and Feishu webhook integration via `WebhookNotificationService` + per-platform payload builders. `tb_im_push_config` table with CRUD API and frontend config page.
- **Batch device commands** - `POST /service/batch/{identifier}` endpoint, frontend batch selection dialog with per-device result reporting.
- **Device topology** - `Device.parentId` field + `GET /device/{id}/children` API, frontend child-devices tab in device details.
- **Data export** - Excel export buttons on device list and alarm list pages.
- **Dashboard widgets** - 2 new widget types: `device-grid` (online/offline status matrix) and `alarm-list` (recent 10 alarms with severity tags).
- **InfluxDB telemetry retention** - scheduled daily purge of telemetry data older than configurable 180 days (default).
- **VitePress docs** - 8 new guide pages (4 zh + 4 en): device-topology, batch-command, im-push, schedule-trigger.
- **API rate limiting + audit log** - Bucket4j token bucket + `tb_audit_log` table + AOP `@AuditLog` aspect.
- **Alarm suppression** - 5-minute dedup window per device+type, prevents alarm storms on flapping devices.
- **9 new unit tests** (190 total) - InputScheduleNode, InputScheduleConfig, ImPushPayload (DingTalk/WeCom/Feishu).

### Changed
- **Platform maturity** - raised from 65% to 75% (12 capability domains updated).
- **NotificationType enum** - added `DINGTALK(3)`, `WECOM(4)`, `FEISHU(5)`.
- **RuleNodeType enum** - added `INPUT_SCHEDULE` to INPUT category.
- **WidgetRenderer** - WS subscription for `telemetry` and `alarm` channels, replaces pure 30s polling.

## [0.1.1] — 2026-06-25

### Added
- **Bilingual frontend UI** — added `zh-CN` / `en-US` language packs, a language switcher, Element Plus locale sync and semantic translation keys across the main console.
- **Rule-chain editor improvements** — canvas zoom/pan, keyboard shortcuts, node search, validation, script filter support and debug path highlighting.
- **Signed push testing** — better delivery-setting validation and HMAC-style push test coverage for integration scenarios.

### Changed
- **Project introduction** — refreshed README and documentation homepage around product capabilities and release highlights, without competitor-comparison positioning.
- **Demo deployment** — GitHub Actions now triggers a remote demo-server build via SSH/nohup/status polling instead of uploading Docker image tarballs over a slow SSH link.
- **Frontend CI** — added `pnpm lint` to the GitHub Actions frontend job.

### Fixed
- **Script/protocol test errors** — user script mistakes now return readable validation responses instead of noisy server-side 500 errors.
- **Driver page dropdowns** — added two missing dictionary endpoints (`GET /dict/driver-type`, `GET /dict/connection-type`) and frontend API exports so filters load correctly. ([#0fa7f53](https://github.com/dingdaoyi/simple-iot/commit/0fa7f53))
- **Demo health checks** — disabled mail health from failing container health when SMTP is intentionally not configured in the demo environment.

## [0.1.0] — 2026-06-17

First public release. The platform has been used internally for over a year and is now opened up with proper docs, branding and community infrastructure.

### Added
- **Bilingual README** (English + 简体中文) with badges, architecture diagram, screenshots and quick-start instructions.
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

[Unreleased]: https://github.com/dingdaoyi/simple-iot/compare/v0.1.1...HEAD
[0.1.1]: https://github.com/dingdaoyi/simple-iot/compare/v0.1.0...v0.1.1
[0.1.0]: https://github.com/dingdaoyi/simple-iot/releases/tag/v0.1.0
