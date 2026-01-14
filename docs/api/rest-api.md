# REST API 文档

MD-Agent 提供 RESTful API 用于配置管理和任务控制。

## 基础信息

**Base URL**: `http://localhost:8080/api`

**认证**: 根据配置可能需要 API Key 或 JWT Token

**Content-Type**: `application/json`

## 数据源管理

### 获取所有数据源

```http
GET /api/datasource
```

**响应示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "1",
      "name": "mysql-prod",
      "type": "MYSQL",
      "url": "jdbc:mysql://localhost:3306/mydb",
      "status": "ACTIVE"
    }
  ]
}
```

### 获取单个数据源

```http
GET /api/datasource/{id}
```

**路径参数：**
- `id` (string): 数据源 ID

### 创建数据源

```http
POST /api/datasource
```

**请求体：**
```json
{
  "name": "mysql-prod",
  "type": "MYSQL",
  "url": "jdbc:mysql://localhost:3306/mydb",
  "username": "app_user",
  "password": "secret",
  "properties": {
    "maxActive": "20",
    "initialSize": "5"
  }
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "数据源创建成功",
  "data": {
    "id": "2",
    "name": "mysql-prod"
  }
}
```

### 更新数据源

```http
PUT /api/datasource/{id}
```

### 删除数据源

```http
DELETE /api/datasource/{id}
```

### 测试数据源连接

```http
POST /api/datasource/{id}/test
```

**响应示例：**
```json
{
  "code": 200,
  "message": "连接成功",
  "data": {
    "connected": true,
    "responseTime": 125
  }
}
```

## 任务管理

### 获取所有任务

```http
GET /api/task
```

**查询参数：**
- `status` (string, 可选): 任务状态 (RUNNING, STOPPED, ERROR)
- `page` (int, 可选): 页码，默认 1
- `size` (int, 可选): 每页数量，默认 20

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "total": 50,
    "items": [
      {
        "id": "task-001",
        "name": "数据同步任务",
        "status": "RUNNING",
        "schedule": "0 */5 * * * ?",
        "lastExecuteTime": "2026-01-14T10:30:00",
        "nextExecuteTime": "2026-01-14T10:35:00"
      }
    ]
  }
}
```

### 获取任务详情

```http
GET /api/task/{id}
```

### 创建任务

```http
POST /api/task
```

**请求体：**
```json
{
  "name": "数据同步任务",
  "description": "从 MySQL 同步到 PostgreSQL",
  "schedule": "0 */5 * * * ?",
  "enabled": true,
  "config": {
    "input": {
      "type": "JDBC",
      "datasourceId": "1",
      "sql": "SELECT * FROM users WHERE updated_at > ?"
    },
    "transform": {
      "type": "JAVASCRIPT",
      "script": "function transform(data) { return data; }"
    },
    "output": {
      "type": "JDBC",
      "datasourceId": "2",
      "table": "users"
    }
  }
}
```

### 更新任务

```http
PUT /api/task/{id}
```

### 删除任务

```http
DELETE /api/task/{id}
```

### 启动任务

```http
POST /api/task/{id}/start
```

### 停止任务

```http
POST /api/task/{id}/stop
```

### 立即执行任务

```http
POST /api/task/{id}/execute
```

**请求体（可选）：**
```json
{
  "parameters": {
    "startDate": "2026-01-01",
    "endDate": "2026-01-14"
  }
}
```

### 获取任务执行历史

```http
GET /api/task/{id}/history
```

**查询参数：**
- `startTime` (datetime, 可选): 开始时间
- `endTime` (datetime, 可选): 结束时间
- `status` (string, 可选): 执行状态
- `page` (int, 可选): 页码
- `size` (int, 可选): 每页数量

**响应示例：**
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "items": [
      {
        "id": "exec-001",
        "taskId": "task-001",
        "startTime": "2026-01-14T10:30:00",
        "endTime": "2026-01-14T10:30:15",
        "status": "SUCCESS",
        "processedRecords": 1500,
        "errorMessage": null
      }
    ]
  }
}
```

## 配置管理

### 获取系统配置

```http
GET /api/config
```

### 更新系统配置

```http
PUT /api/config
```

**请求体：**
```json
{
  "logging": {
    "level": "INFO"
  },
  "performance": {
    "threadPoolSize": 10
  }
}
```

## 监控接口

### 健康检查

```http
GET /actuator/health
```

**响应示例：**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "SQLite",
        "validationQuery": "SELECT 1"
      }
    }
  }
}
```

### 应用信息

```http
GET /actuator/info
```

### 性能指标

```http
GET /actuator/metrics
```

### 线程转储

```http
GET /actuator/threaddump
```

## 错误响应

所有 API 错误响应遵循统一格式：

```json
{
  "code": 400,
  "message": "请求参数错误",
  "error": "Missing required field: name",
  "timestamp": "2026-01-14T10:30:00"
}
```

**常见错误代码：**
- `400` - 请求参数错误
- `401` - 未授权
- `403` - 禁止访问
- `404` - 资源不存在
- `409` - 资源冲突
- `500` - 服务器内部错误

## 使用示例

### cURL 示例

```bash
# 创建数据源
curl -X POST http://localhost:8080/api/datasource \
  -H "Content-Type: application/json" \
  -d '{
    "name": "mysql-test",
    "type": "MYSQL",
    "url": "jdbc:mysql://localhost:3306/testdb",
    "username": "root",
    "password": "password"
  }'

# 获取任务列表
curl http://localhost:8080/api/task?status=RUNNING

# 启动任务
curl -X POST http://localhost:8080/api/task/task-001/start
```

### Python 示例

```python
import requests

BASE_URL = "http://localhost:8080/api"

# 创建数据源
response = requests.post(
    f"{BASE_URL}/datasource",
    json={
        "name": "mysql-prod",
        "type": "MYSQL",
        "url": "jdbc:mysql://localhost:3306/mydb",
        "username": "user",
        "password": "pass"
    }
)
print(response.json())

# 获取任务列表
response = requests.get(f"{BASE_URL}/task", params={"status": "RUNNING"})
tasks = response.json()["data"]["items"]

# 执行任务
for task in tasks:
    response = requests.post(f"{BASE_URL}/task/{task['id']}/execute")
    print(f"Task {task['name']}: {response.json()['message']}")
```

### JavaScript 示例

```javascript
const BASE_URL = 'http://localhost:8080/api';

// 创建数据源
async function createDatasource() {
    const response = await fetch(`${BASE_URL}/datasource`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name: 'mysql-prod',
            type: 'MYSQL',
            url: 'jdbc:mysql://localhost:3306/mydb',
            username: 'user',
            password: 'pass'
        })
    });
    return await response.json();
}

// 获取任务列表
async function getTasks() {
    const response = await fetch(`${BASE_URL}/task?status=RUNNING`);
    const data = await response.json();
    return data.data.items;
}
```

## 认证示例

如果 API 启用了认证：

```bash
# 使用 API Key
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/task

# 使用 JWT Token
curl -H "Authorization: Bearer your-jwt-token" \
  http://localhost:8080/api/task
```

## WebSocket 实时通知

MD-Agent 支持通过 WebSocket 接收实时任务状态更新：

```javascript
const ws = new WebSocket('ws://localhost:8080/ws/task-status');

ws.onmessage = (event) => {
    const message = JSON.parse(event.data);
    console.log('Task update:', message);
    // { taskId: 'task-001', status: 'RUNNING', progress: 50 }
};
```

## 下一步

- 查看 [配置指南](../guides/datasource-config.md)
- 了解 [任务配置](../guides/task-config.md)
- 参考 [使用示例](../examples/database-sync.md)
