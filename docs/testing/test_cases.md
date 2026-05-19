# VibeCommerce 海外独立站项目测试用例文档

## 项目概述

项目采用Spring Cloud微服务架构，包含以下服务：

| 服务名称 | 端口 | 应用名称 | 数据库 |
|---------|------|----------|--------|
| gateway | 8080 | gateway | - |
| product-service | 8081 | product-service | vibecoding |
| order-service | 8082 | order-service | vibecoding |
| user-service | 8083 | user-service | vibecoding |
| cms-service | 8084 | cms-service | vibecoding |

### 技术栈
- Spring Boot 3.2 + Spring Cloud
- MyBatis Plus
- H2 (开发) / MySQL (生产)
- JWT 认证
- Nacos 服务注册

---

## 测试用例

### 1. 用户服务 (user-service: 8083)

#### 认证模块

##### TC-USER-001: 用户注册
- **接口**: `POST /auth/register`
- **请求**:
```bash
curl -X POST http://localhost:8083/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","phone":"+8613800138000"}'
```
- **期望**: code=0

##### TC-USER-002: 用户登录
- **接口**: `POST /auth/login`
- **请求**:
```bash
curl -X POST http://localhost:8083/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```
- **期望**: code=0, 返回token
- **状态**: ✅ PASSED

##### TC-USER-003: Token刷新
- **接口**: `POST /auth/refresh`
- **请求**:
```bash
curl -X POST http://localhost:8083/auth/refresh \
  -H "Authorization: Bearer {token}"
```
- **期望**: code=0

---

#### 用户模块

##### TC-USER-004: 获取用户资料
- **接口**: `GET /user/profile`
- **需要认证**: 是

##### TC-USER-005: 更新用户资料
- **接口**: `PUT /user/profile`
- **需要认证**: 是

##### TC-USER-006: 获取收货地址
- **接口**: `GET /user/addresses`
- **需要认证**: 是

##### TC-USER-007: 添加收货地址
- **接口**: `POST /user/addresses`
- **需要认证**: 是

##### TC-USER-008: 删除收货地址
- **接口**: `DELETE /user/addresses/{id}`
- **需要认证**: 是

---

### 2. 产品服务 (product-service: 8081)

#### 产品模块

##### TC-PRODUCT-001: 获取产品列表
- **接口**: `GET /api/v1/products`
- **请求**:
```bash
curl "http://localhost:8081/api/v1/products?page=1&pageSize=20"
```
- **期望**: code=0

##### TC-PRODUCT-002: 获取产品详情
- **接口**: `GET /api/v1/products/{id}`
- **请求**:
```bash
curl http://localhost:8081/api/v1/products/1
```
- **期望**: code=0
- **状态**: ✅ PASSED

##### TC-PRODUCT-003: 根据分类查询产品
- **接口**: `GET /api/v1/products?categoryId=1`
- **请求**:
```bash
curl "http://localhost:8081/api/v1/products?categoryId=1"
```

##### TC-PRODUCT-004: 关键词搜索
- **接口**: `GET /api/v1/products?keyword=iPhone`
- **请求**:
```bash
curl "http://localhost:8081/api/v1/products?keyword=iPhone"
```

##### TC-PRODUCT-005: 获取产品SKU
- **接口**: `GET /api/v1/products/{id}/skus`
- **请求**:
```bash
curl http://localhost:8081/api/v1/products/1/skus
```

---

#### 分类模块

##### TC-CATEGORY-001: 获取分类树
- **接口**: `GET /categories/tree`
- **请求**:
```bash
curl http://localhost:8081/categories/tree
```

##### TC-CATEGORY-002: 获取分类列表
- **接口**: `GET /categories`
- **请求**:
```bash
curl http://localhost:8081/categories
```

##### TC-CATEGORY-003: 获取分类详情
- **接口**: `GET /categories/{id}`
- **请求**:
```bash
curl http://localhost:8081/categories/1
```

---

### 3. 订单��务 (order-service: 8082)

#### 购物车模块

##### TC-CART-001: 获取购物车
- **接口**: `GET /cart`
- **请求**:
```bash
curl http://localhost:8082/cart
```
- **期望**: code=0
- **状态**: ✅ PASSED

##### TC-CART-002: 添加到购物车
- **接口**: `POST /cart/items`
- **请求**:
```bash
curl -X POST http://localhost:8082/cart/items \
  -H "Content-Type: application/json" \
  -d '{"productId":1,"quantity":2}'
```

##### TC-CART-003: 更新购物车
- **接口**: `PUT /cart/items/{id}`
- **请求**:
```bash
curl -X PUT http://localhost:8082/cart/items/1 \
  -H "Content-Type: application/json" \
  -d '{"quantity":5}'
```

##### TC-CART-004: 删除商品
- **接口**: `DELETE /cart/items/{id}`
- **请求**:
```bash
curl -X DELETE http://localhost:8082/cart/items/1
```

##### TC-CART-005: 清空购物车
- **接口**: `DELETE /cart/clear`
- **请求**:
```bash
curl -X DELETE http://localhost:8082/cart/clear
```

---

#### 订单模块

##### TC-ORDER-001: 创建订单
- **接口**: `POST /orders/checkout`
- **需要认证**: 是

##### TC-ORDER-002: 获取订单列表
- **接口**: `GET /orders`

##### TC-ORDER-003: 获取订单详情
- **接口**: `GET /orders/{id}`

##### TC-ORDER-004: 取消订单
- **接口**: `POST /orders/{id}/cancel`

---

### 4. CMS服务 (cms-service: 8084)

##### TC-CMS-001: 获取文章列表
- **接口**: `GET /cms/articles`
- **请求**:
```bash
curl "http://localhost:8084/cms/articles?page=1&pageSize=10"
```

##### TC-CMS-002: 获取文章详情
- **接口**: `GET /cms/articles/{id}`
- **请求**:
```bash
curl http://localhost:8084/cms/articles/1
```

##### TC-CMS-003: 根据slug获取文章
- **接口**: `GET /cms/articles/slug/{slug}`
- **请求**:
```bash
curl http://localhost:8084/cms/articles/slug/welcome
```

##### TC-CMS-004: 获取推荐文章
- **接口**: `GET /cms/articles/featured`
- **请求**:
```bash
curl http://localhost:8084/cms/articles/featured
```

---

## 批量测试脚本

```bash
#!/bin/bash
# test_api.sh - VibeCommerce API批量测试脚本

BASE="http://localhost"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "=== VibeCommerce API 测试 ==="

# Test Product
echo -n "测试产品列表... "
RESULT=$(curl -s "$BASE:8081/api/v1/products")
if echo "$RESULT" | grep -q '"code":0'; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL${NC}"
fi

# Test Product Detail
echo -n "测试产品详情... "
RESULT=$(curl -s "$BASE:8081/api/v1/products/1")
if echo "$RESULT" | grep -q '"code":0'; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL${NC}"
fi

# Test Cart
echo -n "测试购物车... "
RESULT=$(curl -s "$BASE:8082/cart")
if echo "$RESULT" | grep -q '"code":0'; then
  echo -e "${GREEN}✓ PASS${NC}"
else
  echo -e "${RED}✗ FAIL${NC}"
fi

echo "=== 测试完成 ==="
```

---

## 测试数据

### 测试用户
用户名: testuser
密码: password123

### 测试产品
| ID | 产品名称 | 价格 |
|----|----------|------|
| 1 | iPhone 15 Pro | 999.00 |
| 2 | Samsung Galaxy S24 | 899.00 |
| 3 | Sony WH-1000XM5 | 349.00 |

### 测试分类
| ID | 名称 |
|----|------|
| 1 | Electronics |
| 2 | Clothing |
| 3 | Home & Garden |

---

## 测试检查清单

- [x] Product API 正常
- [x] Cart API 正常
- [ ] Auth API 登录正常
- [ ] Auth API 注册正常
- [ ] Order API 正常
- [ ] CMS API 正常
- [ ] User API 正常

---

## 性能目标

| 指标 | 目标值 |
|------|--------|
| API响应时间 | < 200ms |
| 并发用户 | > 100 |
| 可用性 | > 99.9% |

---

## ���档更新记录

| 日期 | 版本 | 更新内容 |
|------|------|---------|
| 2026-05-19 | 1.0 | 初始版本 |