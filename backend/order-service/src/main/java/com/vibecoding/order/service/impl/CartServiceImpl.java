package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.entity.CartItem;
import com.vibecoding.order.mapper.CartItemMapper;
import com.vibecoding.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemMapper cartItemMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private Cart getOrCreateCart(Long userId) {
        // 简化实现：使用 Redis 存储购物车
        String key = "cart:" + userId;
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setCartType(1);
            cart.setItemCount(0);
            cart.setUsdAmount(BigDecimal.ZERO);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            redisTemplate.opsForValue().set(key, cart);
        }
        return cart;
    }

    @Override
    public Map<String, Object> getCart() {
        // 从 Redis 获取
        Long userId = 1L; // TODO: 从 JWT 获取
        Cart cart = getOrCreateCart(userId);

        String itemKey = "cart:items:" + userId;
        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(itemKey);
        if (items == null) {
            items = new ArrayList<>();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("subtotal", cart.getUsdAmount());
        result.put("itemCount", cart.getItemCount());

        return result;
    }

    @Override
    @Transactional
    public void addItem(Map<String, Object> params) {
        Long userId = 1L; // TODO: 从 JWT 获取
        Long productId = Long.parseLong(params.get("productId").toString());
        Long skuId = params.get("skuId") != null ? Long.parseLong(params.get("skuId").toString()) : null;
        Integer quantity = Integer.parseInt(params.get("quantity").toString());

        Cart cart = getOrCreateCart(userId);
        String itemKey = "cart:items:" + userId;

        List<CartItem> items = (List<CartItem>) redisTemplate.opsForValue().get(itemKey);
        if (items == null) {
            items = new ArrayList<>();
        }

        // 检查是否已存在
        for (CartItem item : items) {
            if (item.getProductId().equals(productId) && (skuId == null || skuId.equals(item.getSkuId()))) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setUpdateTime(LocalDateTime.now());
                redisTemplate.opsForValue().set(itemKey, items);
                return;
            }
        }

        // 添加新项
        CartItem item = new CartItem();
        item.setCartId(cart.getId());
        item.setProductId(productId);
        item.setSkuId(skuId);
        item.setQuantity(quantity);
        item.setUsdPrice(BigDecimal.ZERO);
        item.setUsdAmount(BigDecimal.ZERO);
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        items.add(item);
        cart.setItemCount(cart.getItemCount() + quantity);
        cart.setUpdateTime(LocalDateTime.now());

        redisTemplate.opsForValue().set(itemKey, items);
    }

    @Override
    public void updateItem(Long itemId, Map<String, Object> params) {
        // TODO: 实现
    }

    @Override
    public void removeItem(Long itemId) {
        // TODO: 实现
    }

    @Override
    public void clearCart() {
        Long userId = 1L; // TODO: 从 JWT 获取
        String key = "cart:" + userId;
        String itemKey = "cart:items:" + userId;
        redisTemplate.delete(key);
        redisTemplate.delete(itemKey);
    }
}