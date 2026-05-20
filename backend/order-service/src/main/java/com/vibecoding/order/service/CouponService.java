package com.vibecoding.order.service;

import com.vibecoding.order.entity.Coupon;
import com.vibecoding.order.entity.UserCoupon;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CouponService {

    /**
     * 获取优惠券列表（管理后台）
     */
    List<Coupon> listCoupons(Map<String, Object> params);

    /**
     * 获取优惠券详情
     */
    Coupon getCouponById(Long id);

    /**
     * 创建优惠券
     */
    Coupon createCoupon(Coupon coupon);

    /**
     * 更新优惠券
     */
    Coupon updateCoupon(Coupon coupon);

    /**
     * 删除优惠券
     */
    void deleteCoupon(Long id);

    /**
     * 验证优惠码
     * @return 优惠信息包含是否可用、可抵扣金额
     */
    Map<String, Object> validateCoupon(String couponCode, BigDecimal totalAmount);

    /**
     * 用户领取优惠券
     */
    UserCoupon receiveCoupon(Long userId, Long couponId);

    /**
     * 获取用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(Long userId, Integer status);

    /**
     * 使用优惠券（订单创建时）
     */
    void useCoupon(Long userCouponId, Long orderId, BigDecimal discountAmount);

    /**
     * 退还优惠券（订单取消时）
     */
    void returnCoupon(Long userCouponId);
}