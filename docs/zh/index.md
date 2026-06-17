---
layout: home

hero:
  name: Simple IoT
  text: 极简物联网平台
  tagline: 轻量、单体、60 秒可用。
  image:
    src: /banner.png
    alt: Simple IoT
  actions:
    - theme: brand
      text: 快速开始 →
      link: /zh/guide/getting-started
    - theme: alt
      text: 在线演示
      link: /zh/guide/demo
    - theme: alt
      text: GitHub
      link: https://github.com/dingdaoyi/simple-iot

features:
  - icon: 🪶
    title: 单体内核
    details: 一个 Spring Boot jar + 一个 Vue 3 单页应用。不要 Kafka、不要 Zookeeper、不要微服务全家桶——2 GB 小服务器就能跑全套。
  - icon: ⚡
    title: 60 秒部署
    details: <code>./deploy.sh deploy</code> 一键拉起 PostgreSQL、InfluxDB、RustFS、后端、Web UI。
  - icon: 🧩
    title: 可视化规则引擎
    details: 输入 / 过滤 / 转换 / 动作 / 告警 五类节点拖拽组合，支持模板变量。
  - icon: 🔌
    title: 协议热加载
    details: MQTT / TCP / HTTP / UDP 解码脚本，可用 Java / JavaScript / Groovy / Lua 编写，运行时加载。
  - icon: 📡
    title: 内置 MQTT Broker
    details: 基于 <code>mica-mqtt</code>，TCP 1883 + WebSocket 8083，开箱即用。
  - icon: 🌗
    title: 极简 UI
    details: Vue 3 管理后台，浅色 / 深色 / 自动三主题，专为遥测大屏调优。
---

## 为什么选 Simple IoT？

主流 IoT 平台往往体量重、组件多。**Simple IoT** 走相反的路线：单 jar + Vue 3 前端，小型 VPS 就能接入数千设备。

| | Simple IoT | ThingsBoard CE | EMQX + 自研 UI |
|---|---|---|---|
| 架构 | 单体 | 多服务 | 仅 Broker |
| 首次启动 | ≈ 60s | ≈ 10 分钟 | 自己造 |
| 规则引擎 | 内置可视化 | 内置 | 自己造 |
| 时序存储 | InfluxDB 3 | Cassandra/Postgres | 自己造 |
| 内存占用 | < 1 GB | > 4 GB | 视方案 |

→ [阅读快速开始](/zh/guide/getting-started)
