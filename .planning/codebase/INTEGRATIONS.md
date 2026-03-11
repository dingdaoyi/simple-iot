# External Integrations

**Analysis Date:** 2026-03-11

## APIs & External Services

**SMS Services:**
- SMS4J 3.3.4 - Multi-provider SMS gateway
  - SDK: `org.dromara.sms4j:sms4j-spring-boot-starter`
  - Config: `sms.config-type: interface` in application.yml
  - Files: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/SmsNotificationService.java`
  - Files: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/SmsConfigServiceImpl.java`

**Map Services:**
- AMap (Gaode Maps) - Location/map display
  - SDK: `@amap/amap-jsapi-loader` (frontend)
  - Used in frontend for location-related features

## Data Storage

**Primary Database:**
- PostgreSQL 16
  - Connection: `jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${DATABASE}`
  - Driver: `org.postgresql.Driver`
  - ORM: MyBatis-Plus 3.5.15
  - Env vars: `POSTGRES_HOST`, `POSTGRES_PORT`, `POSTGRES_USERNAME`, `POSTGRES_PASSWORD`, `DATABASE`
  - Default: `postgres:5432/simple`

**Time-Series Database:**
- InfluxDB 3 (Cloud/Edge)
  - SDK: `com.influxdb:influxdb3-java:1.8.0`
  - Config class: `iot-server/src/main/java/com/github/dingdaoyi/iot/influx/InfluxDbConfig.java`
  - Data processor: `iot-server/src/main/java/com/github/dingdaoyi/iot/influx/InfluxDataProcessor.java`
  - Env vars: `INFLUXDB_URL`, `INFLUXDB_TOKEN`, `INFLUXDB_DATABASE`
  - Databases: `simple` (main), `prop_data` (properties), `even_data` (events)
  - Default: `http://127.0.0.1:8181`

**Object Storage:**
- S3-Compatible Storage (RustFS / MinIO / AWS S3)
  - SDK: `software.amazon.awssdk:s3:2.40.15`
  - Implementation: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/S3StorageService.java`
  - Env vars: `S3_ACCESS_KEY_ID`, `S3_SECRET_ACCESS_KEY`, `S3_BUCKET_NAME`, `S3_REGION`, `S3_ENDPOINT`
  - Default: `http://rustfs:9000` (Docker) / `http://localhost:9000` (local)
  - Bucket auto-creation on startup

**Caching:**
- Caffeine (local in-memory cache)
  - Config: `spring.cache.type: caffeine`
  - Spec: `maximumSize=1000,expireAfterWrite=30s`
  - Used for TSL models, product types, etc.

**File Storage:**
- Local filesystem (optional alternative to S3)
  - Config: `sample.iot.storage-type: file` or `s3`
  - Not actively used (S3 is default)

## Authentication & Identity

**Auth Provider:**
- Sa-Token (Custom implementation)
  - SDK: `cn.dev33:sa-token-spring-boot3-starter:1.44.0`
  - Config: `iot-server/src/main/java/com/github/dingdaoyi/config/satoken/SaTokenConfigure.java`
  - Token header: `X-TOKEN`
  - Token style: UUID
  - Timeout: 30 days (2592000 seconds)
  - Skip URLs: `/user/login`, `/dict/**`, `/driver/http/**`

**Device Authentication:**
- Device secret authentication
  - Config: `sample.iot.enable-device-secret: true`
  - Handled by MQTT auth handler

**Frontend Auth:**
- Token stored in localStorage (`VEA-TOKEN`)
- Token attached to requests via Axios interceptor
- Store: `iot-web/src/store/modules/account.js`

## Messaging & Protocols

**MQTT Broker:**
- Mica MQTT Server 2.5.11 (embedded)
  - SDK: `org.dromara.mica-mqtt:mica-mqtt-server-spring-boot-starter`
  - Auth handler: `iot-server/src/main/java/com/github/dingdaoyi/driver/mqtt/MqttServerAuthHandler.java`
  - Connection listener: `iot-server/src/main/java/com/github/dingdaoyi/driver/mqtt/MqttConnectStatusListener.java`
  - Ports:
    - MQTT TCP: 1883
    - MQTT WebSocket: 8083
    - MQTT HTTP API: 18083
  - Auth: username/password (`mica/mica`)
  - Topic prefix: `simple/iot`

## Notification Services

**Email:**
- Spring Boot Mail
  - Config: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/EmailNotificationService.java`
  - Email config service: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/EmailConfigServiceImpl.java`
  - Controller: `iot-server/src/main/java/com/github/dingdaoyi/controller/system/EmailConfigController.java`

**SMS:**
- SMS4J integration
  - Service: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/SmsNotificationService.java`

## Monitoring & Observability

**Health Checks:**
- Spring Boot Actuator
  - Endpoint: `/iot/actuator/health`
  - Config: `management.endpoints.web.exposure.include: health`
  - Used by Docker healthcheck

**Logging:**
- SLF4J with Logback (Spring Boot default)
  - Level: `info` (root)
  - Lombok `@Slf4j` annotation used throughout

**API Documentation:**
- Knife4j (OpenAPI 3)
  - Endpoint: `/iot/doc.html`
  - Config: `knife4j.enable: true`, `knife4j.setting.language: zh_cn`

## CI/CD & Deployment

**Hosting:**
- Docker Compose (recommended)
  - Config: `docker-compose.yml`
  - Services: postgres, rustfs, iot-server, iot-web
  - Network: bridge (`iot-network`)

**CI Pipeline:**
- GitHub Actions
  - Workflow: `.github/workflows/maven.yml`
  - Trigger: push/PR to main branch
  - JDK: Temurin 25
  - Steps: checkout, setup JDK, Maven build

**Deployment Script:**
- `deploy.sh` - One-click deployment script

## Environment Configuration

**Required env vars:**
```
# Database
POSTGRES_HOST, POSTGRES_PORT, POSTGRES_USERNAME, POSTGRES_PASSWORD, DATABASE

# InfluxDB
INFLUXDB_URL, INFLUXDB_TOKEN, INFLUXDB_DATABASE

# S3 Storage
S3_ACCESS_KEY_ID, S3_SECRET_ACCESS_KEY, S3_BUCKET_NAME, S3_REGION, S3_ENDPOINT

# Service Ports
SERVER_PORT (default: 5010)
WEB_PORT (default: 80)
MQTT_PORT (default: 1883)
MQTT_WS_PORT (default: 8083)
MQTT_HTTP_PORT (default: 18083)
```

**Secrets location:**
- `.env` file (local development, git-ignored)
- Environment variables (Docker/production)

## Webhooks & Callbacks

**Incoming:**
- MQTT device messages (topic: `simple/iot/#`)
- HTTP driver callbacks: `/driver/http/**` (auth skipped)
- Device service invocation: `/service/{deviceKey}/{identifier}`

**Outgoing:**
- Email notifications (via SMTP)
- SMS notifications (via SMS4J providers)

## Script Engines (Dynamic Protocols)

**GraalVM JavaScript:**
- SDK: `org.graalvm.polyglot:polyglot:24.1.1`, `org.graalvm.polyglot:js:24.1.1`
- Implementation: `iot-server/src/main/java/com/github/dingdaoyi/iot/proto/script/ScriptProtocolDecoder.java`
- Used for dynamic IoT protocol parsing/encoding

**Groovy:**
- SDK: `org.apache.groovy:groovy:4.0.24`, `org.apache.groovy:groovy-jsr223:4.0.24`
- Partial support (falls back to JavaScript)

**Jython (Python):**
- SDK: `org.python:jython-standalone:2.7.4`
- Declared but not actively used

---

*Integration audit: 2026-03-11*
