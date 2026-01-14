# 数据源配置指南

MD-Agent 支持多种数据源类型，包括关系型数据库和消息队列。本指南详细介绍各种数据源的配置方法。

## 数据库数据源

### SQLite（默认）

SQLite 是默认的嵌入式数据库，无需额外配置即可使用。

```yaml
spring:
  datasource:
    url: jdbc:sqlite:/db/bcp.db
    driver-class-name: org.sqlite.JDBC
```

**使用场景：**
- 开发和测试环境
- 小规模数据存储
- 无需独立数据库服务器的场景

### MySQL

```yaml
# application.yml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:mysql://localhost:3306/database_name?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

    # Druid 连接池配置
    druid:
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
```

**依赖：**
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### PostgreSQL

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:postgresql://localhost:5432/database_name
    username: your_username
    password: your_password
    driver-class-name: org.postgresql.Driver
```

**依赖：**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.6.0</version>
</dependency>
```

### Oracle

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:oracle:thin:@localhost:1521:ORCL
    username: your_username
    password: your_password
    driver-class-name: oracle.jdbc.OracleDriver
```

**依赖：**
```xml
<dependency>
    <groupId>com.oracle.database.jdbc</groupId>
    <artifactId>ojdbc8</artifactId>
    <version>21.9.0.0</version>
</dependency>
```

### SQL Server

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:sqlserver://localhost:1433;databaseName=database_name
    username: your_username
    password: your_password
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**依赖：**
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>12.2.0.jre8</version>
</dependency>
```

### GaussDB

```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    url: jdbc:gaussdb://localhost:5432/database_name
    username: your_username
    password: your_password
    driver-class-name: com.huawei.gauss200.jdbc.Driver
```

### MS Access

```yaml
spring:
  datasource:
    url: jdbc:ucanaccess://path/to/database.accdb
    driver-class-name: net.ucanaccess.jdbc.UcanaccessDriver
```

## 消息队列数据源

### Apache Kafka

```yaml
ag:
  kafka:
    enabled: true
    bootstrap-servers: localhost:9092
    consumer:
      group-id: md-agent-group
      auto-offset-reset: earliest
      enable-auto-commit: false
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
      acks: all
      retries: 3
```

**主题配置：**
```java
// 消费消息
@KafkaListener(topics = "your-topic", groupId = "md-agent-group")
public void consume(String message) {
    // 处理消息
}

// 生产消息
kafkaTemplate.send("your-topic", message);
```

### Apache Pulsar

```yaml
ag:
  pulsar:
    enabled: true
    service-url: pulsar://localhost:6650
    namespace: default
    tenant: public
```

### RabbitMQ

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
    virtual-host: /
```

### MQTT

```yaml
ag:
  mqtt:
    enabled: true
    broker-url: tcp://localhost:1883
    client-id: md-agent-client
    username: your_username
    password: your_password
    qos: 1
```

## MongoDB（可选）

```yaml
ag:
  mongodb:
    enabled: true
    uri: mongodb://username:password@localhost:27017/database_name
    database: your_database
```

## SAP RFC 连接

```yaml
ag:
  sap:
    enabled: true
    destinations:
      - name: SAP_DEV
        ashost: sap-host.example.com
        sysnr: "00"
        client: "100"
        user: your_username
        password: your_password
        lang: ZH
        pool-capacity: 3
        peak-limit: 10
```

**SAP 连接属性：**
- `ashost`: SAP 应用服务器主机
- `sysnr`: SAP 系统号
- `client`: SAP 客户端
- `user`: SAP 用户名
- `password`: SAP 密码
- `lang`: 登录语言（ZH=中文, EN=英文）
- `pool-capacity`: 连接池大小
- `peak-limit`: 最大连接数

## 华为 IoT

```yaml
ag:
  huawei-iot:
    enabled: true
    app-id: your_app_id
    app-secret: your_app_secret
    iot-platform-url: https://iot-platform.example.com
```

## 数据源管理

### 动态数据源配置

MD-Agent 支持通过 API 动态添加和管理数据源：

```bash
# 添加数据源
POST /api/datasource
Content-Type: application/json

{
  "name": "mysql-prod",
  "type": "MYSQL",
  "url": "jdbc:mysql://prod-db:3306/mydb",
  "username": "app_user",
  "password": "encrypted_password",
  "properties": {
    "maxActive": "20",
    "initialSize": "5"
  }
}
```

### 连接池配置建议

**开发环境：**
```yaml
druid:
  initial-size: 2
  min-idle: 2
  max-active: 5
```

**生产环境：**
```yaml
druid:
  initial-size: 10
  min-idle: 10
  max-active: 50
  max-wait: 60000
  time-between-eviction-runs-millis: 60000
  min-evictable-idle-time-millis: 300000
  validation-query: SELECT 1
  test-while-idle: true
  test-on-borrow: false
  test-on-return: false
  pool-prepared-statements: true
  max-pool-prepared-statement-per-connection-size: 20
  filters: stat,wall,log4j2
```

## 安全最佳实践

1. **密码加密**
   - 不要在配置文件中明文存储密码
   - 使用环境变量：`${DB_PASSWORD}`
   - 使用 Jasypt 等加密工具

2. **最小权限原则**
   - 数据源用户仅授予必要的权限
   - 读取操作使用只读用户
   - 生产环境禁用 DDL 权限

3. **连接安全**
   - 生产环境使用 SSL/TLS 连接
   - 限制数据库网络访问
   - 使用防火墙规则

## 故障排查

### 常见问题

**连接超时：**
```
Caused by: java.net.ConnectException: Connection timed out
```
- 检查网络连接和防火墙设置
- 验证数据库服务是否运行
- 检查连接 URL 和端口

**认证失败：**
```
Caused by: java.sql.SQLException: Access denied
```
- 验证用户名和密码
- 检查数据库用户权限
- 确认客户端 IP 在允许列表中

**驱动类未找到：**
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```
- 确认依赖已添加到 `pom.xml`
- 检查驱动类名是否正确
- 运行 `mvn clean package` 重新构建

## 下一步

- 了解 [任务配置](task-config.md)
- 查看 [数据库同步示例](../examples/database-sync.md)
- 阅读 [性能优化指南](performance.md)
