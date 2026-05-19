# VibeCommerce 海外独立站功能介绍

**版本**: 1.0
**日期**: 2026-05-19

---

## 一、项目概述

VibeCommerce 是一款面向海外市场的 B2C 跨境电商独立站系统，采用前后端分离架构，微服务设计。

### 技术栈

| 层级 | 技术 |
|------|------|
| 前端 | Nuxt 3 (Vue 3) + Vite |
| 管理后台 | Vue 3 + Vue Router + Pinia |
| 后端 | Spring Boot 3.2.5 + Spring Cloud |
| 数据库 | MyBatis-Plus + H2 (开发) / MySQL (生产) |
| 认证 | JWT |

---

## 二、已实现功能

### 2.1 客户端功能 (Nuxt3 前端)

#### 首页
- [x] 多语言支持（中/英文切换）
- [x] 精选产品展示
- [x] 随机产品图片（占位图）
- [x] 响应式布局

#### 产品列表页
- [x] 产品网格展示
- [x] 搜索功能
- [x] 分类筛选
- [x] 分页加载
- [x] 多语言支持

#### 产品详情页
- [x] 产品基本信息展示
- [x] 价格显示
- [x] 加入购物车功能

#### 购物车
- [x] 查看购物车内容
- [x] 添加商品
- [x] 修改数量
- [x] 删除商品
- [x] 清空购物车

#### 用户认证
- [x] 用户登录
- [x] 用户注册
- [x] Token 认证

#### 结账流程 (页面已创建)
- [x] 收货地址选择/输入
- [x] 配送方式选择
- [x] 支付方式选择（PayPal/Alipay/Credit Card）
- [x] 订单摘要
- [x] 提交订单

#### 用户中心 (页面已创建)
- [x] 个人资料查看/编辑
- [x] 收货地址管理（增删改）
- [x] 订单列表查看
- [x] 订单取消

---

### 2.2 后端服务

#### Product Service (8081)
- [x] 产品列表查询（分页、搜索、分类筛选）
- [x] 产品详情查询
- [x] 分类查询
- [x] 品牌查询
- [ ] 产品 CRUD 管理（未完成）
- [ ] SKU 查询（未完成）

#### Order Service (8082)
- [x] 购物车管理（基于内存）
- [x] 创建订单
- [x] 订单列表查询
- [x] 订单详情查询
- [x] 取消订单
- [x] 支付创建
- [x] 支付回调（模拟）
- [ ] 支付网关集成（模拟）
- [ ] Stripe 支付（未实现）

#### User Service (8083)
- [x] 用户注册
- [x] 用户登录
- [x] Token 刷新
- [x] 获取用户资料
- [x] 更新用户资料
- [x] 收货地址 CRUD
- [ ] 邮箱验证（未实现）
- [ ] 忘记密码（未实现）
- [ ] JWT 黑名单（未实现）

#### CMS Service
- [x] 文章列表查询
- [x] 文章详情查询
- [x] 特色文章查询
- [ ] 文章 CRUD 管理（未完成）
- [ ] 文章分类管理（未完成）

---

### 2.3 数据展示

#### 测试数据

| 类型 | 数据 |
|------|------|
| 产品 | iPhone 15 Pro ($999), Samsung Galaxy S24 ($899), Sony WH-1000XM5 ($349) |
| 分类 | Electronics, Clothing, Home & Garden |
| 品牌 | Apple, Samsung, Sony |

---

## 三、功能完善程度

### 完成度统计

| 模块 | 完成度 | 说明 |
|------|--------|------|
| 前端首页 | 90% | 基础功能完善 |
| 产品列表 | 85% | 搜索/筛选待优化 |
| 购物车 | 80% | 后端待持久化 |
| 结账流程 | 70% | 需后端订单服务配合 |
| 用户中心 | 60% | 地址管理待完善 |
| 登录注册 | 75% | 需后端用户服务配合 |
| 后端 API | 60% | 核心功能可用，部分待完善 |
| 管理后台 | 20% | 基础框架搭建 |

---

## 四、待完成功能

### 优先级 P1 (必须)

1. **数据库持久化** - 购物车、订单数据持久化到 MySQL
2. **支付网关集成** - 真实支付宝/PayPal/Stripe 集成
3. **产品图片上传** - 阿里云 OSS 或本地存储
4. **邮件服务** - 发送验证邮件、订单通知
5. **JWT 黑名单** - 登出后 Token 失效

### 优先级 P2 (重要)

1. **后台管理系统** - Vue-Admin 完善
2. **文章管理** - CMS CRUD
3. **客户分组** - B2B 批发功能
4. **优惠券系统** - 促销功能
5. **物流追踪** - 集成物流 API

### 优先级 P3 (优化)

1. **搜索优化** - Elasticsearch 集成
2. **性能优化** - 缓存、CDN
3. **SEO 优化** - Meta 标签、sitemap
4. **多币种** - 实时汇率
5. **多语言** - 更多语言支持

---

## 五、快速开始

### 启动后端服务

```bash
cd backend

# 启动产品服务 (8081)
java -jar product-service/target/product-service-1.0.0-SNAPSHOT.jar

# 启动订单服务 (8082)
java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar

# 启动用户服务 (8083)
java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar
```

### 启动前端

```bash
cd frontend/nuxt3
npm install
npm run dev
```

访问 http://localhost:3000

---

## 六、API 端点

### 认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 用户登录 |
| POST | /auth/register | 用户注册 |
| POST | /auth/refresh | 刷新 Token |

### 产品
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /products | 产品列表 |
| GET | /products/{id} | 产品详情 |

### 购物车
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /cart | 获取购物车 |
| POST | /cart/items | 添加商品 |
| PUT | /cart/items/{id} | 更新数量 |
| DELETE | /cart/items/{id} | 删除商品 |
| DELETE | /cart/clear | 清空购物车 |

### 订单
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /orders/checkout | 创建订单 |
| GET | /orders | 订单列表 |
| GET | /orders/{id} | 订单详情 |
| POST | /orders/{id}/cancel | 取消订单 |

---

## 七、目录结构

```
vibecoding-site/
├── frontend/
│   ├── nuxt3/          # 客户端前端
│   │   └── app/
│   │       ├── pages/      # 页面组件
│   │       ├── layouts/    # 布局
│   │       └── composables/ # 组合式函数
│   └── vue-admin/      # 管理后台
├── backend/
│   ├── common/         # 公共模块
│   ├── gateway/        # API 网关
│   ├── product-service/   # 商品服务
│   ├── order-service/     # 订单服务
│   ├── user-service/      # 用户服务
│   └── cms-service/       # CMS 服务
├── deploy/            # 部署配置
└── docs/             # 文档
```

---

## 八、后续计划

1. 完善支付网关集成
2. 实现数据库持久化
3. 搭建管理后台
4. 优化搜索体验
5. 增强安全特性

---

*持续更新中...*
