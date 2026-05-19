# VibeCommerce - 外贸独立站

B2B + B2C 跨境电商独立站

## 项目结构

```
vibecoding/
├── docs/                 # 设计文档
│   ├── 01-design.md     # 技术设计文档
│   ├── API.md         # API接口文档
│   ├── testing/       # 测试文档
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
- H2 (开发) / MySQL (生产)
- JWT
- Nacos

### 前台
- Nuxt 3
- Pinia
- Vue 3 Composition API

## 快速启动

### 前置要求
- Java 17+
- Maven 3.6+

### 1. 编译项目
```bash
cd backend
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
mvn clean package -DskipTests
```

### 2. 启动服务
```bash
# 产品服务 (8081)
cd product-service && java -jar target/product-service-1.0.0-SNAPSHOT.jar &

# 用户服务 (8083)
cd user-service && java -jar target/user-service-1.0.0-SNAPSHOT.jar &

# 订单服务 (8082)
cd order-service && java -jar target/order-service-1.0.0-SNAPSHOT.jar &

# CMS服务 (8084)
cd cms-service && java -jar target/cms-service-1.0.0-SNAPSHOT.jar &

# API网关 (8080)
cd gateway && java -jar target/gateway-1.0.0-SNAPSHOT.jar &
```

### 3. 验证服务
```bash
# 产品API
curl http://localhost:8081/api/v1/products/1

# 购物车API
curl http://localhost:8082/cart
```

## 接口文档

详见 [docs/API.md](docs/API.md)

## 测试用例

详见 [docs/testing/test_cases.md](docs/testing/test_cases.md)

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| Gateway | 8080 | API网关 |
| Product-service | 8081 | 产品服务 |
| Order-service | 8082 | 订单服务 |
| User-service | 8083 | 用户服务 |
| CMS-service | 8084 | CMS服务 |

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