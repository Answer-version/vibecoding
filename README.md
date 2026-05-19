# VibeCommerce 海外独立站

[![GitHub stars](https://img.shields.io/github/stars/Answer-version/vibecoding?style=flat)](https://github.com/Answer-version/vibecoding/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/Answer-version/vibecoding?style=flat)](https://github.com/Answer-version/vibecoding/network)
[![License](https://img.shields.io/github/license/Answer-version/vibecoding?style=flat)](https://github.com/Answer-version/vibecoding/blob/main/LICENSE)

> VibeCommerce 是一款面向海外市场的 B2C 跨境电商独立站系统，采用前后端分离架构，微服务设计。

## 项目简介

VibeCommerce 是一个功能完善的海外电商独立站系统，支持：
- 多语言（中/英文）
- 多币种价格
- 用户注册/登录（含手机验证码）
- 购物车
- 订单管理
- 商品展示
- 支付集成（支持 PayPal、Alipay、Stripe）

## 技术栈

### 前端
| 技术 | 说明 |
|------|------|
| Nuxt 3 | SSR 框架 |
| Vue 3 | 渐进式前端框架 |
| Vite | 构建工具 |
| Pinia | 状态管理 |

### 后端
| 技术 | 说明 |
|------|------|
| Spring Boot 3.2.5 | Java 开发框架 |
| Spring Cloud | 微服务架构 |
| MyBatis-Plus | ORM 框架 |
| JWT | 身份认证 |
| H2 / MySQL | 数据库 |

### 基础设施
| 技术 | 说明 |
|------|------|
| Docker | 容器化部署 |
| Nacos | 服务注册/配置中心 |

## 项目结构

```
vibecoding-site/
├── frontend/                 # 前端项目
│   ├── nuxt3/              # 客户端 (Nuxt 3)
│   │   └── app/
│   │       ├── pages/       # 页面组件
│   │       ├── layouts/     # 布局
│   │       └── composables/  # 组合式函数
│   └── vue-admin/           # 管理后台 (开发中)
│
├── backend/                  # 后端项目
│   ├── common/              # 公共模块
│   ├── gateway/             # API 网关 (8080)
│   ├── product-service/      # 商品服务 (8081)
│   ├── order-service/       # 订单服务 (8082)
│   ├── user-service/        # 用户服务 (8083)
│   └── cms-service/         # CMS 服务
│
├── deploy/                   # 部署配置
└── docs/                    # 项目文档
```

## 功能列表

### ✅ 已完成

#### 客户端功能
- [x] 首页展示 + 精选产品
- [x] 产品列表 + 搜索 + 筛选
- [x] 产品详情页
- [x] 购物车 (增删改查)
- [x] 用户登录 (密码登录)
- [x] 用户登录 (手机验证码)
- [x] 用户注册 (密码/手机)
- [x] 忘记密码 (手机重置)
- [x] 用户中心 (订单/地址管理)
- [x] 多语言支持 (中/英文)
- [x] 商品图片占位图

#### 后端 API
- [x] 产品查询 (分页/搜索)
- [x] 分类/品牌查询
- [x] 购物车管理
- [x] 订单创建/查询/取消
- [x] 用户注册/登录
- [x] 手机验证码登录
- [x] JWT 认证

#### 管理后台 (基础框架)
- [x] 登录页面
- [x] 商品列表
- [x] 订单列表

### 🚧 开发中
- [ ] 支付网关集成 (PayPal/Stripe/Alipay)
- [ ] 购物车持久化
- [ ] 商品图片上传
- [ ] 完整后台管理
- [ ] 邮件/短信通知
- [ ] 优惠券系统

### 📋 待开发
- [ ] 客户分组/专属价
- [ ] 物流追踪
- [ ] 会员积分
- [ ] 评价系统
- [ ] 客服系统

## 快速开始

### 环境要求

| 软件 | 版本 |
|------|------|
| Java | 17+ |
| Node.js | 18+ |
| Maven | 3.8+ |

### 本地启动

#### 1. 克隆项目

```bash
git clone https://github.com/Answer-version/vibecoding.git
cd vibecoding-site
```

#### 2. 启动后端服务

```bash
cd backend

# 启动产品服务 (8081)
java -jar product-service/target/product-service-1.0.0-SNAPSHOT.jar

# 启动订单服务 (8082)
java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar

# 启动用户服务 (8083)
java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar
```

或者使用启动脚本：

```bash
./deploy/start-backend.sh
```

#### 3. 启动前端

```bash
cd frontend/nuxt3
npm install
npm run dev
```

#### 4. 访问

- 客户端: http://localhost:3000
- API Gateway: http://localhost:8080
- Product API: http://localhost:8081
- Order API: http://localhost:8082
- User API: http://localhost:8083

### Docker 启动

```bash
cd deploy
cp docs/02-ddl.sql deploy/init.sql
docker-compose up -d
```

## API 文档

### 认证 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 密码登录 |
| POST | /auth/register | 用户注册 |
| POST | /auth/phone/send | 发送手机验证码 |
| POST | /auth/phone/login | 手机验证码登录 |
| POST | /auth/phone/register | 手机注册 |
| POST | /auth/password/reset | 密码重置 |
| POST | /auth/refresh | 刷新 Token |

### 产品 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /products | 产品列表 |
| GET | /products/{id} | 产品详情 |
| GET | /categories/tree | 分类树 |
| GET | /brands | 品牌列表 |

### 购物车 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /cart | 获取购物车 |
| POST | /cart/items | 添加商品 |
| PUT | /cart/items/{id} | 更新数量 |
| DELETE | /cart/items/{id} | 删除商品 |
| DELETE | /cart/clear | 清空购物车 |

### 订单 API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /orders/checkout | 创建订单 |
| GET | /orders | 订单列表 |
| GET | /orders/{id} | 订单详情 |
| POST | /orders/{id}/cancel | 取消订单 |

### 用户 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /user/profile | 获取用户资料 |
| PUT | /user/profile | 更新用户资料 |
| GET | /user/addresses | 地址列表 |
| POST | /user/addresses | 添加地址 |
| PUT | /user/addresses/{id} | 更新地址 |
| DELETE | /user/addresses/{id} | 删除地址 |

## 测试账号

系统内置测试数据：

| 类型 | 数据 |
|------|------|
| 产品 | iPhone 15 Pro ($999), Samsung Galaxy S24 ($899), Sony WH-1000XM5 ($349) |
| 分类 | Electronics, Clothing, Home & Garden |
| 品牌 | Apple, Samsung, Sony |

## 项目文档

| 文档 | 说明 |
|------|------|
| docs/04-features.md | 项目功能介绍 |
| docs/05-production-features.md | 生产级功能清单 |
| docs/06-p0-design.md | P0 功能设计方案 |

## 贡献指南

欢迎提交 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -m 'Add xxx'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

## 开源协议

MIT License - 查看 [LICENSE](LICENSE) 文件

---

**Made with ❤️ by VibeCommerce Team**
