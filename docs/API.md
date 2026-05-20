# VibeCommerce 海外独立站项目文档

## 项目概述

VibeCommerce 是一个基于 Spring Cloud 和 Spring Boot 的微服务电商系统，支持多语言（中文/英文）、多币种结算。

### 技术栈

- **后端**: Spring Boot 3.2 + Spring Cloud
- **ORM**: MyBatis Plus
- **数据库**: H2 (开发) / MySQL/PostgreSQL (生产)
- **认证**: JWT
- **服务注册**: Nacos
- **网关**: Spring Cloud Gateway

### 微服务架构

```
                    ┌─────────────┐
                    │  Gateway   │
                    │   (8080)   │
                    └─────────────┘
                         │
    ┌────────────────────┼────────────────────┐
    │          │        │        │           │
┌───┴───┐  ┌──┴───┐  ┌─┴───┐  ┌┴────┐  ┌──┴──┐
│Product│  │ User │  │Order│  │ CMS │  │Common│
│(8081) │  │(8083)│  │(8082)│  │(8084)│  │     │
└──────┘  └─────┘  └─────┘  └─────┘  └─────┘
```

---

## 服务端口配置

| 服务 | 端口 | 服务名 | 说明 |
|------|------|--------|------|
| Gateway | 8080 | gateway | API网关 |
| Product-service | 8081 | product-service | 产品服务 |
| User-service | 8083 | user-service | 用户服务 |
| Order-service | 8082 | order-service | 订单服务 |
| CMS-service | 8084 | cms-service | CMS文章服务 |

---

## API 接口文档

### 响应格式

所有API统一返回以下格式：

```json
{
  "code": 0,
  "message": "success",
  "data": {},
  "page": null
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| code | int | 0=成功，非0=失败 |
| message | string | 消息 |
| data | object | 数据payload |
| page | object | 分页信息(null表示不分页) |

---

### Product Service (8081)

#### 产品接口 `/api/v1/products`

##### 1. 获取产品列表
```
GET /api/v1/products?page=1&pageSize=20&categoryId=1&keyword=iPhone
```

**参数:**
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，默认1 |
| pageSize | int | 否 | 每页数量，默认20 |
| categoryId | long | 否 | 分类ID |
| brandId | long | 否 | 品牌ID |
| keyword | string | 否 | 关键词搜索 |

**响应:**
```json
{
  "code": 0,
  "data": {
    "records": [...],
    "total": 100,
    "page": 1,
    "pageSize": 20
  }
}
```

##### 2. 获取产品详情
```
GET /api/v1/products/{id}
```

**响应:**
```json
{
  "code": 0,
  "data": {
    "id": 1,
    "productCode": "PROD001",
    "name": "iPhone 15 Pro",
    "price": 999.00,
    "originalPrice": 1099.00,
    "stockQuantity": 100,
    "status": 1
  }
}
```

##### 3. 获取产品SKU列表
```
GET /api/v1/products/{id}/skus
```

---

#### 分类接口 `/categories`

##### 4. 获取分类树
```
GET /categories/tree
```

##### 5. 获取分类列表
```
GET /categories
```

##### 6. 获取分类详情
```
GET /categories/{id}
```

---

### User Service (8083)

#### 认证接口 `/auth`

##### 7. 用户注册
```
POST /auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "phone": "+8613800138000"
}
```

##### 8. 用户登录
```
POST /auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

**响应:**
```json
{
  "code": 0,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "expiresIn": 86400
  }
}
```

##### 9. 刷新Token
```
POST /auth/refresh
Authorization: Bearer {token}
```

---

#### 用户接口 `/user`

##### 10. 获取用户资料
```
GET /user/profile
Authorization: Bearer {token}
```

##### 11. 更新用户资料
```
PUT /user/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "新昵称",
  "firstName": "John",
  "lastName": "Doe"
}
```

##### 12. 获取收货地址列表
```
GET /user/addresses
Authorization: Bearer {token}
```

##### 13. 添加收货地址
```
POST /user/addresses
Authorization: Bearer {token}
Content-Type: application/json

{
  "receiverName": "张三",
  "phone": "+8613800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园路123号",
  "isDefault": 1
}
```

##### 14. 更新收货地址
```
PUT /user/addresses/{id}
Authorization: Bearer {token}
```

##### 15. 删除收货地址
```
DELETE /user/addresses/{id}
Authorization: Bearer {token}
```

---

### Order Service (8082)

#### 购物车接口 `/cart`

##### 16. 获取购物车
```
GET /cart
Authorization: Bearer {token}
```

**响应:**
```json
{
  "code": 0,
  "data": {
    "items": [],
    "subtotal": 0,
    "itemCount": 0
  }
}
```

##### 17. 添加商品到购物车
```
POST /cart/items
Authorization: Bearer {token}
Content-Type: application/json

{
  "productId": 1,
  "skuId": null,
  "quantity": 2
}
```

##### 18. 更新购物车商品数量
```
PUT /cart/items/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "quantity": 3
}
```

##### 19. 删除购物车商品
```
DELETE /cart/items/{id}
Authorization: Bearer {token}
```

##### 20. 清空购物车
```
DELETE /cart/clear
Authorization: Bearer {token}
```

---

#### 订单接口 `/orders`

##### 21. 创建订单
```
POST /orders/checkout
Authorization: Bearer {token}
Content-Type: application/json

{
  "addressId": 1,
  "remark": "请尽快发货"
}
```

**响应:**
```json
{
  "code": 0,
  "data": {
    "orderId": 1,
    "orderNo": "ORD202605190001",
    "totalAmount": 1998.00
  }
}
```

##### 22. 获取订单列表
```
GET /orders?page=1&pageSize=20&status=PENDING
Authorization: Bearer {token}
```

##### 23. 获取订单详情
```
GET /orders/{id}
Authorization: Bearer {token}
```

##### 24. 取消订单
```
POST /orders/{id}/cancel
Authorization: Bearer {token}
```

---

#### 支付接口 `/payments`

##### 25. 创建支付
```
POST /payments
Authorization: Bearer {token}
Content-Type: application/json

{
  "orderId": 1,
  "payMethod": "PAYPAL"
}
```

##### 26. 支付回调
```
POST /payments/callback
Content-Type: application/json

{
  "orderId": 1,
  "transactionId": "PAYID-xxx",
  "status": "COMPLETED"
}
```

---

### CMS Service (8084)

#### 文章接口 `/cms/articles`

##### 27. 获取文章列表
```
GET /cms/articles?page=1&pageSize=10&categoryId=1
```

##### 28. 获取文章详情
```
GET /cms/articles/{id}
```

##### 29. 根据slug获取文章
```
GET /cms/articles/slug/{slug}
```

##### 30. 获取推荐文章
```
GET /cms/articles/featured
```

---

## 测试数据

### 用户
| username | password | email |
|----------|----------|-------|
| testuser | password123 | test@example.com |

### 产品
| id | productCode | name | price |
|----|-------------|------|-------|
| 1 | PROD001 | iPhone 15 Pro | 999.00 |
| 2 | PROD002 | Samsung Galaxy S24 | 899.00 |
| 3 | PROD003 | Sony WH-1000XM5 | 349.00 |

### 分类
| id | name | level |
|----|------|-------|
| 1 | Electronics | 1 |
| 2 | Clothing | 1 |
| 3 | Home & Garden | 1 |

---

## 快速开始

### 前置要求
- Java 17+
- Maven 3.6+

### 编译
```bash
cd backend
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
mvn clean package -DskipTests
```

### 启动服务
```bash
# 启动各个服务
cd product-service && java -jar target/product-service-1.0.0-SNAPSHOT.jar &
cd user-service && java -jar target/user-service-1.0.0-SNAPSHOT.jar &
cd order-service && java -jar target/order-service-1.0.0-SNAPSHOT.jar &
cd cms-service && java -jar target/cms-service-1.0.0-SNAPSHOT.jar &
cd gateway && java -jar target/gateway-1.0.0-SNAPSHOT.jar &
```

### 运行测试
```bash
# 用户注册
curl -X POST http://localhost:8083/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"123456"}'

# 用户登录
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"123456"}'

# 获取产品列表
curl http://localhost:8081/api/v1/products

# 获取产品详情
curl http://localhost:8081/api/v1/products/1

# 获取购物车
curl http://localhost:8082/cart
```

---

## 错误码

| code | message | 说明 |
|------|---------|------|
| 0 | success | 成功 |
| 400 | Bad Request | 请求参数错误 |
| 401 | Unauthorized | 未授权 |
| 403 | Forbidden | 禁止访问 |
| 404 | Not Found | 资源不存在 |
| 500 | Internal Server Error | 服务器内部错误 |

---

## 数据库表结构

### product 表
```sql
CREATE TABLE product (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_code VARCHAR(50) NOT NULL,
    name VARCHAR(500) NOT NULL,
    price DECIMAL(18,2),
    original_price DECIMAL(18,2),
    stock_quantity INT DEFAULT 0,
    status TINYINT DEFAULT 1
);
```

### users 表
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status TINYINT DEFAULT 1
);
```

---

## 开发指南

### 添加新产品API

1. 在 `entity/` 目录创建实体类
2. 在 `mapper/` 目录创建Mapper接口
3. 在 `service/` 目录创建Service接口和实现
4. 在 `controller/` 目录创建Controller
5. 在 `schema.sql` 添加表结构
6. 添加测试用例到 `docs/testing/test_cases.md`

### 配置说明

数据库配置在 `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:vibecoding;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
```

---

## 文档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|---------|
| 2026-05-19 | 1.0 | 初始版本，完成所有核心API |