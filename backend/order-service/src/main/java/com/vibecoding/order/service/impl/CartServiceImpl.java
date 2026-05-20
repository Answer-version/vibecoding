package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.order.entity.Cart;
import com.vibecoding.order.entity.CartItem;
import com.vibecoding.order.mapper.CartItemMapper;
import com.vibecoding.order.mapper.CartMapper;
import com.vibecoding.order.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Override
    public Map<String, Object> getCart(Long userId) {
        validateUserId(userId);

        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            cart = createCart(userId);
        }

        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .orderByAsc(CartItem::getId));

        Map<String, Object> result = new HashMap<>();
        result.put("cart", cart);
        result.put("items", items);
        result.put("subtotal", cart.getUsdAmount());
        result.put("itemCount", items.size());

        return result;
    }

    @Override
    @Transactional
    public Cart addItem(Long userId, Long productId, Long skuId, Integer quantity,
                       BigDecimal usdPrice, String productName, String skuCode, String skuAttrs) {
        validateUserId(userId);
        validateProductParams(productId, quantity);

        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            cart = createCart(userId);
        }

        CartItem existingItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .eq(CartItem::getProductId, productId)
                .eq(CartItem::getSkuId, skuId));

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUsdAmount(usdPrice.multiply(BigDecimal.valueOf(existingItem.getQuantity())));
            existingItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(existingItem);
        } else {
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setProductId(productId);
            item.setSkuId(skuId);
            item.setSkuCode(skuCode);
            item.setProductName(productName);
            item.setSkuAttrs(skuAttrs);
            item.setQuantity(quantity);
            item.setUsdPrice(usdPrice);
            item.setUsdAmount(usdPrice.multiply(BigDecimal.valueOf(quantity)));
            item.setCreateTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            cartItemMapper.insert(item);
        }

        updateCartTotals(cart);
        return cart;
    }

    @Override
    @Transactional
    public Cart updateItem(Long userId, Long itemId, Integer quantity) {
        validateUserId(userId);
        validateQuantity(quantity);

        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        CartItem targetItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getCartId, cart.getId()));

        if (targetItem == null) {
            throw new IllegalArgumentException("Cart item not found: " + itemId);
        }

        if (quantity <= 0) {
            cartItemMapper.deleteById(itemId);
        } else {
            targetItem.setQuantity(quantity);
            targetItem.setUsdAmount(targetItem.getUsdPrice().multiply(BigDecimal.valueOf(quantity)));
            targetItem.setUpdateTime(LocalDateTime.now());
            cartItemMapper.updateById(targetItem);
        }

        updateCartTotals(cart);
        return cart;
    }

    @Override
    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        validateUserId(userId);

        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        if (cart == null) {
            throw new IllegalArgumentException("Cart not found");
        }

        CartItem targetItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, itemId)
                .eq(CartItem::getCartId, cart.getId()));

        if (targetItem == null) {
            throw new IllegalArgumentException("Cart item not found: " + itemId);
        }

        cartItemMapper.deleteById(itemId);
        updateCartTotals(cart);
        return cart;
    }

    @Override
    @Transactional
    public Cart clearCart(Long userId) {
        validateUserId(userId);

        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId));

        if (cart != null) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getCartId, cart.getId()));

            cart.setUsdAmount(BigDecimal.ZERO);
            cart.setItemCount(0);
            cart.setUpdateTime(LocalDateTime.now());
            cartMapper.updateById(cart);
        }

        return null;
    }

    private Cart createCart(Long userId) {
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setCartType(1);
        cart.setItemCount(0);
        cart.setUsdAmount(BigDecimal.ZERO);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.insert(cart);
        return cart;
    }

    private void updateCartTotals(Cart cart) {
        List<CartItem> items = cartItemMapper.selectList(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId()));

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getUsdAmount() != null) {
                subtotal = subtotal.add(item.getUsdAmount());
            }
        }

        cart.setUsdAmount(subtotal);
        cart.setItemCount(items.size());
        cart.setUpdateTime(LocalDateTime.now());
        cartMapper.updateById(cart);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    private void validateProductParams(Long productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity is required");
        }
    }
}