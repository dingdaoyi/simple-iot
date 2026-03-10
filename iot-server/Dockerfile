# 构建阶段
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /build

# 复制 maven 配置
COPY pom.xml .
COPY iot-common/pom.xml iot-common/
COPY iot-server/pom.xml iot-server/
COPY iot-driver/pom.xml iot-driver/

# 复制源代码
COPY iot-common/ iot-common/
COPY iot-server/ iot-server/
COPY iot-driver/ iot-driver/

# 安装 Maven 并构建
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests -pl iot-server -am

# 运行阶段
FROM eclipse-temurin:25-jre

WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y curl tzdata && \
    rm -rf /var/lib/apt/lists/* && \
    ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

# 从构建阶段复制 jar 文件
COPY --from=builder /build/iot-server/target/iot-server-*.jar app.jar

# 创建非 root 用户
RUN groupadd -r iot && useradd -r -g iot iot
USER iot

# 暴露端口
EXPOSE 5010 1883 8083 18083

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:5010/iot/actuator/health || exit 1

# 启动命令 - 使用 shell 格式以支持 JDK_JAVA_OPTIONS 环境变量
ENTRYPOINT java $JDK_JAVA_OPTIONS -Xms512m -Xmx1024m -jar app.jar
