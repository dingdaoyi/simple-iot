# 架构总览

## 整体图

```text
                 ┌───────────────────────┐
                 │      Vue 3 Web UI     │  iot-web (Nginx :80)
                 └────────────┬──────────┘
                              │ /iot/*  (REST + WebSocket)
                              ▼
   ┌───────────────────────────────────────────────────────────┐
   │                   iot-server (Spring Boot :5010)          │
   │ ┌──────────┐ ┌────────────┐ ┌─────────────┐ ┌───────────┐ │
   │ │ REST API │ │ Sa-Token   │ │ 规则引擎    │ │ MQTT      │ │
   │ │ (mvc)    │ │ 鉴权       │ │ (链式 DAG)  │ │ broker    │ │
   │ └────┬─────┘ └──────┬─────┘ └──────┬──────┘ └─────┬─────┘ │
   │      │              │              │              │       │
   │      └──────────────┴──────┬───────┴──────────────┘       │
   └────────────────────────────┼──────────────────────────────┘
                                │
        ┌───────────────────────┼─────────────────────────────┐
        ▼                       ▼                             ▼
  ┌──────────┐           ┌─────────────┐              ┌──────────────┐
  │PostgreSQL│           │ InfluxDB 3  │              │ RustFS (S3)  │
  │ 元数据   │           │ 时序数据    │              │ 文件 / 固件  │
  └──────────┘           └─────────────┘              └──────────────┘

         ▲
         │ MQTT 1883 / WS 8083
   ┌─────┴──────┐
   │   设备     │
   └────────────┘
```

## 组件说明

### 后端 — `iot-server`

单一 Spring Boot 4 jar，提供：

- `/iot/**` 下的 **REST API**（控制器在 `com.github.dingdaoyi.controller`）。
- **Sa-Token** 会话与 token 鉴权。
- **MyBatis-Plus** 操作 PostgreSQL。
- **mica-mqtt** 内置 broker，TCP 1883 / WebSocket 8083。
- **规则引擎** 消费 MQTT 与 REST 事件，分发到动作节点。
- **协议工厂** 动态加载解码脚本（Java / JS / Groovy / Lua）。
- **InfluxDB 3** 客户端读写时序数据。

### 前端 — `iot-web`

Vue 3 + Vite + Element Plus + Pinia。

- 源码在 `iot-web/src/`。
- 开发服务器：`pnpm dev` 监听 `9999`，把 `/iot` 反代到后端 `:5010`。
- 生产构建：`pnpm build` 产出静态文件，由 Nginx 托管。

### 存储

| 组件 | 作用 |
|---|---|
| **PostgreSQL 16** | 设备 / 产品 / 规则 / 用户 / 配置 / 告警等元数据。 |
| **InfluxDB 3** | 时序遥测数据（`tb_telemetry` 测量）。 |
| **RustFS** | S3 兼容对象存储，用于固件 / 图标 / 上传文件。 |

## 本地开发流程

```bash
# 1) 仅启动数据层
docker compose up -d postgres        # influx 与 rustfs 开发期可不开

# 2) 后端（无 hot-reload，重打 jar 即可）
mvn -B -ntp -DskipTests clean install -pl iot-server -am
java -jar iot-server/target/iot-server-0.0.1-SNAPSHOT.jar

# 3) 前端
cd iot-web
pnpm install
pnpm dev    # http://localhost:9999  → /iot 反代后端
```

## 模块结构

```
simple-iot/
├── iot-common/         # 公共 DTO / 工具
├── iot-driver/         # 协议驱动（MQTT/TCP/HTTP/UDP）
├── iot-rule/           # 规则引擎节点
├── iot-server/         # Spring Boot 主模块
├── iot-web/            # Vue 3 管理后台
├── doc/                # 原始笔记与截图
├── docs/               # 当前 VitePress 站点
└── deploy.sh           # 一键 Docker Compose 部署
```
