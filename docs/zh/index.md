---
layout: home

hero:
  name: Simple IoT
  text: 极简物联网平台
  tagline: 一套紧凑的自托管物联网控制台：设备、遥测、规则、告警。
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
    details: 一个 Spring Boot 应用 + 一个 Vue 3 控制台，易部署、易阅读、易改造成真实项目。
  - icon: ⚡
    title: Docker Compose 部署
    details: <code>./deploy.sh deploy</code> 一键拉起 PostgreSQL、InfluxDB、RustFS、后端和 Web UI。
  - icon: 🧩
    title: 可视化规则引擎
    details: 拖拽编排规则链，支持规则校验、脚本过滤、调试路径高亮和节点复用。
  - icon: 🔌
    title: 协议热加载
    details: MQTT / TCP / HTTP / UDP 解码脚本，可用 Java / JavaScript / Groovy / Lua 编写，运行时加载。
  - icon: 📡
    title: 内置 MQTT Broker
    details: 基于 <code>mica-mqtt</code>，TCP 1883 + WebSocket 8083，开箱即用。
  - icon: 🌗
    title: 国际化 UI
    details: Vue 3 控制台，支持 zh-CN / en-US、浅色 / 深色 / 自动三主题和遥测页面。
---

## 它能做什么？

Simple IoT 提供一套物联网控制台的核心能力：产品与物模型管理、MQTT/TCP/HTTP 接入、脚本化协议解析、时序数据存储、可视化规则链、告警和通知推送。

本次发版补齐了前端双语界面，优化了规则链编辑器，增加脚本过滤能力，改进脚本测试错误反馈，并加固了演示环境部署流水线。

→ [阅读快速开始](/zh/guide/getting-started)
