# Codebase Structure

**Analysis Date:** 2026-03-11

## Directory Layout

```
sample-iot/
├── iot-server/                    # Main backend service module
├── iot-common/                    # Shared library modules
│   ├── iot-core/                  # Core base classes and utilities
│   └── iot-protocol-core/         # Protocol interfaces and models
├── iot-driver/                    # Driver implementations module
│   └── system-driver/             # System-level device drivers
├── iot-web/                       # Frontend Vue 3 application
├── doc/                           # Documentation
├── sql/                           # Database scripts
├── scripts/                       # Deployment scripts
├── .planning/                     # GSD planning documents
├── pom.xml                        # Maven parent POM
├── docker-compose.yml             # Docker deployment config
├── deploy.sh                      # Deployment script
└── CLAUDE.md                      # Development guide
```

## Directory Purposes

### Backend (iot-server/)

```
iot-server/src/main/java/com/github/dingdaoyi/
├── config/                        # Spring configuration classes
│   ├── base/                      # General config (MyBatisPlus, ExceptionHandler, FileService)
│   ├── iot/                       # IoT-specific configuration
│   └── satoken/                   # Authentication configuration
├── controller/                    # REST API controllers
│   ├── iot/                       # IoT domain controllers (Device, Product, Protocol, etc.)
│   └── system/                    # System management controllers
├── driver/                        # Transport driver implementations
│   ├── tcp/                       # TCP transport driver
│   ├── mqtt/                      # MQTT transport driver
│   ├── http/                      # HTTP transport driver
│   └── DeviceTransportManager.java # Driver lifecycle manager
├── entity/                        # Database entity classes
│   └── enu/                       # Entity-related enums
├── iot/                           # IoT core processing logic
│   ├── impl/                      # IotDataProcessor implementation
│   ├── influx/                    # InfluxDB time-series storage
│   ├── proto/                     # Protocol initialization
│   └── model/                     # IoT-specific models
├── mapper/                        # MyBatis-Plus mapper interfaces
├── model/                         # Data transfer objects
│   ├── DTO/                       # Data transfer objects
│   ├── query/                     # Request query objects (PageQuery, AddQuery, UpdateQuery)
│   ├── vo/                        # View objects for responses
│   ├── enu/                       # Model-related enums
│   ├── exception/                 # Custom exceptions
│   ├── valid/                     # Custom validators
│   ├── PageQuery.java             # Base pagination query
│   └── ToEntity.java              # Query-to-Entity converter interface
├── rule/                          # Rule chain engine
│   ├── node/                      # Rule node executors
│   ├── RuleChainEngine.java       # Rule chain execution engine
│   ├── RuleContext.java           # Execution context
│   └── RuleNodeExecutor.java      # Node executor interface
├── service/                       # Business logic layer
│   └── impl/                      # Service implementations
└── utils/                         # Utility classes
    ├── PageHelper.java            # Pagination helper
    ├── EnumUtils.java             # Enum utilities
    └── MessageIdGenerator.java    # Message ID generator
```

**iot-server/src/main/resources/**
```
resources/
├── mapper/                        # MyBatis XML mapper files
├── application.yml                # Spring Boot configuration
├── static/                        # Static resources
└── templates/                     # Server-side templates
```

### Shared Libraries (iot-common/)

```
iot-common/iot-core/src/main/java/com/github/dingdaoyi/core/
├── base/                          # Base classes
│   ├── BaseEntity.java            # Base entity with timestamps
│   ├── BaseResult.java            # Unified API response
│   └── PageResult.java            # Paginated response wrapper
├── driver/                        # Driver interfaces
│   └── DeviceTransport.java       # Transport driver interface
├── enums/                         # Core enumerations
│   └── ResultCode.java            # API response codes
├── exception/                     # Core exceptions
├── model/                         # Core models (DeviceBase)
├── service/                       # Core service interfaces (DeviceProvider)
└── utils/                         # Core utilities
```

```
iot-common/iot-protocol-core/src/main/java/com/github/dingdaoyi/proto/
├── inter/                         # Protocol interfaces
│   └── ProtocolDecoder.java       # Protocol decoder interface
├── model/                         # Protocol data models
│   ├── DeviceRequest.java         # Device uplink request
│   ├── DecodeResult.java          # Decoding result
│   ├── EncoderMessage.java        # Downlink message
│   ├── EncoderResult.java         # Encoding result
│   ├── DeviceConnection.java      # Device connection abstraction
│   ├── ProtocolException.java     # Protocol error
│   └── tsl/                       # Thing Specification Language models
└── utils/                         # Protocol utilities
```

### Frontend (iot-web/)

```
iot-web/src/
├── api/                           # API request definitions
│   ├── index.js                   # Main API functions (754 lines)
│   ├── dashboard.js               # Dashboard API
│   ├── dict.js                    # Dictionary API
│   ├── driver.js                  # Driver API
│   ├── email.js                   # Email config API
│   └── sms.js                     # SMS config API
├── assets/                        # Static assets (images, fonts)
├── components/                    # Reusable Vue components
│   ├── IotTable.vue               # Universal table component with pagination
│   ├── Breadcrumb.vue             # Navigation breadcrumb
│   ├── IconInput.vue              # Icon picker input
│   ├── DefaultIcon.vue            # Default icon display
│   └── selectTree/                # Tree selector component
├── composables/                   # Vue 3 composition functions
│   ├── useTable.js                # Table CRUD logic (177 lines)
│   ├── useForm.js                 # Form dialog logic (86 lines)
│   ├── useTheme.js                # Theme switching
│   └── useSidebar.js              # Sidebar collapse state
├── config/                        # Frontend configuration
├── layout/                        # Layout components
│   ├── index.vue                  # Main layout
│   └── widget/                    # Layout parts
│       ├── header.vue             # Top header
│       └── slider.vue             # Side navigation
├── router/                        # Vue Router configuration
│   ├── index.js                   # Router setup
│   └── modules/                   # Route modules
│       └── layoutRout.js          # Main routes with icons
├── store/                         # Pinia state management
│   └── modules/                   # Store modules
│       ├── account.js             # User account state
│       └── theme.js               # Theme state
├── styles/                        # Global styles
│   ├── global.scss                # Design system + global styles
│   ├── element.scss               # Element Plus overrides
│   └── var.scss                   # CSS variables
├── utils/                         # Utility functions
│   └── request.js                 # Axios HTTP client wrapper
├── views/                         # Page components
│   ├── home/                      # Dashboard home
│   ├── device/                    # Device management
│   ├── product/                   # Product management
│   ├── productType/               # Product type management
│   ├── protocol/                  # Protocol management
│   ├── driver/                    # Driver management
│   ├── tslModel/                  # TSL model configuration
│   ├── rule/                      # Rule processing
│   ├── rule-chain/                # Rule chain editor
│   ├── alarm/                     # Alarm management
│   ├── login/                     # Login page
│   └── system/                    # System settings
│       ├── email/                 # Email configuration
│       ├── sms/                   # SMS configuration
│       ├── icon/                  # Icon management
│       └── messageReceive/        # Message notification config
├── App.vue                        # Root Vue component
├── main.js                        # Application entry
└── permission.js                  # Route permission guard
```

## Key File Locations

### Entry Points

**Backend Entry:**
- `iot-server/src/main/java/com/github/dingdaoyi/ServerApplication.java`

**Frontend Entry:**
- `iot-web/src/main.js`
- `iot-web/src/App.vue`

**Route Configuration:**
- `iot-web/src/router/modules/layoutRout.js`

### Configuration

**Backend Config:**
- `iot-server/src/main/resources/application.yml`
- `iot-server/src/main/java/com/github/dingdaoyi/config/base/MyBatisPlusConfig.java`
- `iot-server/src/main/java/com/github/dingdaoyi/config/base/GlobalExceptionHandler.java`

**Frontend Config:**
- `iot-web/package.json`
- `iot-web/vite.config.js`
- `iot-web/src/styles/var.scss` - CSS variables

### Core Logic

**Base Classes:**
- `iot-common/iot-core/src/main/java/com/github/dingdaoyi/core/base/BaseEntity.java`
- `iot-common/iot-core/src/main/java/com/github/dingdaoyi/core/base/BaseResult.java`
- `iot-common/iot-core/src/main/java/com/github/dingdaoyi/core/base/PageResult.java`

**IoT Processing:**
- `iot-server/src/main/java/com/github/dingdaoyi/iot/impl/IotDataProcessorImpl.java`
- `iot-server/src/main/java/com/github/dingdaoyi/iot/proto/ProtocolFactory.java`
- `iot-server/src/main/java/com/github/dingdaoyi/driver/DeviceTransportManager.java`

**Rule Engine:**
- `iot-server/src/main/java/com/github/dingdaoyi/rule/RuleChainEngine.java`
- `iot-server/src/main/java/com/github/dingdaoyi/rule/RuleContext.java`
- `iot-server/src/main/java/com/github/dingdaoyi/rule/RuleNodeExecutor.java`

### Testing

**Backend Tests:**
- `iot-server/src/test/java/com/github/dingdaoyi/`

**Frontend Tests:**
- Not detected (no test directory in iot-web)

## Naming Conventions

### Backend

**Files:**
- Entity: `PascalCase.java` (e.g., `Device.java`, `ProductType.java`)
- Service Interface: `XxxService.java`
- Service Implementation: `XxxServiceImpl.java`
- Controller: `XxxController.java`
- Mapper: `XxxMapper.java`
- Query: `XxxPageQuery.java`, `XxxAddQuery.java`, `XxxUpdateQuery.java`
- VO: `XxxVo.java`, `XxxPageVo.java`
- Enum: `PascalCase.java` in `entity/enu/` or `model/enu/`

**Database:**
- Table names: `tb_` prefix, snake_case (e.g., `tb_device`, `tb_product_type`)

**Packages:**
- All under `com.github.dingdaoyi`

### Frontend

**Files:**
- Vue components: `PascalCase.vue` or `kebab-case.vue`
- JavaScript: `camelCase.js`
- Composables: `use*.js` (e.g., `useTable.js`)

**Directories:**
- Feature-based: `device/`, `product/`, `protocol/`
- Widget sub-components: `widget/` inside view directories

**API Functions:**
- Pattern: `resourceActionApi` (e.g., `devicePageApi`, `productAddApi`, `protocolDeleteApi`)

## Where to Add New Code

### New Feature (Backend)

**Entity:**
- `iot-server/src/main/java/com/github/dingdaoyi/entity/Xxx.java`
- Extend `BaseEntity`, use `@TableName("tb_xxx")`

**Mapper:**
- Interface: `iot-server/src/main/java/com/github/dingdaoyi/mapper/XxxMapper.java`
- XML: `iot-server/src/main/resources/mapper/XxxMapper.xml`

**Service:**
- Interface: `iot-server/src/main/java/com/github/dingdaoyi/service/XxxService.java`
- Implementation: `iot-server/src/main/java/com/github/dingdaoyi/service/impl/XxxServiceImpl.java`

**Controller:**
- `iot-server/src/main/java/com/github/dingdaoyi/controller/iot/XxxController.java`

**Query/VO:**
- `iot-server/src/main/java/com/github/dingdaoyi/model/query/XxxPageQuery.java`
- `iot-server/src/main/java/com/github/dingdaoyi/model/query/XxxAddQuery.java`
- `iot-server/src/main/java/com/github/dingdaoyi/model/vo/XxxVo.java`

### New Feature (Frontend)

**Page:**
- `iot-web/src/views/xxx/index.vue`
- Dialog: `iot-web/src/views/xxx/widget/editDia.vue`

**API:**
- Add functions to `iot-web/src/api/index.js`

**Route:**
- Add entry to `iot-web/src/router/modules/layoutRout.js`

**Component:**
- Shared: `iot-web/src/components/Xxx.vue`
- Page-specific: `iot-web/src/views/xxx/widget/Xxx.vue`

### New Protocol

**Protocol Decoder:**
- Implement `ProtocolDecoder` interface
- Place in `iot-server/src/main/java/com/github/dingdaoyi/iot/proto/impl/` or `defaul/`

**Initialize:**
- Add `ProtocolInitialize` implementation
- Register in `ProtoType` enum

### New Driver

**Transport:**
- Implement `DeviceTransport` interface
- Place in `iot-server/src/main/java/com/github/dingdaoyi/driver/{type}/`

### New Rule Node

**Executor:**
- Implement `RuleNodeExecutor` interface
- Place in `iot-server/src/main/java/com/github/dingdaoyi/rule/node/`
- Register in `RuleNodeType` enum

## Special Directories

**sql/:**
- Purpose: Database schema and migration scripts
- Generated: No
- Committed: Yes

**doc/:**
- Purpose: Project documentation
- Generated: No
- Committed: Yes

**scripts/:**
- Purpose: Deployment and utility scripts
- Generated: No
- Committed: Yes

**.planning/:**
- Purpose: GSD planning documents (this directory)
- Generated: Yes
- Committed: Yes (part of project documentation)

**iot-driver/system-driver/:**
- Purpose: System-level device key parsers (AEP, GB protocols)
- Generated: No
- Committed: Yes

---

*Structure analysis: 2026-03-11*
