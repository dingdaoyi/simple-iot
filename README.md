# Simple IoT Platform

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883" alt="Vue 3">
  <img src="https://img.shields.io/badge/Java-25-orange" alt="Java 21">
  <img src="https://img.shields.io/badge/License-Apache-blue" alt="License">
</p>

<p align="center">
  <b>轻量级 · 易部署 · 高性能</b>
</p>

<p align="center">
  一款面向中小企业的轻量级物联网管理平台，采用单体架构设计，<br>
  聚焦核心功能，降低分布式复杂度，开箱即用。
</p>

---

## 在线体验

- **演示地址**：http://106.13.72.27:8962
- **体验账号**：`admin` / `123456`

---

## 核心亮点

### 现代化技术栈
- **后端**：Java 21 + Spring Boot 4.0.1，拥抱最新技术
- **前端**：Vue 3 + Vite + Element Plus，极速开发体验
- **数据库**：PostgreSQL + InfluxDB 3，业务数据与时序数据分离

### 精美的 UI 设计
- **玻璃态设计** (Glassmorphism)，现代感十足
- **暗色/亮色主题** 自动切换，护眼舒适
- **响应式布局**，完美适配各种屏幕尺寸

### 灵活的协议支持
- 内置 **MQTT Broker**，无需额外部署
- 支持 **Java / JavaScript / Groovy / Lua** 脚本协议
- 动态协议加载，无需重启服务

### 高效的数据处理
- **时序数据库** InfluxDB 3，高效存储设备遥测数据
- **本地缓存** Caffeine，降低数据库压力
- **细粒度权限** Sa-Token，安全可控

---

## 功能模块

| 模块 | 功能描述 |
|------|----------|
| **设备管理** | 设备注册、在线状态监控、批量操作 |
| **产品管理** | 产品类型定义、物模型配置、产品图标 |
| **协议管理** | 多语言协议脚本、动态加载、启用/禁用 |
| **规则引擎** | 数据流转、告警通知、第三方推送 |
| **数据监控** | 实时数据展示、历史数据查询 |
| **系统设置** | 用户管理、权限配置、系统参数 |

---

## 技术架构

### 后端技术

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.1 | 核心框架 |
| Java | 21 | 运行时环境 |
| Sa-Token | - | 认证鉴权 |
| MyBatis-Plus | - | ORM 框架 |
| PostgreSQL | - | 业务数据库 |
| InfluxDB | 3.0 | 时序数据库 |
| Caffeine | - | 本地缓存 |
| mica-mqtt | - | MQTT Broker |
| Hutool | - | 工具库 |

### 前端技术

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 渐进式框架 |
| Vite | - | 构建工具 |
| Element Plus | - | UI 组件库 |
| Pinia | - | 状态管理 |
| Vue Router | - | 路由管理 |
| Axios | - | HTTP 客户端 |

---

## 快速开始

### 环境要求

- JDK 21+
- Node.js 18+
- PostgreSQL 14+
- InfluxDB 3.0 (可选)

### 后端启动

```bash
# 克隆项目
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot

# 配置数据库 (修改 application.yml)
# 创建 PostgreSQL 数据库

# 启动后端服务
cd iot-server
mvn spring-boot:run
```

### 前端启动

```bash
# 安装依赖
cd iot-web
pnpm install

# 启动开发服务器
pnpm dev
```

### 访问系统

- 前端地址：http://localhost:5173
- API 文档：http://localhost:8080/doc.html
- 默认账号：`admin` / `123456`

---

## 项目结构

```
sample-iot/
├── iot-server/                 # 后端服务
│   ├── src/main/java/
│   │   └── com/github/dingdaoyi/
│   │       ├── config/         # 配置类
│   │       ├── controller/     # 控制器
│   │       ├── service/        # 业务逻辑
│   │       ├── mapper/         # 数据访问
│   │       ├── entity/         # 实体类
│   │       └── model/          # 数据模型
│   └── src/main/resources/
│       ├── mapper/             # MyBatis XML
│       └── application.yml     # 配置文件
│
├── iot-web/                    # 前端项目
│   ├── src/
│   │   ├── api/                # API 接口
│   │   ├── components/         # 公共组件
│   │   ├── composables/        # 组合式函数
│   │   ├── layout/             # 布局组件
│   │   ├── router/             # 路由配置
│   │   ├── store/              # 状态管理
│   │   ├── styles/             # 全局样式
│   │   └── views/              # 页面组件
│   └── vite.config.mjs         # Vite 配置
│
├── iot-protocol-core/          # 协议核心模块
└── iot-common/                 # 公共模块
```

---

## 界面预览

### 首页仪表盘
- 设备概览统计（总数、在线、离线、今日新增）
- 系统资源监控（CPU、内存、磁盘）
- 最近设备列表
- 告警动态通知

### 产品管理
- 卡片视图 / 列表视图切换
- 产品图标自定义上传
- 快速跳转设备列表

### 设备管理
- 设备状态实时监控
- 物模型数据展示
- 在线/离线状态管理

### 协议管理
- 多语言脚本支持
- 动态协议加载
- 一键启用/禁用

---

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 开源协议

本项目基于 [Apache](LICENSE) 协议开源。

---

## 联系方式

如有问题或建议，欢迎提交 Issue 或 Pull Request。

<p align="center">
  <b>⭐ 如果这个项目对你有帮助，请给一个 Star 支持一下！⭐</b>
</p>
