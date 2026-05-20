package com.vibecoding.order.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Coupon;
import com.vibecoding.order.entity.UserCoupon;
import com.vibecoding.order.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 验证优惠码 - 必须在 {id} 路由之前
     */
    @PostMapping("/validate")
    public R<Map<String, Object>> validate(
            @RequestParam String couponCode,
            @RequestParam BigDecimal totalAmount) {
        Map<String, Object> result = couponService.validateCoupon(couponCode, totalAmount);
        return R.ok(result);
    }

    /**
     * 获取优惠券列表（管理后台）
     */
    @GetMapping
    public R<List<Coupon>> list(@RequestParam Map<String, Object> params) {
        List<Coupon> coupons = couponService.listCoupons(params);
        return R.ok(coupons);
    }

    /**
     * 获取优惠券详情
     */
    @GetMapping("/{id}")
    public R<Coupon> getById(@PathVariable Long id) {
        Coupon coupon = couponService.getCouponById(id);
        return R.ok(coupon);
    }

    /**
     * 创建优惠券
     */
    @PostMapping
    public R<Coupon> create(@RequestBody Coupon coupon) {
        Coupon created = couponService.createCoupon(coupon);
        return R.ok(created);
    }

    /**
     * 更新优惠券
     */
    @PutMapping("/{id}")
    public R<Coupon> update(@PathVariable Long id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        Coupon updated = couponService.updateCoupon(coupon);
        return R.ok(updated);
    }

    /**
     * 删除优惠券
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return R.ok();
    }

    /**
     * 用户领取优惠券
     */
    @PostMapping("/{couponId}/receive")
    public R<UserCoupon> receive(
            @PathVariable Long couponId,
            @RequestAttribute Long userId) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        UserCoupon userCoupon = couponService.receiveCoupon(userId, couponId);
        return R.ok(userCoupon);
    }

    /**
     * 获取用户优惠券列表
     */
    @GetMapping("/my")
    public R<List<UserCoupon>> getMyCoupons(
            @RequestAttribute Long userId,
            @RequestParam(required = false) Integer status) {
        if (userId == null) {
            return R.fail(401, "Please login first");
        }
        List<UserCoupon> coupons = couponService.getUserCoupons(userId, status);
        return R.ok(coupons);
    }
}