# SMS Integration

Simple IoT ships with a pluggable SMS notification subsystem. Multiple providers (Aliyun, Tencent Cloud, Huawei Cloud) can coexist; rules and alarm chains pick a config by id or default.

## Database schema

`sms_config` table:

```sql
CREATE TABLE sms_config (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,           -- config name
    supplier VARCHAR(50) NOT NULL,        -- provider key (alibaba/tencent/huawei)
    access_key VARCHAR(200) NOT NULL,
    secret_key VARCHAR(200) NOT NULL,
    sign_name VARCHAR(100),               -- SMS sign / brand
    template_id VARCHAR(100),
    endpoint VARCHAR(200),
    region VARCHAR(50),
    config_json TEXT,                     -- extra JSON config
    is_default BOOLEAN DEFAULT FALSE,
    status INTEGER DEFAULT 1,             -- 1 = enabled, 2 = disabled
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## REST API

### Config CRUD

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/sms/config` | List configs |
| `POST` | `/sms/config` | Create config |
| `PUT` | `/sms/config` | Update config |
| `DELETE` | `/sms/config/{id}` | Delete config |
| `PUT` | `/sms/config/{id}/default` | Set as default |
| `PUT` | `/sms/config/{id}/status` | Toggle enabled/disabled |

### Send SMS

| Method | Path | Purpose |
|---|---|---|
| `POST` | `/sms/send` | Send a message |

## Frontend

- View: `iot-web/src/views/system/sms/index.vue`
- Provides full CRUD + a "send test SMS" form.

## `application.yml`

```yaml
sms:
  config-type: interface   # config source
  enabled: true            # enable SMS feature
  default-supplier: alibaba
```

## Supported providers

| Provider | Required fields |
|---|---|
| **Aliyun** (`alibaba`) | `accessKey`, `secretKey`, `signName`, `templateId` |
| **Tencent Cloud** (`tencent`) | `accessKey`, `secretKey`, `signName`, `templateId`, `region` |
| **Huawei Cloud** (`huawei`) | `accessKey`, `secretKey`, `signName`, `templateId`, `endpoint` |

## Examples

### Create config

```json
{
  "name": "Aliyun production",
  "supplier": "alibaba",
  "accessKey": "your_access_key",
  "secretKey": "your_secret_key",
  "signName": "MyBrand",
  "templateId": "SMS_123456789"
}
```

### Send

```json
{
  "phone": "13800138000",
  "message": "Your code is 123456",
  "configId": 1
}
```

## Notes

- At least one **enabled default** config must exist or sends will fail.
- Phone number format is validated server-side.
- Encrypt access keys at rest in production deployments.
- The integration layer stubs out an SMS4J-compatible bridge — drop in a real SDK by implementing the `SmsClient` SPI.
