package com.vibecoding.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("verification_code")
public class VerificationCode {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String phone;

    private String email;

    private String code;

    private String type; // login, register, reset

    private LocalDateTime expireTime;

    private Integer used; // 0: not used, 1: used

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
