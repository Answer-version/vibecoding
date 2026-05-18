package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer orderType;
    private String orderNo;
    private Long userId;
    private Long customerGroupId;
    private Long quoteId;

    private String orderStatus;
    private String payStatus;
    private String shipStatus;

    private BigDecimal usdAmount;
    private BigDecimal shippingFee;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String currency;

    private String payMethod;
    private LocalDateTime payTime;

    private String shipName;
    private String shipPhone;
    private String shipCountryCode;
    private String shipCountry;
    private String shipState;
    private String shipCity;
    private String shipAddress1;
    private String shipAddress2;
    private String shipZip;

    private String shippingMethod;
    private BigDecimal shippingFeeUsd;
    private String trackingNo;
    private LocalDateTime shipTime;
    private LocalDateTime deliverTime;
    private LocalDateTime completeTime;

    private String customerNote;
    private String adminNote;
    private String ip;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}