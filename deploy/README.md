# 启动说明

## 运行环境要求

- **Java**: JDK 17+ (必需)
- **Node.js**: 18+ 
- **MySQL**: 8.0+ (可选，使用H2内存数据库可跳过)

---

## 方案一：本地启动（需要Java 17）

### 1. 安装 Java 17

```bash
# macOS 使用 Homebrew
brew install openjdk@17

# 设置 JAVA_HOME
export JAVA_HOME=/usr/local/opt/openjdk@17
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. 启动后端服务

```bash
cd backend

# 启动网关（端口8080）
java -jar gateway/target/gateway-1.0.0-SNAPSHOT.jar

# 启动用户服务（端口8083）- 新终端
java -jar user-service/target/user-service-1.0.0-SNAPSHOT.jar

# 启动商品服务（端口8081）
java -jar product-service/target/product-service-1.0.0-SNAPSHOT.jar

# 启动订单服务（端口8082）
java -jar order-service/target/order-service-1.0.0-SNAPSHOT.jar
```

### 3. 启动前端

```bash
cd frontend/nuxt3
npm install
npm run dev
```

访问 http://localhost:3000

---

## 方案二：Docker启动（推荐）

### 1. 初始化数据库

```bash
# 复制DDL到deploy目录
cp docs/02-ddl.sql deploy/init.sql
```

### 2. 启动所有服务

```bash
cd deploy
docker-compose up -d
```

服务端口：
- 网关: http://localhost:8080
- 前端: http://localhost:3000 (需单独启动nuxt3)
- MySQL: localhost:3306
- Redis: localhost:6379

---

## API端点

| 服务 | 端点 |
|------|------|
| 登录 | POST /api/v1/auth/login |
| 注册 | POST /api/v1/auth/register |
| 购物车 | GET/POST /api/v1/cart |
| 创建订单 | POST /api/v1/orders/checkout |
| 产品列表 | GET /api/v1/products |
| 用户信息 | GET /api/v1/user/profile |
| 地址列表 | GET /api/v1/user/addresses |