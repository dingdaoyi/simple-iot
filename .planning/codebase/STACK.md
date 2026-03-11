# Technology Stack

**Analysis Date:** 2026-03-11

## Languages

**Primary:**
- Java 25 - Backend server (IoT platform)
- JavaScript/TypeScript - Frontend web application (Vue 3)

**Secondary:**
- SQL - Database queries (PostgreSQL)
- SCSS - Frontend styling
- JavaScript (GraalVM) - Dynamic protocol scripting
- Groovy - Dynamic protocol scripting (partial support)

## Runtime

**Backend:**
- JDK 25 (Temurin distribution)
- Spring Boot 4.0.2

**Frontend:**
- Node.js (version not specified in .nvmrc)
- Vite 7.1.11 (build tool)

**Package Managers:**
- Maven 3 (backend) - pom.xml present
- pnpm (frontend) - inferred from project structure

## Frameworks

**Backend (Core):**
- Spring Boot 4.0.2 - Main application framework
- MyBatis-Plus 3.5.15 - ORM framework (spring-boot4-starter)
- Sa-Token 1.44.0 - Authentication/Authorization
- Knife4j 4.5.0 - API documentation (OpenAPI 3)

**Backend (IoT-specific):**
- Mica MQTT 2.5.11 - MQTT server (mica-mqtt-server-spring-boot-starter)
- InfluxDB 3 Java Client 1.8.0 - Time-series database client
- GraalVM Polyglot 24.1.1 - Script engine for dynamic protocols
- Apache Groovy 4.0.24 - Script engine support

**Frontend (Core):**
- Vue 3.5.13 - UI framework (Composition API + `<script setup>`)
- Vite 7.1.11 - Build tool and dev server
- Element Plus 2.11.3 - UI component library
- Pinia 2.2.8 - State management
- Vue Router 4.5.0 - Client-side routing

**Frontend (Utilities):**
- Axios 1.12.0 - HTTP client
- ECharts 5.6.0 + vue-echarts 8.0.1 - Chart visualization
- dayjs 1.11.13 - Date handling
- lodash 4.17.23 - Utility functions
- @vueuse/core 11.2.0 - Vue composition utilities
- colord 2.9.3 - Color manipulation
- @amap/amap-jsapi-loader 1.0.1 - AMap integration

**Testing:**
- spring-boot-starter-test - Backend testing

**Build/Dev:**
- Maven (backend) - Build and dependency management
- Vite (frontend) - Dev server and production builds

## Key Dependencies

**Backend - Critical:**
- PostgreSQL JDBC 42.7.4 - Database driver
- Caffeine (via spring-boot-starter-cache) - Local caching
- Hutool 5.8.42 - Java utility library
- dotenv-java 3.1.0 - Environment variable loading
- AWS S3 SDK 2.40.15 - Object storage
- Netty (multiple modules) - Async I/O for InfluxDB Arrow Flight
- Freemarker - Template engine
- Lombok 1.18.42 - Boilerplate reduction (JDK 25 compatible)
- sms4j 3.3.4 - SMS notification support

**Frontend - Critical:**
- @element-plus/icons-vue 2.3.1 - Icon library
- vue-json-viewer 3.0.4 - JSON display component
- fast-glob 3.3.2 - File pattern matching
- sass 1.83.0 - CSS preprocessor
- unocss 66.6.0 - Atomic CSS framework
- vite-plugin-svg-icons 2.0.1 - SVG sprite generation
- unplugin-vue-define-options 1.5.3 - Vue macro support

## Configuration

**Environment:**
- `.env` - Local environment variables (not committed)
- `.env.example` - Template for environment configuration
- dotenv-java loads `.env` at runtime

**Backend Config:**
- `iot-server/src/main/resources/application.yml` - Main Spring Boot config
- Environment variable substitution: `${VAR_NAME:default_value}`
- Key configs: database, InfluxDB, S3, MQTT, Sa-Token

**Frontend Config:**
- `iot-web/vite.config.mjs` - Vite configuration
- `iot-web/eslint.config.js` - ESLint with @antfu/eslint-config
- Path alias: `@` -> `./src`

**JVM Parameters (Required for JDK 21+):**
```
--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED
--sun-misc-unsafe-memory-access=allow
```

## Platform Requirements

**Development:**
- JDK 25 (Temurin)
- Node.js 18+ (for Vite 7)
- pnpm (package manager)
- PostgreSQL 16
- InfluxDB 3 (local or remote)
- Docker (optional, for full stack)

**Production:**
- Docker / Docker Compose
- PostgreSQL 16 (Alpine image)
- RustFS (S3-compatible object storage)
- InfluxDB 3 (external)

**Deployment:**
- Containerized via Docker
- docker-compose.yml provides full stack orchestration
- Services: postgres, rustfs, iot-server, iot-web
- Ports: 5010 (API), 80 (Web), 1883 (MQTT), 8083 (MQTT WS), 18083 (MQTT HTTP)

---

*Stack analysis: 2026-03-11*
