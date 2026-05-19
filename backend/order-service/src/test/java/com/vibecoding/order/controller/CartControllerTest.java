package com.vibecoding.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.entity.CartItem;
import com.vibecoding.order.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Cart Controller 集成测试
 *
 * 测试API端点：
 * GET /cart - 获取购物车
 * POST /cart/items - 添加商品
 * PUT /cart/items/{id} - 更新数量
 * DELETE /cart/items/{id} - 删除商品
 * DELETE /cart/clear - 清空购物车
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("购物车API测试")
public class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private ObjectMapper objectMapper;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
        objectMapper = new ObjectMapper();

        // 构建测试购物车
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUserId(1001L);
        testCart.setCartType(1);
        testCart.setUsdAmount(new BigDecimal("59.98"));
        testCart.setItemCount(1);

        // 构建测试购物车项
        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setProductId(101L);
        testCartItem.setSkuId(201L);
        testCartItem.setProductName("Test Product");
        testCartItem.setSkuCode("SKU001");
        testCartItem.setQuantity(2);
        testCartItem.setUsdPrice(new BigDecimal("29.99"));
        testCartItem.setUsdAmount(new BigDecimal("59.98"));
    }

    @Test
    @DisplayName("TC-API-001: GET /cart 返回购物车")
    void testGetCart() throws Exception {
        // Arrange
        when(cartService.getCart(1001L)).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(get("/cart")
                .requestAttr("userId", 1001L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.itemCount").value(1));
    }

    @Test
    @DisplayName("TC-API-002: POST /cart/items 添加商品成功")
    void testAddItem() throws Exception {
        // Arrange
        CartItemRequest request = new CartItemRequest();
        request.setSkuId(201L);
        request.setQuantity(2);
        request.setUsdPrice(new BigDecimal("29.99"));
        request.setProductName("Test Product");
        request.setSkuCode("SKU001");
        request.setSkuAttrs("{}");

        when(cartService.addItem(eq(1001L), anyLong(), anyInt(), any(), anyString(), anyString(), anyString()))
            .thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(post("/cart/items")
                .requestAttr("userId", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("TC-API-003: POST /cart/items 参数校验失败")
    void testAddItemValidationFailed() throws Exception {
        // Arrange - 缺少必填字段
        CartItemRequest request = new CartItemRequest();
        // skuId = null, quantity = null

        // Act & Assert
        mockMvc.perform(post("/cart/items")
                .requestAttr("userId", 1001L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(10001));
    }

    @Test
    @DisplayName("TC-API-004: PUT /cart/items/{id} 更新数量成功")
    void testUpdateItem() throws Exception {
        // Arrange
        Long itemId = 1L;
        when(cartService.updateItem(itemId, 5)).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(put("/cart/items/" + itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\": 5}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("TC-API-005: DELETE /cart/items/{id} 删除商品")
    void testRemoveItem() throws Exception {
        // Arrange
        Long itemId = 1L;
        when(cartService.removeItem(itemId)).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(delete("/cart/items/" + itemId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("TC-API-006: DELETE /cart/clear 清空购物车")
    void testClearCart() throws Exception {
        // Arrange
        when(cartService.clearCart(1001L)).thenReturn(testCart);

        // Act & Assert
        mockMvc.perform(delete("/cart/clear")
                .requestAttr("userId", 1001L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("TC-API-007: 购物车为空返回空列表")
    void testGetEmptyCart() throws Exception {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setItemCount(0);
        emptyCart.setUsdAmount(BigDecimal.ZERO);

        when(cartService.getCart(1001L)).thenReturn(emptyCart);

        // Act & Assert
        mockMvc.perform(get("/cart")
                .requestAttr("userId", 1001L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(0))
            .andExpect(jsonPath("$.data.itemCount").value(0));
    }

    /**
     * 请求对象
     */
    static class CartItemRequest {
        private Long skuId;
        private Integer quantity;
        private BigDecimal usdPrice;
        private String productName;
        private String skuCode;
        private String skuAttrs;

        public Long getSkuId() { return skuId; }
        public void setSkuId(Long skuId) { this.skuId = skuId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getUsdPrice() { return usdPrice; }
        public void setUsdPrice(BigDecimal usdPrice) { this.usdPrice = usdPrice; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public String getSkuCode() { return skuCode; }
        public void setSkuCode(String skuCode) { this.skuCode = skuCode; }
        public String getSkuAttrs() { return skuAttrs; }
        public void setSkuAttrs(String skuAttrs) { this.skuAttrs = skuAttrs; }
    }
}