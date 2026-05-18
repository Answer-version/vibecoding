-- =====================================================
-- 外贸独立站核心建表语句 (MySQL 8.0)
-- 版本: 1.0
-- 日期: 2026-05-18
-- =====================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1. 站点配置
-- ----------------------------
DROP TABLE IF EXISTS `site_config`;
CREATE TABLE `site_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `site_code` varchar(50) NOT NULL COMMENT '站点代码',
  `site_name` varchar(200) DEFAULT NULL COMMENT '站点名称',
  `default_language` varchar(10) DEFAULT 'en' COMMENT '默认语言',
  `default_currency` varchar(10) DEFAULT 'USD' COMMENT '默认货币',
  `timezone` varchar(50) DEFAULT 'UTC' COMMENT '时区',
  `logo` varchar(500) DEFAULT NULL COMMENT 'Logo URL',
  `favicon` varchar(500) DEFAULT NULL COMMENT 'Favicon',
  `company_name` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `company_email` varchar(100) DEFAULT NULL COMMENT '公司邮箱',
  `company_phone` varchar(50) DEFAULT NULL COMMENT '公司电话',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `seo_title` varchar(200) DEFAULT NULL COMMENT 'SEO标题',
  `seo_keywords` varchar(500) DEFAULT NULL COMMENT 'SEO关键词',
  `seo_description` varchar(1000) DEFAULT NULL COMMENT 'SEO描述',
  `status` tinyint DEFAULT 1 COMMENT '状态 0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint DEFAULT 0 COMMENT '软删除 0否 1是',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_site_code` (`site_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点配置';

-- ----------------------------
-- 2. 地区/国家
-- ----------------------------
DROP TABLE IF EXISTS `region`;
CREATE TABLE `region` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` bigint DEFAULT 0 COMMENT '父级ID',
  `region_type` tinyint NOT NULL COMMENT '类型 1国家 2省/州 3城市',
  `region_code` varchar(20) NOT NULL COMMENT '地区代码 ISO',
  `region_name` varchar(200) NOT NULL COMMENT '地区名称',
  `region_name_en` varchar(200) DEFAULT NULL COMMENT '英文名称',
  `zip_code` varchar(20) DEFAULT NULL COMMENT '邮编',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_code` (`region_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地区表';

-- ----------------------------
-- 3. 货币
-- ----------------------------
DROP TABLE IF EXISTS `currency`;
CREATE TABLE `currency` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `currency_code` varchar(10) NOT NULL COMMENT '货币代码',
  `currency_name` varchar(100) NOT NULL COMMENT '货币名称',
  `currency_symbol` varchar(10) DEFAULT NULL COMMENT '符号',
  `exchange_rate` decimal(18,6) DEFAULT 1.000000 COMMENT '对美元汇率',
  `decimal_places` tinyint DEFAULT 2 COMMENT '小数位',
  `decimal_separator` varchar(5) DEFAULT '.' COMMENT '小数分隔符',
  `thousand_separator` varchar(5) DEFAULT ',' COMMENT '千分位分隔符',
  `status` tinyint DEFAULT 1 COMMENT '状态',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`currency_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='货币表';

-- ----------------------------
-- 4. 产品分类
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT 0 COMMENT '父分类ID',
  `level` tinyint DEFAULT 1 COMMENT '层级',
  `category_code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `name` varchar(200) NOT NULL COMMENT '分类名称',
  `name_en` varchar(200) DEFAULT NULL COMMENT '英文名称',
  `image` varchar(500) DEFAULT NULL COMMENT '图片',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `description` text COMMENT '描述',
  `seo_title` varchar(200) DEFAULT NULL,
  `seo_keywords` varchar(500) DEFAULT NULL,
  `seo_description` varchar(1000) DEFAULT NULL,
  `sort_order` int DEFAULT 0,
  `is_nav` tinyint DEFAULT 0 COMMENT '是否显示在导航',
  `status` tinyint DEFAULT 1 COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_parent` (`parent_id`),
  KEY `idx_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类';

-- ----------------------------
-- 5. 品牌
-- ----------------------------
DROP TABLE IF EXISTS `brand`;
CREATE TABLE `brand` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `brand_code` varchar(50) DEFAULT NULL COMMENT '品牌编码',
  `name` varchar(200) NOT NULL COMMENT '品牌名称',
  `name_en` varchar(200) DEFAULT NULL COMMENT '英文名称',
  `logo` varchar(500) DEFAULT NULL COMMENT 'Logo',
  `image` varchar(500) DEFAULT NULL COMMENT '图片',
  `description` text COMMENT '描述',
  `website` varchar(200) DEFAULT NULL COMMENT '官网',
  `sort_order` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`brand_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌';

-- ----------------------------
-- 6. 产品(SPU)
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) NOT NULL COMMENT '产品编码',
  `name` varchar(500) NOT NULL COMMENT '产品名称',
  `name_en` varchar(500) DEFAULT NULL COMMENT '英文名称',
  `subtitle` varchar(500) DEFAULT NULL COMMENT '副标题',
  `subtitle_en` varchar(500) DEFAULT NULL,
  `brand_id` bigint DEFAULT NULL COMMENT '品牌ID',
  `category_id` bigint DEFAULT NULL COMMENT '主分类ID',
  `description` text COMMENT '详细描述',
  `description_en` text COMMENT '英文描述',
  `keywords` varchar(500) DEFAULT NULL COMMENT '关键词',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量Kg',
  `length` decimal(10,2) DEFAULT NULL COMMENT '长cm',
  `width` decimal(10,2) DEFAULT NULL COMMENT '宽cm',
  `height` decimal(10,2) DEFAULT NULL COMMENT '高cm',
  `is_featured` tinyint DEFAULT 0 COMMENT '是否推荐',
  `is_new` tinyint DEFAULT 0 COMMENT '是否新品',
  `is_hot` tinyint DEFAULT 0 COMMENT '是否热卖',
  `seo_title` varchar(200) DEFAULT NULL,
  `seo_keywords` varchar(500) DEFAULT NULL,
  `seo_description` varchar(1000) DEFAULT NULL,
  `view_count` int DEFAULT 0 COMMENT '浏览次数',
  `sale_count` int DEFAULT 0 COMMENT '销量',
  `review_count` int DEFAULT 0 COMMENT '评价数',
  `avg_score` decimal(3,2) DEFAULT 5.00 COMMENT '评分',
  `sort_order` int DEFAULT 0,
  `status` tinyint DEFAULT 1 COMMENT '状态 0下架 1上架',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`product_code`),
  KEY `idx_brand` (`brand_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品SPU';

-- ----------------------------
-- 7. 产品分类关联
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `is_primary` tinyint DEFAULT 0 COMMENT '是否主分类',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品分类关联';

-- ----------------------------
-- 8. 产品图片
-- ----------------------------
DROP TABLE IF EXISTS `product_image`;
CREATE TABLE `product_image` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `sku_id` bigint DEFAULT NULL COMMENT 'SKU ID',
  `image_type` tinyint DEFAULT 1 COMMENT '类型 1主图 2详情图 3属性图',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `thumb_url` varchar(500) DEFAULT NULL COMMENT '缩略图',
  `sort_order` int DEFAULT 0,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品图片';

-- ----------------------------
-- 9. 产品SKU
-- ----------------------------
DROP TABLE IF EXISTS `product_sku`;
CREATE TABLE `product_sku` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint NOT NULL,
  `sku_code` varchar(50) NOT NULL COMMENT 'SKU编码',
  `barcode` varchar(50) DEFAULT NULL COMMENT '条形码',
  `name` varchar(500) DEFAULT NULL COMMENT 'SKU名称',
  `usd_price` decimal(18,2) DEFAULT 0.00 COMMENT 'USD售价',
  `eur_price` decimal(18,2) DEFAULT 0.00 COMMENT 'EUR售价',
  `gbp_price` decimal(18,2) DEFAULT 0.00 COMMENT 'GBP售价',
  `cny_price` decimal(18,2) DEFAULT 0.00 COMMENT 'CNY售价',
  `cost_price` decimal(18,2) DEFAULT 0.00 COMMENT '成本价',
  `usd_retail_price` decimal(18,2) DEFAULT 0.00 COMMENT 'USD零售价(B2C)',
  `usd_wholesale_price` decimal(18,2) DEFAULT 0.00 COMMENT 'USD批发价(B2B)',
  `moq` int DEFAULT 1 COMMENT '最小起订量',
  `stock_quantity` int DEFAULT 0 COMMENT '库存数量',
  `low_stock_alert` int DEFAULT 10 COMMENT '低库存预警',
  `stock_status` tinyint DEFAULT 1 COMMENT '库存状态 1有货 2缺货 3预售',
  `weight` decimal(10,2) DEFAULT NULL COMMENT '重量Kg',
  `length` decimal(10,2) DEFAULT NULL,
  `width` decimal(10,2) DEFAULT NULL,
  `height` decimal(10,2) DEFAULT NULL,
  `sku_attrs` json DEFAULT NULL COMMENT '属性JSON {"color":"red","size":"M"}',
  `image_url` varchar(500) DEFAULT NULL COMMENT '主图',
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_sku` (`product_id`, `sku_code`),
  KEY `idx_code` (`sku_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品SKU';

-- ----------------------------
-- 10. 用户
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_type` tinyint DEFAULT 1 COMMENT '类型 1个人 2企业',
  `customer_group_id` bigint DEFAULT NULL COMMENT '客户组ID',
  `username` varchar(100) DEFAULT NULL COMMENT '用户名',
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `country_code` varchar(10) DEFAULT NULL COMMENT '手机区号',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `first_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) DEFAULT NULL,
  `company_name` varchar(200) DEFAULT NULL COMMENT '公司名',
  `company_id` bigint DEFAULT NULL COMMENT '企业ID',
  `locale` varchar(10) DEFAULT 'en' COMMENT '语言',
  `currency` varchar(10) DEFAULT 'USD' COMMENT '货币',
  `sex` tinyint DEFAULT 0 COMMENT '性别 0未知 1男 2女',
  `birthday` date DEFAULT NULL,
  `email_verified` tinyint DEFAULT 0 COMMENT '邮箱验证',
  `phone_verified` tinyint DEFAULT 0 COMMENT '手机验证',
  `kyc_status` tinyint DEFAULT 0 COMMENT 'KYC状态 0未认证 1认证中 2已认证',
  `credit_limit` decimal(18,2) DEFAULT 0.00 COMMENT '信用额度',
  `available_credit` decimal(18,2) DEFAULT 0.00 COMMENT '可用信用',
  `last_login_time` datetime DEFAULT NULL,
  `last_login_ip` varchar(50) DEFAULT NULL,
  `login_count` int DEFAULT 0 COMMENT '登录次数',
  `status` tinyint DEFAULT 1 COMMENT '状态 0禁用 1正常 2待审核',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `uk_phone` (`phone`),
  KEY `idx_customer_group` (`customer_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户';

-- ----------------------------
-- 11. 用户地址
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `address_type` tinyint DEFAULT 1 COMMENT '类型 1收货 2账单',
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `company` varchar(200) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `country_code` varchar(10) NOT NULL COMMENT '国家代码',
  `country_name` varchar(200) DEFAULT NULL,
  `state_code` varchar(20) DEFAULT NULL,
  `state_name` varchar(200) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `district` varchar(100) DEFAULT NULL,
  `address1` varchar(500) NOT NULL COMMENT '地址1',
  `address2` varchar(500) DEFAULT NULL,
  `zip_code` varchar(20) DEFAULT NULL,
  `is_default` tinyint DEFAULT 0 COMMENT '是否默认',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址';

-- ----------------------------
-- 12. 客户分组
-- ----------------------------
DROP TABLE IF EXISTS `customer_group`;
CREATE TABLE `customer_group` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_type` tinyint NOT NULL COMMENT '类型 1B2C等级 2B2B批发商',
  `group_code` varchar(50) NOT NULL COMMENT '分组代码',
  `group_name` varchar(100) NOT NULL COMMENT '分组名称',
  `group_name_en` varchar(100) DEFAULT NULL,
  `discount_rate` decimal(5,2) DEFAULT 0.00 COMMENT '折扣率',
  `moq` int DEFAULT 1 COMMENT '最小起订量',
  `description` varchar(500) DEFAULT NULL,
  `sort_order` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户分组';

-- ----------------------------
-- 13. 购物车/询盘篮
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_type` tinyint DEFAULT 1 COMMENT '类型 1购物车 2询盘篮',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `session_id` varchar(100) DEFAULT NULL COMMENT '会话ID(游客)',
  `item_count` int DEFAULT 0 COMMENT '商品数量',
  `usd_amount` decimal(18,2) DEFAULT 0.00 COMMENT 'USD金额',
  `eur_amount` decimal(18,2) DEFAULT 0.00,
  `gbp_amount` decimal(18,2) DEFAULT 0.00,
  `cny_amount` decimal(18,2) DEFAULT 0.00,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车';

-- ----------------------------
-- 14. 购物车项
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `sku_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT 1 COMMENT '数量',
  `usd_price` decimal(18,2) DEFAULT 0.00 COMMENT '单价',
  `usd_amount` decimal(18,2) DEFAULT 0.00 COMMENT '金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_cart` (`cart_id`),
  KEY `idx_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车项';

-- ----------------------------
-- 15. 订单
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_type` tinyint DEFAULT 1 COMMENT '类型 1B2C 2B2B',
  `order_no` varchar(50) NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `customer_group_id` bigint DEFAULT NULL COMMENT '客户组',
  `quote_id` bigint DEFAULT NULL COMMENT '来源报价ID',
  `order_status` varchar(20) DEFAULT 'PENDING' COMMENT '订单状态',
  `pay_status` varchar(20) DEFAULT 'PENDING' COMMENT '支付状态',
  `ship_status` varchar(20) DEFAULT 'PENDING' COMMENT '发货状态',
  `usd_amount` decimal(18,2) DEFAULT 0.00 COMMENT '商品金额',
  `shipping_fee` decimal(18,2) DEFAULT 0.00 COMMENT '运费',
  `tax_amount` decimal(18,2) DEFAULT 0.00 COMMENT '税费',
  `discount_amount` decimal(18,2) DEFAULT 0.00 COMMENT '优惠',
  `total_amount` decimal(18,2) DEFAULT 0.00 COMMENT '总金额',
  `currency` varchar(10) DEFAULT 'USD' COMMENT '货币',
  `pay_method` varchar(20) DEFAULT NULL COMMENT '支付方式',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `ship_name` varchar(100) DEFAULT NULL,
  `ship_phone` varchar(20) DEFAULT NULL,
  `ship_country_code` varchar(10) DEFAULT NULL,
  `ship_country` varchar(100) DEFAULT NULL,
  `ship_state` varchar(100) DEFAULT NULL,
  `ship_city` varchar(100) DEFAULT NULL,
  `ship_address1` varchar(500) DEFAULT NULL,
  `ship_address2` varchar(500) DEFAULT NULL,
  `ship_zip` varchar(20) DEFAULT NULL,
  `shipping_method` varchar(50) DEFAULT NULL COMMENT '配送方式',
  `shipping_fee_usd` decimal(18,2) DEFAULT 0.00 COMMENT '运费USD',
  `tracking_no` varchar(100) DEFAULT NULL COMMENT '物流单号',
  `ship_time` datetime DEFAULT NULL COMMENT '发货时间',
  `deliver_time` datetime DEFAULT NULL COMMENT '送达时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `customer_note` varchar(500) DEFAULT NULL COMMENT '客户备注',
  `admin_note` varchar(500) DEFAULT NULL COMMENT '管理员备注',
  `ip` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`order_status`),
  KEY `idx_create` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单';

-- ----------------------------
-- 16. 订单明细
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `sku_id` bigint NOT NULL,
  `product_name` varchar(500) NOT NULL,
  `sku_code` varchar(50) DEFAULT NULL,
  `sku_attrs` json DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `usd_price` decimal(18,2) NOT NULL COMMENT '单价',
  `usd_amount` decimal(18,2) NOT NULL COMMENT '金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_sku` (`sku_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细';

-- ----------------------------
-- 17. 订单状态日志
-- ----------------------------
DROP TABLE IF EXISTS `order_status_log`;
CREATE TABLE `order_status_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `order_status` varchar(20) NOT NULL,
  `action` varchar(50) DEFAULT NULL COMMENT '动作',
  `comment` varchar(500) DEFAULT NULL,
  `operator_type` tinyint DEFAULT 1 COMMENT '操作者 1用户 2系统 3管理员',
  `operator_id` bigint DEFAULT NULL COMMENT '操作人ID',
  `ip` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_order` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单状态日志';

-- ----------------------------
-- 18. 报价单(B2B)
-- ----------------------------
DROP TABLE IF EXISTS `quote`;
CREATE TABLE `quote` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quote_no` varchar(50) NOT NULL COMMENT '报价单号',
  `user_id` bigint NOT NULL COMMENT '客户ID',
  `quote_status` varchar(20) DEFAULT 'DRAFT' COMMENT '状态',
  `usd_amount` decimal(18,2) DEFAULT 0.00 COMMENT '报价金额',
  `valid_days` int DEFAULT 30 COMMENT '有效天数',
  `moq` int DEFAULT 1 COMMENT '最小起订量',
  `payment_terms` varchar(100) DEFAULT NULL COMMENT '付款条款',
  `shipping_terms` varchar(100) DEFAULT NULL COMMENT '交货条款',
  `valid_until` date DEFAULT NULL COMMENT '有效期至',
  `customer_note` varchar(500) DEFAULT NULL COMMENT '客户备注',
  `admin_note` varchar(500) DEFAULT NULL COMMENT '管理员备注',
  `admin_id` bigint DEFAULT NULL COMMENT '报价人ID',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_quote_no` (`quote_no`),
  KEY `idx_user` (`user_id`),
  KEY `idx_status` (`quote_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价单';

-- ----------------------------
-- 19. 报价明细
-- ----------------------------
DROP TABLE IF EXISTS `quote_item`;
CREATE TABLE `quote_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quote_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `sku_id` bigint DEFAULT NULL,
  `product_name` varchar(500) NOT NULL,
  `sku_code` varchar(50) DEFAULT NULL,
  `quantity` int NOT NULL DEFAULT 1,
  `usd_price` decimal(18,2) NOT NULL COMMENT '报价单价',
  `usd_amount` decimal(18,2) NOT NULL COMMENT '报价金额',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_quote` (`quote_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报价明细';

-- ----------------------------
-- 20. 支付
-- ----------------------------
DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_no` varchar(50) NOT NULL COMMENT '支付单号',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pay_method` varchar(50) NOT NULL COMMENT '支付方式',
  `pay_channel` varchar(50) DEFAULT NULL COMMENT '支付渠道',
  `amount` decimal(18,2) NOT NULL COMMENT '金额',
  `currency` varchar(10) DEFAULT 'USD',
  `exchange_rate` decimal(18,6) DEFAULT 1.000000,
  `real_amount` decimal(18,2) DEFAULT NULL COMMENT '实际金额',
  `real_currency` varchar(10) DEFAULT NULL,
  `pay_status` varchar(20) DEFAULT 'PENDING' COMMENT '支付状态',
  `channel_trade_no` varchar(100) DEFAULT NULL COMMENT '渠道交易号',
  `channel_order_no` varchar(100) DEFAULT NULL COMMENT '渠道订单号',
  `pay_url` varchar(1000) DEFAULT NULL COMMENT '支付链接',
  `notify_data` json DEFAULT NULL COMMENT '回调数据',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order` (`order_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录';

-- ----------------------------
-- 21. 配送方式
-- ----------------------------
DROP TABLE IF EXISTS `shipping_method`;
CREATE TABLE `shipping_method` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `method_code` varchar(50) NOT NULL COMMENT '方式代码',
  `method_name` varchar(100) NOT NULL COMMENT '名称',
  `method_name_en` varchar(100) DEFAULT NULL,
  `carrier` varchar(100) DEFAULT NULL COMMENT '承运商',
  `delivery_days` int DEFAULT NULL COMMENT '送达天数',
  `first_weight` decimal(10,2) DEFAULT 0.00 COMMENT '首重',
  `first_fee` decimal(18,2) DEFAULT 0.00 COMMENT '首费',
  `continue_weight` decimal(10,2) DEFAULT 0.00 COMMENT '续重',
  `continue_fee` decimal(18,2) DEFAULT 0.00 COMMENT '续费',
  `free_shipping_amount` decimal(18,2) DEFAULT NULL COMMENT '满额包邮',
  `sort_order` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`method_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送方式';

-- ----------------------------
-- 22. 仓库
-- ----------------------------
DROP TABLE IF EXISTS `warehouse`;
CREATE TABLE `warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `warehouse_code` varchar(50) NOT NULL,
  `warehouse_name` varchar(200) NOT NULL COMMENT '仓库名称',
  `warehouse_type` tinyint DEFAULT 1 COMMENT '类型 1自有 2海外仓 3第三方',
  `country_code` varchar(10) DEFAULT NULL COMMENT '国家',
  `country_name` varchar(100) DEFAULT NULL,
  `state` varchar(100) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `contact` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `is_default` tinyint DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`warehouse_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库';

-- ----------------------------
-- 23. 库存
-- ----------------------------
DROP TABLE IF EXISTS `inventory`;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sku_id` bigint NOT NULL,
  `warehouse_id` bigint NOT NULL,
  `quantity` int DEFAULT 0 COMMENT '可用库存',
  `reserved_quantity` int DEFAULT 0 COMMENT '预留数量',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sku_warehouse` (`sku_id`, `warehouse_id`),
  KEY `idx_sku` (`sku_id`),
  KEY `idx_warehouse` (`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存';

-- ----------------------------
-- 24. CMS文章分类
-- ----------------------------
DROP TABLE IF EXISTS `cms_category`;
CREATE TABLE `cms_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `parent_id` bigint DEFAULT 0,
  `category_name` varchar(200) NOT NULL,
  `category_name_en` varchar(200) DEFAULT NULL,
  `slug` varchar(100) NOT NULL COMMENT '别名',
  `description` varchar(500) DEFAULT NULL,
  `sort_order` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CMS分类';

-- ----------------------------
-- 25. CMS文章
-- ----------------------------
DROP TABLE IF EXISTS `cms_article`;
CREATE TABLE `cms_article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category_id` bigint NOT NULL,
  `article_title` varchar(500) NOT NULL COMMENT '标题',
  `article_title_en` varchar(500) DEFAULT NULL,
  `slug` varchar(100) NOT NULL COMMENT '别名',
  `summary` text COMMENT '摘要',
  `summary_en` text,
  `content` longtext COMMENT '内容',
  `content_en` longtext,
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图',
  `seo_title` varchar(200) DEFAULT NULL,
  `seo_keywords` varchar(500) DEFAULT NULL,
  `seo_description` varchar(1000) DEFAULT NULL,
  `view_count` int DEFAULT 0,
  `sort_order` int DEFAULT 0,
  `is_featured` tinyint DEFAULT 0 COMMENT '推荐',
  `status` tinyint DEFAULT 1 COMMENT '状态 0草稿 1已发布',
  `publish_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_slug` (`slug`),
  KEY `idx_category` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CMS文章';

-- ----------------------------
-- 26. 管理员
-- ----------------------------
DROP TABLE IF EXISTS `admin_user`;
CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `real_name` varchar(100) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `password` varchar(200) NOT NULL,
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `avatar` varchar(500) DEFAULT NULL,
  `login_ip` varchar(50) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `login_count` int DEFAULT 0,
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员';

-- ----------------------------
-- 27. 角色
-- ----------------------------
DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) NOT NULL,
  `role_name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `permission_ids` varchar(500) DEFAULT NULL COMMENT '权限ID列表',
  `status` tinyint DEFAULT 1,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色';

SET FOREIGN_KEY_CHECKS = 1;