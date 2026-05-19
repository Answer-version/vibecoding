package com.vibecoding.order.service.impl;

import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.entity.CartItem;
import com.vibecoding.order.service.CartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CartServiceImpl implements CartService {

    private final Map<Long, Cart> carts = new ConcurrentHashMap<>();
    private final Map<Long, List<CartItem>> cartItems = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    private Cart getOrCreateCart(Long userId) {
        Cart cart = carts.get(userId);
        if (cart == null) {
            cart = new Cart();
            cart.setId(idGenerator.getAndIncrement());
            cart.setUserId(userId);
            cart.setCartType(1);
            cart.setItemCount(0);
            cart.setUsdAmount(BigDecimal.ZERO);
            cart.setCreateTime(LocalDateTime.now());
            cart.setUpdateTime(LocalDateTime.now());
            carts.put(userId, cart);
            cartItems.put(userId, new ArrayList<>());
        }
        return cart;
    }

    public Map<String, Object> getCart() {
        Long userId = 1L;
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.getOrDefault(userId, new ArrayList<>());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            subtotal = subtotal.add(item.getUsdAmount());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items", items);
        result.put("subtotal", subtotal);
        result.put("itemCount", cart.getItemCount());

        return result;
    }

    public void addItem(Map<String, Object> params) {
        Long userId = 1L;
        Long productId = Long.parseLong(params.get("productId").toString());
        Long skuId = params.get("skuId") != null ? Long.parseLong(params.get("skuId").toString()) : null;
        Integer quantity = Integer.parseInt(params.get("quantity").toString());

        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.get(userId);

        for (CartItem item : items) {
            if (item.getProductId().equals(productId) && (skuId == null || skuId.equals(item.getSkuId()))) {
                item.setQuantity(item.getQuantity() + quantity);
                item.setUpdateTime(LocalDateTime.now());
                cart.setItemCount(cart.getItemCount() + quantity);
                cart.setUpdateTime(LocalDateTime.now());
                return;
            }
        }

        CartItem item = new CartItem();
        item.setId(idGenerator.getAndIncrement());
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
    }

    public void updateItem(Long itemId, Map<String, Object> params) {
    }

    public void removeItem(Long itemId) {
    }

    public void clearCart() {
        Long userId = 1L;
        carts.remove(userId);
        cartItems.remove(userId);
    }
}