### mqtt 协议数据上报测试
topic:sampleiot/pro/B29Gx0OpapnOtPrQXtb4
```json
{
    "header": {
        "msgId": 1,
        "identifier": "pressure"
    },
    "body": {
        "value":"20"
    }
}
```
### 指令下发测试mqtt
POST
http://127.0.0.1:5010/iot/service/12312321312/customservice
响应:
```json
{
  "code": 1,
  "success": true,
  "msg": "操作成功",
  "data": {
    "levele": 12
  }
}
```
mqtt数据上报
Topic: sampleiot/cam_res/B29Gx0OpapnOtPrQXtb4QoS: 1
```json
{
    "header": {
        "msgId": 0,
        "identifier": "customservice"
    },
    "body": {
        "data":{
            "levele":12
        }
    }
}
```
事件上报
topic sampleiot/ev/B29Gx0OpapnOtPrQXtb4
```json
{
  "header": {
    "msgId": 1,
    "identifier": "alarm"
  },
  "body": {
    "data":{
      "temper":"1"
    }
  }
}
```