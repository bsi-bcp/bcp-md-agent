# Utils 工具类 API 参考手册

> md-agent 接口引擎工具类调用说明书
> 包路径: `com.bsi.utils`

---

## 目录

1. [数据库操作](#1-数据库操作)
   - [DBUtils](#dbutils---数据库操作工具类)
   - [MongoDBUtils](#mongodbutils---mongodb操作工具类)
   - [AccessUtils](#accessutils---access数据库工具类)
   - [DbfUtils](#dbfutils---dbf文件读取工具类)
2. [HTTP 请求](#2-http请求)
   - [HttpUtils](#httputils---http服务调用工具类)
   - [HttpRequestUtils](#httprequestutils---request请求工具类)
   - [WebSocketUtil](#websocketutil---websocket工具类)
3. [消息队列](#3-消息队列)
   - [KafkaUtils](#kafkautils---kafka工具类)
   - [PulsarUtils](#pulsarutils---pulsar工具类)
   - [MqttUtils](#mqttutils---mqtt工具类)
4. [数据转换](#4-数据转换)
   - [JSONUtils](#jsonutils---json工具类)
   - [XmlUtils](#xmlutils---xml工具类)
   - [XmlUtil_V2](#xmlutil_v2---xml工具类v2)
   - [XmlConverterUtils](#xmlconverterutils---xml转换工具类)
5. [加密解密](#5-加密解密)
   - [AESUtils](#aesutils---aes加解密工具类)
   - [RSAUtils](#rsautils---rsa加解密工具类)
   - [SHA256Utils](#sha256utils---sha256签名工具类)
   - [EncryptUtils](#encryptutils---加密工具类)
   - [DecryptUtils](#decryptutils---解密工具类)
   - [CipherUtils](#cipherutils---密码工具类)
   - [SignatureUtils](#signatureutils---签名工具类)
   - [DyEncryptUtils](#dyencryptutils---特定加解密工具类)
6. [文件操作](#6-文件操作)
   - [FileUtils](#fileutils---文件工具类)
   - [ExcelUtil](#excelutil---excel读取工具类)
   - [P42Utils](#p42utils---42号文工具类)
7. [日期时间](#7-日期时间)
   - [DateUtils](#dateutils---日期工具类)
8. [网络通信](#8-网络通信)
   - [SocketUtils](#socketutils---socket工具类)
   - [NetworkUtils](#networkutils---网络连通性工具类)
9. [缓存与线程](#9-缓存与线程)
   - [EHCacheUtils](#ehcacheutils---缓存工具类)
   - [ThreadUtils](#threadutils---线程工具类)
   - [AsyncTaskUtils](#asynctaskutils---异步任务工具类)
10. [其他工具](#10-其他工具)
    - [UUIDUtils](#uuidutils---uuid生成工具类)
    - [DataSourceUtils](#datasourceutils---数据源工具类)
    - [SapRFCUtils](#saprfcutils---sap-rfc工具类)
    - [ModbusUtils](#modbusutils---modbus工具类)
    - [DcClientUtils](#dcclientutils---ot数采工具类)

---

## 1. 数据库操作

### DBUtils - 数据库操作工具类

> SQL 语句执行工具类，支持对数据库的增删改查操作。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `queryForList` | sql, args, dataSourceId | Object (List) | 查询多条数据 |
| `queryListPage` | sql, args, currentPage, pageSize, dataSourceId | Object (JSON) | 分页查询 |
| `queryForObject` | sql, args, dataSourceId | Object (Map) | 查询单条数据 |
| `execute` | sql, args, dataSourceId | int | 执行增删改 |
| `executeBatch` | sql, args, dataSourceId | int[] | 批量执行增删改 |
| `startTransaction` | dataSourceId | void | 开始事务 |
| `commitTransaction` | dataSourceId | void | 提交事务 |
| `rollbackTransaction` | dataSourceId | void | 回滚事务 |

#### 调用示例

```javascript
// 查询多条数据
var sql = "SELECT * FROM users WHERE status = ?";
var result = DBUtils.queryForList(sql, [1], "ds_mysql_01");

// 分页查询
var pageResult = DBUtils.queryListPage(sql, [1], 1, 10, "ds_mysql_01");

// 查询单条数据
var user = DBUtils.queryForObject("SELECT * FROM users WHERE id = ?", [100], "ds_mysql_01");

// 插入数据
var count = DBUtils.execute("INSERT INTO users (name, email) VALUES (?, ?)", ["张三", "test@example.com"], "ds_mysql_01");

// 批量插入
var batchArgs = [["用户1", "user1@test.com"], ["用户2", "user2@test.com"]];
var results = DBUtils.executeBatch("INSERT INTO users (name, email) VALUES (?, ?)", batchArgs, "ds_mysql_01");

// 事务操作
DBUtils.startTransaction("ds_mysql_01");
try {
    DBUtils.execute("UPDATE accounts SET balance = balance - 100 WHERE id = ?", [1], "ds_mysql_01");
    DBUtils.execute("UPDATE accounts SET balance = balance + 100 WHERE id = ?", [2], "ds_mysql_01");
    DBUtils.commitTransaction("ds_mysql_01");
} catch (e) {
    DBUtils.rollbackTransaction("ds_mysql_01");
    throw e;
}
```

#### 注意事项
- 始终使用参数化查询，避免 SQL 注入
- 事务操作必须在 try-catch 块中执行
- `dataSourceId` 必须在系统中已配置

---

### MongoDBUtils - MongoDB操作工具类

> 用于操作系统自带的 MongoDB 数据库。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `batchInsert` | jsonString, collectionName | void | 批量插入数据 |
| `batchUpdate` | jsonString, collectionName | void | 批量更新数据 |
| `queryDocuments` | jsonFilter | List&lt;Document&gt; | 根据条件查询文档 |
| `queryAndAggregate` | jsonFilter | List&lt;Document&gt; | 聚合查询 |
| `getStats` | jsonFilter | Map | 统计指定时间段数据 |

#### 调用示例

```javascript
// 批量插入
var data = '[{"name":"张三","age":25},{"name":"李四","age":30}]';
MongoDBUtils.batchInsert(data, "users");

// 查询文档
var filter = {
    "collectionName": "users",
    "fields": "name,age",
    "filters": [
        {"field": "age", "op": "gte", "value": 18}
    ],
    "limit": 10,
    "sortField": "age",
    "sortOrder": "DESC"
};
var docs = MongoDBUtils.queryDocuments(filter);

// 聚合查询
var aggFilter = {
    "collectionName": "orders",
    "groupFields": "product_id",
    "returnFields": "product_id,total_sum",
    "filters": [{"field": "status", "op": "eq", "value": "completed"}],
    "aggregations": [{"field": "amount", "function": "sum"}]
};
var result = MongoDBUtils.queryAndAggregate(aggFilter);
```

#### 过滤条件操作符
| 操作符 | 说明 |
|--------|------|
| `eq` | 等于 |
| `ne` | 不等于 |
| `gt` | 大于 |
| `gte` | 大于等于 |
| `lt` | 小于 |
| `lte` | 小于等于 |
| `in` | 在列表中 |
| `nin` | 不在列表中 |
| `exists` | 字段存在 |
| `regex` | 正则匹配 |

---

### AccessUtils - Access数据库工具类

> 微软 Access 数据库操作工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `query` | filePath, sql, password | Object (List) | 查询 Access 数据库 |

#### 调用示例

```javascript
// 查询 Access 数据库（无密码）
var result = AccessUtils.query("D:/data/test.mdb", "SELECT * FROM users", null);

// 查询 Access 数据库（有密码）
var result = AccessUtils.query("D:/data/test.mdb", "SELECT * FROM users", "123456");
```

---

### DbfUtils - DBF文件读取工具类

> 用于读取 DBF 格式文件。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `readDbf` | filePath, charSet, skipRecords | List&lt;Map&gt; | 读取 DBF 文件 |

#### 调用示例

```javascript
// 读取 DBF 文件
var data = DbfUtils.readDbf("D:/data/test.dbf", "GBK", 0);

// 跳过前10条记录
var data = DbfUtils.readDbf("D:/data/test.dbf", "UTF-8", 10);
```

---

## 2. HTTP请求

### HttpUtils - HTTP服务调用工具类

> HTTP/HTTPS 服务调用工具类，支持多种请求方式。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `post` | url, headers, body | AgHttpResult | POST JSON 请求 |
| `postForm` | url, headers, params | AgHttpResult | POST 表单请求 |
| `request` | method, url, headers, body | AgHttpResult | 通用请求（JSON） |
| `request2` | method, url, headers, params | AgHttpResult | 通用请求（表单） |
| `postByRestTemplate` | url, headers, body | AgHttpResult | RestTemplate POST |
| `requestByRestTemplate` | method, url, headers, body | AgHttpResult | RestTemplate 通用请求 |
| `requestByRestTemplateHttps` | method, url, headers, body | AgHttpResult | HTTPS 请求 |
| `postFormDataByRT` | url, headers, valueMap | AgHttpResult | FormData 请求 |
| `postForStream` | url, headers, body | AgHttpResult | 流式响应请求 |
| `sendRedirect` | url | void | 重定向 |

#### 调用示例

```javascript
// POST JSON 请求
var headers = {"Authorization": "Bearer token123"};
var body = '{"name":"张三","age":25}';
var result = HttpUtils.post("https://api.example.com/users", headers, body);
console.log(result.getCode());    // 状态码
console.log(result.getResult());  // 响应内容

// POST 表单请求
var params = {"username": "admin", "password": "123456"};
var result = HttpUtils.postForm("https://api.example.com/login", {}, params);

// GET 请求
var result = HttpUtils.request("GET", "https://api.example.com/users/1", {}, null);

// HTTPS 请求（忽略证书）
var result = HttpUtils.requestByRestTemplateHttps("POST", "https://api.example.com/data", headers, body);
```

#### AgHttpResult 属性
| 属性 | 类型 | 说明 |
|------|------|------|
| `code` | int | HTTP 状态码 |
| `result` | String | 响应内容 |
| `header` | Header[] | 响应头 |
| `byteResult` | byte[] | 二进制响应 |

---

### HttpRequestUtils - Request请求工具类

> 获取当前请求的相关信息。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getQueryParam` | - | String | 获取URL查询参数 |
| `getRequestHeaders` | - | Map | 获取请求头 |
| `getMethod` | - | String | 获取请求方法 |
| `getRequestBody` | - | String | 获取请求体 |
| `getRequestCookies` | - | String | 获取Cookies |
| `setResponseHeaders` | headers | void | 设置响应头 |
| `setCode` | code | void | 设置响应状态码 |

#### 调用示例

```javascript
// 获取请求信息
var queryString = HttpRequestUtils.getQueryParam();
var headers = HttpRequestUtils.getRequestHeaders();
var method = HttpRequestUtils.getMethod();
var body = HttpRequestUtils.getRequestBody();

// 设置响应
HttpRequestUtils.setCode(200);
HttpRequestUtils.setResponseHeaders({"X-Custom-Header": "value"});
```

---

### WebSocketUtil - WebSocket工具类

> WebSocket 客户端工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getWebSocketMessage` | url, time | String | 连接并接收消息 |

#### 调用示例

```javascript
// 连接 WebSocket 并接收30秒内的消息
var messages = WebSocketUtil.getWebSocketMessage("ws://localhost:8080/ws", "30000");
console.log(messages);  // JSON 数组格式的消息列表
```

---

## 3. 消息队列

### KafkaUtils - Kafka工具类

> Kafka 消息队列操作工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `poll` | dataSourceId, taskId, topic | Object | 拉取消息 |
| `poll` | dataSourceId, taskId, topic, timeOut | Object | 带超时拉取消息 |
| `poll` | dataSourceId, taskId, topic, from, to | Object | 按时间范围拉取 |
| `send` | dataSourceId, taskId, topic, key, msg | Object | 发送消息 |
| `commit` | dataSourceId, taskId | boolean | 手动提交 offset |

#### 调用示例

```javascript
var dsId = "kafka_ds_01";
var taskId = "task_001";

// 拉取消息
var messages = KafkaUtils.poll(dsId, taskId, "my-topic");

// 带超时拉取
var messages = KafkaUtils.poll(dsId, taskId, "my-topic", 5000);

// 发送消息
KafkaUtils.send(dsId, taskId, "my-topic", "key1", '{"data":"hello"}');

// 手动提交
KafkaUtils.commit(dsId, taskId);
```

---

### PulsarUtils - Pulsar工具类

> Apache Pulsar 消息队列操作工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `poll` | dataSourceId, taskId, topic, autoCommit | Object | 拉取消息 |
| `batchPoll` | dataSourceId, taskId, topic, receiverQueueSize, maxNumMessages, maxNumBytes, pullTimeout, ackTimeout | Object | 批量拉取 |
| `send` | dataSourceId, taskId, topic, key, msg | Object | 发送消息 |
| `commit` | dataSourceId, taskId, msgId | void | 确认消费(字符串ID) |
| `commitMsg` | dataSourceId, taskId, msgId | void | 确认消费(MessageId) |

#### 调用示例

```javascript
var dsId = "pulsar_ds_01";
var taskId = "task_001";

// 拉取消息（自动确认）
var msg = PulsarUtils.poll(dsId, taskId, "my-topic", true);

// 批量拉取
var messages = PulsarUtils.batchPoll(dsId, taskId, "my-topic", 1000, 100, 10485760, 3000, 5000);

// 发送消息
PulsarUtils.send(dsId, taskId, "my-topic", "key1", '{"data":"hello"}');

// 手动确认（使用消息ID字符串，格式 ledgerId:entryId:partitionIndex）
PulsarUtils.commit(dsId, taskId, "12345:67890:0");
```

---

### MqttUtils - MQTT工具类

> MQTT 消息发送工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `send` | dataSourceId, topic, msg, qos | void | 发布消息 |

#### 调用示例

```javascript
// 发布 MQTT 消息
// qos: 0-最多一次, 1-至少一次, 2-恰好一次
MqttUtils.send("mqtt_ds_01", "sensor/temperature", '{"value":25.5}', 1);
```

---

## 4. 数据转换

### JSONUtils - JSON工具类

> JSON 数据转换工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `toJson` | obj | String | 对象转 JSON 字符串 |
| `parseArray` | text | JSONArray | 字符串转 JSON 数组 |
| `parseObject` | text | JSONObject | 字符串转 JSON 对象 |
| `toMap` | json | Map | JSON 转 Map |

#### 调用示例

```javascript
// 对象转 JSON
var jsonStr = JSONUtils.toJson({name: "张三", age: 25});

// 解析 JSON 数组
var arr = JSONUtils.parseArray('[{"id":1},{"id":2}]');

// 解析 JSON 对象
var obj = JSONUtils.parseObject('{"name":"张三","age":25}');

// JSON 转 Map
var map = JSONUtils.toMap('{"key1":"value1","key2":"value2"}');
```

---

### XmlUtils - XML工具类

> XML 与 JSON 互转工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `toXml` | obj | String | 对象转 XML |
| `json2Xml` | json | String | JSON 转 XML |
| `json2Xml` | json, interceptNode | String | JSON 转 XML（指定根节点） |
| `jsonArray2Xml` | json | String | JSON 数组转 XML |
| `xml2json` | xml | String | XML 转 JSON |
| `xml2json` | xml, rootNode | String | XML 转 JSON（指定根节点） |
| `getTextByPath` | xml, path | String | 获取 XML 节点文本 |

#### 调用示例

```javascript
// JSON 转 XML
var xml = XmlUtils.json2Xml('{"user":{"name":"张三","age":25}}');

// XML 转 JSON
var json = XmlUtils.xml2json('<user><name>张三</name><age>25</age></user>');

// 获取 XML 节点文本
var name = XmlUtils.getTextByPath(xml, "//user/name");
```

---

### XmlConverterUtils - XML转换工具类

> 基于 Jackson 的 XML 转 JSON 工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `xml2JsonStr` | xml | String | XML 转 JSON 字符串 |

#### 调用示例

```javascript
var json = XmlConverterUtils.xml2JsonStr('<user><name>张三</name></user>');
```

---

## 5. 加密解密

### AESUtils - AES加解密工具类

> AES/CBC/PKCS5Padding 加解密工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `encrypt` | plainText, key, iv | String | AES 加密 |
| `decrypt` | cipherText, key, iv | String | AES 解密 |

#### 调用示例

```javascript
var key = "1234567890123456";  // 16字节密钥
var iv = "1234567890123456";   // 16字节向量

// 加密
var encrypted = AESUtils.encrypt("Hello World", key, iv);

// 解密
var decrypted = AESUtils.decrypt(encrypted, key, iv);
```

#### 注意事项
- key 和 iv 必须是 16 字节
- 返回 Base64 编码的密文

---

### RSAUtils - RSA加解密工具类

> RSA 非对称加解密工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `encrypt` | content, keyStr, isPrivate | String | RSA 加密 |
| `decrypt` | content, keyStr, isPrivate | String | RSA 解密 |
| `getKeys` | - | String | 生成密钥对 |

#### 调用示例

```javascript
// 生成密钥对
var keys = RSAUtils.getKeys();
var keyObj = JSON.parse(keys);
var publicKey = keyObj.publicKey;
var privateKey = keyObj.privateKey;

// 公钥加密
var encrypted = RSAUtils.encrypt("Hello World", publicKey, false);

// 私钥解密
var decrypted = RSAUtils.decrypt(encrypted, privateKey, true);
```

---

### SHA256Utils - SHA256签名工具类

> SHA256 及 HMAC 签名工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getSHA256` | str | String | SHA256 哈希 |
| `getSHA256` | str, digest | String | 指定算法哈希 |
| `generateSignature` | key, body, name | String | HMAC 签名 |
| `generateSignature_UTF` | key, body, name | String | HMAC 签名(UTF-8) |
| `hmacSHA256` | secret, message | String | HMAC-SHA256 |
| `genSign` | secret, timestamp | String | 飞书签名 |
| `byteArrayToHexString` | b | String | 字节数组转十六进制 |
| `base_64_decode_bytes` | bytes/string | byte[] | Base64 解码 |

#### 调用示例

```javascript
// SHA256 哈希
var hash = SHA256Utils.getSHA256("Hello World");

// HMAC-SHA256 签名
var signature = SHA256Utils.hmacSHA256("secretKey", "message");

// 飞书签名
var timestamp = System.currentTimeMillis() / 1000;
var sign = SHA256Utils.genSign("your_secret", timestamp);
```

---

### EncryptUtils - 加密工具类

> 支持多种加密算法的工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `encrypt` | type, key, data, config | String | 通用加密 |
| `symEncrypt` | strkey, src | String | AES 对称加密 |
| `pubEncrypt` | pubKey, src | String | RSA 公钥加密 |
| `encrypt_AES_GCM_NP` | secretKey, data, IVStr | String | AES/GCM 加密 |
| `encrypt_AES_CBC_PKCS5` | secretKey, data, IVStr | String | AES/CBC 加密 |
| `base64Encode` | v | String | Base64 编码 |

#### 支持的加密类型
| type | 说明 |
|------|------|
| `AES` | AES 对称加密 |
| `RSA` | RSA 公钥加密 |
| `AES/GCM/NoPadding` | AES GCM 模式 |
| `AES/CBC/PKCS5Padding` | AES CBC 模式 |

#### 调用示例

```javascript
// 通用加密接口
var config = {"iv": "1234567890123456"};
var encrypted = EncryptUtils.encrypt("AES/CBC/PKCS5Padding", "1234567890123456", "Hello", config);

// Base64 编码
var encoded = EncryptUtils.base64Encode("Hello World");
```

---

### DecryptUtils - 解密工具类

> 解密工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `decryptFromHWCloud` | text | String | 华为云解密 |
| `symDecrypt` | secretKeySpec, encryptByte, algorithmName | String | 对称解密 |
| `base64Decode` | decodeString | String | Base64 解码 |
| `getSecretKeySpec` | secretKeyBytes, algorithmName | SecretKeySpec | 获取密钥规格 |

#### 调用示例

```javascript
// Base64 解码
var decoded = DecryptUtils.base64Decode("SGVsbG8gV29ybGQ=");
```

---

### SignatureUtils - 签名工具类

> 国密 SM2/SM3 签名工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `sm3Signature` | body | String | SM3 摘要签名 |
| `sm2Signature` | privateKeyStr, dataStr | String | SM2 签名 |

#### 调用示例

```javascript
// SM3 签名
var hash = SignatureUtils.sm3Signature("Hello World");

// SM2 签名（需要私钥十六进制字符串）
var signature = SignatureUtils.sm2Signature(privateKeyHex, "data to sign");
```

---

### CipherUtils - 密码工具类

> 支持 AES/GCM 和 RSA/PSS 的加解密签名工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `encrypt` | data, secret | String | AES/GCM 加密 |
| `decrypt` | data, secret | String | AES/GCM 解密 |
| `sign` | data, privateKey | String | RSA/PSS 签名 |
| `sign` | data, path, type, password | String | 使用文件私钥签名 |
| `verify` | data, sign, publicKey | boolean | 验证签名 |
| `loadCER` | stream | X509Certificate | 加载证书 |
| `loadPrivateKey` | stream, type, password | PrivateKey | 加载私钥 |

#### 调用示例

```javascript
// AES/GCM 加密
var secret = "1234567890123456";  // 16字节密钥
var encrypted = CipherUtils.encrypt("Hello World", secret);

// AES/GCM 解密
var decrypted = CipherUtils.decrypt(encrypted, secret);

// 使用私钥文件签名
var signature = CipherUtils.sign("data", "/path/to/private.pem", "pem", "password");
```

---

## 6. 文件操作

### FileUtils - 文件工具类

> 文件读写工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `readFileToStr` | path, charsets | String | 读取文件为字符串 |
| `readFile` | path | List&lt;String&gt; | 按行读取文件(UTF-8) |
| `readFileByCharset` | path, charsets | List&lt;String&gt; | 按行读取文件 |
| `writeFile` | path, msg, append | boolean | 写入文件 |
| `getNewOrModifiedFiles` | path, lastTs, maxSize, exts | List&lt;String&gt; | 获取新增/修改的文件 |
| `getNewOrModifiedFilesRegex` | path, lastTs, maxSize, exts, regex | List&lt;String&gt; | 正则匹配获取文件 |

#### 调用示例

```javascript
// 读取文件
var content = FileUtils.readFileToStr("D:/data/test.txt", "UTF-8");
var lines = FileUtils.readFile("D:/data/test.txt");

// 写入文件
FileUtils.writeFile("D:/data/output.txt", "Hello World", false);  // 覆盖
FileUtils.writeFile("D:/data/output.txt", "New Line", true);      // 追加

// 获取最近修改的文件
var lastTs = System.currentTimeMillis() - 3600000;  // 1小时前
var files = FileUtils.getNewOrModifiedFiles("D:/data", lastTs, 10485760, ["txt", "csv"]);
```

---

### ExcelUtil - Excel读取工具类

> Excel 文件读取工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `readData` | filepath | List&lt;List&lt;JSONObject&gt;&gt; | 读取 Excel 所有工作表 |

#### 调用示例

```javascript
// 读取 Excel 文件
var sheetList = ExcelUtil.readData("D:/data/test.xlsx");

// 遍历工作表
for (var i = 0; i < sheetList.size(); i++) {
    var sheet = sheetList.get(i);
    for (var j = 0; j < sheet.size(); j++) {
        var row = sheet.get(j);
        console.log(row.get("1"));  // 第1列
        console.log(row.get("2"));  // 第2列
    }
}
```

#### 注意事项
- 返回的列名从 "1" 开始
- 支持 .xls 和 .xlsx 格式

---

### P42Utils - 42号文工具类

> 读取 42 号文格式文件的工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `readData` | filePath | List&lt;JSONObject&gt; | 读取文件(UTF-8) |
| `readData` | filePath, encoding | List&lt;JSONObject&gt; | 指定编码读取 |

#### 调用示例

```javascript
// 读取 42 号文格式文件
var data = P42Utils.readData("D:/data/p42file.txt");
var data = P42Utils.readData("D:/data/p42file.txt", "GBK");
```

#### 注意事项
- 行分隔符: `~`
- 字段分隔符: `;`
- 结束标记: `||`

---

## 7. 日期时间

### DateUtils - 日期工具类

> 日期时间处理工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `nowDate` | pattern | String | 获取当前时间字符串 |
| `preDayForNow` | day, pattern | String | 获取N天前的日期 |
| `preMinuteForNow` | minute, pattern | String | 获取N分钟前的时间 |
| `preSecondsForNow` | seconds, pattern | String | 获取N秒前的时间 |
| `getDateStrFromTime` | time, pattern | String | 毫秒数转日期字符串 |
| `getTimePreMinuteForNow` | minute | long | 获取N分钟前的毫秒数 |
| `getDate` | dateString, pattern | Date | 字符串转日期 |
| `toString` | date, pattern | String | 日期转字符串 |
| `toString` | date | String | 日期转字符串(默认格式) |
| `preDays` | date, preDays | Date | 获取N天前的日期 |
| `toDate` | time, pattern | Date | 字符串转日期 |

#### 调用示例

```javascript
// 获取当前时间
var now = DateUtils.nowDate("yyyy-MM-dd HH:mm:ss");

// 获取昨天
var yesterday = DateUtils.preDayForNow(1, "yyyy-MM-dd");

// 获取5分钟前
var fiveMinAgo = DateUtils.preMinuteForNow(5, "yyyy-MM-dd HH:mm:ss");

// 获取5分钟后（负数）
var fiveMinLater = DateUtils.preMinuteForNow(-5, "yyyy-MM-dd HH:mm:ss");

// 毫秒数转日期
var dateStr = DateUtils.getDateStrFromTime(1704067200000, "yyyy-MM-dd HH:mm:ss");
```

#### 常用格式
| 格式 | 示例 |
|------|------|
| `yyyy-MM-dd` | 2024-01-01 |
| `yyyy-MM-dd HH:mm:ss` | 2024-01-01 12:00:00 |
| `yyyy/MM/dd` | 2024/01/01 |
| `yyyyMMddHHmmss` | 20240101120000 |

---

## 8. 网络通信

### SocketUtils - Socket工具类

> Socket 客户端和服务端管理工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getClient` | key, ip, port | SocketClient | 获取/创建 Socket 客户端 |
| `createServer` | key, port, maxClient, protocol, callBack | SocketServerN | 创建 XCOM 协议服务器 |
| `getServer` | key | SocketServerN | 获取服务器实例 |
| `createSimpleServer` | key, port, maxClient | SimpleSocketServer | 创建简单 Socket 服务器 |
| `getSimpleServer` | key | SimpleSocketServer | 获取简单服务器实例 |

#### 调用示例

```javascript
// 获取 Socket 客户端
var client = SocketUtils.getClient("client_01", "192.168.1.100", 8080);

// 创建 Socket 服务器
var server = SocketUtils.createServer("server_01", 9090, 100, "xcom", true);

// 获取已创建的服务器
var server = SocketUtils.getServer("server_01");

// 创建简单 Socket 服务器
var simpleServer = SocketUtils.createSimpleServer("simple_01", 9091, 50);
```

---

### NetworkUtils - 网络连通性工具类

> 测试网络连通性的工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `isReachable` | localIp, remoteIp, port, timeout | boolean | 测试网络连通性 |
| `getReachableIP` | remoteIp, port | String | 获取可连通的本机 IP |

#### 调用示例

```javascript
// 测试网络连通性
var reachable = NetworkUtils.isReachable("192.168.1.10", "192.168.1.100", 3306, 5000);
if (reachable) {
    console.log("网络连通");
}

// 获取可以连接到目标的本机 IP
var localIp = NetworkUtils.getReachableIP("192.168.1.100", 8080);
```

---

## 9. 缓存与线程

### EHCacheUtils - 缓存工具类

> EHCache 缓存操作工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `put` | key, value | void | 存入缓存 |
| `put` | key, value, timeToLiveSeconds | void | 存入缓存（设置过期时间） |
| `put` | key, value, timeToLiveSeconds, timeToIdleSeconds | void | 存入缓存（设置过期和空闲时间） |
| `get` | key | Object | 获取缓存 |

#### 调用示例

```javascript
// 存入缓存
EHCacheUtils.put("user_001", '{"name":"张三"}');

// 存入缓存（60秒后过期）
EHCacheUtils.put("token_001", "abc123", 60);

// 存入缓存（60秒过期，30秒空闲过期）
EHCacheUtils.put("session_001", sessionData, 60, 30);

// 获取缓存
var data = EHCacheUtils.get("user_001");
```

---

### ThreadUtils - 线程工具类

> 线程相关工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `sleep` | millis | void | 线程休眠 |

#### 调用示例

```javascript
// 休眠 5 秒
ThreadUtils.sleep(5000);
```

---

### AsyncTaskUtils - 异步任务工具类

> 异步执行任务的工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `exec` | taskId, taskName, param | void | 异步执行任务（并发） |
| `singleExec` | taskId, taskName, param | void | 异步执行任务（单线程排队） |

#### 调用示例

```javascript
// 异步执行任务（可并发）
AsyncTaskUtils.exec("task_001", "数据同步任务", '{"param1":"value1"}');

// 异步执行任务（单线程排队）
AsyncTaskUtils.singleExec("task_002", "顺序处理任务", '{"param1":"value1"}');
```

---

## 10. 其他工具

### UUIDUtils - UUID生成工具类

> UUID 生成工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getUUID` | - | String | 生成标准 UUID |
| `getShortUUID` | - | String | 生成无横线 UUID |

#### 调用示例

```javascript
// 生成标准 UUID (带横线)
var uuid = UUIDUtils.getUUID();
// 示例: 550e8400-e29b-41d4-a716-446655440000

// 生成短 UUID (无横线)
var shortUuid = UUIDUtils.getShortUUID();
// 示例: 550e8400e29b41d4a716446655440000
```

---

### DataSourceUtils - 数据源工具类

> 数据源属性获取工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `getProp` | dsId | JSONObject | 获取数据源属性配置 |

#### 调用示例

```javascript
// 获取数据源配置
var props = DataSourceUtils.getProp("ds_mysql_01");
console.log(props.getString("url"));
console.log(props.getString("username"));
```

---

### SapRFCUtils - SAP RFC工具类

> SAP RFC 函数调用工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `execute` | functionName, params, dataSourceId | Object | 执行 SAP RFC 函数 |

#### 调用示例

```javascript
// 调用 SAP RFC 函数
var params = {
    "I_MATNR": "000000000000001234",  // 输入参数
    "I_WERKS": "1000"
};
var result = SapRFCUtils.execute("BAPI_MATERIAL_GET_DETAIL", params, "sap_ds_01");
```

---

### ModbusUtils - Modbus工具类

> 华为工业物联平台数据上报工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `sendMsg` | deviceId, msg | boolean | 向设备发送数据 |
| `reportSubDevicesProperties` | msg | boolean | 批量上报子设备数据 |

#### 调用示例

```javascript
// 向设备发送数据
var msg = '[{"serviceId":"Temperature","properties":{"value":25.5}}]';
ModbusUtils.sendMsg("device_001", msg);

// 批量上报
var batchMsg = '[{"device_id":"device_001","services":[{"service_id":"Temperature","properties":{"value":25.5},"event_time":"2024-01-01T12:00:00+08:00"}]}]';
ModbusUtils.reportSubDevicesProperties(batchMsg);
```

---

### DcClientUtils - OT数采工具类

> OT 数据采集点位上报工具类。

#### API 清单

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `reportPoints` | points | boolean | 上报点位数据 |

#### 调用示例

```javascript
// 上报点位数据
var points = {
    "point_001": 100,
    "point_002": 25.5,
    "point_003": "running"
};
var success = DcClientUtils.reportPoints(points);
```

---

## 附录：工具类汇总表

| 类别 | 工具类 | 主要功能 |
|------|--------|----------|
| 数据库 | DBUtils | 关系型数据库 CRUD |
| 数据库 | MongoDBUtils | MongoDB 操作 |
| 数据库 | AccessUtils | Access 数据库查询 |
| 数据库 | DbfUtils | DBF 文件读取 |
| HTTP | HttpUtils | HTTP 请求 |
| HTTP | HttpRequestUtils | 请求信息获取 |
| HTTP | WebSocketUtil | WebSocket 通信 |
| 消息队列 | KafkaUtils | Kafka 操作 |
| 消息队列 | PulsarUtils | Pulsar 操作 |
| 消息队列 | MqttUtils | MQTT 发布 |
| 数据转换 | JSONUtils | JSON 转换 |
| 数据转换 | XmlUtils | XML 转换 |
| 数据转换 | XmlUtil_V2 | XML 转换 V2 |
| 数据转换 | XmlConverterUtils | XML 转 JSON |
| 加密解密 | AESUtils | AES 加解密 |
| 加密解密 | RSAUtils | RSA 加解密 |
| 加密解密 | SHA256Utils | SHA256/HMAC 签名 |
| 加密解密 | EncryptUtils | 多算法加密 |
| 加密解密 | DecryptUtils | 解密工具 |
| 加密解密 | CipherUtils | AES-GCM/RSA-PSS |
| 加密解密 | SignatureUtils | SM2/SM3 国密签名 |
| 加密解密 | DyEncryptUtils | 特定系统加解密 |
| 文件 | FileUtils | 文件读写 |
| 文件 | ExcelUtil | Excel 读取 |
| 文件 | P42Utils | 42号文读取 |
| 日期 | DateUtils | 日期时间处理 |
| 网络 | SocketUtils | Socket 通信 |
| 网络 | NetworkUtils | 网络连通性测试 |
| 缓存/线程 | EHCacheUtils | 缓存操作 |
| 缓存/线程 | ThreadUtils | 线程休眠 |
| 缓存/线程 | AsyncTaskUtils | 异步任务执行 |
| 其他 | UUIDUtils | UUID 生成 |
| 其他 | DataSourceUtils | 数据源属性 |
| 其他 | SapRFCUtils | SAP RFC 调用 |
| 其他 | ModbusUtils | IoT 数据上报 |
| 其他 | DcClientUtils | OT 点位上报 |
