package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Wishlist;
import com.vibecoding.order.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 添加收藏
     */
    @PostMapping
    public R<Wishlist> add(
            @RequestParam Long productId,
            @RequestParam(required = false) String note,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        Wishlist wishlist = wishlistService.add(userId, productId, note);
        return R.ok(wishlist);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    public R<Void> remove(
            @PathVariable Long productId,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        wishlistService.remove(userId, productId);
        return R.ok();
    }

    /**
     * 获取收藏列表
     */
    @GetMapping
    public R<List<Wishlist>> list(@RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        List<Wishlist> list = wishlistService.list(userId);
        return R.ok(list);
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public R<Boolean> check(
            @PathVariable Long productId,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.ok(false);
        }
        boolean exists = wishlistService.check(userId, productId);
        return R.ok(exists);
    }
}