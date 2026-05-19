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

    // 内存存储 - 生产环境应使用数据库
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

    private void updateCartAmount(Cart cart, List<CartItem> items) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getUsdAmount() != null) {
                subtotal = subtotal.add(item.getUsdAmount());
            }
        }
        cart.setUsdAmount(subtotal);
        cart.setItemCount(items.size());
        cart.setUpdateTime(LocalDateTime.now());
    }

    @Override
    public Map<String, Object> getCart(Long userId) {
        validateUserId(userId);
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.getOrDefault(userId, new ArrayList<>());

        Map<String, Object> result = new HashMap<>();
        result.put("cart", cart);
        result.put("items", items);
        result.put("subtotal", cart.getUsdAmount());
        result.put("itemCount", cart.getItemCount());

        return result;
    }

    @Override
    public Cart addItem(Long userId, Long productId, Long skuId, Integer quantity,
                       BigDecimal usdPrice, String productName, String skuCode, String skuAttrs) {
        validateUserId(userId);
        validateProductParams(productId, quantity);

        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.get(userId);

        // 检查是否已存在相同商品
        for (CartItem item : items) {
            if (item.getProductId().equals(productId) &&
                (skuId == null || skuId.equals(item.getSkuId()))) {
                // 累加数量
                int newQuantity = item.getQuantity() + quantity;
                item.setQuantity(newQuantity);
                item.setUsdAmount(usdPrice.multiply(BigDecimal.valueOf(newQuantity)));
                item.setUpdateTime(LocalDateTime.now());
                updateCartAmount(cart, items);
                return cart;
            }
        }

        // 新增商品
        CartItem item = new CartItem();
        item.setId(idGenerator.getAndIncrement());
        item.setCartId(cart.getId());
        item.setProductId(productId);
        item.setSkuId(skuId);
        item.setSkuCode(skuCode);
        item.setProductName(productName);
        item.setSkuAttrs(skuAttrs);
        item.setQuantity(quantity);
        item.setUsdPrice(usdPrice);
        item.setUsdAmount(usdPrice.multiply(BigDecimal.valueOf(quantity)));
        item.setCreateTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        items.add(item);
        updateCartAmount(cart, items);
        return cart;
    }

    @Override
    public Cart updateItem(Long userId, Long itemId, Integer quantity) {
        validateUserId(userId);
        validateQuantity(quantity);

        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.get(userId);

        CartItem targetItem = null;
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            throw new IllegalArgumentException("Cart item not found: " + itemId);
        }

        if (quantity <= 0) {
            // 数量为0或负数，删除商品
            items.remove(targetItem);
            updateCartAmount(cart, items);
        } else {
            // 更新数量
            BigDecimal unitPrice = targetItem.getUsdPrice();
            targetItem.setQuantity(quantity);
            targetItem.setUsdAmount(unitPrice.multiply(BigDecimal.valueOf(quantity)));
            targetItem.setUpdateTime(LocalDateTime.now());
            updateCartAmount(cart, items);
        }

        return cart;
    }

    @Override
    public Cart removeItem(Long userId, Long itemId) {
        validateUserId(userId);

        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItems.get(userId);

        CartItem targetItem = null;
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                targetItem = item;
                break;
            }
        }

        if (targetItem == null) {
            throw new IllegalArgumentException("Cart item not found: " + itemId);
        }

        items.remove(targetItem);
        updateCartAmount(cart, items);
        return cart;
    }

    @Override
    public Cart clearCart(Long userId) {
        validateUserId(userId);
        carts.remove(userId);
        cartItems.remove(userId);
        return null;
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    private void validateProductParams(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
    }
}