package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vibecoding.order.entity.Coupon;
import com.vibecoding.order.entity.UserCoupon;
import com.vibecoding.order.entity.OrderCoupon;
import com.vibecoding.order.mapper.CouponMapper;
import com.vibecoding.order.mapper.UserCouponMapper;
import com.vibecoding.order.mapper.OrderCouponMapper;
import com.vibecoding.order.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final OrderCouponMapper orderCouponMapper;

    @Override
    public List<Coupon> listCoupons(Map<String, Object> params) {
        Integer page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        Integer size = params.get("size") != null ? Integer.parseInt(params.get("size").toString()) : 10;

        Page<Coupon> pageResult = new Page<>(page, size);
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();

        String keyword = (String) params.get("keyword");
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Coupon::getName, keyword)
                   .or()
                   .like(Coupon::getCouponCode, keyword);
        }

        Integer status = (Integer) params.get("status");
        if (status != null) {
            wrapper.eq(Coupon::getStatus, status);
        }

        wrapper.orderByDesc(Coupon::getCreateTime);
        return couponMapper.selectPage(pageResult, wrapper).getRecords();
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        coupon.setUsedQuantity(0);
        coupon.setCreateTime(LocalDateTime.now());
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.insert(coupon);
        return coupon;
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Coupon coupon) {
        coupon.setUpdateTime(LocalDateTime.now());
        couponMapper.updateById(coupon);
        return couponMapper.selectById(coupon.getId());
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        couponMapper.deleteById(id);
    }

    @Override
    public Map<String, Object> validateCoupon(String couponCode, BigDecimal totalAmount) {
        Map<String, Object> result = new HashMap<>();
        result.put("valid", false);

        if (!StringUtils.hasText(couponCode) || totalAmount == null) {
            result.put("message", "Invalid coupon code or amount");
            return result;
        }

        Coupon coupon = couponMapper.selectOne(
            new LambdaQueryWrapper<Coupon>().eq(Coupon::getCouponCode, couponCode)
        );

        if (coupon == null) {
            result.put("message", "Coupon not found");
            return result;
        }

        if (!coupon.isValid()) {
            result.put("message", "Coupon is invalid or expired");
            return result;
        }

        if (totalAmount.compareTo(coupon.getMinAmount()) < 0) {
            result.put("message", "Minimum order amount not met");
            result.put("minAmount", coupon.getMinAmount());
            return result;
        }

        BigDecimal discount = coupon.calculateDiscount(totalAmount);
        result.put("valid", true);
        result.put("couponId", coupon.getId());
        result.put("couponName", coupon.getName());
        result.put("couponNameEn", coupon.getNameEn());
        result.put("couponType", coupon.getType());
        result.put("discountAmount", discount);
        result.put("message", "Coupon available");

        return result;
    }

    @Override
    @Transactional
    public UserCoupon receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || !coupon.isValid()) {
            throw new RuntimeException("Coupon not available");
        }

        // 检查用户是否已领取
        long count = userCouponMapper.selectCount(new LambdaQueryWrapper<UserCoupon>()
            .eq(UserCoupon::getUserId, userId)
            .eq(UserCoupon::getCouponId, couponId));

        if (count > 0) {
            throw new RuntimeException("You have already received this coupon");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(UserCoupon.STATUS_UNUSED);
        userCoupon.setGetTime(LocalDateTime.now());
        userCoupon.setCreateTime(LocalDateTime.now());
        userCoupon.setUpdateTime(LocalDateTime.now());
        userCouponMapper.insert(userCoupon);

        // 更新已领取数量
        couponMapper.update(null, new LambdaUpdateWrapper<Coupon>()
            .eq(Coupon::getId, couponId)
            .setSql("used_quantity = used_quantity + 1"));

        return userCoupon;
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);

        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }

        wrapper.orderByDesc(UserCoupon::getGetTime);
        return userCouponMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void useCoupon(Long userCouponId, Long orderId, BigDecimal discountAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || userCoupon.getStatus() != UserCoupon.STATUS_UNUSED) {
            throw new RuntimeException("Coupon not available");
        }

        userCoupon.setStatus(UserCoupon.STATUS_USED);
        userCoupon.setUseTime(LocalDateTime.now());
        userCoupon.setOrderId(orderId);
        userCoupon.setUpdateTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);

        // 记录订单优惠券关联
        OrderCoupon orderCoupon = new OrderCoupon();
        orderCoupon.setOrderId(orderId);
        orderCoupon.setCouponId(userCoupon.getCouponId());
        orderCoupon.setDiscountAmount(discountAmount);
        orderCoupon.setCreateTime(LocalDateTime.now());
        orderCouponMapper.insert(orderCoupon);
    }

    @Override
    @Transactional
    public void returnCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon != null && userCoupon.getStatus() == UserCoupon.STATUS_USED) {
            userCoupon.setStatus(UserCoupon.STATUS_UNUSED);
            userCoupon.setUseTime(null);
            userCoupon.setOrderId(null);
            userCoupon.setUpdateTime(LocalDateTime.now());
            userCouponMapper.updateById(userCoupon);

            // 删除订单优惠券关联
            orderCouponMapper.delete(new LambdaQueryWrapper<OrderCoupon>()
                .eq(OrderCoupon::getOrderId, userCoupon.getOrderId()));
        }
    }
}