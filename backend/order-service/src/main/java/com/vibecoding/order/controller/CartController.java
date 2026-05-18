package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public R<Map<String, Object>> getCart() {
        return R.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public R<Void> addItem(@RequestBody Map<String, Object> params) {
        cartService.addItem(params);
        return R.ok();
    }

    @PutMapping("/items/{id}")
    public R<Void> updateItem(@PathVariable Long id, @RequestBody Map<String, Object> params) {
        cartService.updateItem(id, params);
        return R.ok();
    }

    @DeleteMapping("/items/{id}")
    public R<Void> removeItem(@PathVariable Long id) {
        cartService.removeItem(id);
        return R.ok();
    }

    @DeleteMapping("/clear")
    public R<Void> clearCart() {
        cartService.clearCart();
        return R.ok();
    }
}