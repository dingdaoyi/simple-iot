---
layout: home

hero:
  name: Simple IoT
  text: The Minimal IoT Platform
  tagline: Self-hosted device management, telemetry, rules and alarms in one compact stack.
  image:
    src: /banner.png
    alt: Simple IoT
  actions:
    - theme: brand
      text: Get Started →
      link: /guide/getting-started
    - theme: alt
      text: Live Demo
      link: /guide/demo
    - theme: alt
      text: View on GitHub
      link: https://github.com/dingdaoyi/simple-iot

features:
  - icon: 🪶
    title: Single-binary core
    details: One Spring Boot application + one Vue 3 console. Easy to deploy, inspect and adapt for real IoT projects.
  - icon: ⚡
    title: Docker Compose deploy
    details: <code>./deploy.sh deploy</code> brings up PostgreSQL, InfluxDB, RustFS, the backend and the web UI.
  - icon: 🧩
    title: Visual rule engine
    details: Drag-and-drop rule chains with validation, script filters, debug path highlighting and reusable nodes.
  - icon: 🔌
    title: Hot-loaded protocols
    details: Author MQTT/TCP/HTTP/UDP protocol decoders in Java, JavaScript, Groovy or Lua, hot-loaded at runtime.
  - icon: 📡
    title: Built-in MQTT broker
    details: <code>mica-mqtt</code> on TCP 1883 and WebSocket 8083, ready out of the box.
  - icon: 🌗
    title: International UI
    details: Modern Vue 3 console with zh-CN / en-US, light / dark / auto themes and telemetry-friendly pages.
---

## What can you build with it?

Simple IoT gives you the core building blocks of a practical IoT console: product and thing-model management, MQTT/TCP/HTTP ingestion, scriptable protocol decoding, time-series storage, visual rule chains, alarms and notifications.

This release also adds a complete bilingual frontend, rule-chain editor polish, script-filter support, safer script test responses and a more reliable demo deployment pipeline.

→ [Read the Getting Started guide](/guide/getting-started)
