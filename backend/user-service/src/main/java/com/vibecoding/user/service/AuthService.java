package com.vibecoding.user.service;

import java.util.Map;

public interface AuthService {

    Map<String, Object> register(String username, String email, String password, String phone);

    Map<String, Object> login(String loginName, String password);

    Map<String, Object> refreshToken(String token);
}