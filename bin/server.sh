#!/bin/bash

# 配置部分
APP_NAME="iot-server-0.0.1-SNAPSHOT.jar"        # Spring Boot JAR 文件名
APP_PATH="/opt/sample-iot"   # 应用路径
        LOG_DIR="$APP_PATH/logs"  # 日志目录
        PID_FILE="$APP_PATH/app.pid"  # 进程 ID 文件

# 确保日志目录存在
mkdir -p "$LOG_DIR"

        # 启动应用
start() {
    echo "Starting $APP_NAME..."
    if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
    echo "$APP_NAME is already running with PID $PID."
    exit 1
        else
    echo "Removing stale PID file."
    rm "$PID_FILE"
    fi
            fi

    LOG_FILE="$LOG_DIR/app_$(date +'%Y-%m-%d').log"
    nohup java --add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED -jar "$APP_PATH/$APP_NAME" >> "$LOG_FILE" 2>&1 &
                                                         echo $! > "$PID_FILE"
    echo "$APP_NAME started with PID $(cat $PID_FILE). Logs: $LOG_FILE"
}

# 停止应用
stop() {
    echo "Stopping $APP_NAME..."
    if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
    kill $PID
    echo "$APP_NAME stopped."
    rm "$PID_FILE"
        else
    echo "No running process found for PID $PID."
    rm "$PID_FILE"
    fi
    else
    echo "$APP_NAME is not running."
    fi
}

# 重启应用
restart() {
    echo "Restarting $APP_NAME..."
    stop
    sleep 2
    start
}

# 检查运行状态
status() {
    if [ -f "$PID_FILE" ]; then
            PID=$(cat "$PID_FILE")
    if ps -p $PID > /dev/null; then
    echo "$APP_NAME is running with PID $PID."
        else
    echo "$APP_NAME is not running, but PID file exists."
    fi
    else
    echo "$APP_NAME is not running."
    fi
}

# 清理旧日志
clean_logs() {
    find "$LOG_DIR/" -type f -name "*.log" -mtime +7 -exec rm -f {} \;
    echo "Old logs older than 7 days have been deleted."
}

# 脚本用法
case "$1" in
start)
start
;;
stop)
stop
;;
restart)
restart
;;
status)
status
;;
clean_logs)
clean_logs
;;
    *)
echo "Usage: $0 {start|stop|restart|status|clean_logs}"
exit 1
        ;;
esac