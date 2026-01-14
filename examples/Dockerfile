# Dockerfile 示例
# 用于构建 MD-Agent Docker 镜像

FROM openjdk:8-jre-alpine

LABEL maintainer="MD-Agent Team"
LABEL description="MD-Agent - Enterprise Data Integration Engine"

# 设置工作目录
WORKDIR /app

# 安装必要的工具
RUN apk add --no-cache \
    curl \
    bash \
    tzdata

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 复制 JAR 文件
COPY target/md-agent-*.jar /app/md-agent.jar

# 创建日志和配置目录
RUN mkdir -p /app/logs /app/config

# 复制配置文件（可选）
# COPY src/main/resources/application.yml /app/config/application.yml

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1g -XX:+UseG1GC -XX:+PrintGCDetails -XX:+PrintGCDateStamps"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/md-agent.jar"]
