package com.vibecoding.user.controller;

import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.common.result.R;
import com.vibecoding.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public R<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String email = params.get("email");
        String password = params.get("password");
        String phone = params.get("phone");

        return R.ok(authService.register(username, email, password, phone));
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String loginName = params.get("username");
        String password = params.get("password");
        String loginType = params.getOrDefault("loginType", "password");

        if (!"password".equals(loginType)) {
            throw new BusinessException("Unsupported login type");
        }

        return R.ok(authService.login(loginName, password));
    }

    @PostMapping("/refresh")
    public R<Map<String, Object>> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return R.ok(authService.refreshToken(token));
    }
}