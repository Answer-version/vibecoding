# 外贸独立站技术设计文档

**版本**: 1.0  
**日期**: 2026-05-18  
**作者**: 架构师  
**状态**: 初始版本

---

## 一、数据库设计（核心基础）

### 1.1 设计原则

- 核心业务表使用 InnoDB 引擎，字符集 utf8mb4
- 所有表必须包含 `deleted` 软删除标记字段
- 所有表必须包含 `create_time` 和 `update_time` 时间戳
- 主键使用 BIGINT 自增（为未来分表预留）
- 业务字段添加 comment 说明

### 1.2 核心表结构

#### 1.2.1 多租户与多语言基础

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T01 | `site_config` | 站点配置（多语言、多货币、税区） | 核心配置表 |
| T02 | `region` | 地区/国家/州省 | 物流与税务区域 |
| T03 | `currency` | 货币汇率 | 支持实时定价 |
| T04 | `locale_translation` | 多语言文案翻译 | KV 表 |

#### 1.2.2 商品中心

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T10 | `category` | 产品分类 | 多级树状结构 |
| T11 | `brand` | 品牌 | 支持多语言 |
| T12 | `product` | 产品主表（SPU） | 含基础信息 |
| T13 | `product_sku` | SKU 库存单元 | 变体管理 |
| T14 | `product_image` | 产品图片 | 主图/详情图 |
| T15 | `product_seo` | SEO 信息 | Meta/关键词 |
| T16 | `product_category` | 产品-分类关联 | 多对多 |
| T17 | `product_tag` | 产品标签 | 营销标签 |

#### 1.2.3 会员中心

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T20 | `user` | 用户主表 | 含密码/手机/邮箱 |
| T21 | `user_address` | 用户地址 | 多地址管理 |
| T22 | `user_company` | 企业信息 | B2B 客户资质 |
| T23 | `customer_group` | 客户分组 | B2C 等级/B2B 批发商 |
| T24 | `customer_price` | 客户专属价 | 一客一价 |

#### 1.2.4 订单中心

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T30 | `cart` | 购物车/询盘篮 | B2C购物车/B2B询盘篮 |
| T31 | `cart_item` | 购物车/询盘篮项 | 支持SKU |
| T32 | `order` | 订单主表 | 含状态机 |
| T33 | `order_item` | 订单明细 | SKU明细 |
| T34 | `order_status_log` | 订单状态日志 | 履约追踪 |
| T35 | `order_invoice` | 发票信息 | 税务发票 |
| T36 | `quote` | 报价单 | B2B 批发专用 |
| T37 | `quote_item` | 报价明细 | 支持改价 |

#### 1.2.5 支付中心

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T40 | `payment` | 支付记录 | 含状态机 |
| T41 | `refund` | 退款记录 | 关联原订单 |
| T42 | `payment_method` | 支付方式配置 | 多网关 |
| T43 | `currency_rate` | 实时汇率缓存 | 定时任务 |

#### 1.2.6 物流中心

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T50 | `shipping_method` | 配送方式 | 含报价 |
| T51 | `shipping_zone` | 配送区域 | 国家/地区 |
| T52 | `warehouse` | 仓库 | 多仓库 |
| T53 | `inventory` | 库存 | SKU维度 |
| T54 | `shipping_tracking` | 物流追踪 | 轨迹记录 |

#### 1.2.7 CMS 内容中心（SEO 用）

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T60 | `cms_category` | 文章分类 | 博客分类 |
| T61 | `cms_article` | 文章内容 | 支持富文本 |
| T62 | `cms_page` | 页面内容 | 关于/FAQ 等 |
| T63 | `cms_seo_rule` | SEO 规则模板 | 自动生成 |

#### 1.2.8 运营与管理

| 序号 | 表名 | 说明 | 备注 |
|------|------|------|------|
| T70 | `admin_user` | 运营管理员 | RBAC |
| T71 | `admin_role` | 角色 | 权限集 |
| T72 | `admin_permission` | 权限 | 细粒度 |
| T73 | `coupon` | 优惠卷 | 促销 |
| T74 | `promotion` | 促销活动 | 满减/折扣 |
| T75 | `notification` | 站内通知 | 消息推送 |
| T76 | `operation_log` | 操作日志 | 审计 |

### 1.3 核心 ER 关系

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  Category   │────<│  Product    │────<│ ProductSku │
│  (分类)     │     │  (SPU)      │     │  (SKU)     │
└──────────────┘     └──────────────┘     └──────────────┘
                            │                   │
                            │                   │
                     ┌──────┴──────┐   ┌───────┴───────┐
                     │ Product    │   │  Inventory  │
                     │ Image/SEO │   │  (库存)     │
                     └───────────┘   └─────────────┘


┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│    User     │────<│    Cart     │────<│ CartItem    │
│  (用户)     │     │  (询盘篮)  │     │ (询盘篮项)  │
└──────────────┘     └──────────────┘     └──────────────┘
       │                                         │
       │                                         ▼
       │                                   ┌──────────────┐
       │                                   │    Quote     │
       │                                   │  (报价单)    │
       │                                   └──────────────┘
       │                     │                   │
       ▼                    ▼                   ▼
┌──────────────┐   ┌──────────────┐   ┌──────────────┐
│  Order       │──<│  OrderItem  │   │  Payment    │
│  (订单)     │   │  (订单项)   │   │  (支付)     │
└──────────────┘   └──────────────┘   └──────────────┘
       │
       ▼
┌──────────────┐   ┌──────────────┐
│   Shipping   │   │  Refund     │
│  (物流)     │   │  (退款)     │
└──────────────┘   └──────────────┘
```

### 1.4 字段命名规范

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| 表名 | 小写下划线 `t_` 前缀可选 | `product_sku` |
| 主键 | `id` | BIGINT |
| 外键 | `{表名}_id` | `product_id` |
| 时间 | `create_time` / `update_time` | DATETIME |
| 软删 | `deleted` | TINYINT (0/1) |
| 排序 | `sort_order` | INT |
| 多语言 | `_locale` 后缀 | `name_locale` |
| 价格 | `{币种}_price` | `usd_price` / `eur_price` |
| 数量 | `quantity` | INT |
| 金额 | `{币种}_amount` | `usd_amount` |

---

## 二、领域模型与业务流程

### 2.1 核心领域划分

| 领域 | 限界上下文 | 核心实体 | 主要服务 |
|------|----------|---------|---------|----------|
| Product | 商品域 | Product, Sku, Category | ProductService, InventoryService |
| Order | 订单域 | Order, Cart, Quote | OrderService, CartService, QuoteService |
| User | 用户域 | User, Company, Address | AuthService, UserService |
| Payment | 支付域 | Payment, Refund | PaymentService |
| Shipping | 物流域 | Shipping, Tracking | ShippingService |
| CMS | 内容域 | Article, Page | CmsService, SeoService |

### 2.2 状态机设计

#### 2.2.1 订单状态机

```
┌─────────────────────────────────────────────────────────────┐
│                     订单状态机                               │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   PENDING ──[支付成功]──> PAID ──[已发货]──> SHIPPED         │
│     ↑                    │                    │                │
│     │                    │                    │                │
│     │                    │                    │                │
│   CANCELLED <──[取消]──  │                    │                │
│     ↑                    │                    │                │
│     │                    ▼                    ▼                │
│     │              COMPLETED <──[已完成]──  DELIVERED              │
│     │                    │                                     │
│     │                    ▼                                     │
│     └───────[维权]────── REFUNDING ──[退款完成]── REFUNDED    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

| 状态 | 说明 | 可转向 |
|------|------|--------|
| PENDING | 待支付 | PAID, CANCELLED |
| PAID | 已支付 | SHIPPED, REFUNDING |
| SHIPPING | 已发货 | DELIVERED, REFUNDING |
| DELIVERED | 已送达 | COMPLETED |
| COMPLETED | 已完成 | (终止) |
| REFUNDING | 退款中 | REFUNDED, PAID |
| REFUNDED | 已退款 | (终止) |
| CANCELLED | 已取消 | (终止) |

#### 2.2.2 支付状态机

```
PENDING ──[回调成功]──> SUCCESS
    │                       │
    │                       │
    └──[回调失败/超时]──> FAILED
    │
    └──[用户取消]────> CANCELLED
```

#### 2.2.3 报价单状态机（仅 B2B）

```
DRAFT ──[发送]──> SENT ──[客户确认]──> CONFIRMED ──[转订单]──> CONVERTED
                                           │                         │
                                           │                         ▼
                                           └──────[拒绝]──> REJECTED    │
                                                                      ▼
                                                              (终止)
```

### 2.3 核心业务流程

#### 2.3.1 B2C 下单流程（零售）

```
1. 用户浏览 ──> 2. 加入购物车 ──> 3. 结算页选地址/物流
    │                                    │
    │                                    ▼
    │                         4. 选择支付方式 ──> 5. 支付
    │                                             │
    │                                             ▼
    │                                   6. 支付成功创建订单
    │                                             │
    │                                             ▼
    │                                   7. 订单状态流转 PAID
    │                                             │
    │                                             ▼
    │                                   8. 触发库存扣减消息
    │                                             │
    │                                             ���
    │                                   9. 通知仓库拣货 ──> 10. 发货
    │                                             │
    │                                             ▼
    │                                   11. 物流轨迹追踪
    │                                             │
    │                                             ▼
    └────────────────> 12. 确认收货 ──> COMPLETED
```

#### 2.3.2 B2B 批发流程（询盘报价）

```
1. B2B 客户登录 ──> 2. 查看批发价 ──> 3. 加入询盘篮
    │                                      │
    │                                      ▼
    │                         4. 提交询盘（无需支付）
    │                                      │
    │                                      ▼
    │                         5. 创建 Quote（草稿）
    │                                      │
    │                                      ▼
    │                         6. 运营审核报价 ──> 7. 调整价格/条款
    │                                      │
    │                                      ▼
    │                         8. 发送报价单给客户
    │                                      │
    │                                      ▼
    │                         9. 客户确认/还价/拒绝
    │                                      │
    │                                      ▼
    │                         10. 客户确认 ──> 转正式订单
    │                                      │
    │                                      ▼
    │                         11. 进入订单履约流程
    └──────────────────────────────────────────────
```

#### 2.3.3 多.currency 价格计算

```
┌─────────────────────────────────────────────────────────────┐
│                   价格计算流程                              │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  1. 获取用户登录状态                                        │
│                    │                                        │
│                    ▼                                        │
│  2. 获取用户等级 customer_group                             │
│                    │                                        │
│                    ▼                                        │
│  3. 检查客户专属价 customer_price                          │
│                    │                                        │
│                    ▼                                        │
│  4. 检查促销活动 promotion (折扣/满减)                    │
│                    │                                        │
│                    ▼                                        │
│  5. 获取当前站点货币 site_config.currency                   │
│                    │                                        │
│                    ▼                                        │
│  6. 检查是否需要税 (VAT/GST) ──> 计算税费                   │
│                    │                                        │
│                    ▼                                        │
│  7. 应用优惠券 coupon (可选)                              │
│                    │                                        │
│                    ▼                                        │
│  8. 返回最终价格（含税）                                    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 三、API 接口设计

### 3.1 设计规范

- RESTful 风格，资源导向
- 版本号前缀：`/api/v1/`
- 认证方式：JWT Bearer Token
- 请求/响应格式：JSON
- 分页支持：`page`, `pageSize` 参数
- 国际化：`Locale`, `Currency` Header
- 错误码：全局统一错误码体系

### 3.2 全局错误码

| 分类 | 编码区间 | 说明 |
|------|----------|------|
| 1xxxx | 系统错误 | 10000-19999 |
| 2xxxx | 认证错误 | 20000-29999 |
| 3xxxx | 业务错误 | 30000-39999 |
| 4xxxx | 第三方错误 | 40000-49999 |

| 错误码 | 说明 |
|--------|------|
| 10000 | 系统内部错误 |
| 10001 | 参数校验失败 |
| 10002 | 资源不存在 |
| 20001 | Token 无效 |
| 20002 | Token 过期 |
| 20003 | 无访问权限 |
| 30001 | 库存不足 |
| 30002 | 订单已取消 |
| 30003 | 支付失败 |
| 30004 | 不支持该支付方式 |
| 30005 | 物流不可达 |

### 3.3 核心 API 清单

#### 3.3.1 站���与配置

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/site/config` | 获取站点配置 |
| GET | `/api/v1/site/languages` | 获取支持的语言列表 |
| GET | `/api/v1/site/currencies` | 获取支持的货币列表 |
| GET | `/api/v1/regions/countries` | 获取国家列表 |
| GET | `/api/v1/regions/states/{countryCode}` | 获取州/省 |

#### 3.3.2 商品 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/products` | 产品列表（分页+筛选） | 公开 |
| GET | `/api/v1/products/{id}` | 产品详情 | 公开 |
| GET | `/api/v1/products/{id}/skus` | SKU 列表（含库存） | 公开 |
| GET | `/api/v1/products/{id}/reviews` | 评价列表 | 公开 |
| GET | `/api/v1/categories` | 分类树 | 公开 |
| GET | `/api/v1/brands` | 品牌列表 | 公开 |
| GET | `/api/v1/products/search` | 全文搜索（ES） | 公开 |
| GET | `/api/v1/products/recommend` | 推荐产品 | 公开 |
| **POST** | `/api/v1/products` | 创建产品 | 运营 |
| **PUT** | `/api/v1/products/{id}` | 更新产品 | 运营 |
| **DELETE** | `/api/v1/products/{id}` | 删除产品 | 运营 |

#### 3.3.3 会员 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| **POST** | `/api/v1/auth/register` | 注册 | 公开 |
| **POST** | `/api/v1/auth/login` | 登录 | 公开 |
| **POST** | `/api/v1/auth/refresh` | 刷新 Token | 需 Token |
| **POST** | `/api/v1/auth/forgot-password` | 忘记密码 | 公开 |
| **POST** | `/api/v1/auth/reset-password` | 重置密码 | 公开 |
| GET | `/api/v1/user/profile` | 用户资料 | 用户 |
| **PUT** | `/api/v1/user/profile` | 更新资料 | 用户 |
| GET | `/api/v1/user/addresses` | 地址列表 | 用户 |
| **POST** | `/api/v1/user/addresses` | 添加地址 | 用户 |
| **PUT** | `/api/v1/user/addresses/{id}` | 更新地址 | 用户 |
| **DELETE** | `/api/v1/user/addresses/{id}` | 删除地址 | 用户 |
| **POST** | `/api/v1/user/company` | 提交企业资质（B2B） | 用户 |

#### 3.3.4 购物车/询盘篮 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/cart` | 读取购物车/询盘篮 | 用户 |
| **POST** | `/api/v1/cart/items` | 添加商品 | 用户 |
| **PUT** | `/api/v1/cart/items/{id}` | 更新数量 | 用户 |
| **DELETE** | `/api/v1/cart/items/{id}` | 移除商品 | 用户 |
| **DELETE** | `/api/v1/cart/clear` | 清空购物车 | 用户 |

#### 3.3.5 订单 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| **POST** | `/api/v1/orders/checkout` | 创建订单（B2C） | 用户 |
| GET | `/api/v1/orders` | 订单列表 | 用户 |
| GET | `/api/v1/orders/{id}` | 订单详情 | 用户 |
| **POST** | `/api/v1/orders/{id}/cancel` | 取消订单 | 用户 |
| GET | `/api/v1/orders/{id}/status` | 订单状态 | 用户 |
| GET | `/api/v1/orders/{id}/invoice` | 电子发票 | 用户 |

#### 3.3.6 报价 API（仅 B2B）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| **POST** | `/api/v1/quotes` | 提交询盘 | 用户 |
| GET | `/api/v1/quotes` | 报价列表 | 用户/运营 |
| GET | `/api/v1/quotes/{id}` | 报价详情 | 用户/运营 |
| **PUT** | `/api/v1/quotes/{id}` | 运营报价 | 运营 |
| **POST** | `/api/v1/quotes/{id}/send` | 发送报价 | 运营 |
| **POST** | `/api/v1/quotes/{id}/confirm` | 客户确认 | 用户 |
| **POST** | `/api/v1/quotes/{id}/reject` | 客户拒绝 | 用户 |
| **POST** | `/api/v1/quotes/{id}/convert` | 转正式订单 | 用户 |

#### 3.3.7 支付 API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/payments/methods` | 支付方式列表 | 公开 |
| **POST** | `/api/v1/payments/create` | 创建支付 | 用户 |
| **POST** | `/api/v1/payments/{id}/notify` | 支付回调 | 公开 |
| GET | `/api/v1/payments/{id}` | 支付状态 | 用户 |

#### 3.3.8 物流 API

| 方法 | 路�� | 说明 | 权限 |
|------|------|------|------|
| **POST** | `/api/v1/shipping/calculate` | 计算运费 | 公开 |
| GET | `/api/v1/shipping/methods` | 配送方式列表 | 公开 |
| GET | `/api/v1/shipping/tracking/{trackingNo}` | 物流追踪 | 公开 |
| GET | `/api/v1/shipping/regions` | 可送达区域 | 公开 |

#### 3.3.9 CMS API

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/cms/articles` | 文章列表 | 公开 |
| GET | `/api/v1/cms/articles/{id}` | 文章详情 | 公开 |
| GET | `/api/v1/cms/categories` | 文章分类 | 公开 |
| GET | `/api/v1/cms/pages/{slug}` | 页面内容 | 公开 |
| **POST** | `/api/v1/cms/articles` | 创建文章 | 运营 |
| **PUT** | `/api/v1/cms/articles/{id}` | 更新文章 | 运营 |
| **DELETE** | `/api/v1/cms/articles/{id}` | 删除文章 | 运营 |

#### 3.3.10 运营后台 API（管理）

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/v1/admin/dashboard` | 数据看板 | 运营 |
| GET | `/api/v1/admin/orders` | 订单管理 | 运营 |
| **PUT** | `/api/v1/admin/orders/{id}/ship` | 发货 | 运营 |
| **PUT** | `/api/v1/admin/orders/{id}/status` | 修改状态 | 运营 |
| GET | `/api/v1/admin/products` | 商品管理 | 运营 |
| **POST** | `/api/v1/admin/products/import` | 批量导入 | 运营 |
| GET | `/api/v1/admin/customers` | 客户管理 | 运营 |
| **PUT** | `/api/v1/admin/customers/{id}/group` | 调整客户等级 | 运营 |
| **POST** | `/api/v1/admin/prices/custom` | 设置专属价 | 运营 |
| GET | `/api/v1/admin/reports/orders` | 订单报表 | 运营 |
| GET | `/api/v1/admin/reports/sales` | 销售报表 | 运营 |

### 3.4 API 响应结构

#### 3.4.1 统一响应格式

```json
// 成功
{
  "code": 0,
  "message": "success",
  "data": { ... },
  "page": { "page": 1, "pageSize": 20, "total": 100 }
}

// 失败
{
  "code": 10001,
  "message": "参数校验失败",
  "errors": [
    { "field": "email", "message": "邮箱格式不正确" }
  ]
}
```

#### 3.4.2 分页结构

```json
{
  "page": 1,
  "pageSize": 20,
  "total": 100,
  "totalPages": 5
}
```

---

## 四、部署架构（2C4G 资源）

### 4.1 资源分配策略

2核4G 综合考虑：
- 容器化部署（Docker）
- 微服务架构需要多个 JVM 进程
- 单机多容器方案

| 角色 | 容器/进程 | 资源配置 | 说明 |
|------|----------|---------|------|
| API Gateway | 1 容器 | 512M | 入口、限流、鉴权 |
| Product Service | 1 容器 | 512M | 商品域 |
| Order Service | 1 容器 | 512M | 订单域 |
| User Service | 1 容器 | 512M | 用户域 |
| Admin Service | 1 容器 | 512M | 运营后台 |
| CMS Service | 1 容器 | 512M | 内容域 |
| **JVM 小计** | 6 容器 | **3G** | |
| Redis | 1 容器 | 256M | 缓存/会话 |
| MySQL | 1 容器 | 256M | 数据存储 |
| Elasticsearch | 1 容器 | 禁用/共享 | 搜索（开发禁用） |
| MinIO | 1 容器 | 禁用本地 | 对象存储 |
| Nginx | 1 容器 | 128M | 反向代理/静态 |
| **总计** | | **~3.7G** | 留 300M 余量 |

### 4.2 容器编排

```yaml
# docker-compose.yml (一期 MVP)
version: '3.8'
services:
  # API 网关
  gateway:
    image: vibecoding/gateway:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod

  # 微服务
  product-service:
    image: vibecoding/product-service:latest
    
  order-service:
    image: vibecoding/order-service:latest
    
  user-service:
    image: vibecoding/user-service:latest
    
  admin-service:
    image: vibecoding/admin-service:latest
    
  cms-service:
    image: vibecoding/cms-service:latest

  # 中间件
  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=xxx
      - MYSQL_DATABASE=ecommerce
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"

  nginx:
    image: nginx:alpine
    volumes:
      - ./static:/usr/share/nginx/html
    ports:
      - "80:80"

volumes:
  mysql-data:
```

### 4.3 网络规划

```
┌─────────────────────────────────────────────────────────────┐
│                     网络架构                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│   Internet                                                    │
│        │                                                      │
│        ▼                                                      │
│   [CDN/CloudFlare]                                          │
│        │                                                      │
│        ▼                                                      │
│   [Nginx:80] ──> [Gateway:8080] ──> [微服务集群]    │
│        │                              │                       │
│        │                              │                       │
│        │                              ▼                       │
│        │                       [Redis:6379]                  │
│        │                       [MySQL:3306]                 │
│        │                                                       │
│        ▼                                                       │
│   [静态资源: Nginx / MinIO]                                 │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 4.4 开发阶段配置调整

2C4G 资源在开发阶段，可做以下精简：

| 组件 | 开发配置 | 说明 |
|------|---------|------|
| ES 搜索 | 暂时不做 | 前期用 MySQL LIKE 查询 |
| MinIO | 暂时不做 | 开发用本地路径存储 |
| 多容器 | 合并部署 | 1-2 个容器运行所有服务 |
| 数据库 | 开发用 SQLite/内嵌 | 快速启动 |

---

## 五、下一阶段

1. **确认文档**：以上设计是否有需要调整的地方？
2. **详细建模**：确认后，我输出核心表的 DDL 建表语句
3. **API 文档**：Swagger/OpenAPI 格式的具体接口定义
4. **项目脚手架**：开始搭建 Spring Boot + Nuxt 项目

你挑个方向。