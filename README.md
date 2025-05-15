# Simple IoT Platform 🚀

[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://opensource.org/licenses/MIT)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen)](https://spring.io/projects/spring-boot)

> 一个轻量级物联网平台，采用单体架构设计，聚焦核心功能实现，降低分布式架构的复杂度，适合中小规模物联网场景。

## ✨ 功能特性

- **设备全生命周期管理**：产品/物模型/设备管理
- **多协议支持**：内置MQTT协议（系统默认），支持协议扩展
- **时序数据存储**：专为物联网数据优化的存储方案
- **轻量级认证**：细粒度的权限控制体系
- **实时监控**：设备状态实时追踪与管理
- **规则引擎**：实现自定义数据推送,包含数据流转,消息通知

## 🚧 项目状态

开发中，最新进展请关注
诚邀小伙伴一同开发

在线演示：http://106.13.72.27:8962  
测试账号：`admin` / `123456`

## 🛠️ 技术栈

### 后端技术栈
| 组件              | 说明                          |
|-----------------|-----------------------------|
| Spring Boot 3.x | 核心框架                     |
| SaToken         | 认证鉴权解决方案             |
| MyBatis-Plus    | 数据持久层框架               |
| InfluxDB 3.0    | 时序数据存储                 |
| PostgreSQL      | 业务关系型数据库             |
| Caffeine        | 本地缓存解决方案             |
| mica-mqtt       | 内嵌MQTT Broker              |

### 前端技术栈
- Vue3 + javaScript
- Element Plus
- Axios
- Vue Router