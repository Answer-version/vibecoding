package com.vibecoding.order.service;

import com.vibecoding.order.entity.Cart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CartService 单元测试 - 使用数据库持久化
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("购物车服务测试")
public class CartServiceTest {

    @Mock
    private com.vibecoding.order.mapper.CartMapper cartMapper;

    @Mock
    private com.vibecoding.order.mapper.CartItemMapper cartItemMapper;

    @InjectMocks
    private com.vibecoding.order.service.impl.CartServiceImpl cartService;

    @Test
    @DisplayName("TC-CART-001: 获取用户购物车")
    void testGetCart() {
        when(cartMapper.selectOne(any())).thenReturn(null);
        when(cartMapper.insert(any())).thenReturn(1);
        when(cartItemMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

        Map<String, Object> result = cartService.getCart(1001L);

        assertNotNull(result);
        assertNotNull(result.get("cart"));
    }

    @Test
    @DisplayName("TC-CART-002: 新用户创建购物车")
    void testCreateCartForNewUser() {
        when(cartMapper.selectOne(any())).thenReturn(null);
        when(cartMapper.insert(any())).thenAnswer(inv -> {
            Cart c = inv.getArgument(0);
            c.setId(1L);
            return 1;
        });
        when(cartItemMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

        Map<String, Object> result = cartService.getCart(1001L);

        assertNotNull(result);
        verify(cartMapper, times(1)).insert(any());
    }

    @Test
    @DisplayName("TC-CART-003: 添加商品到购物车")
    void testAddItem() {
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1001L);

        when(cartMapper.selectOne(any())).thenReturn(cart);
        when(cartItemMapper.selectOne(any())).thenReturn(null);
        when(cartItemMapper.insert(any())).thenReturn(1);
        when(cartMapper.updateById(any())).thenReturn(1);
        when(cartItemMapper.selectList(any())).thenReturn(java.util.Collections.emptyList());

        Cart result = cartService.addItem(1001L, 101L, 201L, 2, new BigDecimal("29.99"), "Test Product", "SKU001", "{}");

        assertNotNull(result);
        verify(cartItemMapper, times(1)).insert(any());
    }

    @Test
    @DisplayName("TC-CART-004: 用户ID为空应抛异常")
    void testAddItemWithNullUserId() {
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItem(null, 101L, 201L, 1, new BigDecimal("29.99"), "Test", "SKU", "{}")
        );
    }
}