package com.vibecoding.order.service;

import com.vibecoding.order.entity.Review;
import java.util.List;
import java.util.Map;

public interface ReviewService {
    Review add(Long userId, Long productId, Long orderId, Integer rating, String title, String content, String images, Boolean isAnonymous);
    void delete(Long reviewId, Long userId);
    List<Review> list(Long productId, Integer page, Integer pageSize);
    Map<String, Object> getStats(Long productId);
}