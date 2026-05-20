package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_coupon")
public class OrderCoupon {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long couponId;

    private String couponCode;

    private BigDecimal discountAmount;

    private LocalDateTime createTime;
}