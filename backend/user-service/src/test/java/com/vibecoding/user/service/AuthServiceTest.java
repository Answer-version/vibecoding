package com.vibecoding.user.service;

import com.vibecoding.user.entity.User;
import com.vibecoding.user.service.impl.AuthServiceImpl;
import cn.hutool.crypto.digest.BCrypt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("认证服务测试")
public class AuthServiceTest {

    @Mock
    private com.vibecoding.user.mapper.UserMapper userMapper;

    @Mock
    private com.vibecoding.common.security.JwtUtils jwtUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(BCrypt.hashpw("password123"));
        testUser.setUserType(1);
        testUser.setStatus(1);
        testUser.setLoginCount(0);
    }

    @Test
    @DisplayName("TC-AUTH-001: 用户注册成功")
    void testRegisterSuccess() {
        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any())).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);  // Simulate DB auto-generate ID
            return 1;
        });
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("mock-token");

        Map<String, Object> result = authService.register("testuser", "test@example.com", "password123", null);

        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertEquals("testuser", result.get("username"));
        verify(userMapper, times(1)).insert(any());
    }

    @Test
    @DisplayName("TC-AUTH-002: 用户名已存在则注册失败")
    void testRegisterUsernameExists() {
        when(userMapper.selectCount(any())).thenReturn(1L);

        assertThrows(Exception.class, () ->
            authService.register("existinguser", "test@example.com", "password123", null)
        );
    }

    @Test
    @DisplayName("TC-AUTH-004: 用户登录成功")
    void testLoginSuccess() {
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("mock-token");
        when(userMapper.updateById(any())).thenReturn(1);

        Map<String, Object> result = authService.login("testuser", "password123");

        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertEquals("testuser", result.get("username"));
    }

    @Test
    @DisplayName("TC-AUTH-005: 用户名不存在则登录失败")
    void testLoginUserNotFound() {
        when(userMapper.selectOne(any())).thenReturn(null);

        assertThrows(Exception.class, () ->
            authService.login("notfound", "password123")
        );
    }

    @Test
    @DisplayName("TC-AUTH-007: 账户被禁用则登录失败")
    void testLoginAccountDisabled() {
        testUser.setStatus(0); // 禁用
        when(userMapper.selectOne(any())).thenReturn(testUser);

        assertThrows(Exception.class, () ->
            authService.login("testuser", "password123")
        );
    }

    @Test
    @DisplayName("TC-AUTH-010: Token刷新成功")
    void testRefreshToken() {
        when(jwtUtils.getUserId("old_token")).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("new-token");

        Map<String, Object> result = authService.refreshToken("old_token");

        assertNotNull(result);
        assertEquals("new-token", result.get("token"));
    }

    @Test
    @DisplayName("TC-AUTH-011: Token刷新用户不存在")
    void testRefreshTokenUserNotFound() {
        when(jwtUtils.getUserId("old_token")).thenReturn(999L);
        when(userMapper.selectById(999L)).thenReturn(null);

        assertThrows(Exception.class, () ->
            authService.refreshToken("old_token")
        );
    }

    @Test
    @DisplayName("TC-AUTH-012: 手机号登录成功")
    void testPhoneLogin() {
        String phone = "13800138000";
        testUser.setPhone(phone);
        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("mock-token");
        when(userMapper.updateById(any())).thenReturn(1);

        Map<String, Object> result = authService.login(phone, "password123");

        assertNotNull(result);
        assertNotNull(result.get("token"));
    }
}