# MD-Agent

<div align="center">

[![CI Build](https://github.com/paul-zhang-sudo/bcp-md-agent/workflows/CI%20Build%20and%20Test/badge.svg)](https://github.com/paul-zhang-sudo/bcp-md-agent/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-8+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.3.7-green.svg)](https://spring.io/projects/spring-boot)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

</div>

MD-Agent 是一个高度可配置的企业级数据集成引擎，支持 ETL（Extract-Transform-Load）操作，可连接多种异构数据源进行数据交换和同步。

## 功能特性

### 🗄️ 多数据源支持
- **关系型数据库**
  - SQL Server - 企业级数据库，支持 JDBC 连接
  - Oracle - 使用 ojdbc8 驱动，版本 21.5.0.0
  - PostgreSQL - 开源关系型数据库
  - MySQL/GaussDB - 兼容 MySQL 协议的数据库
  - SQLite - 嵌入式数据库，默认配置
  - MS Access - 通过 JDBC-ODBC 桥接
- **连接池** - 集成 Druid 连接池，支持连接复用和监控

### 📨 消息队列集成
- **Kafka** - Apache Kafka 3.6.1，支持生产者/消费者模式
- **RabbitMQ** - AMQP 协议消息队列
- **Apache Pulsar** - 2.11.4 版本，云原生分布式消息系统
- **MQTT** - 轻量级物联网通信协议

### 🏢 企业系统对接
- **SAP RFC 远程函数调用**
  - 使用 sapjco3.jar (SAP JCo 3.0)
  - 支持 SAP 系统远程函数调用
  - 集成企业 ERP 系统数据交换

### 🌐 物联网支持 - Huawei IoT SDK 集成

#### 核心组件
- **IoT Module Client SDK** - 版本 2.1.17
- **三大核心客户端**：
  - `ItClient` - IoT 边缘客户端，提供基础物联网通信能力
  - `DriverClient` - 驱动客户端，用于子设备管理和网关功能
  - `DcClient` - 数采客户端，专用于工业数据采集场景

#### 设备管理能力
- **设备属性管理**
  - 设备属性上报 (Property Report)
  - 设备属性下发与反控 (Property Set)
  - 设备属性查询 (Property Get)
  - 设备影子同步 (Shadow Sync)

- **子设备管理**
  - 子设备添加/删除事件处理
  - 子设备消息接收与转发
  - 子设备命令下发
  - 网关回调机制 (GatewayCallback)

#### 数据采集功能
- **点位数据上报** - 通过 `DcClient.pointReport()` 上报工业数据点位
- **数采配置下发** - 支持从华为云 IoTEdge 平台下发数采配置
- **模块影子** - 自动同步边缘应用模块配置
- **数据源动态配置** - 通过 IoT 平台远程配置数据源连接信息

#### 集成方式
- **环境变量初始化** - 通过 `createFromEnv()` 自动读取边缘环境配置
- **自动重连** - 内置 MQTT 连接状态管理和自动重连机制
- **本地 API 集成** - 设备反控消息自动转发到本地 API 端点 (`http://127.0.0.1:8080/api/device_prop_set`)
- **回调机制** - 支持多种事件回调：
  - 设备消息接收 (onDeviceMessageReceived)
  - 设备命令调用 (onDeviceCommandCalled)
  - 属性设置 (onDevicePropertiesSet)
  - 影子接收 (onDeviceShadowReceived)

#### 应用场景
- 工业数据采集与上报
- 设备远程监控与反控
- 边缘网关设备管理
- IoT 边缘计算应用
- OT/IT 数据融合

### ⚙️ JavaScript 脚本转换
- 使用 Nashorn JavaScript 引擎
- 支持 ES5+ 语法进行灵活的数据转换
- 内置丰富的工具类库
- 支持自定义函数和变量

### ⏰ 定时任务
- 支持 Cron 表达式配置定时执行
- 灵活的任务调度策略
- 任务执行状态监控

### 🔌 实时 API 代理
- 动态路由配置
- 接口鉴权机制
- 请求/响应转换
- 负载均衡支持

### 🔔 告警通知
- **邮件告警** - SMTP 邮件发送
- **飞书机器人** - Webhook 集成
- 自定义告警规则
- 多渠道消息推送

## 技术栈

- Java 8
- Spring Boot 2.3.7
- Spring Data JPA
- Druid 连接池
- Apache Kafka 3.6.1
- Apache Pulsar 2.11.4
- MongoDB（可选）
- Nashorn JavaScript Engine

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.x

### 构建项目

```bash
mvn clean package -DskipTests
```

### 运行

```bash
java -jar target/md-agent-1.0.0-SNAPSHOT.jar
```

服务默认启动在 `http://localhost:8080`

### 配置

主要配置文件位于 `src/main/resources/application.yml`：

```yaml
# 数据库配置（默认使用 SQLite）
spring:
  datasource:
    url: jdbc:sqlite:/db/bcp.db

# MongoDB 配置（可选）
ag:
  mongodb:
    enabled: false
```

## 核心模块

| 模块 | 说明 |
|------|------|
| `engine` | 集成引擎核心，包含输入、转换、输出节点 |
| `datasource` | 数据源管理，支持多种数据库和消息队列 |
| `task` | 定时任务调度 |
| `proxy` | API 代理和路由 |
| `email` | 邮件告警服务 |
| `sap` | SAP RFC 连接管理 |

## 工作流程

```
┌─────────┐    ┌───────────┐    ┌──────────┐
│  Input  │───▶│ Transform │───▶│  Output  │
└─────────┘    └───────────┘    └──────────┘
     │              │                 │
     ▼              ▼                 ▼
  数据源        JS 脚本          目标系统
```

1. **输入节点** - 从数据库、API、消息队列等读取数据
2. **转换节点** - 使用 JavaScript 脚本进行数据转换和处理
3. **输出节点** - 将处理后的数据写入目标系统

## 应用场景

- 多系统间的数据交换和同步
- ETL 数据抽取、转换、加载
- IoT 设备数据采集和处理
- API 网关和接口代理
- 消息队列的生产消费

## 文档

- [完整文档](docs/README.md)
- [配置指南](docs/guides/datasource-config.md) - 数据源配置详解
- [脚本开发](docs/guides/script-development.md) - JavaScript 转换脚本编写
- [API 文档](docs/api/rest-api.md) - REST API 接口说明
- [使用示例](docs/examples/database-sync.md) - 数据库同步示例
- [故障排查](docs/guides/troubleshooting.md) - 常见问题解决

## Docker 部署

```bash
# 使用 Docker Compose 快速启动
docker-compose -f examples/docker-compose.yml up -d

# 或使用 Docker 运行
docker run -d \
  -p 8080:8080 \
  -v $(pwd)/config:/app/config \
  -v $(pwd)/logs:/app/logs \
  md-agent:latest
```

查看 [Dockerfile](examples/Dockerfile) 和 [docker-compose.yml](examples/docker-compose.yml) 示例。

## 配置示例

项目提供多种数据源配置示例：

- [MySQL 配置](examples/application-mysql.yml)
- [PostgreSQL 配置](examples/application-postgres.yml)
- [Docker Compose 完整环境](examples/docker-compose.yml)

## 贡献

欢迎贡献代码！请阅读 [贡献指南](CONTRIBUTING.md) 了解如何参与项目开发。

本项目遵循 [贡献者公约行为准则](CODE_OF_CONDUCT.md)，参与者需遵守其规定。

## 安全

查看我们的 [安全政策](SECURITY.md) 了解如何报告安全漏洞。

## 变更日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解各版本的更新内容。

## 许可证

本项目采用 [MIT License](LICENSE) 开源协议。

---

<div align="center">

**[文档](docs/README.md)** | **[贡献指南](CONTRIBUTING.md)** | **[Issues](https://github.com/paul-zhang-sudo/bcp-md-agent/issues)** | **[讨论](https://github.com/paul-zhang-sudo/bcp-md-agent/discussions)**

Made with ❤️ by the MD-Agent Team

</div>
