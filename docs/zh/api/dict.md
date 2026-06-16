# 字典端点

字典接口返回管理后台下拉框 / 标签所需的枚举元数据，免鉴权。

## `GET /iot/dict/driver-type`

返回支持的协议驱动类型。

```http
GET /iot/dict/driver-type
```

**200 OK**

```json
{
  "code": 200,
  "msg": "操作成功",
  "success": true,
  "data": [
    { "code": "MQTT",   "name": "MQTT",   "value": "MQTT",   "ordinal": 0, "desc": "MQTT 驱动" },
    { "code": "TCP",    "name": "TCP",    "value": "TCP",    "ordinal": 1, "desc": "TCP 驱动" },
    { "code": "UDP",    "name": "UDP",    "value": "UDP",    "ordinal": 2, "desc": "UDP 驱动" },
    { "code": "HTTP",   "name": "HTTP",   "value": "HTTP",   "ordinal": 3, "desc": "HTTP 驱动" },
    { "code": "CUSTOM", "name": "CUSTOM", "value": "CUSTOM", "ordinal": 4, "desc": "自定义驱动" }
  ]
}
```

## `GET /iot/dict/connection-type`

返回驱动接入类型。

```http
GET /iot/dict/connection-type
```

**200 OK**

```json
{
  "code": 200,
  "msg": "操作成功",
  "success": true,
  "data": [
    { "code": "DEFAULT", "name": "DEFAULT", "value": "DEFAULT", "ordinal": 0, "desc": "平台内置" },
    { "code": "CUSTOM",  "name": "CUSTOM",  "value": "CUSTOM",  "ordinal": 1, "desc": "自定义上传" }
  ]
}
```

## 前端封装

`iot-web/src/api/dict.js` 暴露了两个 axios 封装：

```js
import request from '@/utils/request'

export function getDriverTypeEnum() {
  return request({ url: '/dict/driver-type', method: 'get' })
}

export function getConnectionTypeEnum() {
  return request({ url: '/dict/connection-type', method: 'get' })
}
```

驱动列表页 `views/driver/index.vue` 用它们填充协议类型 / 接入类型筛选。

## 历史

两个接口与对应前端封装在 [`0fa7f53`](https://github.com/dingdaoyi/simple-iot/commit/0fa7f53) 一起补齐。早期版本驱动页会报 `getDriverTypeEnum is not exported by src/api/dict.js`，拉取最新 `main` 即可解决。
