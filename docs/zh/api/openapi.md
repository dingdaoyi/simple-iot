# OpenAPI

Simple IoT 后端会暴露 OpenAPI 3.1 文档，并提供 Swagger UI / Knife4j 在线调试入口。

## 本地地址

后端使用默认 context path（`/iot`）启动时：

| 资源 | 地址 |
|---|---|
| OpenAPI JSON | `http://localhost:5010/iot/v3/api-docs` |
| Swagger UI | `http://localhost:5010/iot/swagger-ui.html` |
| Knife4j UI | `http://localhost:5010/iot/doc.html` |

这些文档入口会从 Sa-Token 登录拦截器中放行，方便开发者在登录前查看 API 结构。

## 覆盖范围

生成的接口文档扫描以下 controller 包：

```text
com.github.dingdaoyi.controller
```

包括设备、产品、规则链、告警、字典、用户、消息通知、文件等已添加 Swagger/OpenAPI 注解的接口。

## 约定

- 规范版本为 OpenAPI `3.1.0`。
- 运行时业务接口仍然需要认证，除非显式加入 Sa-Token 放行列表。
- API 文档由 Spring MVC Controller 运行时生成；新增 Controller 时请补充 `@Tag` 和 `@Operation` 注解。
