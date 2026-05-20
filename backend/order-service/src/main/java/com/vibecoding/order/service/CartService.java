package com.vibecoding.order.service;

import com.vibecoding.order.entity.Cart;

import java.math.BigDecimal;
import java.util.Map;

public interface CartService {

    /**
     * 获取用户购物车
     * @param userId 用户ID（从JWT获取）
     * @return 购物车信息
     */
    Map<String, Object> getCart(Long userId);

    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param productId 产品ID
     * @param skuId SKU ID（可选）
     * @param quantity 数量
     * @param usdPrice 单价（USD）
     * @param productName 产品名称
     * @param skuCode SKU编码
     * @param skuAttrs SKU属性JSON
     * @return 购物车
     */
    Cart addItem(Long userId, Long productId, Long skuId, Integer quantity,
                BigDecimal usdPrice, String productName, String skuCode, String skuAttrs);

    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @param quantity 新数量（<=0时删除）
     * @return 购物车
     */
    Cart updateItem(Long userId, Long itemId, Integer quantity);

    /**
     * 删除购物车商品
     * @param userId 用户ID
     * @param itemId 购物车项ID
     * @return 购物车
     */
    Cart removeItem(Long userId, Long itemId);

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return null
     */
    Cart clearCart(Long userId);
}