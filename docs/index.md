---
layout: home

hero:
  name: Simple IoT
  text: The Glass IoT Platform
  tagline: Lightweight, single-binary, ready in 60 seconds.
  image:
    src: /banner.png
    alt: Simple IoT
  actions:
    - theme: brand
      text: Get Started →
      link: /guide/getting-started
    - theme: alt
      text: Live Demo
      link: http://122.51.129.91
    - theme: alt
      text: View on GitHub
      link: https://github.com/dingdaoyi/simple-iot

features:
  - icon: 🪶
    title: Single-binary core
    details: One Spring Boot jar + one Vue 3 SPA. No Kafka, no Zookeeper, no microservice mess. Run the whole stack on a 2 GB VPS.
  - icon: ⚡
    title: 60-second deploy
    details: <code>./deploy.sh deploy</code> brings up PostgreSQL, InfluxDB, RustFS, the backend and the web UI via Docker Compose.
  - icon: 🧩
    title: Visual rule engine
    details: Drag-and-drop rule chain editor with input / filter / transform / action / alarm nodes and template variables.
  - icon: 🔌
    title: Hot-loaded protocols
    details: Author MQTT/TCP/HTTP/UDP protocol decoders in Java, JavaScript, Groovy or Lua, hot-loaded at runtime.
  - icon: 📡
    title: Built-in MQTT broker
    details: <code>mica-mqtt</code> on TCP 1883 and WebSocket 8083, ready out of the box.
  - icon: 🌗
    title: Glassmorphism UI
    details: Modern Vue 3 admin UI with light / dark / auto themes, designed for telemetry dashboards.
---

## Why Simple IoT?

Most IoT platforms are heavy and distributed. **Simple IoT** is the opposite — a single jar plus a Vue 3 web app, ready to onboard thousands of devices on a small VPS.

| | Simple IoT | ThingsBoard CE | EMQX + custom UI |
|---|---|---|---|
| Architecture | Single binary | Multiple services | Broker only |
| First-run time | ≈ 60s | ≈ 10 min | DIY |
| Rule engine | Built-in, visual | Built-in | DIY |
| Time-series | InfluxDB 3 | Cassandra/Postgres | DIY |
| Footprint | < 1 GB RAM | > 4 GB RAM | varies |

→ [Read the Getting Started guide](/guide/getting-started)
