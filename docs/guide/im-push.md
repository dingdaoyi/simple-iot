# IM Push Configuration (DingTalk / WeCom / Feishu)

Simple IoT supports pushing alarm notifications to DingTalk, WeCom (WeChat Work), and Feishu group robots.

## Data Model

The `tb_im_push_config` table stores webhook configs per platform:

| Field | Description |
|-------|-------------|
| `name` | Config name |
| `platform` | `DINGTALK` / `WECOM` / `FEISHU` |
| `webhook_url` | Group robot webhook URL |
| `secret` | Signing secret (DingTalk only) |
| `is_enabled` | Enable toggle |

## API

### List

```
GET /im-push-config/list
```

### Create

```
POST /im-push-config
Content-Type: application/json

{
  "name": "Ops Alarm Group",
  "platform": "DINGTALK",
  "webhookUrl": "https://oapi.dingtalk.com/robot/send?access_token=xxx",
  "secret": "SECxxx",
  "isEnabled": true
}
```

### Update / Delete

```
PUT /im-push-config
DELETE /im-push-config/{id}
```

## Platform Setup

### DingTalk

1. Group Settings -> Smart Assistant -> Add Robot -> Custom
2. Security: select "Sign", copy the secret
3. Copy webhook URL
4. Fill both URL and secret in Simple IoT

### WeCom (WeChat Work)

1. Group chat -> right-click -> Add Group Robot
2. Copy webhook URL
3. Fill URL only (no secret needed)

### Feishu

1. Group Settings -> Group Robots -> Add -> Custom Robot
2. Copy webhook URL
3. Fill URL only (no secret needed)

## Message Format

| Platform | Format |
|----------|--------|
| DingTalk | Markdown (`### Title\n\nContent`) |
| WeCom | Markdown (`### Title\n\nContent`) |
| Feishu | Interactive card (header + markdown element) |

## Rule Engine Integration

IM push is triggered via the `OUTPUT_MESSAGE` rule node. When an alarm rule matches, `AlarmCreateNode` creates the alarm, then `MessageOutputNode` dispatches to the configured IM platform.
