package com.vibecoding.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("customer_group")
public class CustomerGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer groupType;
    private String groupCode;
    private String groupName;
    private String groupNameEn;
    private BigDecimal discountRate;
    private Integer moq;
    private String description;
    private Integer sortOrder;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}