# md-agent 代码层级结构分析

## 项目概述

- **项目名称**: md-agent (接口引擎)
- **GroupId**: `com.bsi.md`
- **ArtifactId**: `agent`
- **Version**: `1.0.0-SNAPSHOT`
- **Java版本**: 1.8
- **框架**: Spring Boot 2.3.7 + Spring Data JPA

---

## 1. 项目整体架构图

```
┌─────────────────────────────────────────────────────────────────────┐
│                         md-agent 接口引擎                            │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    表现层 (Controller)                        │   │
│  │   AgConfigController    AgMongoQueryController               │   │
│  └──────────────────────────────┬──────────────────────────────┘   │
│                                 ↓                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    业务逻辑层 (Service)                       │   │
│  │  AgConfigService  AgJobService  AgDataSourceService          │   │
│  │  AgApiProxyService  AgJobParamService  AgWarnMethodService   │   │
│  └──────────────────────────────┬──────────────────────────────┘   │
│                                 ↓                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    数据访问层 (Repository)                    │   │
│  │  AgConfigRepository  AgJobRepository  AgDataSourceRepository │   │
│  │  AgApiProxyRepository  AgJobConfigRepository                 │   │
│  └──────────────────────────────┬──────────────────────────────┘   │
│                                 ↓                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                    实体模型层 (Entity)                        │   │
│  │  AgConfig  AgJob  AgDataSource  AgApiProxy  AgWarnMethod     │   │
│  │  DTO: AgConfigDto, AgTaskParamDto, AgHttpResult              │   │
│  │  VO:  AgIntegrationConfigVo, AgDataSourceVo, AgNodeVo        │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. 核心引擎架构 (ETL 模式)

```
┌─────────────────────────────────────────────────────────────────────┐
│                      集成引擎核心 (engine/)                          │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  AgTaskRun (定时任务触发)                                            │
│       ↓                                                             │
│  AgTaskBootStrap (任务启动器)                                        │
│       ↓                                                             │
│  AgEngineFactory ──→ 创建引擎实例                                    │
│       ↓                                                             │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │              AgIntegrationEngine (引擎接口)                   │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  AgCommonEngine  │  AgJobEngine  │  AgApiEngine     │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│       │                                                             │
│       ├──→ Input (输入)                                             │
│       │    ├── AgCommonInput (脚本)                                 │
│       │    ├── AgDBInput (数据库)                                   │
│       │    └── AgHttpInput (HTTP)                                  │
│       │                                                             │
│       ├──→ Transform (转换)                                         │
│       │    └── AgJsScriptTransform (JS脚本)                         │
│       │                                                             │
│       └──→ Output (输出)                                            │
│            ├── AgCommonOutput (脚本)                                │
│            ├── AgDBOutput (数据库)                                  │
│            └── AgHttpOutput (HTTP)                                 │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### ETL 三阶段说明

| 阶段 | 接口 | 实现类 | 说明 |
|------|------|--------|------|
| **Extract (抽取)** | `AgInput` | `AgCommonInput`, `AgDBInput`, `AgHttpInput` | 从各种数据源读取数据 |
| **Transform (转换)** | `AgTransform` | `AgJsScriptTransform` | 使用 JS 脚本进行数据转换 |
| **Load (加载)** | `AgOutput` | `AgCommonOutput`, `AgDBOutput`, `AgHttpOutput` | 将数据写入目标系统 |

---

## 3. 数据源适配层

```
┌─────────────────────────────────────────────────────────────────────┐
│                AgDatasourceContainer (单例管理器)                    │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  ┌────────────┐  ┌────────────┐  ┌─────────────┐  ┌────────────┐  │
│  │AgJdbcTemplate│ │AgApiTemplate│ │AgSapRFCTemplate│ │AgKafkaTemplate│ │
│  │  (JDBC)     │  │  (API)     │  │   (SAP)     │  │  (Kafka)   │  │
│  └────────────┘  └────────────┘  └─────────────┘  └────────────┘  │
│                                                                     │
│  ┌─────────────┐  ┌─────────────┐                                  │
│  │AgPulsarTemplate│ │AgMqttTemplate │                                 │
│  │  (Pulsar)    │  │   (MQTT)    │                                  │
│  └─────────────┘  └─────────────┘                                  │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 支持的数据源类型

| 类型 | 模板类 | 说明 |
|------|--------|------|
| **JDBC** | `AgJdbcTemplate` | MySQL, Oracle, SQL Server, PostgreSQL, GaussDB, Access |
| **API** | `AgApiTemplate`, `AgApiUpTemplate` | HTTP/HTTPS 接口 |
| **SAP** | `AgSapRFCTemplate` | SAP RFC 远程调用 |
| **Kafka** | `AgKafkaTemplate` | Kafka 消息队列 |
| **Pulsar** | `AgPulsarTemplate` | Apache Pulsar 消息队列 |
| **MQTT** | `AgMqttTemplate` | MQTT 物联网协议 |

---

## 4. 包结构层级

```
com.bsi.md.agent/
├── controller/     (2)   ← Web控制器
├── service/        (6)   ← 业务服务
├── repository/     (7)   ← JPA数据访问
├── entity/         (13)  ← 实体类
│   ├── dto/        (8)   ← 数据传输对象
│   └── vo/         (3)   ← 视图对象
├── engine/         (24)  ← 核心引擎 ★
│   ├── factory/          ← 引擎工厂
│   ├── integration/      ← 集成引擎
│   │   ├── input/        ← 输入处理
│   │   ├── transform/    ← 转换处理
│   │   └── output/       ← 输出处理
│   ├── script/           ← 脚本引擎
│   ├── plugins/          ← 插件系统
│   └── pool/             ← 线程池
├── datasource/     (9)   ← 数据源模板
├── task/           (1)   ← 任务调度
├── log/            (2)   ← 日志系统
├── utils/          (7)   ← 工具类
├── aop/            (1)   ← AOP切面
├── config/         (2)   ← 配置类
├── sap/            (2)   ← SAP集成
├── pulsar/         (3)   ← Pulsar消息
└── email/          (2)   ← 邮件服务

com.bsi.utils/      (37)  ← 公共工具库
com.bsi.factory/          ← 工厂类
```

---

## 5. 目录结构

```
md-agent/
├── src/
│   ├── main/
│   │   ├── java/com/bsi/
│   │   │   ├── AgApplication.java          # Spring Boot启动类
│   │   │   ├── factory/                    # 工厂类
│   │   │   ├── utils/                      # 工具类 (37个)
│   │   │   └── md/agent/                   # 主项目包
│   │   ├── db/                             # 数据库初始化脚本
│   │   ├── resources/                      # 配置文件
│   │   └── docker/                         # Docker相关
│   └── test/
├── pom.xml                                 # Maven配置
└── lib/                                    # 第三方库 (sapjco3.jar等)
```

---

## 6. 核心类关系

### 主要实现类

| 接口 | 实现类 | 说明 |
|------|--------|------|
| `AgIntegrationEngine` | `AgCommonEngine` | 基础引擎实现 |
| | `AgJobEngine` | 定时任务专用引擎 |
| | `AgApiEngine` | API接口专用引擎 |
| `AgInput` | `AgCommonInput` | 脚本输入 |
| | `AgDBInput` | 数据库查询输入 |
| | `AgHttpInput` | HTTP请求输入 |
| `AgTransform` | `AgJsScriptTransform` | JS脚本数据转换 |
| `AgOutput` | `AgCommonOutput` | 脚本输出 |
| | `AgDBOutput` | 数据库写入输出 |
| | `AgHttpOutput` | HTTP发送输出 |
| `AgDataSourceTemplate` | `AgJdbcTemplate` | JDBC数据源模板 |
| | `AgApiTemplate` | API数据源模板 |
| | `AgSapRFCTemplate` | SAP RFC模板 |

### Context 上下文结构

```
Context {
  env: Map
    ├── config          # 全局参数
    ├── inputConfig     # 输入配置
    ├── outputConfig    # 输出配置
    ├── transformConfig # 转换配置
    ├── ag-data         # 数据内容 (在各阶段更新)
    ├── taskInfoLog     # 任务日志
    ├── ctx_task_id     # 任务ID
    └── ctx_warn_method_id # 告警方式
}
```

---

## 7. 数据流程图

```
┌──────────┐    ┌──────────────┐    ┌────────────┐    ┌──────────┐
│ 定时任务  │───→│ AgTaskBootStrap │───→│ Input阶段  │───→│ 数据读取  │
│AgTaskRun │    │   (启动器)    │    │(DB/HTTP/脚本)│    │          │
└──────────┘    └──────────────┘    └────────────┘    └────┬─────┘
                                                           ↓
┌──────────┐    ┌──────────────┐    ┌────────────┐    ┌──────────┐
│ 日志记录  │←───│ 插件后处理   │←───│ Output阶段 │←───│Transform │
│  & 告警  │    │PluginManager │    │(DB/HTTP/脚本)│    │ (JS脚本) │
└──────────┘    └──────────────┘    └────────────┘    └──────────┘
```

### 定时任务执行流程

1. `AgTaskRun.run()` 启动定时任务
2. 从缓存获取任务配置
3. `AgEngineFactory.getJobEngine()` 创建引擎
4. `AgTaskBootStrap.exec()` 启动执行
5. `engine.input(context)` - 从 DB/HTTP 读取数据
6. `engine.transform(context)` - JS 脚本转换数据
7. `engine.output(context)` - 写入 DB/HTTP
8. `AgAfterOutputPluginManager.runPlugins()` - 后处理插件
9. `AgTaskLogOutput.outputLog()` - 记录日志

---

## 8. 模块职责划分

| 模块 | 核心类 | 职责 |
|------|--------|------|
| **配置管理** | `AgConfig`, `AgConfigService`, `AgConfigController` | 集成配置的CRUD、缓存管理 |
| **任务调度** | `AgJob`, `AgJobService`, `AgTaskRun` | 定时任务注册、执行、参数跟踪 |
| **数据源管理** | `AgDataSource`, `AgDatasourceContainer` | 多数据源支持与管理 |
| **集成引擎** | `AgEngineFactory`, `AgIntegrationEngine`, `Context` | ETL流程编排、引擎创建 |
| **输入处理** | `AgInput`, `AgDBInput`, `AgHttpInput` | 从各种源读取数据 |
| **转换处理** | `AgTransform`, `AgJsScriptTransform` | 数据转换和映射 |
| **输出处理** | `AgOutput`, `AgDBOutput`, `AgHttpOutput` | 数据输出到目标系统 |
| **API代理** | `AgApiProxy`, `AgApiProxyService` | 实时API接口代理转发 |
| **日志告警** | `AgTaskLog`, `AgTaskLogOutput`, `AgWarnMethod` | 任务日志、告警通知 |
| **脚本引擎** | `AgScriptEngine`, `AgJavaScriptEngine` | JavaScript代码执行 |
| **插件系统** | `AgAfterOutputPlugin`, `AgAfterOutputPluginManager` | 输出后处理扩展 |

---

## 9. 设计模式

| 模式 | 使用位置 | 说明 |
|------|----------|------|
| **Factory (工厂)** | `AgEngineFactory` | 动态创建 Input/Transform/Output 实现 |
| **Strategy (策略)** | `AgIntegrationEngine` 及其实现 | 多种引擎实现策略 |
| **Template Method (模板方法)** | `AgCommonEngine` | 定义 ETL 流程模板 |
| **Singleton (单例)** | `AgDatasourceContainer` | 全局数据源管理 |
| **Observer/Plugin (观察者/插件)** | `AgAfterOutputPluginManager` | 输出后处理插件机制 |
| **Context (上下文)** | `Context` 类 | 跨层数据传递 |

---

## 10. 技术栈

### 框架和库
- Spring Boot 2.3.7
- Spring Data JPA
- Fastjson (JSON处理)
- Jackcess (Access数据库支持)
- SAP JCO3 (SAP RFC集成)
- Kafka Client
- Apache Pulsar
- Eclipse Paho MQTT
- Jackson XML

### 数据库支持
- MySQL
- Oracle
- SQL Server
- PostgreSQL
- GaussDB
- Microsoft Access

### 消息队列
- Apache Kafka
- Apache Pulsar
- MQTT
