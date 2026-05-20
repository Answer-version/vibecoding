package com.vibecoding.user.service;

public interface VerificationCodeService {

    /**
     * 发送验证码
     * @param phone 手机号
     * @param type 类型 (login/register/reset)
     * @return 验证码
     */
    String sendCode(String phone, String type);

    /**
     * 验证验证码
     * @param phone 手机号
     * @param code 验证码
     * @param type 类型
     * @return 是否有效
     */
    boolean verifyCode(String phone, String code, String type);
}
