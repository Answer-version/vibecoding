package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Review;
import com.vibecoding.order.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 添加评价
     */
    @PostMapping
    public R<Review> add(
            @RequestParam Long productId,
            @RequestParam Integer rating,
            @RequestParam(required = false) Long orderId,
            @RequestParam(required = false) String title,
            @RequestParam String content,
            @RequestParam(required = false) String images,
            @RequestParam(required = false) Boolean isAnonymous,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        if (rating < 1 || rating > 5) {
            return R.fail(400, "Rating must be between 1 and 5");
        }
        Review review = reviewService.add(userId, productId, orderId, rating, title, content, images, isAnonymous);
        return R.ok(review);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(
            @PathVariable Long id,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        reviewService.delete(id, userId);
        return R.ok();
    }

    /**
     * 获取产品评价列表
     */
    @GetMapping
    public R<List<Review>> list(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        List<Review> reviews = reviewService.list(productId, page, pageSize);
        return R.ok(reviews);
    }

    /**
     * 获取产品评价统计
     */
    @GetMapping("/stats")
    public R<Map<String, Object>> stats(@RequestParam Long productId) {
        Map<String, Object> stats = reviewService.getStats(productId);
        return R.ok(stats);
    }
}