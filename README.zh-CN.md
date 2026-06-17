<div align="center">

<img src="doc/brand/banner.png" alt="Simple IoT — 极简物联网平台" width="100%"/>

# Simple IoT

**极简物联网平台 — 轻量、单体、60 秒可用。**

[![CI](https://github.com/dingdaoyi/simple-iot/actions/workflows/ci.yml/badge.svg)](https://github.com/dingdaoyi/simple-iot/actions/workflows/ci.yml)
[![License](https://img.shields.io/github/license/dingdaoyi/simple-iot?color=blue)](LICENSE)
[![Java](https://img.shields.io/badge/Java-25-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.x-42b883?logo=vue.js)](https://vuejs.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?logo=docker)](docker-compose.yml)
[![GitHub Stars](https://img.shields.io/github/stars/dingdaoyi/simple-iot?style=social)](https://github.com/dingdaoyi/simple-iot/stargazers)

[English](README.md) · **简体中文** · [在线体验](http://122.51.129.91) · [文档](https://dingdaoyi.github.io/simple-iot/zh/) · [反馈 Bug](https://github.com/dingdaoyi/simple-iot/issues)

</div>

---

## ✨ 为什么选 Simple IoT?

大部分物联网平台都太重、太分布式，对中小型场景来说杀鸡用牛刀。
**Simple IoT** 反其道而行：一个 Spring Boot 单体 + Vue 3 前端，**没有 Kafka、没有 Zookeeper、没有微服务地狱**。一台 2 GB 的小服务器就能跑起来，几分钟内接入上千台设备。

| 维度 | Simple IoT | ThingsBoard CE | EMQX + 自研 UI |
|---|---|---|---|
| 架构 | 单体 | 多服务 | Broker + 自己撸 |
| 内存占用 | ~ 512 MB | ~ 2 GB+ | ~ 1 GB + 你的栈 |
| MQTT Broker | 内置（mica-mqtt） | 内置 | 是（仅此而已） |
| 可视化规则引擎 | ✅ 拖拽式 | ✅ | ❌ |
| 脚本化协议 | ✅ Java / JS / Groovy / Lua | 有限 | ❌ |
| 时序存储 | InfluxDB 3 | Cassandra / Postgres | 自选 |
| 现代 UI | 极简风 + 暗色模式 | Material（偏旧） | 自己做 |
| 一键部署 | `./deploy.sh` | docker-compose（重） | DIY |
| 适用场景 | 中小企业、内部系统、边缘网关 | 企业级、多租户 SaaS | 纯消息中转 |

> **一句话** — 想要一套**可生产、能自托管、读得懂、能 Fork、改完就能上线**的物联网平台，又不想被分布式系统折磨，就用 Simple IoT。

---

## 🚀 快速开始（Docker，60 秒）

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
chmod +x deploy.sh
./deploy.sh deploy
```

然后访问：

| 服务 | 地址 | 默认账号 |
|---------|-----|--------------------|
| 前端 | http://localhost | `admin` / `123456` |
| API 文档 | http://localhost:5010/iot/doc.html | — |
| MQTT | `mqtt://localhost:1883` | — |
| MQTT WebSocket | `ws://localhost:8083/mqtt` | — |

完事。PostgreSQL、RustFS（S3 兼容）、后端、前端一起拉起。

> 想本地开发？看 [开发指南](#-本地开发)。

---

## 🎯 核心功能

| 模块 | 能力 |
|--------|--------------|
| **设备管理** | 注册、上下线监控、批量操作、指令下发 |
| **产品 & 物模型** | 产品类型、属性、服务、事件 — TSL 借鉴阿里云 Link |
| **协议引擎** | 热加载脚本，支持 **Java / JavaScript / Groovy / Lua**，无需重启 |
| **可视化规则引擎** | 拖拽式规则链编辑器：输入 → 过滤 → 转换 → 动作 |
| **告警中心** | 多级（提示 / 警告 / 严重 / 紧急），活动 / 已清除生命周期 |
| **数据采集** | InfluxDB 3 时序存储，Caffeine 进程内缓存 |
| **通知推送** | 邮件、短信、HTTP 回调、MQTT 转发、设备指令 |
| **仪表盘** | 设备总数、在线统计、系统资源（CPU / 内存 / 磁盘）、实时告警 |
| **认证权限** | 基于 Sa-Token，细粒度，角色 / 菜单 / 按钮级 |
| **极简 UI** | 极简 Linear 风设计，亮色 / 暗色 / 跟随系统 |

---

## 🖼️ 界面预览

<div align="center">

### 仪表盘
<img src="doc/screenshots/dashboard.png" alt="Dashboard" width="80%"/>

### 可视化规则引擎
<img src="doc/screenshots/rule-chain-editor.png" alt="Rule Engine Editor" width="80%"/>

### 设备管理
<img src="doc/screenshots/device.png" alt="Device" width="80%"/>

### 协议脚本
<img src="doc/screenshots/protocol.png" alt="Protocol" width="80%"/>

### 告警管理
<img src="doc/screenshots/alarm.png" alt="Alarm" width="80%"/>

</div>

---

## 🏗️ 架构

```
┌──────────────────────────────────────────────────────────────────┐
│                          Simple IoT                              │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Vue 3 + Vite + Element Plus  (极简 UI)                       │
│           │                                                      │
│           ▼  REST / WebSocket                                    │
│   ┌──────────────────────────────────────────────────────────┐   │
│   │   Spring Boot 4 (单 JVM、单二进制)                       │   │
│   │                                                          │   │
│   │   • Sa-Token 认证        • MyBatis-Plus                  │   │
│   │   • Caffeine 本地缓存    • Knife4j OpenAPI               │   │
│   │   • 可视化规则引擎       • 热加载协议脚本                │   │
│   │   • mica-mqtt Broker (1883 / 8083 ws)                    │   │
│   └──────────────────────────────────────────────────────────┘   │
│           │                          │                           │
│           ▼                          ▼                           │
│   ┌──────────────┐          ┌────────────────┐                   │
│   │ PostgreSQL   │          │ InfluxDB 3     │                   │
│   │ (业务数据)   │          │ (遥测数据)     │                   │
│   └──────────────┘          └────────────────┘                   │
│                                                                  │
│   ┌──────────────────────────────────────────────────────────┐   │
│   │  设备  →  MQTT / TCP / HTTP  →  协议脚本                 │   │
│   └──────────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### 技术栈

**后端** — Java 25 · Spring Boot 4.0.2 · Sa-Token · MyBatis-Plus · PostgreSQL · InfluxDB 3 · mica-mqtt · Caffeine · Hutool · AWS S3 SDK
**前端** — Vue 3 · Vite · Element Plus · Pinia · Vue Router · Axios · ECharts
**基建** — Docker Compose · RustFS（S3 兼容） · GitHub Actions

---

## 📦 项目结构

```
simple-iot/
├── iot-server/         # 主服务（REST + MQTT Broker + 规则引擎）
├── iot-common/         # 公共基类（BaseEntity、ResultCode、分页）
├── iot-driver/         # 系统内置协议驱动
├── iot-web/            # Vue 3 管理后台
├── doc/                # 文档、SQL 脚本、截图、品牌资产
├── docker-compose.yml  # 一键栈：postgres + rustfs + iot-server + iot-web
├── deploy.sh           # 一键部署 / 启停 / 日志
└── pom.xml             # Maven 多模块构建
```

---

## 🛠️ 本地开发

### 环境要求
- JDK 25+
- Node.js 18+
- pnpm 8+
- PostgreSQL 14+
- Docker & Docker Compose（可选，推荐）

### 后端

```bash
# 1. 仅启动 PostgreSQL + RustFS（不启后端/前端）
docker compose up -d postgres rustfs

# 2. 在 IDE 或命令行启动
cd iot-server
mvn spring-boot:run
```

后端就绪 `http://localhost:5010/iot/`，OpenAPI 文档 `http://localhost:5010/iot/doc.html`。

### 前端

```bash
cd iot-web
pnpm install
pnpm dev
```

前端就绪 `http://localhost:5173`，Vite 自动代理 `/iot` → `http://localhost:5010`。

> 完整代码规范、组件模式、设计 Token、命名约定见 [`AGENTS.md`](AGENTS.md)。

---

## 🗺️ 路线图

- [ ] **v0.1** — 单节点稳定版、英文文档、Docker Hub 镜像
- [ ] **v0.2** — i18n（英文界面）、物模型导入导出、设备分组
- [ ] **v0.3** — 自定义数据大屏（拖拽组件）
- [ ] **v0.4** — OTA 升级流程、边缘网关打包
- [ ] **v0.5** — 插件系统（协议包独立 JAR）
- [ ] **v1.0** — 生产硬化、性能 Benchmark、Helm Chart

想要的功能？开 [Issue](https://github.com/dingdaoyi/simple-iot/issues) 或 [Discussion](https://github.com/dingdaoyi/simple-iot/discussions)。

---

## 🤝 参与贡献

非常欢迎贡献。无论是改错别字、加协议脚本、调 UI、做翻译 — 每个 PR 都有价值。

1. 阅读 [CONTRIBUTING.md](CONTRIBUTING.md)
2. Fork → 建分支 → 提交（推荐 [Conventional Commits](https://www.conventionalcommits.org/)：`feat:` / `fix:` / `docs:` …）
3. 提交 PR

发现 Bug 或有问题？[开 Issue](https://github.com/dingdaoyi/simple-iot/issues/new/choose) 或加入 [Discussions](https://github.com/dingdaoyi/simple-iot/discussions)。

---

## 🛡️ 安全

发现安全漏洞？**请勿** 公开提 Issue，参考 [SECURITY.md](SECURITY.md) 的负责任披露流程。

---

## ⭐ Star 趋势

<a href="https://star-history.com/#dingdaoyi/simple-iot&Date">
  <img src="https://api.star-history.com/svg?repos=dingdaoyi/simple-iot&type=Date" alt="Star History" width="600"/>
</a>

如果这个项目对你有帮助，**点个 Star ⭐** 是最直接的支持，也能让更多人发现它。

---

## 📄 开源协议

[Apache License 2.0](LICENSE) © dingdaoyi 与所有贡献者

---

<div align="center">

<sub>用心打造，献给所有想认认真真做 IoT、又不想被复杂度税压垮的小团队、集成商和创客。</sub>

</div>
