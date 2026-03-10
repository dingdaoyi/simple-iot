#!/bin/bash

# Simple IoT 部署脚本
# 使用方法: ./deploy.sh [命令]

set -e

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查 Docker 和 Docker Compose
check_docker() {
    if ! command -v docker &> /dev/null; then
        log_error "Docker 未安装，请先安装 Docker"
        exit 1
    fi

    if ! docker compose version &> /dev/null; then
        log_error "Docker Compose 未安装，请先安装 Docker Compose"
        exit 1
    fi

    log_info "Docker 环境检查通过"
}

# 检查 InfluxDB 服务是否可用
check_influxdb() {
    # 自动创建 .env 文件（如果不存在）
    if [ ! -f .env ]; then
        log_info ".env 文件不存在，自动从 .env.example 创建..."
        cp .env.example .env
        log_warn "已创建 .env 文件，请根据实际情况修改 INFLUXDB_URL 和 INFLUXDB_TOKEN"
    fi

    # 从 .env 读取 InfluxDB 配置
    INFLUXDB_URL=$(grep "^INFLUXDB_URL=" .env 2>/dev/null | cut -d'=' -f2-)
    INFLUXDB_TOKEN=$(grep "^INFLUXDB_TOKEN=" .env 2>/dev/null | cut -d'=' -f2-)

    # 检查 URL 是否配置
    if [ -z "$INFLUXDB_URL" ]; then
        log_error "INFLUXDB_URL 未配置，请编辑 .env 文件"
        exit 1
    fi

    # 检查 Token 是否配置
    if [ -z "$INFLUXDB_TOKEN" ]; then
        log_warn "INFLUXDB_TOKEN 未配置，建议设置"
    fi

    # 检查 InfluxDB 服务是否可用
    # 注意: /health 端点可能返回 401（需要认证），但也说明服务正常
    log_info "检测 InfluxDB 服务: ${INFLUXDB_URL}"
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" --connect-timeout 5 "${INFLUXDB_URL}/health" 2>/dev/null)

    # 200=成功, 401=需要认证但服务正常
    if [ "$HTTP_CODE" = "200" ] || [ "$HTTP_CODE" = "401" ]; then
        log_info "InfluxDB 服务已就绪 (HTTP $HTTP_CODE)"
        return 0
    fi

    log_error "InfluxDB 服务不可用: ${INFLUXDB_URL} (HTTP $HTTP_CODE)"
    echo ""
    echo -e "${YELLOW}请检查：${NC}"
    echo "1. InfluxDB 是否已启动"
    echo "2. .env 中的 INFLUXDB_URL 是否正确"
    echo ""
    echo "如果使用 Docker Compose 安装的 InfluxDB："
    echo "  cd ~/.influxdb/docker && docker compose up -d"
    echo ""
    echo "如果 InfluxDB 在远程服务器，请配置正确的地址："
    echo "  INFLUXDB_URL=http://your-server:8181"
    echo ""
    exit 1
}

# 初始化环境
init() {
    log_info "初始化部署环境..."

    # .env 文件由 check_influxdb 自动创建

    log_info "初始化完成"
}

# 安装 InfluxDB 3
install_influxdb() {
    log_info "使用官方脚本安装 InfluxDB 3..."
    log_warn "这将安装 InfluxDB 3 Core + Explorer UI (Docker Compose 方式)"
    echo ""
    echo -e "是否继续? (y/n)"
    read -r confirm

    if [ "$confirm" = "y" ]; then
        curl -O https://www.influxdata.com/d/install_influxdb3.sh && sh install_influxdb3.sh
        rm -f install_influxdb3.sh
        log_info "InfluxDB 3 安装完成"
        echo ""
        echo -e "${GREEN}访问地址:${NC}"
        echo "  InfluxDB API: http://localhost:8181"
        echo "  Explorer UI:  http://localhost:8888"
        echo ""
        echo -e "${YELLOW}请配置 .env 文件:${NC}"
        echo "  INFLUXDB_URL=http://host.docker.internal:8181"
        echo "  INFLUXDB_TOKEN=<从 Explorer 获取的 token>"
    fi
}

# 构建镜像
build() {
    log_info "构建 Docker 镜像..."

    docker compose build --no-cache

    log_info "镜像构建完成"
}

# 启动服务
start() {
    log_info "启动服务..."

    docker compose up -d

    log_info "服务启动完成"
    log_info "前端访问地址: http://localhost:${WEB_PORT:-80}"
    log_info "API 文档地址: http://localhost:${SERVER_PORT:-5010}/iot/doc.html"
    log_info "MQTT 端口: ${MQTT_PORT:-1883}"
    log_info "RustFS 控制台: http://localhost:${RUSTFS_CONSOLE_PORT:-9001}"
}

# 停止服务
stop() {
    log_info "停止服务..."

    docker compose down

    log_info "服务已停止"
}

# 重启服务
restart() {
    stop
    start
}

# 查看日志
logs() {
    docker compose logs -f "$@"
}

# 查看状态
status() {
    docker compose ps
}

# 清理数据
clean() {
    log_warn "这将删除所有数据，是否继续? (y/n)"
    read -r confirm

    if [ "$confirm" = "y" ]; then
        log_info "清理数据..."
        docker compose down -v
        rm -rf data/
        log_info "数据清理完成"
    else
        log_info "取消清理"
    fi
}

# 完整部署
deploy() {
    check_docker
    check_influxdb
    init
    build
    start
    status
}

# 更新部署
update() {
    log_info "更新部署..."
    git pull
    build
    restart
    log_info "更新完成"
}

# 帮助信息
help() {
    echo "Simple IoT 部署脚本"
    echo ""
    echo "使用方法: ./deploy.sh [命令]"
    echo ""
    echo "命令:"
    echo "  init      - 初始化部署环境"
    echo "  build     - 构建 Docker 镜像"
    echo "  start     - 启动服务"
    echo "  stop      - 停止服务"
    echo "  restart   - 重启服务"
    echo "  logs      - 查看日志 (可选: 服务名)"
    echo "  status    - 查看服务状态"
    echo "  clean     - 清理所有数据"
    echo "  deploy    - 完整部署 (检查InfluxDB + init + build + start)"
    echo "  update    - 更新部署 (git pull + build + restart)"
    echo "  influxdb  - 安装 InfluxDB 3 (使用官方脚本)"
    echo "  help      - 显示帮助信息"
}

# 主入口
case "$1" in
    init)
        check_docker
        init
        ;;
    build)
        check_docker
        build
        ;;
    start)
        check_docker
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    logs)
        shift
        logs "$@"
        ;;
    status)
        status
        ;;
    clean)
        clean
        ;;
    deploy)
        deploy
        ;;
    update)
        update
        ;;
    influxdb)
        install_influxdb
        ;;
    *)
        help
        ;;
esac