# 故障排查指南

本指南帮助你诊断和解决 MD-Agent 使用过程中遇到的常见问题。

## 启动问题

### 应用无法启动

**症状：**
```
Error: Could not find or load main class com.bsi.md.agent.AgentApplication
```

**解决方案：**
1. 检查 JAR 文件是否完整构建：
   ```bash
   mvn clean package -DskipTests
   ```

2. 验证 Java 版本：
   ```bash
   java -version  # 应该是 1.8+
   ```

3. 检查 MANIFEST.MF 中的 Main-Class 配置

### 端口被占用

**症状：**
```
Web server failed to start. Port 8080 was already in use.
```

**解决方案：**
1. 修改配置文件中的端口：
   ```yaml
   server:
     port: 8081
   ```

2. 或查找并终止占用端口的进程：
   ```bash
   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <进程ID> /F

   # Linux/Mac
   lsof -i :8080
   kill -9 <PID>
   ```

### 数据库连接失败

**症状：**
```
Failed to configure a DataSource: 'url' attribute is not specified
```

**解决方案：**
1. 检查 `application.yml` 中的数据源配置：
   ```yaml
   spring:
     datasource:
       url: jdbc:sqlite:/db/bcp.db
       driver-class-name: org.sqlite.JDBC
   ```

2. 确保数据库驱动依赖已添加到 `pom.xml`

3. 验证数据库路径和权限

## 数据源问题

### JDBC 连接超时

**症状：**
```
java.sql.SQLTimeoutException: Connection timed out
```

**排查步骤：**
1. 验证网络连接：
   ```bash
   ping database-host
   telnet database-host 3306
   ```

2. 检查防火墙规则

3. 增加连接超时时间：
   ```yaml
   spring:
     datasource:
       druid:
         max-wait: 120000  # 120秒
   ```

### 认证失败

**症状：**
```
java.sql.SQLException: Access denied for user 'username'@'host'
```

**解决方案：**
1. 验证用户名和密码

2. 检查数据库用户权限：
   ```sql
   -- MySQL
   SHOW GRANTS FOR 'username'@'host';

   -- PostgreSQL
   \du username
   ```

3. 确认客户端 IP 在允许列表中

### 连接池耗尽

**症状：**
```
Cannot get a connection, pool error Timeout waiting for idle object
```

**解决方案：**
1. 增加连接池大小：
   ```yaml
   druid:
     max-active: 50  # 增加最大连接数
   ```

2. 检查是否存在连接泄漏（未关闭的连接）

3. 启用连接池监控：
   ```yaml
   druid:
     stat-view-servlet:
       enabled: true
       url-pattern: /druid/*
   ```

## Kafka 问题

### 无法连接 Kafka

**症状：**
```
org.apache.kafka.common.errors.TimeoutException: Failed to update metadata
```

**排查步骤：**
1. 验证 Kafka 服务是否运行：
   ```bash
   # 检查 Kafka 进程
   ps aux | grep kafka

   # 测试连接
   telnet kafka-host 9092
   ```

2. 检查 bootstrap-servers 配置：
   ```yaml
   ag:
     kafka:
       bootstrap-servers: localhost:9092  # 确保地址正确
   ```

3. 检查网络和防火墙

### 消费者组问题

**症状：**
消息重复消费或消费偏移异常

**解决方案：**
1. 重置消费者组偏移：
   ```bash
   kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
     --group md-agent-group \
     --reset-offsets --to-earliest --topic your-topic --execute
   ```

2. 检查自动提交配置：
   ```yaml
   kafka:
     consumer:
       enable-auto-commit: false  # 手动提交更可靠
   ```

## SAP RFC 问题

### JCo 库未找到

**症状：**
```
java.lang.UnsatisfiedLinkError: no sapjco3 in java.library.path
```

**解决方案：**
1. 下载 SAP JCo 库

2. 配置库路径：
   ```bash
   # Windows
   set PATH=%PATH%;C:\path\to\sapjco3

   # Linux
   export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/sapjco3
   ```

3. 或在启动时指定：
   ```bash
   java -Djava.library.path=/path/to/sapjco3 -jar md-agent.jar
   ```

### SAP 连接失败

**症状：**
```
JCoException: Connect to SAP gateway failed
```

**排查步骤：**
1. 验证 SAP 服务器地址和端口

2. 检查 SAP 系统是否运行

3. 验证用户名、密码和客户端配置

4. 检查网络连接和防火墙

## JavaScript 脚本问题

### 脚本执行错误

**症状：**
```
javax.script.ScriptException: ReferenceError: "xyz" is not defined
```

**解决方案：**
1. 检查变量名拼写

2. 确保使用正确的 JavaScript 语法（ES5.1）

3. 添加调试日志：
   ```javascript
   logger.info("变量值: " + JSON.stringify(data));
   ```

### 脚本超时

**症状：**
脚本长时间无响应

**解决方案：**
1. 优化脚本性能，避免死循环

2. 减少不必要的计算

3. 批量处理大数据集

### 内存溢出

**症状：**
```
java.lang.OutOfMemoryError: Java heap space
```

**解决方案：**
1. 增加 JVM 堆内存：
   ```bash
   java -Xmx2g -Xms1g -jar md-agent.jar
   ```

2. 优化脚本，避免创建大量对象

3. 分批处理数据

## 性能问题

### 应用响应慢

**排查步骤：**
1. 检查数据库查询性能：
   - 启用 SQL 日志
   - 分析慢查询
   - 添加必要的索引

2. 监控 JVM 性能：
   ```bash
   # 查看 GC 日志
   java -XX:+PrintGCDetails -jar md-agent.jar

   # 使用 jstat 监控
   jstat -gc <pid> 1000
   ```

3. 检查连接池配置

4. 分析应用日志

### 内存使用高

**排查步骤：**
1. 生成堆转储：
   ```bash
   jmap -dump:format=b,file=heap.bin <pid>
   ```

2. 使用 MAT 或 JProfiler 分析

3. 检查是否存在内存泄漏

## 日志问题

### 找不到日志文件

**解决方案：**
1. 检查日志配置：
   ```yaml
   logging:
     file:
       name: logs/md-agent.log
     level:
       root: INFO
   ```

2. 确保日志目录存在且有写权限

### 日志级别调整

临时启用 DEBUG 日志：

```yaml
logging:
  level:
    com.bsi.md.agent: DEBUG
    org.springframework: INFO
```

## 部署问题

### Docker 容器无法访问

**症状：**
容器运行但无法访问服务

**解决方案：**
1. 检查端口映射：
   ```bash
   docker run -p 8080:8080 md-agent:latest
   ```

2. 检查容器日志：
   ```bash
   docker logs <container-id>
   ```

3. 进入容器调试：
   ```bash
   docker exec -it <container-id> /bin/bash
   ```

### 权限问题

**症状：**
```
java.io.FileNotFoundException: /db/bcp.db (Permission denied)
```

**解决方案：**
1. 检查文件和目录权限：
   ```bash
   ls -la /db/
   ```

2. 修改权限：
   ```bash
   chmod 755 /db
   chmod 644 /db/bcp.db
   ```

3. 检查运行用户

## 常用诊断命令

### 系统信息
```bash
# 检查 Java 版本
java -version

# 检查进程
ps aux | grep java

# 检查端口
netstat -tulpn | grep 8080

# 检查磁盘空间
df -h
```

### JVM 诊断
```bash
# 线程转储
jstack <pid> > thread-dump.txt

# 堆转储
jmap -dump:format=b,file=heap.bin <pid>

# JVM 参数
jinfo <pid>

# 实时监控
jconsole
```

### 网络诊断
```bash
# 测试连接
telnet host port
nc -zv host port

# 查看路由
traceroute host

# DNS 解析
nslookup host
```

## 获取帮助

如果问题仍未解决：

1. **查看日志**：收集完整的错误日志
2. **准备信息**：
   - MD-Agent 版本
   - Java 版本
   - 操作系统
   - 错误堆栈
   - 配置文件（隐藏敏感信息）

3. **提交 Issue**：
   - [GitHub Issues](https://github.com/paul-zhang-sudo/bcp-md-agent/issues)
   - 使用 Bug 报告模板
   - 提供详细复现步骤

4. **社区讨论**：
   - [GitHub Discussions](https://github.com/paul-zhang-sudo/bcp-md-agent/discussions)

## 下一步

- 了解 [性能优化](performance.md)
- 查看 [部署指南](deployment.md)
- 阅读 [监控和日志](monitoring.md)
