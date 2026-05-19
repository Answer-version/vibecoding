# 海外独立站功能完善设计文档

**版本**: 1.1  
**日期**: 2026-05-19  
**状态**: 功能完善分析

---

## 一、海外独立站功能完善程度总览

### 1.1 各模块完成度评估

| 模块 | 完成度 | 关键问题 |
|------|--------|----------|
| Product Service | 70% | 缺少CRUD API，无测试用例 |
| Order Service | 40% | 硬编码用户ID，空实现，内存存储 |
| User Service | 60% | 地址字段不一致，无测试用例 |
| CMS Service | 30% | 未接入网关，CRUD缺失 |
| Gateway | 50% | 缺少CMS路由，配置不完整 |
| Nuxt3 客户端 | 60% | 缺少结账、用户中心、博客详情 |
| Vue-Admin 后台 | 40% | 缺少文章/分类管理、设置 |

---

## 二、严重问题清单（必须修复）

### 2.1 Order Service - 购物车服务

| ID | 问题 | 位置 | 严重程度 | 修复方案 |
|----|------|------|----------|----------|----------|
| O-01 | 硬编码用户ID | CartServiceImpl | 🔴严重 | 从JWT获取真实用户ID |
| O-02 | updateItem空实现 | CartServiceImpl:94-98 | 🔴严重 | 实现数量更新逻辑 |
| O-03 | removeItem空实现 | CartServiceImpl:100-104 | 🔴严重 | 实现删除商品逻辑 |
| O-04 | 内存存储非持久化 | CartServiceImpl | 🔴严重 | 使用数据库持久化 |
| O-05 | 价格默认为0 | CartServiceImpl:84 | 🔴严重 | 调用的ProductService获取真实价格 |

### 2.2 Order Service - 订单服务

| ID | 问题 | 位置 | 严重程度 | 修复方案 |
|----|------|------|----------|----------|----------|
| O-06 | OrderItem未创建 | OrderServiceImpl | 🟠中等 | 创建订单时同步创建OrderItem |
| O-07 | 金额计算简单 | OrderServiceImpl | 🟠中等 | 根据商品实际价格计算 |
| O-08 | 订单查询TODO | OrderServiceImpl:42,124 | 🟠中等 | 实现OrderMapper查询 |

### 2.3 Order Service - 支付服务

| ID | 问题 | 位置 | 严重程度 | 修复方案 |
|----|------|------|----------|----------|----------|
| O-09 | 支付回调未验证签名 | PaymentServiceImpl | 🔴严重 | 实现真实签名验证逻辑 |
| O-10 | 缺少OrderMapper依赖 | PaymentServiceImpl | 🟠中等 | 注入OrderMapper |
| O-11 | 缺少Stripe支付网关 | PaymentGateway | 🟠中等 | 实现Stripe支付网关 |

### 2.4 User Service - 地址实体

| ID | 问题 | 位置 | 严重程度 | 修复方案 |
|----|------|------|----------|----------|----------|
| U-01 | 字段与DB不一致 | UserAddress实体 | 🔴严重 | 修复Entity字段映射 |

---

## 三、功能缺失清单

### 3.1 Product Service

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| P-01 | 创建产品API | ❌ 缺失 | 需实现POST /api/v1/products |
| P-02 | 更新产品API | ❌ 缺失 | 需实现PUT |
| P-03 | 删除产品API | ❌ 缺失 | 需实现DELETE |
| P-04 | 创建分类API | ❌ 缺失 | 需实现POST /categories |
| P-05 | 更新分类API | ❌ 缺失 | 需实现PUT |
| P-06 | 删除分类API | ❌ 缺失 | 需实现DELETE |
| P-07 | 创建品牌API | ❌ 缺失 | 需实现POST /brands |
| P-08 | SKU查询未实现 | ProductController | 🟠不完整 | 返回空数组 |

### 3.2 Order Service

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| O-12 | 订单状态自动更新 | ❌ 缺失 | 支付成功后自动更新状态 |
| O-13 | 订单追踪API | ❌ 缺失 | 需实现GET /orders/{id}/status |
| O-14 | 电子发票 | ❌ 缺失 | 需实现GET /orders/{id}/invoice |
| O-15 | 取消订单逻辑 | 🟠不完整 | 无库存释放逻辑 |

### 3.3 User Service

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| U-02 | 邮箱验证 | ❌ 缺失 | emailVerification流程 |
| U-03 | 手机验证 | ❌ 缺失 | phoneVerification流程 |
| U-04 | 忘记密码 | ❌ 缺失 | forgot-password流程 |
| U-05 | 密码修改 | ❌ 缺失 | change-password流程 |
| U-06 | 客户组功能 | ❌ 缺失 | CustomerGroup未实现 |
| U-07 | 用户列表API | ❌ 缺失 | 管理员查看用户列表 |
| U-08 | JWT黑名单 | ❌ 缺失 | Token失效机制 |
| U-09 | 登录日志 | ❌ 缺失 | 记录登录IP |

### 3.4 CMS Service

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| C-01 | CMS未接入网关 | ❌ 缺失 | 网关缺少CMS路由配置 |
| C-02 | 创建文章API | ❌ 缺失 | 需实现POST /cms/articles |
| C-03 | 更新文章API | ❌ 缺失 | 需实现PUT |
| C-04 | 删除文章API | ❌ 缺失 | 需实现DELETE |
| C-05 | 文章分类API | ❌ 缺失 | 需实现/cms/categories |
| C-06 | 全文搜索 | ❌ 缺失 | 关键词搜索 |

### 3.5 前端 - Nuxt3客户端

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| F-01 | 结账页面 | ❌ 缺失 | checkout.vue |
| F-02 | 登录/注册页面 | ❌ 缺失 | auth页面 |
| F-03 | 用户中心 | ❌ 缺失 | profile.vue |
| F-04 | 博客详情页 | ❌ 缺失 | blog/[slug].vue |
| F-05 | 订单追踪 | ❌ 缺失 | orders页面 |
| F-06 | 支付流程 | ❌ 缺失 | payment页面 |

### 3.6 前端 - Vue-Admin后台

| ID | 功能 | 状态 | 说明 |
|----|------|------|------|
| A-01 | 订单详情 | ❌ 缺失 | 订单操作 |
| A-02 | 客户详情 | ❌ 缺失 | 客户管理 |
| A-03 | 文章CRUD | ❌ 缺失 | Articles.vue |
| A-04 | 分类管理 | ❌ 缺失 | 页面 |
| A-05 | 品牌管理 | ❌ 缺失 | 页面 |
| A-06 | 设置功能 | ❌ 缺失 | Settings.vue |

---

## 四、修复优先级

### P0 - 阻断性问题（立即修复）

1. **CartService硬编码用户ID** - 导致购物车无法区分用户
2. **UserAddress字段不一致** - 导致地址CRUD全部失败
3. **支付回调未验证签名** - 严重安全风险
4. **updateItem/removeItem空实现** - 购物车功能不可用

### P1 - 核心功能缺失（24小时内修复）

1. OrderService OrderItem创建 - 订单明细不完整
2. JWT黑名单机制 - 登出后Token仍可用
3. 创建/更新/删除产品API - 后台无法管理商品
4. CMS接入网关 - 前端无法访问CMS

### P2 - 用户体验优化（72小时内修复）

1. 结账页面 - 客户无法完成购买
2. 登录/注册 - 客户无法注册登录
3. 用户中心 - 客户无法管理个人信息
4. 订单追踪 - 客户无法查看订单状态

### P3 - 完善功能（1周内修复）

1. 文章管理CRUD
2. 分类/品牌管理
3. Stripe支付网关
4. 客户组功能

### P4 - 增强功能（2周内完成）

1. 邮箱/手机验证
2. 忘记密码
3. 登录日志审计
4. 客户专属价

---

## 五、技术改进建议

### 5.1 数据库持久化

当前 CartServiceImpl 使用 ConcurrentHashMap 内存存储，需改为：

```java
// CartMapper + CartItemMapper
// 使用 MyBatis-Plus 进行数据库持久化
```

### 5.2 API安全增强

```java
// JWT黑名单实现
@Component
public class JwtBlacklistService {
    private final RedisTemplate<String, String> redis;
    
    // Token加入黑名单
    public void addToBlacklist(String token, long expireSeconds) {
        redis.opsForValue().set("jwt:blacklist:" + token, "1", 
            Duration.ofSeconds(expireSeconds));
    }
    
    // 检查是否在黑名单
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
            redis.hasKey("jwt:blacklist:" + token));
    }
}
```

### 5.3 价格计算优化

```java
// 从ProductService获取真实价格
@Service
public class CartServiceImpl {
    
    public BigDecimal calculateItemPrice(Long skuId, Integer quantity) {
        ProductSku sku = productService.getSkuById(skuId);
        // 应用客户专属价、促销折扣等
        return priceCalculationService.calculate(
            sku.getUsdPrice(), quantity, userId);
    }
}
```

---

## 六、测试用例覆盖计划

### 6.1 单元测试覆盖

| 服务 | 测试类 | 覆盖目标 |
|------|--------|----------|
| ProductService | ProductServiceTest | CRUD操作 |
| CartService | CartServiceTest | 购物车增删改查 |
| OrderService | OrderServiceTest | 订单创建/取消 |
| AuthService | AuthServiceTest | 登录认证 |

### 6.2 集成测试覆盖

| 场景 | 测试类 | 说明 |
|------|--------|------|
| 完整购物流程 | CheckoutIntegrationTest | 从浏览到支付 |
| 用户注册流程 | RegisterIntegrationTest | 注册到激活 |
| 管理员操作 | AdminIntegrationTest | 商品管理 |

---

## 七、结论

海外独立站项目当前功能完成度评估：

- **后端核心服务**: 约50%完成，存在多个阻断性问题
- **前端客户端**: 约60%完成，缺少关键交易流程
- **管理后台**: 约40%完成，功能不完善
- **测试覆盖**: 0%，完全缺失

**建议立即行动**：
1. 优先修复P0级别问题，使系统可运行
2. 完善购物流程，使客户可完成购买
3. 添加测试用例，确保功能稳定
4. 迭代优化，提升用户体验