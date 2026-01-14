# 变更日志

本文档记录 MD-Agent 项目的所有重要变更。

格式基于 [Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)，
版本号遵循 [语义化版本](https://semver.org/lang/zh-CN/)。

## [Unreleased]

### 已添加
- 项目开源文档完善
- GitHub Issue 和 PR 模板
- 贡献者公约行为准则
- 安全政策文档

### 已变更
- 优化代码质量，添加 final 关键字

### 已修复
- 无

## [1.0.0-SNAPSHOT] - 2026-01-14

### 已添加

#### 核心功能
- 企业级数据集成引擎核心实现
- ETL（Extract-Transform-Load）操作支持
- 任务引擎和调度系统
- 定时任务支持（Cron 表达式）

#### 数据源支持
- SQL Server 数据源连接
- Oracle 数据源连接
- PostgreSQL 数据源连接
- MySQL 数据源连接
- GaussDB 数据源连接
- SQLite 数据源连接（默认）
- MS Access 数据源连接

#### 消息队列集成
- Apache Kafka 3.6.1 集成
- RabbitMQ 支持
- Apache Pulsar 2.11.4 支持
- MQTT 协议支持

#### 企业系统对接
- SAP RFC 远程函数调用支持
- SAP 目标数据提供器
- JCo 连接管理

#### 数据转换
- JavaScript 脚本引擎（Nashorn）
- 灵活的数据转换脚本支持
- 自定义转换逻辑

#### API 代理
- 动态 API 路由
- 接口鉴权
- 请求代理和转发

#### 告警通知
- 邮件告警服务
- 飞书机器人集成
- 任务错误数据告警插件

#### 物联网支持
- 华为 IoT SDK 集成
- IoT 设备数据采集

#### 其他功能
- 连接池管理（Druid）
- MongoDB 支持（可选）
- Socket 服务器实现
- 多种加密工具（AES, SHA256, 签名验证）
- Excel 和 DBF 文件处理
- XML 转换工具

### 技术栈
- Java 8
- Spring Boot 2.3.7.RELEASE
- Spring Data JPA
- Druid 1.x 连接池
- Maven 构建工具

### 文档
- README 项目说明
- LICENSE MIT 许可证
- CONTRIBUTING 贡献指南
- CODE_OF_CONDUCT 行为准则
- SECURITY 安全政策

---

## 版本说明

### 语义化版本规则

- **主版本号（Major）**：不兼容的 API 变更
- **次版本号（Minor）**：向下兼容的功能新增
- **修订号（Patch）**：向下兼容的问题修复

### 变更类型

- **已添加（Added）**：新功能
- **已变更（Changed）**：现有功能的变更
- **已弃用（Deprecated）**：即将移除的功能
- **已移除（Removed）**：已移除的功能
- **已修复（Fixed）**：Bug 修复
- **安全（Security）**：安全相关的修复

[Unreleased]: https://github.com/paul-zhang-sudo/bcp-md-agent/compare/v1.0.0-SNAPSHOT...HEAD
[1.0.0-SNAPSHOT]: https://github.com/paul-zhang-sudo/bcp-md-agent/releases/tag/v1.0.0-SNAPSHOT
