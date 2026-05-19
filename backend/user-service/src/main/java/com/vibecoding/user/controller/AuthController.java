package com.vibecoding.user.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.user.service.AuthService;
import com.vibecoding.user.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final VerificationCodeService verificationCodeService;

    /**
     * 发送手机验证码
     */
    @PostMapping("/phone/send")
    public R<Void> sendPhoneCode(@RequestParam String phone, @RequestParam(defaultValue = "login") String type) {
        verificationCodeService.sendCode(phone, type);
        return R.ok();
    }

    /**
     * 手机验证码登录
     */
    @PostMapping("/phone/login")
    public R<Map<String, Object>> phoneLogin(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");

        if (phone == null || code == null) {
            return R.fail(10001, "Phone and code are required");
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code, "login")) {
            return R.fail(10001, "Invalid or expired code");
        }

        // 执行手机登录
        return R.ok(authService.phoneLogin(phone));
    }

    /**
     * 手机验证码注册
     */
    @PostMapping("/phone/register")
    public R<Map<String, Object>> phoneRegister(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        String password = params.get("password");

        if (phone == null || code == null || password == null) {
            return R.fail(10001, "Phone, code and password are required");
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code, "register")) {
            return R.fail(10001, "Invalid or expired code");
        }

        // 执行手机注册
        return R.ok(authService.phoneRegister(phone, password));
    }

    /**
     * 忘记密码 - 发送验证码
     */
    @PostMapping("/password/send-code")
    public R<Void> sendResetCode(@RequestParam String phone) {
        verificationCodeService.sendCode(phone, "reset");
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PostMapping("/password/reset")
    public R<Void> resetPassword(@RequestBody Map<String, String> params) {
        String phone = params.get("phone");
        String code = params.get("code");
        String newPassword = params.get("newPassword");

        if (phone == null || code == null || newPassword == null) {
            return R.fail(10001, "Phone, code and newPassword are required");
        }

        // 验证验证码
        if (!verificationCodeService.verifyCode(phone, code, "reset")) {
            return R.fail(10001, "Invalid or expired code");
        }

        // 重置密码
        authService.resetPassword(phone, newPassword);
        return R.ok();
    }

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

        return R.ok(authService.login(loginName, password));
    }

    @PostMapping("/refresh")
    public R<Map<String, Object>> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        return R.ok(authService.refreshToken(token));
    }
}
