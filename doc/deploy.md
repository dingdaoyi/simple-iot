# Docker 部署指南

## 快速开始

```bash
# 1. 赋予脚本执行权限
chmod +x deploy.sh

# 2. 一键部署（会检查并提示安装 InfluxDB）
./deploy.sh deploy
```

## 部署命令

| 命令 | 说明 |
|------|------|
| `./deploy.sh deploy` | 完整部署（推荐首次使用） |
| `./deploy.sh start` | 启动服务 |
| `./deploy.sh stop` | 停止服务 |
| `./deploy.sh restart` | 重启服务 |
| `./deploy.sh logs` | 查看日志 |
| `./deploy.sh logs iot-server` | 查看后端日志 |
| `./deploy.sh status` | 查看服务状态 |
| `./deploy.sh update` | 更新部署 |
| `./deploy.sh clean` | 清理所有数据 |
| `./deploy.sh influxdb` | 单独安装 InfluxDB 3 |

## 手动部署

```bash
# 1. 复制环境变量配置
cp .env.example .env

# 2. 安装 InfluxDB 3 (如果未安装)
./deploy.sh influxdb
# 或手动安装
curl -O https://www.influxdata.com/d/install_influxdb3.sh && sh install_influxdb3.sh

# 3. 修改配置(influxdb的token, 地址、数据库信息)
vim .env

# 4. 构建并启动
docker compose up -d --build

# 5. 查看状态
docker compose ps

# 6. 查看日志
docker compose logs -f
```

## InfluxDB 3 配置

InfluxDB 3 需要本地安装（非 Docker），部署脚本会自动检测并提示安装。

### 手动安装 InfluxDB 3

```bash
# macOS / Linux
curl -O https://www.influxdata.com/d/install_influxdb3.sh && sh install_influxdb3.sh

# 启动 InfluxDB
influxdb3 serve --node-id=node0 --object-store=file --data-dir=~/.influxdb/data

# 创建 admin token
influxdb3 create token --admin
```

### 配置连接

在 `.env` 文件中配置 InfluxDB 连接信息：

```bash
# macOS Docker 访问本地服务
INFLUXDB_URL=http://host.docker.internal:8181

# Linux Docker 访问本地服务
INFLUXDB_URL=http://172.17.0.1:8181
```

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost |
| API 文档 | http://localhost:5010/iot/doc.html |
| MQTT | localhost:1883 |
| MQTT WebSocket | ws://localhost:8083 |
| RustFS API | http://localhost:9000 |
| RustFS Console | http://localhost:9001 |
| InfluxDB 3 API | http://localhost:8181 |

## 默认账号

| 服务 | 用户名 | 密码 |
|------|--------|------|
| PostgreSQL | postgres | postgres123 |
| InfluxDB 3 | - | iot_token_123456 (token) |
| RustFS | rustfsadmin | rustfsadmin123 |
| MQTT | mica | mica |

## 环境变量

编辑 `.env` 文件修改配置：

```bash
# 数据库
POSTGRES_USERNAME=postgres
POSTGRES_PASSWORD=postgres123
DATABASE=simple

# InfluxDB
INFLUXDB_URL=http://host.docker.internal:8181
INFLUXDB_TOKEN=your-token
INFLUXDB_DATABASE=simple

# RustFS (S3)
S3_ACCESS_KEY_ID=rustfsadmin
S3_SECRET_ACCESS_KEY=rustfsadmin123
S3_BUCKET_NAME=iot-bucket

# 端口
WEB_PORT=80
SERVER_PORT=5010
MQTT_PORT=1883
```

## 常见问题

### 1. 端口冲突
修改 `.env` 文件中的端口配置

### 2. 内存不足
修改 `iot-server/Dockerfile` 中的 JVM 参数：
```dockerfile
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
```

### 3. 数据持久化
数据存储在 Docker volumes 中，清理时需谨慎：
```bash
# 仅停止服务（保留数据）
docker compose down

# 停止并删除数据卷
docker compose down -v

# 查看数据卷
docker volume ls | grep iot
```

### 4. Docker 容器无法连接本地 InfluxDB

macOS:
```bash
INFLUXDB_URL=http://host.docker.internal:8181
```

Linux:
```bash
# 方法1: 使用 docker0 网桥 IP
INFLUXDB_URL=http://172.17.0.1:8181

# 方法2: 使用 host 网络模式 (docker-compose.yml 中添加)
# network_mode: host
```
