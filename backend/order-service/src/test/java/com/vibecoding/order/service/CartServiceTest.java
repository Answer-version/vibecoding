package com.vibecoding.order.service;

import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.entity.CartItem;
import com.vibecoding.order.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CartService 单元测试
 *
 * 测试覆盖：
 * 1. 获取购物车
 * 2. 添加商品到购物车
 * 3. 更新购物车商品数量
 * 4. 删除购物车商品
 * 5. 清空购物车
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("购物车服务测试")
public class CartServiceTest {

    @Mock
    private com.vibecoding.order.mapper.CartMapper cartMapper;

    @Mock
    private com.vibecoding.order.mapper.CartItemMapper cartItemMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    private Long testUserId;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testUserId = 1001L;

        // 构建测试购物车
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(testUserId);
        testCart.setCartType(1); // B2C购物车
        testCart.setUsdAmount(BigDecimal.ZERO);
        testCart.setItemCount(0);

        // 构建测试购物车项
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCartId(1L);
        testCartItem.setProductId(101L);
        testCartItem.setSkuId(201L);
        testCartItem.setProductName("Test Product");
        testCartItem.setSkuCode("SKU001");
        testCartItem.setQuantity(2);
        testCartItem.setUsdPrice(new BigDecimal("29.99"));
        testCartItem.setUsdAmount(new BigDecimal("59.98"));
    }

    @Test
    @DisplayName("TC-CART-001: 获取用户购物车")
    void testGetCart() {
        // Arrange
        when(cartMapper.selectByUserId(testUserId)).thenReturn(testCart);

        // Act
        Cart result = cartService.getCart(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(testUserId, result.getUserId());
        verify(cartMapper, times(1)).selectByUserId(testUserId);
    }

    @Test
    @DisplayName("TC-CART-002: 新用户创建购物车")
    void testCreateCartForNewUser() {
        // Arrange
        when(cartMapper.selectByUserId(testUserId)).thenReturn(null);

        Cart savedCart = new Cart();
        savedCart.setId(1L);
        savedCart.setUserId(testUserId);
        savedCart.setCartType(1);

        when(cartMapper.insert(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.getCart(testUserId);

        // Assert
        assertNotNull(result);
        verify(cartMapper, times(1)).insert(any(Cart.class));
    }

    @Test
    @DisplayName("TC-CART-003: 添加商品到购物车")
    void testAddItem() {
        // Arrange
        Long skuId = 201L;
        Integer quantity = 2;
        BigDecimal skuPrice = new BigDecimal("29.99");

        when(cartMapper.selectByUserId(testUserId)).thenReturn(testCart);
        when(cartItemMapper.selectByCartIdAndSkuId(testCart.getId(), skuId)).thenReturn(null);
        when(cartItemMapper.insert(any(CartItem.class))).thenReturn(1);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.addItem(testUserId, skuId, quantity, skuPrice, "Test Product", "SKU001", "{}");

        // Assert
        assertNotNull(result);
        verify(cartItemMapper, times(1)).insert(any(CartItem.class));
    }

    @Test
    @DisplayName("TC-CART-004: 添加已有商品增加数量")
    void testAddExistingItem() {
        // Arrange
        Long skuId = 201L;
        Integer addQuantity = 3;
        BigDecimal skuPrice = new BigDecimal("29.99");

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCartId(testCart.getId());
        existingItem.setSkuId(skuId);
        existingItem.setQuantity(2);
        existingItem.setUsdPrice(skuPrice);
        existingItem.setUsdAmount(new BigDecimal("59.98"));

        when(cartMapper.selectByUserId(testUserId)).thenReturn(testCart);
        when(cartItemMapper.selectByCartIdAndSkuId(testCart.getId(), skuId)).thenReturn(existingItem);

        // Act
        Cart result = cartService.addItem(testUserId, skuId, addQuantity, skuPrice, "Test Product", "SKU001", "{}");

        // Assert - 验证数量累加
        assertNotNull(result);
        verify(cartItemMapper, times(1)).updateById(any(CartItem.class));
    }

    @Test
    @DisplayName("TC-CART-005: 更新购物车商品数量")
    void testUpdateItemQuantity() {
        // Arrange
        Long itemId = 1L;
        Integer newQuantity = 5;
        BigDecimal unitPrice = new BigDecimal("29.99");

        when(cartItemMapper.selectById(itemId)).thenReturn(testCartItem);
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.updateItem(itemId, newQuantity);

        // Assert
        assertNotNull(result);
        verify(cartItemMapper, times(1)).updateById(any(CartItem.class));
    }

    @Test
    @DisplayName("TC-CART-006: 删除购物车商品")
    void testRemoveItem() {
        // Arrange
        Long itemId = 1L;

        when(cartItemMapper.selectById(itemId)).thenReturn(testCartItem);
        when(cartItemMapper.deleteById(itemId)).thenReturn(1);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.removeItem(itemId);

        // Assert
        assertNotNull(result);
        verify(cartItemMapper, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("TC-CART-007: 清空购物车")
    void testClearCart() {
        // Arrange
        when(cartMapper.selectByUserId(testUserId)).thenReturn(testCart);
        when(cartItemMapper.deleteByCartId(testCart.getId())).thenReturn(1);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.clearCart(testUserId);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getItemCount());
        verify(cartItemMapper, times(1)).deleteByCartId(testCart.getId());
    }

    @Test
    @DisplayName("TC-CART-008: 计算购物车总金额")
    void testCalculateTotalAmount() {
        // Arrange
        List<CartItem> items = new ArrayList<>();
        items.add(testCartItem);

        testCart.setItemCount(1);
        testCart.setUsdAmount(new BigDecimal("59.98"));

        when(cartMapper.selectByUserId(testUserId)).thenReturn(testCart);
        when(cartItemMapper.selectByCartId(testCart.getId())).thenReturn(items);

        // Act
        BigDecimal total = cartService.calculateTotalAmount(testUserId);

        // Assert
        assertNotNull(total);
        assertEquals(new BigDecimal("59.98"), total);
    }

    @Test
    @DisplayName("TC-CART-009: 用���ID为空应抛异常")
    void testAddItemWithNullUserId() {
        // Arrange
        Long nullUserId = null;
        Long skuId = 201L;
        Integer quantity = 1;
        BigDecimal price = new BigDecimal("29.99");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.addItem(nullUserId, skuId, quantity, price, "Test", "SKU", "{}")
        );
    }

    @Test
    @DisplayName("TC-CART-010: 数量为0应删除商品")
    void testUpdateQuantityToZero() {
        // Arrange
        Long itemId = 1L;
        Integer zeroQuantity = 0;

        when(cartItemMapper.selectById(itemId)).thenReturn(testCartItem);
        when(cartItemMapper.deleteById(itemId)).thenReturn(1);
        when(cartMapper.updateById(any(Cart.class))).thenReturn(1);

        // Act
        Cart result = cartService.updateItem(itemId, zeroQuantity);

        // Assert - 数量为0应该删除商品
        assertNotNull(result);
        verify(cartItemMapper, times(1)).deleteById(itemId);
    }

    @Test
    @DisplayName("TC-CART-011: 负数数量应抛异常")
    void testUpdateNegativeQuantity() {
        // Arrange
        Long itemId = 1L;
        Integer negativeQuantity = -1;

        when(cartItemMapper.selectById(itemId)).thenReturn(testCartItem);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.updateItem(itemId, negativeQuantity)
        );
    }

    @Test
    @DisplayName("TC-CART-012: 更新不存在的商品应抛异常")
    void testUpdateNonExistentItem() {
        // Arrange
        Long nonExistentItemId = 999L;
        Integer newQuantity = 5;

        when(cartItemMapper.selectById(nonExistentItemId)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            cartService.updateItem(nonExistentItemId, newQuantity)
        );
    }
}