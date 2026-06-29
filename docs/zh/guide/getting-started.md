# 快速开始

这份指南带你在 5 分钟内本地跑起 **Simple IoT**。

## 前置依赖

- Docker 24+ 与 Docker Compose v2
- 2 GB 可用内存
- 端口 `5010`、`1883`、`8083`、`9999`、`5432`、`8181` 未被占用

## 1. 克隆仓库

```bash
git clone https://github.com/dingdaoyi/simple-iot.git
cd simple-iot
```

## 2. 启动整套服务

```bash
cp .env.example .env       # 按需修改密码
./deploy.sh deploy
```

这条命令会用 Docker Compose 拉起完整栈：

| 服务 | 端口 | 用途 |
|---|---|---|
| `iot-server` | 5010 | Spring Boot REST + MQTT broker |
| `iot-web` | 80 | Vue 3 后台（Nginx） |
| `iot-postgres` | 5432 | PostgreSQL 16（元数据） |
| `influxdb` | 8181 | InfluxDB 3（时序数据） |
| `rustfs` | 9000 | S3 兼容存储 |

## 3. 登录

打开 <http://localhost>，默认账号：

```
账号：admin
密码：123456
```

> **公网部署前请务必改掉默认密码。**

## 4. 试跑实时遥测

Docker 栈默认会预置一个演示传感器，你可以直接发布一条样例数据：

```bash
mosquitto_pub -h localhost -p 1883 \
  -i simple_demo-sensor-001 \
  -u demo-sensor-001 \
  -P demo-secret \
  -t simpleiot/pro/demo-smart-sensor \
  -m '{"temperature":72.5,"humidity":43,"voltage":220.8,"online":true,"mode":"auto"}'
```

这条数据会经过演示 JavaScript 协议解码，写入遥测，并触发预置的高温告警规则。
更多细节见 [MQTT 快速测试](./mqtt-test)。

## 5. 接入第一台设备

1. **产品** → **+ 新建** → 选 `MQTT` 协议。
2. 在该产品下新增 **设备**，记下 `clientId` / `secret`。
3. 用任意 MQTT 客户端连接 `tcp://<host>:1883`，使用上一步的凭证。
4. 在 **设备 → 遥测** 页实时观察上报数据。

## 6. 配一条规则

1. **规则引擎** → **+ 新建规则链**。
2. 拖一个 **输入** 节点，连到 **过滤**，再连到 **动作**。
3. 动作选「发邮件」/「MQTT 转发」/「下发设备命令」。

保存即生效。

## 本地开发

如果你想改源码而不是跑镜像，参考 [架构总览](./architecture) 中的 dev 流程（Maven + pnpm）。
