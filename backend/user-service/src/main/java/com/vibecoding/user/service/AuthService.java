package com.vibecoding.user.service;

import java.util.Map;

public interface AuthService {

    Map<String, Object> register(String username, String email, String password, String phone);

    Map<String, Object> login(String loginName, String password);

    Map<String, Object> refreshToken(String token);

    /**
     * 手机验证码登录
     */
    Map<String, Object> phoneLogin(String phone);

    /**
     * 手机验证码注册
     */
    Map<String, Object> phoneRegister(String phone, String password);

    /**
     * 重置密码
     */
    void resetPassword(String phone, String newPassword);
}
