package com.vibecoding.order.service;

import com.vibecoding.order.entity.Wishlist;
import java.util.List;

public interface WishlistService {
    Wishlist add(Long userId, Long productId, String note);
    void remove(Long userId, Long productId);
    List<Wishlist> list(Long userId);
    boolean check(Long userId, Long productId);
}