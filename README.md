# VibeCommerce - 外贸独立站

B2B + B2C 跨境电商独立站

## 项目结构

```
vibecoding/
├── docs/                 # 设计文档
│   ├── 01-design.md     # 技术设计文档
│   └── 02-ddl.sql       # MySQL 建表语句
│
├── backend/             # Java 后端 (Spring Boot)
│   ├── pom.xml         # 父 POM
│   ├── common/        # 公共模块
│   ├── gateway/       # API 网关
│   ├── product-service/ # 商品服务
│   ├── order-service/ # 订单服务
│   ├── user-service/  # 用户服务
│   └── cms-service/    # CMS 服务
│
└── frontend/
    └── nuxt3/        # B2C 前台 (Nuxt 3)
```

## 技术栈

### 后端
- Spring Boot 3.2
- Spring Cloud Gateway
- MyBatis-Plus
- MySQL 8.0
- Redis
- JWT

### 前台
- Nuxt 3
- Pinia
- Vue 3 Composition API

## 快速启动

### 1. 初始化数据库

```bash
mysql -u root -p < docs/02-ddl.sql
```

### 2. 启动后端

```bash
cd backend
mvn clean install -DskipTests
# 依次启动各服务
java -jar gateway/target/gateway.jar &
java -jar product-service/target/product-service.jar &
```

### 3. 启动前端

```bash
cd frontend/nuxt3
npm install
npm run dev
```

## 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| DB_HOST | localhost | 数据库地址 |
| DB_PORT | 3306 | 数据库端口 |
| DB_USER | root | 数据库用户 |
| DB_PASSWORD | | 数据库密码 |
| NACOS_SERVER | 127.0.0.1:8848 | Nacos 地址 |
| REDIS_HOST | localhost | Redis 地址 |
| JWT_SECRET | xxx | JWT 密钥 |

## API 前缀

```
/api/v1/products   - 商品
/api/v1/orders    - 订单
/api/v1/cart      - 购物车
/api/v1/auth     - 认证
/api/v1/user     - 用户
/api/v1/cms      - 内容
```

## License

MIT