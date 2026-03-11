# Testing Patterns

**Analysis Date:** 2026-03-11

## Test Framework

### Backend (Java)

**Runner:**
- JUnit 5 (Jupiter)
- Spring Boot Test (implicit via dependencies)

**Test Location:**
```
iot-server/src/test/java/com/github/dingdaoyi/
├── FunCoverTest.java
└── SqlParseTest.java
```

**Run Commands:**
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=FunCoverTest

# Run with verbose output
mvn test -Dtest=FunCoverTest -X
```

### Frontend (JavaScript/Vue)

**Status:** Not detected - No test framework configured

**Observations:**
- No `vitest.config.*` or `jest.config.*` in project root
- No `*.test.js` or `*.spec.js` files in `iot-web/src/`
- Test dependencies not present in `package.json`

---

## Test File Organization

### Backend

**Location:** `iot-server/src/test/java/com/github/dingdaoyi/`

**Naming:**
- Test classes: `XxxTest.java` (e.g., `FunCoverTest.java`, `SqlParseTest.java`)

**Structure:**
```
iot-server/src/test/java/
└── com/github/dingdaoyi/
    ├── FunCoverTest.java      # Rule function tests
    └── SqlParseTest.java      # SQL/SpEL parsing tests
```

---

## Test Structure

### Backend Test Pattern

**Suite Organization:**
```java
package com.github.dingdaoyi;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FunCoverTest {
    private static FunCover funCover;

    @BeforeAll
    public static void init() {
        funCover = new FunCover();
    }

    @Test
    void testMath() {
        Object parse = funCover.parse(10, "#value-2*3");
        assertEquals(4, parse);
    }

    @Test
    void testEql() {
        Object parse = funCover.parse("yanbing", "#value=='yanbing'");
        assertEquals(true, parse);
    }
}
```

**Patterns observed:**
- Static initialization with `@BeforeAll`
- Direct assertions with `assertEquals`
- No mocking framework detected
- Tests are unit-level, isolated tests

**Test method naming:**
- `testXxx` pattern (e.g., `testMath`, `testEql`, `testQuery`)

---

## Mocking

### Backend

**Framework:** None detected

**Current approach:** Direct instantiation without mocking
```java
// Example: Direct object creation
@BeforeAll
public static void init() {
    funCover = new FunCover();
}
```

**Recommendation:** Consider adding Mockito for service layer testing

### Frontend

**Status:** No test infrastructure

---

## Fixtures and Factories

### Backend

**Test Data:**
```java
// Inline data creation in tests
List<DeviceData> dataList = List.of(
    new DeviceData("pressure", DataTypeEnum.INT, 30),
    new DeviceData("pressure", DataTypeEnum.INT, 32),
    new DeviceData("temperature", DataTypeEnum.INT, 25)
);
```

**Location:** No dedicated fixture directory - data created inline in tests

---

## Coverage

### Backend

**Requirements:** None enforced

**Coverage tool:** Not configured

**Recommendation:** Add JaCoCo plugin for coverage reporting
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
</plugin>
```

### Frontend

**Requirements:** None enforced

**Coverage tool:** Not configured

---

## Test Types

### Unit Tests

**Backend:**
- Location: `iot-server/src/test/java/`
- Scope: Individual class/function testing
- Approach: Direct instantiation, no Spring context

**Example patterns:**
```java
// Rule expression testing
@Test
void testMath() {
    Object parse = funCover.parse(10, "#value-2*3");
    assertEquals(4, parse);
}

// SpEL expression testing
@Test
void testParseNumber() {
    ExpressionParser parser = new SpelExpressionParser();
    StandardEvaluationContext context = new StandardEvaluationContext();
    context.setVariable("x", 22.0);
    double result = (double) parser.parseExpression("(#x - 4) / (20 - 4) * 10").getValue(context);
    // Verify result
}
```

### Integration Tests

**Backend:** Not detected

**Recommendation:** Add `@SpringBootTest` integration tests for:
- Controller endpoints
- Service layer with database
- MQTT message handling

### E2E Tests

**Status:** Not used

---

## Common Patterns

### Async Testing

**Not observed in current tests**

### Error Testing

**Not observed in current tests**

**Recommendation:**
```java
@Test
void testExceptionHandling() {
    Exception exception = assertThrows(BusinessException.class, () -> {
        service.methodThatThrows();
    });
    assertEquals("Expected message", exception.getMessage());
}
```

### Parameterized Testing

**Not observed**

**Recommendation:**
```java
@ParameterizedTest
@ValueSource(strings = {"value1", "value2", "value3"})
void testMultipleValues(String input) {
    // Test logic
}
```

---

## Test Coverage Gaps

### Backend

**Untested areas:**
- Controller layer (`com.github.dingdaoyi.controller.*`)
- Service layer (`com.github.dingdaoyi.service.*`)
- Mapper/DAO layer (`com.github.dingdaoyi.mapper.*`)
- Entity validation
- MQTT driver implementations
- Protocol handling

**Priority: High**

**Risk:** Business logic changes could break without detection

### Frontend

**Untested areas:**
- All Vue components
- Composables (`useTable`, `useForm`, `useTheme`)
- API functions
- Store modules

**Priority: Medium**

**Risk:** UI regressions may go undetected

---

## Recommendations

### Backend

1. **Add Mockito dependency:**
```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

2. **Create integration tests:**
```java
@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetDevice() throws Exception {
        mockMvc.perform(get("/device/1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.code").value(200));
    }
}
```

3. **Add test profiles:**
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
```

### Frontend

1. **Add Vitest:**
```bash
pnpm add -D vitest @vue/test-utils jsdom
```

2. **Create vitest.config.js:**
```javascript
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
  },
})
```

3. **Add test script to package.json:**
```json
{
  "scripts": {
    "test": "vitest",
    "test:coverage": "vitest --coverage"
  }
}
```

4. **Example composable test:**
```javascript
// composables/__tests__/useTable.test.js
import { useTable } from '../useTable'
import { describe, it, expect, vi } from 'vitest'

describe('useTable', () => {
  it('should initialize with default values', () => {
    const { params, tableData, total } = useTable({})
    expect(params.page).toBe(1)
    expect(params.size).toBe(20)
    expect(tableData.value).toEqual([])
  })
})
```

---

*Testing analysis: 2026-03-11*
