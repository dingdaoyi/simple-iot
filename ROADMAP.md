# Roadmap

> Last updated: 2026-06-25 · Maintainer: [@dingdaoyi](https://github.com/dingdaoyi)
>
> Have an idea? Open a [GitHub Discussion](https://github.com/dingdaoyi/simple-iot/discussions) or vote on an item below 👍.

This roadmap is the honest list of what is **missing**, **rough**, or **deserves a rewrite**. Items are bucketed by priority, not by date — Simple IoT is a side-project, things ship when they ship.

---

## P0 — quality / production readiness

| # | Item | Why it matters |
|---|---|---|
| P0-1 | ✅ **Test coverage** - 173 tests across 34 test files, 12/12 rule-engine node types covered. | The platform handles real devices - silent regressions are unacceptable. |
| P0-2 | ✅ **Integration test** harness with Testcontainers (Postgres container + embedded MQTT). Smoke test boots Spring context, verifies tb_user schema and OpenAPI docs endpoint. | Catches Docker / wiring breakage before release. |
| P0-3 | ✅ **Open API spec** - springdoc-openapi 3.1.0 published at `/v3/api-docs`, Knife4j Swagger UI at `/iot/doc.html`. | API docs are now auto-generated, not hand-written. |
| P0-4 | **Auth hardening** - JWT secret rotation, refresh-token revocation list, password complexity policy. | Default deployments expose admin/123456 - needs a forced change-password on first login. |
| P0-5 | ✅ **Data retention** - `DataRetentionService` purges CLEARED alarms (>90d) and push logs (>30d) via daily cron. Retention period configurable. | Disks fill up silently today. |
| P0-6 | ✅ **Backup / restore** scripts - `deploy.sh backup` and `deploy.sh restore` for Postgres + RustFS. | Required before anyone uses this in prod. |
| P0-7 | ✅ **Observability** - Micrometer + Prometheus endpoint at `/actuator/prometheus`, JVM + HTTP + DB metrics exposed. Grafana dashboard JSON TBD. | Today there is no way to tell if the broker is dropping packets. |

## P1 — features users keep asking for

| # | Item | Why it matters |
|---|---|---|
| P1-1 | **Multi-tenant** — tenant_id on every table, row-level security, per-tenant quotas. | Right now everyone shares one space. |
| P1-2 | ✅ **Device groups & tags** - tree-structured groups, multi-tag system, bulk device assignment. | Hard to operate >1000 devices without grouping. |
| P1-3 | ✅ **Alarm enhancements** - comments, escalate severity, acknowledge-with-note. | Current alarm is fire-and-forget. |
| P1-4 | ✅ **Rule-engine v2** - sub-flows with cycle detection, persistent execution logs, replay from recorded payloads. | Complex flows should be easy to author and debug. |
| P1-5 | **Custom dashboards** — drag-and-drop widget canvas (line, gauge, value card, map, table) bound to device telemetry. | Required to call this an "IoT platform" with a straight face. |
| ~~P1-6~~ | ✅ **OTA firmware** - upload firmware -> push to device groups -> progress tracking. | Most real IoT projects need OTA on day 1. |
| P1-7 | **Mobile-friendly Web UI** — current admin is desktop-only; need a responsive read-only view for ops on the phone. | Tied to the UI overhaul (see "Frontend" below). |
| P1-8 | ✅ **i18n foundation** — `zh-CN` / `en-US` UI, language switcher and Element Plus locale sync have landed; continue translating new pages as they are added. | Keeps the console usable for both Chinese and English users. |

## P2 — protocol / driver expansion

| # | Item | Notes |
|---|---|---|
| P2-1 | **Modbus TCP/RTU** driver | most-requested industrial protocol |
| P2-2 | **OPC UA** client driver | factory / SCADA integrations |
| P2-3 | **CoAP** server | low-power devices |
| P2-4 | **HTTP webhook** ingress with HMAC verification | third-party platforms push to us |
| P2-5 | **LwM2M** driver | mobile / NB-IoT modules |
| P2-6 | Driver SDK + Maven archetype `simple-iot-driver-archetype` | so contributors can publish drivers without forking |

## P3 — frontend / UI overhaul (tracked separately in `UI-DESIGN.md`)

The current Web UI works, but visually it is closer to "internal admin tool" than to "modern IoT platform". A redesign is being scoped:

- New design tokens (typography scale, semantic colors, spacing rhythm) with a clean product-console style.
- Component audit (forms, tables, dialogs, empty states, dark mode parity).
- Real telemetry-oriented dashboard widgets (sparkline cards, status grids, alarm timeline).
- Onboarding flow (first-run wizard, sample device + sample rule chain).
- Accessibility pass (focus rings, keyboard nav, ARIA on table actions).

→ Detailed proposal lives in [`UI-DESIGN.md`](./UI-DESIGN.md).

## P4 — DX / community

| # | Item |
|---|---|
| P4-1 | Helm chart + Kubernetes manifests |
| P4-2 | One-click cloud deploy buttons (Sealos / Zeabur / Render / Railway) |
| P4-3 | Demo seed script — fake devices + simulated MQTT publisher |
| P4-4 | English-language video tour (3–5 min) |
| P4-5 | Contributor guide deep-dive: how to add a driver, how to add a rule node |

---

## Done recently

- ✅ v0.1.0 release - Spring Boot 4 + JDK 25 baseline
- ✅ Bilingual README (EN + zh-CN)
- ✅ Brand identity - logo, banner, tagline
- ✅ GitHub Discussions, Issue / PR templates, CODE_OF_CONDUCT, SECURITY policy
- ✅ CI: backend build + frontend build/lint + Docker smoke test
- ✅ VitePress documentation site at <https://dingdaoyi.github.io/simple-iot/>
- ✅ Driver-page dictionary endpoint fix
- ✅ Frontend i18n foundation (`zh-CN` / `en-US`)
- ✅ Rule-chain editor zoom/pan, shortcuts, node search, validation and debug path highlighting
- ✅ Demo deploy workflow hardened for flaky SSH and slow links
- ✅ P0-1: 173 tests, 12/12 rule-engine node types covered
- ✅ P0-2: Testcontainers integration test (Postgres + Spring context smoke)
- ✅ P0-3: OpenAPI 3.1 spec at `/v3/api-docs` + Knife4j Swagger UI
- ✅ P0-5: DataRetentionService - daily cron purges old alarms + push logs
- ✅ P0-6: `deploy.sh backup` / `deploy.sh restore` for Postgres + RustFS
- ✅ P0-7: Micrometer + Prometheus metrics at `/actuator/prometheus`

## Not planned

- ❌ Microservice split. Simple IoT's value proposition is "one jar". A multi-service variant is out of scope.
- ❌ Kafka / Pulsar dependency. We will not require an external message bus.
- ❌ Commercial / enterprise edition. Single MIT-licensed codebase forever.

---

Want to take one of these on? Comment on the matching Discussion or open a draft PR — happy to pair on it.
