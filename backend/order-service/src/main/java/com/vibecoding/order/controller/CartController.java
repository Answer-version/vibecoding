package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取购物车 - 支持游客和登录用户
     */
    @GetMapping
    public R<Map<String, Object>> getCart(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(value = "guestId", required = false) String guestId) {
        Long effectiveUserId = resolveUserId(userId, guestId);
        if (effectiveUserId == null) {
            return R.ok(Map.of("cart", null, "items", java.util.Collections.emptyList(), "subtotal", BigDecimal.ZERO, "itemCount", 0));
        }
        return R.ok(cartService.getCart(effectiveUserId));
    }

    /**
     * 添加商品到购物车 - 支持游客
     */
    @PostMapping("/items")
    public R<Cart> addItem(
            @RequestParam Long productId,
            @RequestParam(required = false) Long skuId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal usdPrice,
            @RequestParam String productName,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false, defaultValue = "{}") String skuAttrs,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(value = "guestId", required = false) String guestId) {

        Long effectiveUserId = resolveUserId(userId, guestId);
        if (effectiveUserId == null) {
            // 创建一个临时ID用于游客购物车
            effectiveUserId = guestId != null ? guestId.hashCode() % 100000L : System.currentTimeMillis() % 100000L;
        }

        Cart cart = cartService.addItem(effectiveUserId, productId, skuId, quantity, usdPrice,
                productName, skuCode, skuAttrs);
        return R.ok(cart);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/items/{id}")
    public R<Cart> updateItem(
            @PathVariable Long id,
            @RequestParam Integer quantity,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(value = "guestId", required = false) String guestId) {
        Long effectiveUserId = resolveUserId(userId, guestId);
        if (effectiveUserId == null) {
            return R.fail(401, "Please login to update cart");
        }
        Cart cart = cartService.updateItem(effectiveUserId, id, quantity);
        return R.ok(cart);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/items/{id}")
    public R<Cart> removeItem(
            @PathVariable Long id,
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(value = "guestId", required = false) String guestId) {
        Long effectiveUserId = resolveUserId(userId, guestId);
        if (effectiveUserId == null) {
            return R.fail(401, "Please login to remove from cart");
        }
        Cart cart = cartService.removeItem(effectiveUserId, id);
        return R.ok(cart);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public R<Void> clearCart(
            @RequestAttribute(value = "userId", required = false) Long userId,
            @RequestParam(value = "guestId", required = false) String guestId) {
        Long effectiveUserId = resolveUserId(userId, guestId);
        if (effectiveUserId != null) {
            cartService.clearCart(effectiveUserId);
        }
        return R.ok();
    }

    private Long resolveUserId(Long userId, String guestId) {
        if (userId != null) {
            return userId;
        }
        if (guestId != null) {
            // 使用guestId的hashCode作为临时用户ID
            return (long) Math.abs(guestId.hashCode() % 100000);
        }
        return null;
    }
}