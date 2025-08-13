# 数字餐票系统 API 接口文档

## 📋 目录
- [1. 用户管理接口](#1-用户管理接口)
- [2. 订单管理接口](#2-订单管理接口)
- [3. 餐票管理接口](#3-餐票管理接口)
- [4. 部门管理接口](#4-部门管理接口)
- [5. 食堂管理接口](#5-食堂管理接口)
- [6. 系统功能接口](#6-系统功能接口)

## 🌐 基础信息

### 服务器地址
- **开发环境**: `http://localhost:8080`
- **生产环境**: `https://api.mealticket.com`

### 通用响应格式
```json
{
  "code": "200",
  "msg": "操作成功",
  "data": {}
}
```

### 状态码说明
- `200`: 成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 禁止访问
- `404`: 资源不存在
- `500`: 服务器内部错误

---

## 1. 用户管理接口

### 1.1 获取所有用户
- **接口**: `GET /api/users`
- **描述**: 获取系统中所有用户列表
- **请求参数**: 无
- **响应示例**:
```json
{
  "code": "200",
  "msg": "获取用户列表成功",
  "data": [
    {
      "userId": "USER001",
      "userName": "admin",
      "roleId": "ROLE001",
      "agenciesId": "DEPT001",
      "fullName": "管理员",
      "status": 1,
      "phoneNumber": "13800138000",
      "fkCreator": "system",
      "createDatetime": "20250730120000",
      "createIP": "127.0.0.1"
    }
  ]
}
```

### 1.2 创建用户
- **接口**: `POST /api/users`
- **描述**: 创建新用户
- **请求体**:
```json
{
  "userId": "USER002",
  "userName": "testuser",
  "roleId": "ROLE001",
  "agenciesId": "DEPT001",
  "fullName": "测试用户",
  "password": "123456",
  "status": 1,
  "phoneNumber": "13900139000"
}
```
- **响应示例**:
```json
{
  "code": "200",
  "msg": "用户创建成功",
  "data": {
    "userId": "USER002",
    "userName": "testuser",
    "fullName": "测试用户",
    "status": 1
  }
}
```

### 1.3 查询用户
- **接口**: `GET /api/users/{id}`
- **描述**: 根据用户ID查询用户信息
- **路径参数**:
  - `id`: 用户ID
- **响应示例**: 同创建用户响应

### 1.4 删除用户
- **接口**: `DELETE /api/users/{id}`
- **描述**: 删除指定用户
- **路径参数**:
  - `id`: 用户ID
- **响应示例**:
```json
{
  "code": "200",
  "msg": "用户删除成功",
  "data": null
}
```

### 1.5 用户统计信息
- **接口**: `GET /api/users/{id}/statistics`
- **描述**: 获取用户统计信息
- **路径参数**:
  - `id`: 用户ID
- **响应示例**:
```json
{
  "code": "200",
  "msg": "获取用户统计成功",
  "data": {
    "totalOrders": 15,
    "totalTickets": 50,
    "pendingOrders": 2,
    "completedOrders": 13
  }
}
```

---

## 2. 订单管理接口

### 2.1 获取所有订单
- **接口**: `GET /api/orders`
- **描述**: 获取系统中所有订单
- **请求参数**: 无
- **响应示例**:
```json
{
  "code": "200",
  "msg": "获取订单列表成功",
  "data": [
    {
      "orderId": "ORDER001",
      "orderNumber": "ORD001",
      "agenciesId": "DEPT001",
      "userId": "USER001",
      "status": 1,
      "creator": "USER001",
      "createDatetime": "20250730120000"
    }
  ]
}
```

### 2.2 分页查询订单
- **接口**: `GET /api/orders/page`
- **描述**: 分页查询订单列表
- **查询参数**:
  - `page`: 页码（从0开始）
  - `size`: 每页大小
  - `sortBy`: 排序字段（默认：createDatetime）
  - `sortDir`: 排序方向（asc/desc，默认：desc）
- **响应示例**:
```json
{
  "code": "200",
  "msg": "分页查询订单成功",
  "data": {
    "content": [...],
    "page": 0,
    "size": 10,
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  }
}
```

### 2.3 按用户分页查询订单
- **接口**: `GET /api/orders/page/user/{userId}`
- **描述**: 查询指定用户的订单
- **路径参数**:
  - `userId`: 用户ID
- **查询参数**: 同分页查询订单

### 2.4 按状态分页查询订单
- **接口**: `GET /api/orders/page/status/{status}`
- **描述**: 查询指定状态的订单
- **路径参数**:
  - `status`: 订单状态（0=待审核，1=已通过，2=已拒绝）
- **查询参数**: 同分页查询订单

### 2.5 开始订单草稿
- **接口**: `POST /api/orders/start`
- **描述**: 创建订单草稿
- **请求体**:
```json
{
  "userId": "USER001",
  "agenciesId": "DEPT001",
  "orderItems": [
    {
      "typeId": "TYPE001",
      "amount": 5
    }
  ]
}
```
- **响应示例**:
```json
{
  "code": "200",
  "msg": "订单草稿创建成功",
  "data": {
    "draftId": "DRAFT_USER001_1753856416625",
    "userId": "USER001",
    "agenciesId": "DEPT001",
    "status": "draft",
    "orderItems": [
      {
        "typeId": "TYPE001",
        "amount": 5,
        "typeName": "早餐券"
      }
    ]
  }
}
```

### 2.6 保存草稿
- **接口**: `PUT /api/orders/draft/{draftId}/save`
- **描述**: 保存订单草稿
- **路径参数**:
  - `draftId`: 草稿ID
- **请求体**:
```json
{
  "orderItems": [
    {
      "typeId": "TYPE001",
      "amount": 3
    },
    {
      "typeId": "TYPE002",
      "amount": 2
    }
  ]
}
```
- **响应示例**:
```json
{
  "code": "200",
  "msg": "草稿保存成功",
  "data": {
    "draftId": "DRAFT_USER001_1753856416625",
    "status": "saved"
  }
}
```

### 2.7 提交订单
- **接口**: `POST /api/orders/draft/{draftId}/submit`
- **描述**: 将草稿提交为正式订单
- **路径参数**:
  - `draftId`: 草稿ID
- **响应示例**:
```json
{
  "code": "200",
  "msg": "订单提交成功",
  "data": {
    "orderId": "ORDER123",
    "orderNumber": "ORD20250730001",
    "status": 0,
    "totalAmount": 5,
    "submitTime": "20250730120000"
  }
}
```

### 2.8 查询订单详情
- **接口**: `GET /api/orders/{id}`
- **描述**: 查询订单基本信息
- **路径参数**:
  - `id`: 订单ID

### 2.9 查询订单详细信息
- **接口**: `GET /api/orders/{id}/details`
- **描述**: 查询订单详细信息（包含订单项）
- **路径参数**:
  - `id`: 订单ID

### 2.10 删除订单
- **接口**: `DELETE /api/orders/{id}`
- **描述**: 删除指定订单
- **路径参数**:
  - `id`: 订单ID

---

## 3. 餐票管理接口

### 3.1 获取所有餐票
- **接口**: `GET /api/tickets/selectAll`
- **描述**: 获取系统中所有餐票
- **请求参数**: 无
- **响应示例**:
```json
{
  "code": "200",
  "msg": "获取餐票列表成功",
  "data": [
    {
      "tickerId": "TICKET001",
      "typeID": "TYPE001",
      "stallId": "STALL001",
      "usedId": "USER001",
      "orderId": "ORDER001",
      "status": "0",
      "createDatetime": "20250730120000",
      "expirationTime": "20250731"
    }
  ]
}
```

### 3.2 批量创建餐票
- **接口**: `POST /api/tickets/create`
- **描述**: 根据订单批量创建餐票
- **请求体**:
```json
{
  "orderId": "ORDER001",
  "typeId": "TYPE001",
  "quantity": 10,
  "stallId": "STALL001"
}
```
- **响应示例**:
```json
{
  "code": "200",
  "msg": "餐票创建成功",
  "data": {
    "createdCount": 10,
    "ticketIds": [
      "TICKET001",
      "TICKET002",
      "..."
    ],
    "orderId": "ORDER001",
    "expirationTime": "20250731"
  }
}
```

### 3.3 查询餐票
- **接口**: `GET /api/tickets/{id}`
- **描述**: 查询指定餐票信息
- **路径参数**:
  - `id`: 餐票ID
- **响应示例**:
```json
{
  "code": "200",
  "msg": "获取餐票成功",
  "data": {
    "tickerId": "TICKET001",
    "typeID": "TYPE001",
    "typeName": "早餐券",
    "stallId": "STALL001",
    "stallName": "第一档口",
    "usedId": "USER001",
    "status": "0",
    "useTime": null,
    "checkTime": null,
    "expirationTime": "20250731"
  }
}
```

### 3.4 核销餐票
- **接口**: `POST /api/tickets/{id}/cancel`
- **描述**: 核销指定餐票
- **路径参数**:
  - `id`: 餐票ID
- **查询参数**:
  - `checkId`: 核销人ID
- **响应示例**:
```json
{
  "code": "200",
  "msg": "餐票核销成功",
  "data": {
    "tickerId": "TICKET001",
    "status": "1",
    "checkId": "USER002",
    "checkTime": "20250730130000",
    "useTime": "20250730130000"
  }
}
```

---

## 4. 部门管理接口

### 4.1 获取所有部门
- **接口**: `GET /api/agencies`
- **描述**: 获取系统中所有部门
- **请求参数**: 无

### 4.2 创建部门
- **接口**: `POST /api/agencies`
- **描述**: 创建新部门
- **请求体**:
```json
{
  "agenciesId": "DEPT005",
  "agenciesName": "新部门",
  "status": 1,
  "fkCreator": "system",
  "createDatetime": "20250730120000",
  "createIP": "127.0.0.1"
}
```

### 4.3 查询部门
- **接口**: `GET /api/agencies/{id}`
- **描述**: 查询指定部门信息
- **路径参数**:
  - `id`: 部门ID

### 4.4 删除部门
- **接口**: `DELETE /api/agencies/{id}`
- **描述**: 删除指定部门
- **路径参数**:
  - `id`: 部门ID

---

## 5. 食堂管理接口

### 5.1 获取所有食堂
- **接口**: `GET /canteen`
- **描述**: 获取系统中所有食堂
- **请求参数**: 无

### 5.2 创建食堂
- **接口**: `POST /canteen`
- **描述**: 创建新食堂
- **请求体**:
```json
{
  "canteenId": "CANTEEN005",
  "canteenName": "新食堂",
  "status": 1,
  "fkCreator": "system",
  "createDatetime": "20250730120000",
  "createIp": "127.0.0.1"
}
```

### 5.3 查询食堂
- **接口**: `GET /canteen/{id}`
- **描述**: 查询指定食堂信息
- **路径参数**:
  - `id`: 食堂ID

### 5.4 更新食堂
- **接口**: `PUT /canteen`
- **描述**: 更新食堂信息
- **请求体**: 同创建食堂

### 5.5 删除食堂
- **接口**: `DELETE /canteen/{id}`
- **描述**: 删除指定食堂
- **路径参数**:
  - `id`: 食堂ID

---

## 6. 系统功能接口

### 6.1 健康检查
- **接口**: `GET /api/database-test/health`
- **描述**: 检查系统健康状态
- **请求参数**: 无

### 6.2 数据库连接测试
- **接口**: `GET /api/database-test/connection`
- **描述**: 测试数据库连接状态
- **请求参数**: 无

### 6.3 数据库信息
- **接口**: `GET /api/database-test/info`
- **描述**: 获取数据库基本信息
- **请求参数**: 无

---

## 📝 数据模型

### 用户状态
- `0`: 禁用
- `1`: 启用

### 订单状态
- `0`: 待审核
- `1`: 已通过
- `2`: 已拒绝

### 餐票状态
- `0`: 未使用
- `1`: 已核销
- `2`: 已过期

---

## 🧪 接口测试示例

### 使用curl测试

#### 创建用户
```bash
curl -X POST "http://localhost:8080/api/users" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "TEST001",
    "userName": "testuser",
    "roleId": "ROLE001",
    "agenciesId": "DEPT001",
    "fullName": "测试用户",
    "password": "123456",
    "status": 1,
    "phoneNumber": "13900139000"
  }'
```

#### 分页查询订单
```bash
curl -X GET "http://localhost:8080/api/orders/page?page=0&size=10&sortBy=createDatetime&sortDir=desc" \
  -H "Content-Type: application/json"
```

#### 创建订单草稿
```bash
curl -X POST "http://localhost:8080/api/orders/start" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "USER001",
    "agenciesId": "DEPT001",
    "orderItems": [
      {
        "typeId": "TYPE001",
        "amount": 5
      }
    ]
  }'
```

### 使用JavaScript测试

#### 基础请求封装
```javascript
const API_BASE = 'http://localhost:8080';

async function apiRequest(method, endpoint, data = null) {
  const config = {
    method,
    headers: {
      'Content-Type': 'application/json',
    },
  };

  if (data) {
    config.body = JSON.stringify(data);
  }

  const response = await fetch(`${API_BASE}${endpoint}`, config);
  return await response.json();
}

// 使用示例
const users = await apiRequest('GET', '/api/users');
console.log(users);
```

#### 创建用户示例
```javascript
const newUser = {
  userId: 'TEST002',
  userName: 'testuser2',
  roleId: 'ROLE001',
  agenciesId: 'DEPT001',
  fullName: '测试用户2',
  password: '123456',
  status: 1,
  phoneNumber: '13900139001'
};

const result = await apiRequest('POST', '/api/users', newUser);
console.log('用户创建结果:', result);
```

### 使用Postman测试

#### 环境变量设置
```json
{
  "baseUrl": "http://localhost:8080",
  "userId": "USER001",
  "orderId": "ORDER001"
}
```

#### 预请求脚本示例
```javascript
// 生成随机用户ID
pm.environment.set("randomUserId", "USER" + Math.floor(Math.random() * 1000));

// 设置当前时间戳
pm.environment.set("timestamp", new Date().toISOString().slice(0, 19).replace(/[-:]/g, '').replace('T', ''));
```

#### 测试脚本示例
```javascript
// 验证响应状态
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

// 验证响应格式
pm.test("Response has correct format", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('code');
    pm.expect(jsonData).to.have.property('msg');
    pm.expect(jsonData).to.have.property('data');
});

// 保存响应数据
pm.test("Save response data", function () {
    const jsonData = pm.response.json();
    if (jsonData.data && jsonData.data.userId) {
        pm.environment.set("createdUserId", jsonData.data.userId);
    }
});
```

## 🔧 错误处理

### 常见错误响应
```json
{
  "code": "400",
  "msg": "请求参数错误",
  "data": null
}
```

### 错误码对照表
| 错误码 | 说明 | 解决方案 |
|--------|------|----------|
| 400 | 请求参数错误 | 检查请求参数格式和必填字段 |
| 401 | 未授权访问 | 检查认证信息 |
| 403 | 权限不足 | 联系管理员分配权限 |
| 404 | 资源不存在 | 检查请求的资源ID是否正确 |
| 500 | 服务器内部错误 | 联系技术支持 |

## 🚀 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 启动步骤
1. 克隆项目代码
2. 配置数据库连接
3. 启动Redis服务
4. 运行 `mvn spring-boot:run`
5. 访问 `http://localhost:8080`

### 测试接口
使用提供的测试页面进行接口测试：
- 打开 `完整功能测试页面.html`
- 选择对应的功能模块
- 填写测试数据
- 点击测试按钮查看结果

## 📊 接口性能

### 响应时间标准
- 查询接口: < 200ms
- 创建接口: < 500ms
- 更新接口: < 300ms
- 删除接口: < 200ms

### 并发支持
- 最大并发用户: 1000
- 每秒请求数: 500 QPS
- 数据库连接池: 20

## 🔐 安全说明

### 认证方式
- 基于Session的认证
- 支持JWT Token认证
- API密钥认证

### 数据加密
- 密码使用BCrypt加密
- 敏感数据传输使用HTTPS
- 数据库连接加密

### 权限控制
- 基于角色的访问控制(RBAC)
- 接口级权限验证
- 数据级权限隔离

## 📈 监控与日志

### 系统监控
- 接口调用统计
- 响应时间监控
- 错误率统计
- 系统资源监控

### 日志记录
- 请求日志记录
- 错误日志记录
- 业务操作日志
- 安全审计日志

## 📋 最佳实践

### 接口调用建议

#### 1. 错误处理
```javascript
async function safeApiCall(endpoint, options) {
  try {
    const response = await fetch(endpoint, options);
    const data = await response.json();

    if (data.code !== '200') {
      throw new Error(data.msg || '请求失败');
    }

    return data.data;
  } catch (error) {
    console.error('API调用失败:', error);
    // 显示用户友好的错误信息
    showErrorMessage(error.message);
    throw error;
  }
}
```

#### 2. 分页查询优化
```javascript
// 推荐的分页查询方式
const loadOrders = async (page = 0, size = 20) => {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    sortBy: 'createDatetime',
    sortDir: 'desc'
  });

  return await safeApiCall(`/api/orders/page?${params}`);
};
```

#### 3. 批量操作
```javascript
// 批量创建餐票
const createTicketsInBatch = async (orders) => {
  const promises = orders.map(order =>
    safeApiCall('/api/tickets/create', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(order)
    })
  );

  return await Promise.allSettled(promises);
};
```

### 性能优化建议

#### 1. 请求缓存
- 对于不经常变化的数据（如部门列表、食堂列表），建议使用缓存
- 缓存时间建议：5-10分钟

#### 2. 分页加载
- 列表数据建议使用分页加载
- 每页数据量建议：10-50条

#### 3. 请求合并
- 避免在短时间内发送大量相似请求
- 使用防抖和节流技术

### 安全建议

#### 1. 数据验证
- 前端和后端都要进行数据验证
- 使用白名单验证而非黑名单

#### 2. 敏感信息处理
- 密码等敏感信息不要在URL中传递
- 使用HTTPS传输敏感数据

#### 3. 权限检查
- 每个接口调用前检查用户权限
- 实现细粒度的权限控制


