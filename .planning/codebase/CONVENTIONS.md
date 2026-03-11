# Coding Conventions

**Analysis Date:** 2026-03-11

## Frontend Conventions (Vue 3)

### File Naming

**Vue Components:**
- Page components: `kebab-case.vue` (e.g., `edit-dia.vue`, `index.vue`)
- Widget/child components: `kebab-case.vue` in `widget/` subdirectory
- Composables: `camelCase.js` (e.g., `useTable.js`, `useForm.js`)

**JavaScript Files:**
- API files: `camelCase.js` (e.g., `index.js`, `sms.js`)
- Utility files: `snake_case.js` (e.g., `date_utils.js`)
- Store modules: `camelCase.js` (e.g., `account.js`, `theme.js`)

### Vue Component Structure

**Script setup pattern:**
```vue
<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { IconName } from '@element-plus/icons-vue'
import { apiFunction } from '@/api'
import { useTable, useForm } from '@/composables'

// Props definition
const props = defineProps(['datas', 'modelValue', 'otherProp'])

// Emits definition
const emits = defineEmits(['update', 'update:modelValue'])

// Reactive state
const loading = ref(false)
const dataList = ref([])

// Composables usage
const { params, tableData, total, onSearch, onDelete } = useTable({
  deleteApi: xxxDeleteApi,
  fetchApi: xxxPageApi,
  diaName: 'xxx',
})

// Methods (use camelCase)
function handleSubmit() {
  // ...
}

// Lifecycle
onMounted(() => {
  loadData()
})
</script>

<template>
  <!-- Template content -->
</template>

<style scoped lang="scss">
/* Styles */
</style>
```

### Naming Patterns

**Variables:**
- Use `camelCase` for all variables and functions
- Use `UPPER_SNAKE_CASE` for constants

**Functions:**
```javascript
// Event handlers: prefix with 'on' or 'handle'
function onSearch() { }
function handleSubmit() { }
function handleUpload(options) { }

// Data fetching: prefix with 'load' or 'fetch'
function loadData() { }
async function fetchData() { }

// Computed: descriptive nouns
const products = computed(() => tableData.value || [])
const isApiMode = computed(() => !!props.api)
```

**Props/Emits:**
```javascript
// Props
defineProps(['datas', 'modelValue', 'productTypeList'])

// Emits
defineEmits(['update', 'update:modelValue'])
```

### Import Organization

**Order:**
1. Vue APIs (`ref`, `watch`, `computed`, etc.)
2. Vue Router (`useRouter`, `useRoute`)
3. Element Plus components and icons
4. Local composables (`useTable`, `useForm`)
5. API functions
6. Local components

**Example:**
```javascript
import { computed, onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Box, Delete, Edit, Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTable } from '@/composables/useTable.js'
import { useForm } from '@/composables/useForm.js'
import { productPageApi, productDeleteApi } from '@/api/index.js'
import IotTable from '@/components/IotTable.vue'
import EditDia from '@/views/product/widget/editDia.vue'
```

**Path Aliases:**
- `@/` maps to `iot-web/src/`
- Use `@/api`, `@/components`, `@/composables`, `@/store`

### Code Style

**Linting:**
- ESLint with `@antfu/eslint-config`
- Config: `iot-web/eslint.config.js`
- Run: `pnpm lint` or `pnpm lint:fix`

**Key rules from antfu config:**
- Single quotes for strings
- No semicolons
- Trailing commas in multi-line

**Vue-specific rules:**
```javascript
// vue/no-mutating-props: error with shallowOnly: true
'vue/no-mutating-props': ['error', { shallowOnly: true }]
```

### Error Handling

**Pattern:**
```javascript
// In composables
try {
  const res = await fetchApi(params)
  if (res && res.data) {
    tableData.value = res.data.records || res.data
  }
}
catch (error) {
  console.error('获取数据失败:', error)
  ElMessage.error('获取数据失败')
}
finally {
  loading.value = false
}

// In form submissions
async function onSubmit() {
  const valid = await editRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    await api(form.value)
    ElMessage.success('操作成功')
    emits('update')
    emits('update:modelValue', false)
  }
  catch (err) {
    ElMessage.error(err?.message || '操作失败')
  }
}
```

### Logging

**Framework:** console (native)

**Patterns:**
```javascript
// Development warnings
console.warn('useTable: 请传入 fetchApi 函数')

// Error logging
console.error('获取数据失败:', error)

// Never log sensitive data
```

### Comments

**JSDoc style for functions:**
```javascript
/**
 * 图片上传处理
 * @param {Object} options - Upload options
 * @param {File} options.file - The file to upload
 */
async function handleUpload(options) { }
```

**Inline comments:**
```javascript
// 通过添加时间戳强制触发 params 变化
params._t = Date.now()
```

### Function Design

**Size:** Keep functions focused, typically under 30 lines

**Parameters:** Use options object pattern for multiple parameters
```javascript
export function useTable(options = {}) {
  const {
    deleteApi = null,
    fetchApi = null,
    defParams = {},
    pageSize = 20,
    diaName = '项',
  } = options
  // ...
}
```

**Return Values:** Return object with named exports
```javascript
return {
  // Data
  params,
  tableData,
  total,
  loading,
  // Methods
  onSearch,
  onDelete,
  updatePage,
}
```

### Module Design

**Exports:** Named exports preferred
```javascript
// Composables
export function useTable(options = {}) { }
export function useForm(options = {}) { }

// API functions
export function productPageApi(data) { }
export function productAddApi(data) { }
```

**Barrel Files:** Used in API directory
```javascript
// api/index.js exports all API functions
import { productPageApi, devicePageApi } from '@/api/index.js'
```

---

## Backend Conventions (Java)

### Naming Patterns

**Classes:**
- Entity: `PascalCase` (e.g., `Device`, `ProductType`)
- Controller: `XxxController` (e.g., `DeviceController`)
- Service: `XxxService` (e.g., `DeviceService`)
- ServiceImpl: `XxxServiceImpl` (e.g., `DeviceServiceImpl`)
- Mapper: `XxxMapper` (e.g., `DeviceMapper`)
- Query: `XxxPageQuery`, `XxxAddQuery`, `XxxUpdateQuery`
- VO: `XxxVo`, `XxxPageVo`
- DTO: `XxxDTO`
- Enum: `PascalCase` (e.g., `ResultCode`, `ProtoType`)

**Database Tables:**
- Prefix: `tb_` (e.g., `tb_device`, `tb_product_type`)
- Snake_case column names

**Variables/Methods:**
- camelCase for all variables and methods
- UPPER_SNAKE_CASE for constants

### Package Structure

```
com.github.dingdaoyi/
├── config/          # Configuration classes
├── controller/      # REST controllers
│   └── iot/        # IoT-related controllers
├── driver/          # Driver implementations (MQTT, TCP, HTTP)
├── entity/          # JPA entities
│   └── enu/        # Enum classes
├── iot/             # IoT core implementations
├── mapper/          # MyBatis mappers
├── model/           # Data models
│   ├── query/      # Request parameters
│   ├── vo/         # View objects
│   └── DTO/        # Data transfer objects
├── proto/           # Protocol implementations
└── service/         # Business logic
    └── impl/       # Service implementations
```

### Controller Pattern

```java
@AllArgsConstructor
@RestController
@RequestMapping("/device")
@Tag(name = "设备管理")
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping("{id}")
    @Operation(summary = "设备详情")
    public BaseResult<DeviceVo> details(@PathVariable Integer id) {
        Optional<DeviceVo> optional = deviceService.details(id);
        return optional.map(BaseResult::success)
                       .orElse(BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "设备不存在"));
    }

    @PostMapping
    @Operation(summary = "添加设备")
    public BaseResult<Boolean> save(@RequestBody @Valid DeviceAddQuery query) {
        return BaseResult.success(deviceService.save(query.toEntity()));
    }

    @PostMapping("page")
    @Operation(summary = "设备分页列表")
    public PageResult<DevicePageVo> page(@RequestBody @Valid DevicePageQuery query) {
        return deviceService.pageByQuery(query);
    }
}
```

### Service Pattern

**Interface:**
```java
public interface DeviceService extends IService<Device> {
    Optional<DeviceVo> details(Integer id);
    PageResult<DevicePageVo> pageByQuery(@Valid DevicePageQuery query);
    void updateOlinStatus(String deviceKey, boolean online);
}
```

**Implementation:**
```java
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Resource
    private ProductService productService;

    @Override
    public Optional<DeviceVo> details(Integer id) {
        Device device = baseMapper.selectById(id);
        if (device == null) {
            return Optional.empty();
        }
        return getDeviceVo(device);
    }

    @Override
    public PageResult<DevicePageVo> pageByQuery(DevicePageQuery query) {
        Page<DevicePageVo> page = PageHelper.page(query);
        Page<DevicePageVo> result = baseMapper.pageByQuery(page, query);
        return PageResult.of(result.getRecords(), result.getTotal(), result.getCurrent(), result.getSize());
    }
}
```

### Entity Pattern

```java
@Schema
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "tb_device")
public class Device extends BaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "设备编号")
    private Integer id;

    @TableField(value = "product_id")
    @Schema(description = "产品id")
    private Integer productId;

    @TableField(value = "online")
    @Schema(description = "在线状态")
    private Boolean online;
}
```

### Query Pattern

**PageQuery (extends base):**
```java
@EqualsAndHashCode(callSuper = true)
@Data
public class DevicePageQuery extends PageQuery {
    @Schema(description = "设备编号")
    private String deviceKey;

    @Schema(description = "产品类型id")
    private Integer productTypeId;
}
```

**AddQuery (implements ToEntity):**
```java
@Data
public class ProductTypeAddQuery implements ToEntity<ProductType> {
    @Schema(description = "产品类型名称")
    @NotBlank(message = "产品类型不能为空")
    private String name;

    @Override
    public ProductType toEntity() {
        ProductType productType = new ProductType();
        productType.setName(name);
        return productType;
    }
}
```

### VO Pattern

```java
@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceVo extends Device {
    @Schema(description = "产品")
    private Product product;

    public static DeviceVo build(Device device) {
        DeviceVo deviceVo = new DeviceVo();
        BeanUtil.copyProperties(device, deviceVo);
        return deviceVo;
    }
}
```

### Mapper Pattern

```java
public interface DeviceMapper extends BaseMapper<Device> {
    Optional<DeviceDTO> findByDeviceKey(@Param("deviceKey") String deviceKey);

    Page<DevicePageVo> pageByQuery(Page<DevicePageVo> page, @Param("query") DevicePageQuery query);

    String findDeviceKeyById(@Param("id") Integer id);
}
```

### Error Handling

**Global exception handler:** `GlobalExceptionHandler.java`

**Patterns:**
```java
// Business validation
if (!productService.existsById(query.getProductId())) {
    return BaseResult.fail(ResultCode.BAD_REQUEST.getCode(), "产品不存在");
}

// Optional handling
Optional<DeviceVo> optional = deviceService.details(id);
return optional.map(BaseResult::success)
               .orElse(BaseResult.fail(ResultCode.NOT_FOUND.getCode(), "设备不存在"));
```

### Dependency Injection

**Controller:** Use `@AllArgsConstructor` with `final` fields
```java
@AllArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;
}
```

**Service:** Use `@Resource` for cross-service dependencies
```java
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> {
    @Resource
    private ProductService productService;
}
```

### Annotations Summary

**Spring:**
- `@RestController`, `@Service`, `@RequestMapping`
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- `@RequestBody`, `@PathVariable`, `@RequestParam`
- `@AllArgsConstructor`, `@Resource`

**MyBatis-Plus:**
- `@TableName("tb_xxx")`
- `@TableId(type = IdType.AUTO)`
- `@TableField("column_name")`

**Validation (Jakarta):**
- `@Valid`, `@NotBlank`, `@NotNull`

**Documentation (Swagger):**
- `@Tag(name = "xxx管理")`
- `@Operation(summary = "xxx")`
- `@Schema(description = "xxx")`

---

*Convention analysis: 2026-03-11*
