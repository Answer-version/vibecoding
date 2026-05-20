-- H2 Database Schema for Development
-- VibeCommerce海外独立站

SET MODE MySQL;

-- 用户表 (user是H2保留字，改为users)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    user_type TINYINT DEFAULT 1,
    status TINYINT DEFAULT 1,
    login_count INT DEFAULT 0,
    last_login_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(username),
    UNIQUE(email)
);

-- 用户收货地址
CREATE TABLE IF NOT EXISTS user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    receiver_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    province VARCHAR(100),
    city VARCHAR(100),
    district VARCHAR(100),
    detail_address VARCHAR(500) NOT NULL,
    is_default TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 产品分类
CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT 0,
    level TINYINT DEFAULT 1,
    category_code VARCHAR(50),
    name VARCHAR(200) NOT NULL,
    name_en VARCHAR(200),
    image VARCHAR(500),
    icon VARCHAR(200),
    description TEXT,
    seo_title VARCHAR(200),
    seo_keywords VARCHAR(500),
    seo_description VARCHAR(1000),
    sort_order INT DEFAULT 0,
    is_nav TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 品牌
CREATE TABLE IF NOT EXISTS brand (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand_code VARCHAR(50),
    name VARCHAR(200) NOT NULL,
    name_en VARCHAR(200),
    logo VARCHAR(500),
    image VARCHAR(500),
    description TEXT,
    website VARCHAR(200),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 产品
CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL,
    name VARCHAR(500) NOT NULL,
    name_en VARCHAR(500),
    subtitle VARCHAR(500),
    subtitle_en VARCHAR(500),
    brand_id BIGINT,
    category_id BIGINT,
    description TEXT,
    description_en TEXT,
    keywords VARCHAR(500),
    weight DECIMAL(10,2),
    length_val DECIMAL(10,2),
    width_val DECIMAL(10,2),
    height_val DECIMAL(10,2),
    price DECIMAL(18,2),
    original_price DECIMAL(18,2),
    cost_price DECIMAL(18,2),
    stock_quantity INT DEFAULT 0,
    is_featured TINYINT DEFAULT 0,
    is_new TINYINT DEFAULT 0,
    is_hot TINYINT DEFAULT 0,
    seo_title VARCHAR(200),
    seo_keywords VARCHAR(500),
    seo_description VARCHAR(1000),
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    view_count INT DEFAULT 0,
    sales_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(product_code)
);

-- 产品SKU
CREATE TABLE IF NOT EXISTS product_sku (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(50) NOT NULL,
    sku_name VARCHAR(200),
    price DECIMAL(18,2),
    original_price DECIMAL(18,2),
    stock_quantity INT DEFAULT 0,
    image VARCHAR(500),
    attributes VARCHAR(500),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(sku_code)
);

-- 购物车
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    session_id VARCHAR(100),
    cart_type TINYINT DEFAULT 1,
    item_count INT DEFAULT 0,
    usd_amount DECIMAL(18,2) DEFAULT 0,
    eur_amount DECIMAL(18,2) DEFAULT 0,
    gbp_amount DECIMAL(18,2) DEFAULT 0,
    cny_amount DECIMAL(18,2) DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 购物车项
CREATE TABLE IF NOT EXISTS cart_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cart_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_id BIGINT,
    sku_code VARCHAR(50),
    product_name VARCHAR(200),
    sku_attrs VARCHAR(500),
    quantity INT DEFAULT 1,
    usd_price DECIMAL(18,2) DEFAULT 0,
    usd_amount DECIMAL(18,2) DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 订单
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(18,2),
    discount_amount DECIMAL(18,2) DEFAULT 0,
    pay_amount DECIMAL(18,2),
    pay_method VARCHAR(50),
    pay_time TIMESTAMP,
    status VARCHAR(20) DEFAULT 'PENDING',
    receiver_name VARCHAR(100),
    receiver_phone VARCHAR(50),
    receiver_address VARCHAR(500),
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(order_no)
);

-- 订单明细
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    sku_id BIGINT,
    product_name VARCHAR(500),
    sku_name VARCHAR(200),
    price DECIMAL(18,2),
    quantity INT DEFAULT 1,
    total_amount DECIMAL(18,2),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- CMS文章
CREATE TABLE IF NOT EXISTS cms_article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    title_en VARCHAR(200),
    slug VARCHAR(200) NOT NULL,
    summary TEXT,
    summary_en TEXT,
    content TEXT,
    content_en TEXT,
    category_id BIGINT,
    author VARCHAR(100),
    image VARCHAR(500),
    view_count INT DEFAULT 0,
    is_featured TINYINT DEFAULT 0,
    status TINYINT DEFAULT 1,
    publish_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(slug)
);

-- 支付记录
CREATE TABLE IF NOT EXISTS payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    pay_method VARCHAR(50) NOT NULL,
    amount DECIMAL(18,2),
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    pay_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

-- 优惠券表
CREATE TABLE IF NOT EXISTS coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    coupon_code VARCHAR(50) NOT NULL UNIQUE COMMENT '优惠码',
    name VARCHAR(100) NOT NULL COMMENT '优惠券名称',
    name_en VARCHAR(100) COMMENT '英文名称',
    type TINYINT NOT NULL COMMENT '1:满减 2:折扣 3:免运费',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '优惠金额/折扣率(0-1)',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    total_quantity INT NOT NULL COMMENT '总发行量',
    used_quantity INT DEFAULT 0 COMMENT '已使用数量',
    start_time DATETIME NOT NULL COMMENT '生效时间',
    end_time DATETIME NOT NULL COMMENT '过期时间',
    status TINYINT DEFAULT 1 COMMENT '1:启用 0:禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户优惠券表
CREATE TABLE IF NOT EXISTS user_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    coupon_id BIGINT NOT NULL,
    status TINYINT DEFAULT 0 COMMENT '0:未使用 1:已使用 2:已过期',
    get_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    use_time DATETIME,
    order_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 订单优惠券关联表
CREATE TABLE IF NOT EXISTS order_coupon (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL UNIQUE,
    coupon_id BIGINT NOT NULL,
    coupon_code VARCHAR(50) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL COMMENT '实际优惠金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 插入初始测试数据
INSERT INTO category (id, parent_id, level, name, name_en, sort_order, status) VALUES
(1, 0, 1, 'Electronics', 'Electronics', 1, 1),
(2, 0, 1, 'Clothing', 'Clothing', 2, 1),
(3, 0, 1, 'Home & Garden', 'Home & Garden', 3, 1);

INSERT INTO brand (id, brand_code, name, name_en, status) VALUES
(1, 'APPLE', 'Apple', 'Apple', 1),
(2, 'SAMSUNG', 'Samsung', 'Samsung', 1),
(3, 'SONY', 'Sony', 'Sony', 1);

INSERT INTO product (id, product_code, name, name_en, brand_id, category_id, price, original_price, stock_quantity, status) VALUES
(1, 'PROD001', 'iPhone 15 Pro', 'iPhone 15 Pro', 1, 1, 999.00, 1099.00, 100, 1),
(2, 'PROD002', 'Samsung Galaxy S24', 'Samsung Galaxy S24', 2, 1, 899.00, 999.00, 100, 1),
(3, 'PROD003', 'Sony WH-1000XM5', 'Sony WH-1000XM5', 3, 1, 349.00, 399.00, 50, 1);

INSERT INTO cms_article (id, title, title_en, slug, summary, summary_en, content, category_id, status) VALUES
(1, 'Welcome to VibeCommerce', 'Welcome to VibeCommerce', 'welcome', 'Welcome to our store', 'Welcome to our store', 'This is our official store.', 1, 1),
(2, 'Shipping Info', 'Shipping Info', 'shipping', 'Shipping information', 'Shipping information', 'Free shipping on orders over $50.', 1, 1);

-- 插入优惠券测试数据
INSERT INTO coupon (id, coupon_code, name, name_en, type, discount_value, min_amount, total_quantity, used_quantity, start_time, end_time, status) VALUES
(1, 'SAVE10', 'Save $10', 'Save $10', 1, 10.00, 50.00, 100, 0, '2025-01-01 00:00:00', '2027-12-31 23:59:59', 1),
(2, 'SAVE20', 'Save $20', 'Save $20', 1, 20.00, 100.00, 50, 0, '2025-01-01 00:00:00', '2027-12-31 23:59:59', 1),
(3, 'DISCOUNT15', '15% Off', '15% Off', 2, 0.15, 100.00, 30, 0, '2025-01-01 00:00:00', '2027-12-31 23:59:59', 1),
(4, 'FREESHIP', 'Free Shipping', 'Free Shipping', 3, 9.99, 30.00, 200, 0, '2025-01-01 00:00:00', '2027-12-31 23:59:59', 1);