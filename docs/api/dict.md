# Dictionary Endpoints

Dictionary endpoints return enum metadata used by the admin UI for dropdowns and labels. They are public (no auth) and cheap.

## `GET /iot/dict/driver-type`

Returns the list of supported protocol driver types.

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

Returns supported driver connection categories.

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

## Frontend wrappers

`iot-web/src/api/dict.js` exports thin axios wrappers:

```js
import request from '@/utils/request'

export function getDriverTypeEnum() {
  return request({ url: '/dict/driver-type', method: 'get' })
}

export function getConnectionTypeEnum() {
  return request({ url: '/dict/connection-type', method: 'get' })
}
```

Used in `views/driver/index.vue` to populate the protocol-type and connection-type filters on the driver list page.

## History

Both endpoints (and their JS wrappers) were added in [`0fa7f53`](https://github.com/dingdaoyi/simple-iot/commit/0fa7f53) — the driver page used to fail with `getDriverTypeEnum is not exported by src/api/dict.js`. Pull `main` if you're on an earlier checkout.
