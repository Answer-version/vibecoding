package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vibecoding.order.entity.Review;
import com.vibecoding.order.mapper.ReviewMapper;
import com.vibecoding.order.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public Review add(Long userId, Long productId, Long orderId, Integer rating, String title, String content, String images, Boolean isAnonymous) {
        Review review = new Review();
        review.setProductId(productId);
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setRating(rating);
        review.setTitle(title);
        review.setContent(content);
        review.setImages(images);
        review.setIsAnonymous(isAnonymous != null && isAnonymous ? 1 : 0);
        review.setStatus(1); // 默认显示
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.insert(review);

        log.info("Review added: productId={}, userId={}, rating={}", productId, userId, rating);
        return review;
    }

    @Override
    @Transactional
    public void delete(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review != null && review.getUserId().equals(userId)) {
            reviewMapper.deleteById(reviewId);
            log.info("Review deleted: id={}", reviewId);
        }
    }

    @Override
    public List<Review> list(Long productId, Integer page, Integer pageSize) {
        Page<Review> p = new Page<>(page != null ? page : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<Review> query = new LambdaQueryWrapper<>();
        query.eq(Review::getProductId, productId)
             .eq(Review::getStatus, 1)
             .orderByDesc(Review::getCreateTime);
        return reviewMapper.selectPage(p, query).getRecords();
    }

    @Override
    public Map<String, Object> getStats(Long productId) {
        LambdaQueryWrapper<Review> query = new LambdaQueryWrapper<>();
        query.eq(Review::getProductId, productId)
             .eq(Review::getStatus, 1);

        List<Review> reviews = reviewMapper.selectList(query);

        int count = reviews.size();
        double avgRating = 0;
        if (count > 0) {
            avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        }

        int[] ratingCounts = new int[5];
        for (Review r : reviews) {
            int idx = r.getRating() - 1;
            if (idx >= 0 && idx < 5) ratingCounts[idx]++;
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", count);
        stats.put("avgRating", avgRating);
        stats.put("rating5", ratingCounts[4]);
        stats.put("rating4", ratingCounts[3]);
        stats.put("rating3", ratingCounts[2]);
        stats.put("rating2", ratingCounts[1]);
        stats.put("rating1", ratingCounts[0]);

        return stats;
    }
}