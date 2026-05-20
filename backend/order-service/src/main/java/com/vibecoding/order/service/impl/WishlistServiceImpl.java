package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.order.entity.Wishlist;
import com.vibecoding.order.mapper.WishlistMapper;
import com.vibecoding.order.service.WishlistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistMapper wishlistMapper;

    @Override
    @Transactional
    public Wishlist add(Long userId, Long productId, String note) {
        // 检查是否已存在
        LambdaQueryWrapper<Wishlist> query = new LambdaQueryWrapper<>();
        query.eq(Wishlist::getUserId, userId)
             .eq(Wishlist::getProductId, productId);
        Wishlist existing = wishlistMapper.selectOne(query);

        if (existing != null) {
            log.info("Product already in wishlist: userId={}, productId={}", userId, productId);
            return existing;
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        wishlist.setNote(note);
        wishlist.setCreateTime(LocalDateTime.now());
        wishlist.setUpdateTime(LocalDateTime.now());
        wishlistMapper.insert(wishlist);

        log.info("Added to wishlist: userId={}, productId={}", userId, productId);
        return wishlist;
    }

    @Override
    @Transactional
    public void remove(Long userId, Long productId) {
        LambdaQueryWrapper<Wishlist> query = new LambdaQueryWrapper<>();
        query.eq(Wishlist::getUserId, userId)
             .eq(Wishlist::getProductId, productId);
        wishlistMapper.delete(query);

        log.info("Removed from wishlist: userId={}, productId={}", userId, productId);
    }

    @Override
    public List<Wishlist> list(Long userId) {
        LambdaQueryWrapper<Wishlist> query = new LambdaQueryWrapper<>();
        query.eq(Wishlist::getUserId, userId)
             .orderByDesc(Wishlist::getCreateTime);
        return wishlistMapper.selectList(query);
    }

    @Override
    public boolean check(Long userId, Long productId) {
        LambdaQueryWrapper<Wishlist> query = new LambdaQueryWrapper<>();
        query.eq(Wishlist::getUserId, userId)
             .eq(Wishlist::getProductId, productId);
        return wishlistMapper.selectCount(query) > 0;
    }
}