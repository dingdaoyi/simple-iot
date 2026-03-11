# Architecture

**Analysis Date:** 2026-03-11

## Pattern Overview

**Overall:** Multi-module Monolithic with Layered Architecture

**Key Characteristics:**
- Layered architecture: Controller -> Service -> Mapper (MyBatis-Plus)
- Protocol-driven IoT data processing pipeline
- Plugin-based driver system for multiple transport protocols (TCP, MQTT, HTTP)
- Rule chain engine for event processing
- Frontend-backend separation (Vue 3 + Spring Boot)

## Layers

### Backend Layers

**Controller Layer:**
- Purpose: HTTP request handling, input validation, response formatting
- Location: `iot-server/src/main/java/com/github/dingdaoyi/controller/`
- Contains: REST endpoints with `@RestController`, `@RequestMapping`
- Depends on: Service layer, Query/VO models
- Used by: Frontend via HTTP API

**Service Layer:**
- Purpose: Business logic, transaction management, data transformation
- Location: `iot-server/src/main/java/com/github/dingdaoyi/service/`
- Contains: Service interfaces extending `IService<Entity>`, implementations in `impl/`
- Depends on: Mapper layer, Entity, external integrations
- Used by: Controller layer

**Mapper Layer:**
- Purpose: Database access via MyBatis-Plus
- Location: `iot-server/src/main/java/com/github/dingdaoyi/mapper/`
- Contains: Interfaces extending `BaseMapper<Entity>`, XML mappings in `resources/mapper/`
- Depends on: Entity classes
- Used by: Service layer

**Model Layer:**
- Purpose: Data transfer objects, query parameters, view objects
- Location: `iot-server/src/main/java/com/github/dingdaoyi/model/`
- Contains: `query/`, `vo/`, `DTO/`, `PageQuery.java`, `ToEntity.java`
- Depends on: Entity classes, validation annotations
- Used by: Controller, Service layers

**Entity Layer:**
- Purpose: Database table mappings
- Location: `iot-server/src/main/java/com/github/dingdaoyi/entity/`
- Contains: JPA-annotated POJOs extending `BaseEntity`
- Depends on: MyBatis-Plus annotations, core BaseEntity
- Used by: Mapper, Service layers

### Frontend Layers

**Views Layer:**
- Purpose: Page components for routes
- Location: `iot-web/src/views/`
- Contains: Feature-based page components (device/, product/, protocol/, etc.)
- Depends on: Composables, Components, API layer
- Used by: Router

**Composables Layer:**
- Purpose: Reusable composition functions for Vue 3
- Location: `iot-web/src/composables/`
- Contains: `useTable.js`, `useForm.js`, `useTheme.js`, `useSidebar.js`
- Depends on: Vue 3 Composition API, Element Plus
- Used by: Views, Components

**Components Layer:**
- Purpose: Reusable UI components
- Location: `iot-web/src/components/`
- Contains: `IotTable.vue`, `Breadcrumb.vue`, `IconInput.vue`, `selectTree/`
- Depends on: Element Plus
- Used by: Views, Layout

**API Layer:**
- Purpose: HTTP request definitions
- Location: `iot-web/src/api/`
- Contains: `index.js` (main API), `dashboard.js`, `dict.js`, `driver.js`, `email.js`, `sms.js`
- Depends on: Axios (via `@/utils/request`)
- Used by: Views, Composables

## Data Flow

**Device Data Uplink Flow:**

1. Device connects via TCP/MQTT/HTTP driver (`driver/tcp/`, `driver/mqtt/`, `driver/http/`)
2. `DeviceTransportManager` receives raw data
3. `IotDataProcessor.messageUp()` routes to protocol decoder
4. `ProtocolFactory.getDecoder()` retrieves appropriate `ProtocolDecoder`
5. `ProtocolDecoder.decode()` parses raw bytes into `DecodeResult`
6. `DataProcessor` implementations process result (e.g., `InfluxDataProcessor` stores time-series data)
7. `RuleDataProcessor` triggers rule chain if applicable

**Device Command Downlink Flow:**

1. Frontend calls `/service/{deviceKey}/{identifier}` API
2. `ModelServiceController` receives request
3. `IotCommandProcessor.messageDown()` processes command
4. `ProtocolDecoder.encode()` creates binary message
5. `DeviceChannelManager.sendMessage()` sends to device via appropriate transport

**Rule Chain Execution Flow:**

1. Data event triggers `RuleChainDataProcessor`
2. `RuleChainEngine.execute()` loads rule chain configuration
3. Input nodes are identified and executed first
4. Each `RuleNodeExecutor` processes and routes to next node via connections
5. Results flow through: Filter -> Transform -> Action nodes

**State Management:**
- Frontend: Pinia stores in `iot-web/src/store/modules/`
- Backend: Caffeine cache for frequently accessed data
- Device state: PostgreSQL (device online status, metadata)

## Key Abstractions

**DeviceTransport Interface:**
- Purpose: Abstract transport protocol lifecycle
- Examples: `iot-server/src/main/java/com/github/dingdaoyi/driver/tcp/`, `driver/mqtt/`, `driver/http/`
- Pattern: Strategy pattern - each transport implements start(), stop(), isRunning(), getType(), getName()
- Registration: `DeviceTransportManager` auto-discovers and manages all implementations

**ProtocolDecoder Interface:**
- Purpose: Protocol-agnostic message parsing and encoding
- Examples: `iot-common/iot-protocol-core/src/main/java/com/github/dingdaoyi/proto/inter/ProtocolDecoder.java`
- Pattern: Factory pattern via `ProtocolFactory`
- Methods: `decode()` for uplink, `encode()` for downlink, `responseError()` for error handling

**BaseEntity Abstract Class:**
- Purpose: Common entity fields (createTime, updateTime)
- Examples: All entities in `iot-server/src/main/java/com/github/dingdaoyi/entity/`
- Pattern: Template Method - auto-fill via `DateTimeMetaObjectHandler`

**ToEntity Interface:**
- Purpose: Convert Query DTOs to Entity objects
- Examples: `*AddQuery`, `*UpdateQuery` classes in `model/query/`
- Pattern: Converter pattern - single method `toEntity()`

**BaseResult/PageResult:**
- Purpose: Standardized API response format
- Examples: `iot-common/iot-core/src/main/java/com/github/dingdaoyi/core/base/`
- Pattern: Result wrapper pattern with success/fail factory methods

**useTable/useForm Composables:**
- Purpose: Reusable table and form logic for CRUD pages
- Examples: `iot-web/src/composables/useTable.js`, `useForm.js`
- Pattern: Composition API pattern - encapsulates state and methods

## Entry Points

**Backend Application Entry:**
- Location: `iot-server/src/main/java/com/github/dingdaoyi/ServerApplication.java`
- Triggers: Spring Boot application start
- Responsibilities: `@SpringBootApplication`, `@MapperScan`, `@EnableCaching`

**Frontend Application Entry:**
- Location: `iot-web/src/main.js`
- Triggers: Vite dev server / build
- Responsibilities: Vue app creation, router setup, Pinia store, Element Plus registration

**REST API Entry:**
- Location: `iot-server/src/main/java/com/github/dingdaoyi/controller/iot/*.java`
- Triggers: HTTP requests from frontend
- Responsibilities: Request validation, response formatting, delegating to services

**Driver Startup Entry:**
- Location: `iot-server/src/main/java/com/github/dingdaoyi/driver/DeviceTransportManager.java`
- Triggers: `ApplicationReadyEvent` on Spring Boot startup
- Responsibilities: Discovers and starts all `DeviceTransport` beans

**Protocol Initialization Entry:**
- Location: `iot-server/src/main/java/com/github/dingdaoyi/iot/proto/ProtocolFactory.java`
- Triggers: `ApplicationReadyEvent` on Spring Boot startup
- Responsibilities: Loads enabled protocols, initializes `ProtocolDecoder` instances

## Error Handling

**Strategy:** Global exception handler with standardized error responses

**Patterns:**
- `GlobalExceptionHandler` (`config/base/GlobalExceptionHandler.java`) catches all exceptions
- `BusinessException` for domain-specific errors with `ResultCode`
- `ProtocolException` for protocol parsing errors
- Controller returns `BaseResult.fail(code, message)` for expected errors
- Service layer throws exceptions, controller handles transformation

**Error Response Format:**
```java
{
  "code": 400,
  "msg": "Error message",
  "data": null,
  "success": false
}
```

## Cross-Cutting Concerns

**Logging:** SLF4J with Lombok `@Slf4j` annotation

**Validation:** Jakarta Validation (`@Valid`, `@NotBlank`, `@NotNull`)

**Authentication:** Sa-Token framework (`config/satoken/`)

**Caching:** Caffeine cache (`@EnableCaching`), configured in `CacheService`

**Time-series Storage:** InfluxDB 3 for device telemetry (`iot/influx/InfluxDataProcessor.java`)

**File Storage:** AWS S3 SDK (`config/base/FileServiceConfig.java`)

**Async Processing:** `ExecutorService` bean for concurrent data processing

**API Documentation:** Knife4j/OpenAPI 3 (`@Tag`, `@Operation`, `@Schema` annotations)

---

*Architecture analysis: 2026-03-11*
