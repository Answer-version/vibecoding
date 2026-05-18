package com.vibecoding.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_method")
public class PaymentMethod {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String methodCode;
    private String methodName;
    private String methodNameEn;
    private String icon;
    private Integer methodType;
    private String channel;
    private String config;
    private Integer sortOrder;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}