# JavaScript 脚本开发指南

MD-Agent 使用 JavaScript（Nashorn 引擎）进行数据转换。本指南介绍如何编写高效的数据转换脚本。

## 基础概念

### 脚本执行环境

- **引擎**：Nashorn JavaScript Engine（Java 8 内置）
- **语法**：ECMAScript 5.1 标准
- **执行上下文**：每个脚本运行在独立的上下文中

### 内置对象

脚本中可以访问以下对象：

```javascript
// 输入数据（从数据源读取）
var inputData = data;

// 日志输出
logger.info("处理数据：" + JSON.stringify(inputData));

// 工具类（Java 互操作）
var DateUtils = Java.type('com.bsi.utils.DateUtils');
var StringUtils = Java.type('org.apache.commons.lang3.StringUtils');
```

## 基本用法

### 简单数据转换

```javascript
// 输入：{ "name": "张三", "age": 25 }
// 输出：{ "userName": "张三", "userAge": 25, "status": "active" }

function transform(data) {
    return {
        userName: data.name,
        userAge: data.age,
        status: "active",
        processTime: new Date().toISOString()
    };
}

transform(data);
```

### 数组数据处理

```javascript
// 输入：[{ "id": 1, "name": "A" }, { "id": 2, "name": "B" }]
// 输出：过滤和转换后的数组

function transformList(dataList) {
    return dataList
        .filter(function(item) {
            return item.id > 0; // 过滤条件
        })
        .map(function(item) {
            return {
                identifier: "ID-" + item.id,
                label: item.name.toUpperCase(),
                timestamp: Date.now()
            };
        });
}

transformList(data);
```

### 条件逻辑

```javascript
function processOrder(order) {
    var result = {
        orderId: order.id,
        amount: order.amount
    };

    // 根据金额设置等级
    if (order.amount > 10000) {
        result.level = "VIP";
        result.discount = 0.8;
    } else if (order.amount > 5000) {
        result.level = "GOLD";
        result.discount = 0.9;
    } else {
        result.level = "NORMAL";
        result.discount = 1.0;
    }

    // 计算最终金额
    result.finalAmount = order.amount * result.discount;

    return result;
}

processOrder(data);
```

## 高级功能

### 日期处理

```javascript
function formatDate(data) {
    // 使用 Java 日期工具
    var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
    var Date = Java.type('java.util.Date');

    var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    return {
        id: data.id,
        createTime: sdf.format(new Date()),
        // 解析字符串日期
        parsedDate: sdf.parse(data.dateString)
    };
}

formatDate(data);
```

### 字符串处理

```javascript
function processText(data) {
    var StringUtils = Java.type('org.apache.commons.lang3.StringUtils');

    return {
        // 去除空格
        trimmed: StringUtils.trim(data.text),
        // 判断是否为空
        isEmpty: StringUtils.isEmpty(data.text),
        // 首字母大写
        capitalized: StringUtils.capitalize(data.text),
        // 分割字符串
        parts: data.text.split(",")
    };
}

processText(data);
```

### JSON 处理

```javascript
function parseJson(data) {
    // 解析 JSON 字符串
    var parsed = JSON.parse(data.jsonString);

    // 处理嵌套对象
    var result = {
        userId: parsed.user.id,
        userName: parsed.user.name,
        items: parsed.items.map(function(item) {
            return {
                code: item.code,
                quantity: item.qty
            };
        })
    };

    // 转换回 JSON 字符串
    return JSON.stringify(result);
}

parseJson(data);
```

### 调用 Java 方法

```javascript
function useJavaUtils(data) {
    // 导入 Java 类
    var AESUtils = Java.type('com.bsi.utils.AESUtils');
    var HttpUtils = Java.type('com.bsi.utils.HttpUtils');

    // 加密数据
    var encrypted = AESUtils.encrypt(data.sensitiveData, "mySecretKey");

    // HTTP 请求（同步）
    var response = HttpUtils.get("https://api.example.com/data/" + data.id);

    return {
        id: data.id,
        encrypted: encrypted,
        apiResponse: JSON.parse(response)
    };
}

useJavaUtils(data);
```

### 异常处理

```javascript
function safeTransform(data) {
    try {
        // 可能出错的操作
        var result = complexCalculation(data);

        return {
            success: true,
            data: result
        };
    } catch (e) {
        // 记录错误
        logger.error("转换失败: " + e.message);

        return {
            success: false,
            error: e.message,
            originalData: data
        };
    }
}

function complexCalculation(data) {
    if (!data || !data.value) {
        throw new Error("缺少必要字段: value");
    }
    return data.value * 2;
}

safeTransform(data);
```

## 性能优化

### 避免重复创建对象

```javascript
// 不推荐：每次都创建新对象
function transformSlow(data) {
    var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
    var sdf = new SimpleDateFormat("yyyy-MM-dd"); // 每次调用都创建
    return sdf.format(new Date());
}

// 推荐：复用对象
var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
var sdf = new SimpleDateFormat("yyyy-MM-dd"); // 创建一次

function transformFast(data) {
    return sdf.format(new Date()); // 复用
}
```

### 批量处理

```javascript
// 处理大量数据时，批量操作
function batchProcess(dataList) {
    var batchSize = 100;
    var results = [];

    for (var i = 0; i < dataList.length; i += batchSize) {
        var batch = dataList.slice(i, i + batchSize);

        // 批量处理
        var batchResults = processBatch(batch);
        results = results.concat(batchResults);
    }

    return results;
}

function processBatch(batch) {
    return batch.map(function(item) {
        return {
            id: item.id,
            processed: true
        };
    });
}
```

### 减少日志输出

```javascript
// 不推荐：频繁日志
function processWithLogging(dataList) {
    dataList.forEach(function(item) {
        logger.info("处理: " + item.id); // 过多日志
    });
}

// 推荐：汇总日志
function processWithSummary(dataList) {
    var count = 0;
    dataList.forEach(function(item) {
        // 处理逻辑
        count++;
    });
    logger.info("处理完成，共 " + count + " 条记录");
}
```

## 调试技巧

### 日志调试

```javascript
function debugTransform(data) {
    logger.debug("输入数据: " + JSON.stringify(data));

    var result = {
        id: data.id,
        value: data.value * 2
    };

    logger.debug("输出数据: " + JSON.stringify(result));

    return result;
}
```

### 断点调试

使用条件判断模拟断点：

```javascript
function debugWithBreakpoint(data) {
    if (data.id === 12345) {
        // 特定条件下输出详细信息
        logger.info("调试点 - 数据详情: " + JSON.stringify(data, null, 2));
    }

    return transform(data);
}
```

## 常见模式

### 数据验证

```javascript
function validateAndTransform(data) {
    // 验证必填字段
    var requiredFields = ['id', 'name', 'amount'];
    for (var i = 0; i < requiredFields.length; i++) {
        var field = requiredFields[i];
        if (!data[field]) {
            throw new Error("缺少必填字段: " + field);
        }
    }

    // 验证数据类型
    if (typeof data.amount !== 'number') {
        throw new Error("amount 必须是数字");
    }

    // 验证数据范围
    if (data.amount < 0) {
        throw new Error("amount 不能为负数");
    }

    // 验证通过，执行转换
    return transform(data);
}
```

### 数据聚合

```javascript
function aggregateData(dataList) {
    var summary = {
        total: 0,
        count: 0,
        byCategory: {}
    };

    dataList.forEach(function(item) {
        summary.total += item.amount;
        summary.count++;

        var category = item.category || 'unknown';
        if (!summary.byCategory[category]) {
            summary.byCategory[category] = {
                count: 0,
                total: 0
            };
        }
        summary.byCategory[category].count++;
        summary.byCategory[category].total += item.amount;
    });

    summary.average = summary.total / summary.count;

    return summary;
}
```

### 数据扁平化

```javascript
function flattenData(data) {
    return {
        id: data.id,
        name: data.name,
        // 嵌套对象扁平化
        addressCity: data.address.city,
        addressStreet: data.address.street,
        addressZipCode: data.address.zipCode,
        // 数组转字符串
        tags: data.tags.join(',')
    };
}
```

## 最佳实践

1. **函数化**：将复杂逻辑拆分为多个小函数
2. **验证输入**：始终验证输入数据的有效性
3. **错误处理**：使用 try-catch 处理可能的异常
4. **日志记录**：记录关键步骤和错误信息
5. **性能考虑**：避免在循环中创建对象，减少 Java 互操作
6. **代码注释**：添加清晰的注释说明逻辑
7. **测试验证**：在部署前充分测试脚本

## 限制和注意事项

1. **ECMAScript 5.1**：不支持 ES6+ 特性（箭头函数、let/const等）
2. **同步执行**：脚本执行是同步的，长时间运行会阻塞
3. **内存限制**：处理大量数据时注意内存使用
4. **安全性**：不要在脚本中硬编码敏感信息

## 下一步

- 查看 [数据库同步示例](../examples/database-sync.md)
- 了解 [任务配置](task-config.md)
- 阅读 [性能优化指南](performance.md)
