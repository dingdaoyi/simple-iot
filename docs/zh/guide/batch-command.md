# 批量设备指令

Simple IoT 支持向多台设备批量下发服务指令（如批量重启、批量配置下发）。

## API

### 批量下发指令

```
POST /service/batch/{identifier}
```

**路径参数：**
- `identifier` - 服务标识（物模型中定义的 service identifier）

**请求体：**

```json
{
  "deviceKeys": ["device-001", "device-002", "device-003"],
  "params": {
    "action": "reboot"
  }
}
```

**响应示例：**

```json
{
  "code": 200,
  "data": {
    "total": 3,
    "success": 2,
    "failed": 1,
    "details": [
      { "deviceKey": "device-001", "status": "SUCCESS" },
      { "deviceKey": "device-002", "status": "SUCCESS" },
      { "deviceKey": "device-003", "status": "FAILED", "error": "设备离线" }
    ]
  }
}
```

## 前端操作

1. 进入「设备管理」页面
2. 勾选需要批量操作的设备（表格左侧 checkbox）
3. 点击「批量指令」按钮
4. 在弹窗中输入服务标识和参数（JSON 格式）
5. 点击「执行」，查看每台设备的执行结果

## 注意事项

- 批量指令是同步执行的，设备数量较多时响应时间会变长
- 离线设备的指令会返回 FAILED，不会阻塞其他设备
- 每台设备的指令执行互相独立，单台失败不影响其他设备
