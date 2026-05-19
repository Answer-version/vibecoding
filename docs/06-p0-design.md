# P0 功能设计方案

**版本**: 1.0
**日期**: 2026-05-19
**优先级**: P0 (阻断级)

---

## 一、P0 功能清单

### 1.1 用户系统

| 功能 | 当前状态 | 目标状态 |
|------|----------|----------|
| 邮箱注册/登录 | 已有基础 | 完成 |
| 手机注册/登录 | 未实现 | 完成 |
| 邮箱验证 | 未实现 | 完成 |
| 手机验证码验证 | 未实现 | 完成 |
| 忘记密码 | 未实现 | 完成 |
| 修改密码 | 未实现 | 完成 |
| JWT 认证 | 已有基础 | 完善 + Refresh Token |

### 1.2 商品系统

| 功能 | 当前状态 | 目标状态 |
|------|----------|----------|
| 产品列表/详情 | 已有基础 | 完成 |
| 产品搜索 | 已有基础 | 优化 |
| 分类导航 | 已有基础 | 完成 |
| SKU 变体选择 | 未实现 | 完成 |
| 多语言产品信息 | 未实现 | 完成 |
| 多币种价格 | 未实现 | 完成 |

### 1.3 购物车 & 订单

| 功能 | 当前状态 | 目标状态 |
|------|----------|----------|
| 购物车 CRUD | 已有基础 | 持久化 |
| 库存检测 | 未实现 | 完成 |
| 订单创建 | 已有基础 | 完成 |
| 订单列表/详情 | 已有基础 | 完成 |
| 取消订单 | 已有基础 | 完成 |

### 1.4 支付系统

| 功能 | 当前状态 | 目标状态 |
|------|----------|----------|
| PayPal 支付 | 模拟 | 完成集成 |
| Alipay 支付 | 模拟 | 完成集成 |
| Stripe 支付 | 未实现 | 完成 |
| 支付回调 | 模拟 | 完成 |
| 退款流程 | 未实现 | 完成 |

---

## 二、数据库设计

### 2.1 用户相关表 (扩展)

```sql
-- 用户表扩展
ALTER TABLE users ADD COLUMN phone VARCHAR(50) AFTER email;
ALTER TABLE users ADD COLUMN phone_verified TINYINT DEFAULT 0 AFTER email_verified;
ALTER TABLE users ADD COLUMN password_reset_token VARCHAR(100);
ALTER TABLE users ADD COLUMN password_reset_expire TIMESTAMP;
ALTER TABLE users ADD COLUMN email_verify_token VARCHAR(100);

-- 验证码表
CREATE TABLE verification_code (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL,
    code VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT 'login/register/reset',
    expire_time TIMESTAMP NOT NULL,
    used TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_phone_type ON verification_code(phone, type);
```

### 2.2 支付相关表

```sql
-- 支付方式配置
CREATE TABLE payment_method (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL COMMENT 'paypal/alipay/stripe',
    name VARCHAR(100) NOT NULL,
    name_en VARCHAR(100),
    icon VARCHAR(500),
    type VARCHAR(20) COMMENT 'online/offline',
    config JSON COMMENT 'API配置',
    fee_rate DECIMAL(5,4) COMMENT '手续费率',
    status TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 支付记录扩展
ALTER TABLE payment ADD COLUMN pay_method VARCHAR(50);
ALTER TABLE payment ADD COLUMN transaction_id VARCHAR(100);
ALTER TABLE payment ADD COLUMN raw_response TEXT COMMENT '第三方返回原始数据';

-- 退款记录
CREATE TABLE refund (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    refund_no VARCHAR(50) NOT NULL,
    order_id BIGINT NOT NULL,
    payment_id BIGINT NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    reason TEXT,
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/APPROVED/REJECTED/REFUNDED',
    remark VARCHAR(500),
    operator_id BIGINT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(refund_no)
);
```

### 2.3 商品扩展

```sql
-- 产品多语言
ALTER TABLE product ADD COLUMN name_locale VARCHAR(500);
ALTER TABLE product ADD COLUMN description_locale TEXT;

-- SKU 扩展
ALTER TABLE product_sku ADD COLUMN usd_price DECIMAL(18,2);
ALTER TABLE product_sku ADD COLUMN eur_price DECIMAL(18,2);
ALTER TABLE product_sku ADD COLUMN gbp_price DECIMAL(18,2);
ALTER TABLE product_sku ADD COLUMN cny_price DECIMAL(18,2);
```

---

## 三、API 设计

### 3.1 认证 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/phone/send | 发送手机验证码 |
| POST | /auth/phone/login | 手机验证码登录 |
| POST | /auth/phone/register | 手机验证码注册 |
| POST | /auth/password/forgot | 忘记密码 |
| POST | /auth/password/reset | 重置密码 |
| POST | /auth/email/verify | 邮箱验证 |
| POST | /auth/refresh | 刷新 Token |

### 3.2 支付 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /payments/methods | 获取支付方式列表 |
| POST | /payments/create | 创建支付 |
| POST | /payments/{id}/notify | 支付回调 |
| GET | /payments/{id} | 支付状态查询 |
| POST | /refunds | 申请退款 |
| GET | /refunds/{id} | 退款进度 |

---

## 四、前端页面清单

### 4.1 需要创建/优化的页面

| 页面 | 文件 | 说明 |
|------|------|------|
| 产品详情 | products/[id].vue | 完善SKU选择 |
| 登录 | auth/login.vue | 增加手机登录 |
| 注册 | auth/register.vue | 增加手机注册 |
| 忘记密码 | auth/forgot-password.vue | 新增 |
| 支付结果 | payment/result.vue | 新增 |
| 支付页面 | payment/checkout.vue | 新增 |

### 4.2 需要创建的后台页面

| 页面 | 组件 | 说明 |
|------|-------|------|
| 商品列表 | products/index.vue | 管理商品 |
| 商品编辑 | products/edit.vue | 新增/编辑 |
| 分类管理 | categories/index.vue | 分类CRUD |
| 品牌管理 | brands/index.vue | 品牌CRUD |
| 订单列表 | orders/index.vue | 订单管理 |
| 订单详情 | orders/detail.vue | 订单操作 |
| 支付配置 | settings/payment.vue | 支付设置 |

---

## 五、支付集成设计

### 5.1 支付流程

```
用户选择支付方式
    ↓
前端调用 /payments/create
    ↓
后端创建支付记录，返回支付页面URL/参数
    ↓
跳转到第三方支付页面
    ↓
支付完成后第三方回调 /payments/{id}/notify
    ↓
后端验证签名，更新支付状态
    ↓
前端轮询或WebSocket获取支付结果
```

### 5.2 支付网关接口

```java
public interface PaymentGateway {
    // 创建支付
    CreatePaymentResult createPayment(Payment payment);

    // 验证回调
    boolean verifyCallback(Map<String, String> params);

    // 查询支付状态
    PaymentStatus queryPayment(String transactionId);

    // 退款
    RefundResult refund(Payment payment, BigDecimal amount);
}
```

---

## 六、实施计划

### Phase 1: 用户系统 (1-2天)

1. 手机验证码登录/注册
2. 邮箱验证
3. 忘记/重置密码
4. Refresh Token

### Phase 2: 商品系统 (1-2天)

1. SKU 变体选择
2. 多语言支持
3. 多币种价格

### Phase 3: 支付系统 (2-3天)

1. PayPal 集成
2. Stripe 集成
3. 支付回调
4. 退款流程

### Phase 4: 后台管理 (3-5天)

1. 商品管理
2. 订单管理
3. 支付配置

---

## 七、总结

P0 阶段核心目标：
1. 完善用户系统（手机/邮箱验证）
2. 完成支付集成（PayPal/Stripe/Alipay）
3. 搭建后台基础框架

预计开发周期：**7-12天**

---

*持续更新中...*
