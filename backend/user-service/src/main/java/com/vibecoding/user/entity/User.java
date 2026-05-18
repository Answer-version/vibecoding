package com.vibecoding.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer userType;
    private Long customerGroupId;
    private String username;
    private String email;
    private String phone;
    private String countryCode;
    private String password;
    private String nickname;
    private String avatar;
    private String firstName;
    private String lastName;
    private String companyName;
    private Long companyId;
    private String locale;
    private String currency;
    private Integer sex;
    private LocalDateTime birthday;
    private Integer emailVerified;
    private Integer phoneVerified;
    private Integer kycStatus;

    private BigDecimal creditLimit;
    private BigDecimal availableCredit;

    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private Integer loginCount;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}