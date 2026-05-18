package com.vibecoding.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_address")
public class UserAddress {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Integer addressType;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String countryCode;
    private String countryName;
    private String stateCode;
    private String stateName;
    private String city;
    private String district;
    private String address1;
    private String address2;
    private String zipCode;
    private Integer isDefault;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}