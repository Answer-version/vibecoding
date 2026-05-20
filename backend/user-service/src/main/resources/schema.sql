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
    avatar VARCHAR(500),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    user_type TINYINT DEFAULT 1,
    customer_group_id BIGINT,
    company_name VARCHAR(200),
    company_id BIGINT,
    locale VARCHAR(10),
    currency VARCHAR(10),
    sex TINYINT,
    birthday DATE,
    email_verified TINYINT DEFAULT 0,
    phone_verified TINYINT DEFAULT 0,
    kyc_status TINYINT DEFAULT 0,
    credit_limit DECIMAL(18,2),
    available_credit DECIMAL(18,2),
    last_login_time TIMESTAMP,
    last_login_ip VARCHAR(50),
    login_count INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    UNIQUE(username),
    UNIQUE(email)
);

-- 验证码表
CREATE TABLE IF NOT EXISTS verification_code (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20),
    email VARCHAR(100),
    code VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL COMMENT 'login/register/reset',
    expire_time TIMESTAMP NOT NULL,
    used TINYINT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入测试用户
INSERT INTO users (username, email, password, nickname, status) VALUES
('test', 'test@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Test User', 1);
