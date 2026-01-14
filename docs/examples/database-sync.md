# 数据库同步示例

本示例演示如何使用 MD-Agent 实现跨数据库的数据同步。

## 场景说明

将 MySQL 数据库中的用户数据定期同步到 PostgreSQL 数据库。

**需求：**
- 每 5 分钟自动同步一次
- 只同步更新的数据（增量同步）
- 数据转换和清洗
- 同步失败时发送告警

## 前置准备

### 1. 配置数据源

**MySQL 源数据库配置：**

```yaml
# application.yml
spring:
  datasource:
    mysql-source:
      type: com.alibaba.druid.pool.DruidDataSource
      url: jdbc:mysql://source-db:3306/crm
      username: reader
      password: ${MYSQL_PASSWORD}
      driver-class-name: com.mysql.cj.jdbc.Driver
```

**PostgreSQL 目标数据库配置：**

```yaml
spring:
  datasource:
    postgres-target:
      type: com.alibaba.druid.pool.DruidDataSource
      url: jdbc:postgresql://target-db:5432/warehouse
      username: writer
      password: ${POSTGRES_PASSWORD}
      driver-class-name: org.postgresql.Driver
```

### 2. 准备表结构

**MySQL 源表：**
```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX idx_updated_at ON users(updated_at);
```

**PostgreSQL 目标表：**
```sql
CREATE TABLE dim_users (
    user_id INTEGER PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL,
    email_address VARCHAR(100),
    phone_number VARCHAR(20),
    user_status VARCHAR(20),
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    sync_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 配置同步任务

### 方式 1：通过 API 配置

```bash
curl -X POST http://localhost:8080/api/task \
  -H "Content-Type: application/json" \
  -d '{
    "name": "用户数据同步",
    "description": "MySQL to PostgreSQL 用户数据同步",
    "schedule": "0 */5 * * * ?",
    "enabled": true,
    "config": {
      "input": {
        "type": "JDBC",
        "datasourceId": "mysql-source",
        "sql": "SELECT id, username, email, phone, status, created_at, updated_at FROM users WHERE updated_at > ? ORDER BY updated_at",
        "parameters": ["${lastSyncTime}"]
      },
      "transform": {
        "type": "JAVASCRIPT",
        "script": "function transform(data) { return { user_id: data.id, user_name: data.username, email_address: data.email, phone_number: data.phone, user_status: data.status == 1 ? \"ACTIVE\" : \"INACTIVE\", created_date: data.created_at, updated_date: data.updated_at }; }"
      },
      "output": {
        "type": "JDBC",
        "datasourceId": "postgres-target",
        "table": "dim_users",
        "mode": "UPSERT",
        "keyColumns": ["user_id"]
      }
    }
  }'
```

### 方式 2：通过配置文件

创建任务配置文件 `config/tasks/user-sync.json`：

```json
{
  "name": "用户数据同步",
  "description": "MySQL to PostgreSQL 用户数据同步",
  "schedule": "0 */5 * * * ?",
  "enabled": true,
  "config": {
    "input": {
      "type": "JDBC",
      "datasourceId": "mysql-source",
      "sql": "SELECT * FROM users WHERE updated_at > ?",
      "parameters": ["${lastSyncTime}"]
    },
    "transform": {
      "type": "JAVASCRIPT",
      "scriptFile": "scripts/user-transform.js"
    },
    "output": {
      "type": "JDBC",
      "datasourceId": "postgres-target",
      "table": "dim_users",
      "mode": "UPSERT",
      "keyColumns": ["user_id"]
    },
    "errorHandling": {
      "retry": 3,
      "retryDelay": 5000,
      "onError": "ALERT"
    }
  }
}
```

## 数据转换脚本

创建 `scripts/user-transform.js`：

```javascript
/**
 * 用户数据转换脚本
 * 输入：MySQL users 表记录
 * 输出：PostgreSQL dim_users 表记录
 */

function transform(sourceData) {
    // 记录日志
    logger.info("转换用户数据: " + sourceData.id);

    // 数据验证
    if (!sourceData.id || !sourceData.username) {
        logger.warn("跳过无效数据: " + JSON.stringify(sourceData));
        return null; // 返回 null 会跳过该记录
    }

    // 转换数据结构
    var targetData = {
        user_id: sourceData.id,
        user_name: sourceData.username,
        email_address: sourceData.email || "",
        phone_number: formatPhoneNumber(sourceData.phone),
        user_status: mapUserStatus(sourceData.status),
        created_date: sourceData.created_at,
        updated_date: sourceData.updated_at
    };

    // 数据清洗：移除邮箱中的空格
    if (targetData.email_address) {
        targetData.email_address = targetData.email_address.trim();
    }

    return targetData;
}

/**
 * 格式化电话号码
 */
function formatPhoneNumber(phone) {
    if (!phone) return "";

    // 移除所有非数字字符
    var cleaned = phone.replace(/\D/g, '');

    // 格式化为 xxx-xxxx-xxxx
    if (cleaned.length === 11) {
        return cleaned.substring(0, 3) + "-" +
               cleaned.substring(3, 7) + "-" +
               cleaned.substring(7);
    }

    return phone;
}

/**
 * 映射用户状态
 */
function mapUserStatus(status) {
    switch (status) {
        case 1:
            return "ACTIVE";
        case 0:
            return "INACTIVE";
        case 2:
            return "SUSPENDED";
        default:
            return "UNKNOWN";
    }
}

// 批量处理支持
function transformBatch(dataList) {
    var results = [];

    for (var i = 0; i < dataList.length; i++) {
        var transformed = transform(dataList[i]);
        if (transformed !== null) {
            results.push(transformed);
        }
    }

    logger.info("批量转换完成: " + results.length + "/" + dataList.length);

    return results;
}

// 单条处理
transform(data);
```

## 高级功能

### 1. 增量同步

使用时间戳跟踪：

```javascript
// 在转换脚本中使用上次同步时间
function getIncrementalData() {
    var lastSync = context.get("lastSyncTime") || "1970-01-01 00:00:00";

    logger.info("增量同步，上次同步时间: " + lastSync);

    // SQL 查询会使用这个参数
    return lastSync;
}
```

### 2. 数据验证

```javascript
function validate(data) {
    var errors = [];

    // 必填字段检查
    if (!data.username) {
        errors.push("username 不能为空");
    }

    // 邮箱格式验证
    if (data.email && !isValidEmail(data.email)) {
        errors.push("邮箱格式不正确: " + data.email);
    }

    // 电话号码验证
    if (data.phone && data.phone.length < 10) {
        errors.push("电话号码太短");
    }

    if (errors.length > 0) {
        logger.warn("数据验证失败: " + errors.join(", "));
        return false;
    }

    return true;
}

function isValidEmail(email) {
    var regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
}

function transform(data) {
    if (!validate(data)) {
        return null; // 跳过无效数据
    }

    // 转换逻辑...
}
```

### 3. 错误处理和重试

```json
{
  "config": {
    "errorHandling": {
      "retry": 3,
      "retryDelay": 5000,
      "retryStrategy": "EXPONENTIAL_BACKOFF",
      "onError": "CONTINUE",
      "errorThreshold": 10,
      "notification": {
        "type": "EMAIL",
        "recipients": ["admin@example.com"],
        "template": "sync-error-alert"
      }
    }
  }
}
```

### 4. 性能优化

```json
{
  "config": {
    "performance": {
      "batchSize": 1000,
      "parallelism": 4,
      "fetchSize": 500,
      "commitInterval": 100
    }
  }
}
```

## 监控和告警

### 1. 配置邮件告警

```yaml
ag:
  email:
    enabled: true
    host: smtp.example.com
    port: 587
    username: ${EMAIL_USERNAME}
    password: ${EMAIL_PASSWORD}
    from: noreply@example.com
```

### 2. 查看同步状态

```bash
# 获取任务状态
curl http://localhost:8080/api/task/user-sync/status

# 查看执行历史
curl http://localhost:8080/api/task/user-sync/history?page=1&size=10

# 查看最近的错误
curl http://localhost:8080/api/task/user-sync/errors
```

### 3. 监控指标

```bash
# 查看同步性能指标
curl http://localhost:8080/actuator/metrics/task.user-sync.records-processed
curl http://localhost:8080/actuator/metrics/task.user-sync.duration
curl http://localhost:8080/actuator/metrics/task.user-sync.error-rate
```

## 完整示例：复杂数据同步

### 场景：订单数据同步

**需求：**
- 同步订单及订单明细（一对多关系）
- 计算订单总金额
- 关联商品信息
- 处理删除的订单

```javascript
/**
 * 订单数据同步转换脚本
 */

// 导入 Java 工具类
var BigDecimal = Java.type('java.math.BigDecimal');

function transformOrder(order) {
    logger.info("处理订单: " + order.order_id);

    // 查询订单明细
    var items = queryOrderItems(order.order_id);

    // 计算总金额
    var totalAmount = calculateTotalAmount(items);

    // 转换订单数据
    var result = {
        order_id: order.order_id,
        order_no: order.order_no,
        customer_id: order.customer_id,
        total_amount: totalAmount,
        item_count: items.length,
        status: order.status,
        created_at: order.created_at,
        items: items.map(transformOrderItem)
    };

    return result;
}

function queryOrderItems(orderId) {
    // 使用 JDBC 查询订单明细
    var sql = "SELECT * FROM order_items WHERE order_id = ?";
    return jdbcTemplate.queryForList(sql, orderId);
}

function calculateTotalAmount(items) {
    var total = new BigDecimal("0");

    for (var i = 0; i < items.length; i++) {
        var itemTotal = new BigDecimal(items[i].price)
            .multiply(new BigDecimal(items[i].quantity));
        total = total.add(itemTotal);
    }

    return total.doubleValue();
}

function transformOrderItem(item) {
    return {
        item_id: item.item_id,
        product_id: item.product_id,
        product_name: getProductName(item.product_id),
        quantity: item.quantity,
        price: item.price,
        subtotal: item.price * item.quantity
    };
}

function getProductName(productId) {
    // 从缓存或数据库获取商品名称
    var cache = context.get("productCache") || {};

    if (!cache[productId]) {
        var sql = "SELECT name FROM products WHERE id = ?";
        var result = jdbcTemplate.queryForObject(sql, [productId]);
        cache[productId] = result.name;
        context.set("productCache", cache);
    }

    return cache[productId];
}

transformOrder(data);
```

## 测试

### 1. 单元测试脚本

```bash
# 测试转换脚本
curl -X POST http://localhost:8080/api/script/test \
  -H "Content-Type: application/json" \
  -d '{
    "script": "function transform(data) { return data; }",
    "testData": {
      "id": 1,
      "username": "test",
      "email": "test@example.com"
    }
  }'
```

### 2. 干运行模式

```bash
# 执行任务但不写入数据库
curl -X POST http://localhost:8080/api/task/user-sync/execute?dryRun=true
```

## 故障排查

### 常见问题

**1. 数据未同步**
- 检查源数据库查询条件
- 验证时间戳字段
- 查看任务日志

**2. 性能慢**
- 添加数据库索引
- 调整批处理大小
- 增加并行度

**3. 数据重复**
- 检查 UPSERT 配置
- 验证主键设置
- 查看冲突处理策略

## 下一步

- 了解 [Kafka 集成](kafka-integration.md)
- 查看 [脚本开发指南](../guides/script-development.md)
- 阅读 [性能优化](../guides/performance.md)
