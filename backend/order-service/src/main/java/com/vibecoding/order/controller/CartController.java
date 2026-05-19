package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取购物车
     */
    @GetMapping
    public R<Map<String, Object>> getCart(@RequestAttribute("userId") Long userId) {
        return R.ok(cartService.getCart(userId));
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/items")
    public R<Cart> addItem(
            @RequestAttribute("userId") Long userId,
            @RequestParam Long productId,
            @RequestParam(required = false) Long skuId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal usdPrice,
            @RequestParam String productName,
            @RequestParam(required = false) String skuCode,
            @RequestParam(required = false, defaultValue = "{}") String skuAttrs) {
        Cart cart = cartService.addItem(userId, productId, skuId, quantity, usdPrice,
                productName, skuCode, skuAttrs);
        return R.ok(cart);
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/items/{id}")
    public R<Cart> updateItem(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        Cart cart = cartService.updateItem(userId, id, quantity);
        return R.ok(cart);
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/items/{id}")
    public R<Cart> removeItem(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        Cart cart = cartService.removeItem(userId, id);
        return R.ok(cart);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public R<Void> clearCart(@RequestAttribute("userId") Long userId) {
        cartService.clearCart(userId);
        return R.ok();
    }
}