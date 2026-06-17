# OpenAPI

Simple IoT exposes an OpenAPI 3.1 document and a hosted Swagger UI / Knife4j UI from the backend service.

## Local endpoints

When the backend runs with the default context path (`/iot`):

| Resource | URL |
|---|---|
| OpenAPI JSON | `http://localhost:5010/iot/v3/api-docs` |
| Swagger UI | `http://localhost:5010/iot/swagger-ui.html` |
| Knife4j UI | `http://localhost:5010/iot/doc.html` |

The documentation endpoints are intentionally excluded from the Sa-Token login interceptor so developers can inspect the API before signing in.

## What is included

The generated spec scans controller classes under:

```text
com.github.dingdaoyi.controller
```

This includes device, product, rule-chain, alarm, dictionary, user, notification, and file endpoints annotated with Swagger/OpenAPI metadata.

## Notes

- The spec version is OpenAPI `3.1.0`.
- Runtime business endpoints still require authentication unless they are explicitly listed in the Sa-Token skip list.
- The API document is generated at runtime from Spring MVC controllers; if you add a controller, annotate it with `@Tag` and each operation with `@Operation`.
