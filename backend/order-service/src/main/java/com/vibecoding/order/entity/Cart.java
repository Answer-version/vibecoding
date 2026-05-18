package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("cart")
public class Cart {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer cartType;
    private Long userId;
    private String sessionId;
    private Integer itemCount;
    private BigDecimal usdAmount;
    private BigDecimal eurAmount;
    private BigDecimal gbpAmount;
    private BigDecimal cnyAmount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}