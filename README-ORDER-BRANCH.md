# Order分支 - 首页餐票订单功能

## 🎯 功能概述

本分支实现了完整的首页餐票订单功能，包括：

### ✅ 后端功能
- **分页查询**：支持按页获取餐票订单数据
- **排序功能**：按createDatetime降序排序（最新订单在前）
- **DTO转换**：统一使用Orderandott DTO类进行数据转换
- **序号管理**：排序后自动分配连续序号
- **错误处理**：完善的异常处理和降级机制

### ✅ 前端功能
- **数据展示**：表格形式展示餐票订单列表
- **分页控件**：支持分页浏览
- **状态显示**：订单状态的友好显示
- **响应式设计**：适配不同屏幕尺寸

## 📊 API接口

### 分页接口
```
GET /api/orders/home-tickets/page?page=0&size=10
```

**响应格式：**
```json
{
  "code": "200",
  "msg": "分页查询首页餐票订单成功",
  "data": {
    "content": [
      {
        "sequence": 1,
        "orderNumber": "ORD0000030",
        "createDatetime": "20250731",
        "totalQuantity": 50,
        "usedQuantity": 0,
        "status": 2
      }
    ],
    "page": 0,
    "size": 10,
    "totalElements": 30,
    "totalPages": 3,
    "first": true,
    "last": false
  }
}
```

### 无分页接口
```
GET /api/orders/home-tickets
```

## 🔧 技术实现

### 后端核心类
- **OrderController**: 控制器层，处理HTTP请求
- **OrderService**: 业务逻辑层，实现分页和排序
- **OrderRepository**: 数据访问层，SQL查询
- **Orderandott**: DTO类，数据传输对象

### 前端核心文件
- **TicketManagement.vue**: 主页面组件
- **router/index.js**: 路由配置

### 排序逻辑
1. 获取所有订单数据
2. 转换为DTO对象
3. 按createDatetime降序排序
4. 分配连续序号
5. 执行分页切片

## 🚀 部署说明

### 后端启动
```bash
cd "back end/demo"
./mvnw spring-boot:run
```

### 前端启动
```bash
cd vue-project
npm run dev
```

### 访问地址
- 前端页面: http://localhost:5173/ticket-management
- 后端API: http://localhost:8080/api/orders/home-tickets/page

## 📝 测试文件

项目包含多个测试页面：
- `datetime-sort-test.html`: 日期排序测试
- `dto-test.html`: DTO转换测试
- `simple-test.html`: 简化功能测试
- `sequence-pagination-test.html`: 序号分页测试

## 🔄 Git推送说明

当前代码已提交到本地order分支，如需推送到远程仓库：

```bash
# 确认当前分支
git branch

# 推送到远程order分支
git push -u origin order
```

**注意**: 推送可能需要身份验证，请确保：
1. 已配置正确的git凭据
2. 有远程仓库的推送权限
3. 网络连接正常

## 📋 提交信息

**提交哈希**: 01afeb2
**提交信息**: 首页餐票订单功能完成 - 支持分页和按createDatetime降序排序

## 🎉 功能特点

- ✅ **用户友好**: 最新订单优先显示
- ✅ **性能优化**: 高效的分页查询
- ✅ **数据一致**: 统一的DTO转换
- ✅ **错误处理**: 完善的异常处理
- ✅ **可扩展**: 清晰的代码结构
