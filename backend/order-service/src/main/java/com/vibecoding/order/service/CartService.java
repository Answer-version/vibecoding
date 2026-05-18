package com.vibecoding.order.service;

import java.util.Map;

public interface CartService {

    Map<String, Object> getCart();

    void addItem(Map<String, Object> params);

    void updateItem(Long itemId, Map<String, Object> params);

    void removeItem(Long itemId);

    void clearCart();
}