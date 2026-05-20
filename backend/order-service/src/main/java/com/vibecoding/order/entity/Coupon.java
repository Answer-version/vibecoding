package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String couponCode;

    private String name;

    private String nameEn;

    private Integer type;

    private BigDecimal discountValue;

    private BigDecimal minAmount;

    private Integer totalQuantity;

    private Integer usedQuantity;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /**
     * 优惠券类型常量
     */
    public static final int TYPE_DISCOUNT_AMOUNT = 1;  // 满减
    public static final int TYPE_DISCOUNT_RATE = 2;    // 折扣
    public static final int TYPE_FREE_SHIPPING = 3;     // 免运费

    /**
     * 状态常量
     */
    public static final int STATUS_DISABLED = 0;
    public static final int STATUS_ENABLED = 1;

    /**
     * 计算优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(minAmount) < 0) {
            return BigDecimal.ZERO;
        }

        if (type == TYPE_DISCOUNT_AMOUNT) {
            return discountValue;
        } else if (type == TYPE_DISCOUNT_RATE) {
            return orderAmount.multiply(discountValue).setScale(2, java.math.RoundingMode.HALF_UP);
        } else if (type == TYPE_FREE_SHIPPING) {
            return new BigDecimal("9.99"); // 假设运费9.99
        }
        return BigDecimal.ZERO;
    }

    /**
     * 检查是否有效
     */
    public boolean isValid() {
        if (status == null || status != STATUS_ENABLED) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(startTime) || now.isAfter(endTime)) {
            return false;
        }
        if (usedQuantity != null && totalQuantity != null && usedQuantity >= totalQuantity) {
            return false;
        }
        return true;
    }
}