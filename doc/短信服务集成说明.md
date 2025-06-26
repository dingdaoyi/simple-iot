# 短信服务集成说明

## 概述
本系统集成了短信服务功能，支持通过接口配置多种短信供应商，包括阿里云、腾讯云、华为云等。

## 数据库表结构

### sms_config 表
```sql
CREATE TABLE sms_config (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,           -- 配置名称
    supplier VARCHAR(50) NOT NULL,        -- 短信供应商
    access_key VARCHAR(200) NOT NULL,     -- 访问密钥
    secret_key VARCHAR(200) NOT NULL,     -- 密钥
    sign_name VARCHAR(100),               -- 短信签名
    template_id VARCHAR(100),             -- 模板ID
    endpoint VARCHAR(200),                -- 接入点
    region VARCHAR(50),                   -- 区域
    config_json TEXT,                     -- 其他配置JSON
    is_default BOOLEAN DEFAULT FALSE,     -- 是否默认配置
    status INTEGER DEFAULT 1,             -- 状态 1启用 2禁用
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 后端接口

### 短信配置管理
- `GET /sms/config` - 获取短信配置列表
- `POST /sms/config` - 添加短信配置
- `PUT /sms/config` - 更新短信配置
- `DELETE /sms/config/{id}` - 删除短信配置
- `PUT /sms/config/{id}/default` - 设置默认配置
- `PUT /sms/config/{id}/status` - 更新配置状态

### 短信发送
- `POST /sms/send` - 发送短信

## 前端页面
- 路径: `/views/system/sms/index.vue`
- 功能: 短信配置的增删改查、发送短信测试

## 配置说明

### application.yml 配置
```yaml
sms:
  config-type: interface  # 配置类型
  enabled: true          # 是否启用短信功能
  default-supplier: alibaba  # 默认供应商
```

### 支持的短信供应商
1. **阿里云 (alibaba)**
   - 需要配置: accessKey, secretKey, signName, templateId
   
2. **腾讯云 (tencent)**
   - 需要配置: accessKey, secretKey, signName, templateId, region
   
3. **华为云 (huawei)**
   - 需要配置: accessKey, secretKey, signName, templateId, endpoint

## 使用示例

### 1. 添加短信配置
```json
{
  "name": "阿里云短信配置",
  "supplier": "alibaba",
  "accessKey": "your_access_key",
  "secretKey": "your_secret_key",
  "signName": "测试签名",
  "templateId": "SMS_123456789"
}
```

### 2. 发送短信
```json
{
  "phone": "13800138000",
  "message": "您的验证码是123456",
  "configId": 1
}
```

## 扩展说明
- 系统支持多个短信配置，可以设置默认配置
- 支持启用/禁用配置
- 集成了通知服务接口，可以通过NotificationService统一调用
- 预留了SMS4J集成接口，可以根据需要集成具体的短信SDK

## 注意事项
1. 确保数据库中至少有一个启用的默认配置
2. 短信发送前会验证手机号格式
3. 密钥等敏感信息建议加密存储
4. 生产环境需要配置真实的短信供应商参数