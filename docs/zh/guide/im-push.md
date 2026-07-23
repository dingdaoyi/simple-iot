# IM 推送配置（钉钉/企微/飞书）

Simple IoT 支持将告警通知推送到钉钉、企业微信、飞书的群机器人。

## 数据模型

`tb_im_push_config` 表存储各平台的 Webhook 配置：

| 字段 | 说明 |
|------|------|
| `name` | 配置名称 |
| `platform` | 平台类型：`DINGTALK` / `WECOM` / `FEISHU` |
| `webhook_url` | 群机器人 Webhook URL |
| `secret` | 签名密钥（仅钉钉使用） |
| `is_enabled` | 是否启用 |

## API

### 列表

```
GET /im-push-config/list
```

### 新增

```
POST /im-push-config
Content-Type: application/json

{
  "name": "运维告警群",
  "platform": "DINGTALK",
  "webhookUrl": "https://oapi.dingtalk.com/robot/send?access_token=xxx",
  "secret": "SECxxx",
  "isEnabled": true
}
```

### 修改

```
PUT /im-push-config
```

### 删除

```
DELETE /im-push-config/{id}
```

## 前端操作

1. 侧边栏「IM 推送」菜单
2. 点击「新增」，选择平台，填入 Webhook URL
3. 钉钉平台需要填写签名密钥（机器人安全设置中的「加签」密钥）

## 各平台配置指南

### 钉钉

1. 群设置 → 智能群助手 → 添加机器人 → 自定义
2. 安全设置选择「加签」，复制密钥
3. 复制 Webhook URL
4. 在 Simple IoT 中填入 URL 和密钥

### 企业微信

1. 群聊 → 右键 → 添加群机器人
2. 复制 Webhook 地址
3. 在 Simple IoT 中填入 URL（不需要密钥）

### 飞书

1. 群设置 → 群机器人 → 添加机器人 → 自定义机器人
2. 复制 Webhook URL
3. 在 Simple IoT 中填入 URL（不需要密钥）

## 推送格式

| 平台 | 消息格式 |
|------|---------|
| 钉钉 | Markdown（`### 标题\n\n内容`） |
| 企业微信 | Markdown（`### 标题\n\n内容`） |
| 飞书 | 交互卡片（header + markdown element） |

## 与规则引擎联动

IM 推送通过规则引擎的 `OUTPUT_MESSAGE` 节点触发。当告警规则匹配时，`AlarmCreateNode` 创建告警后，`MessageOutputNode` 根据 `NotifyType` 选择对应的推送服务。
