package com.vibecoding.user.service;

import com.vibecoding.user.entity.User;
import com.vibecoding.user.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 *
 * 测试覆盖：
 * 1. 用户注册
 * 2. 用户登录
 * 3. Token刷新
 */
@ExtendWith(MockitoExtension.class)
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
        testUser.setPassword("$2a$10$abcdefghijklmnopqrstuv");
        testUser.setUserType(1);
        testUser.setStatus(1);
        testUser.setLoginCount(0);
    }

    @Test
    @DisplayName("TC-AUTH-001: 用户注册成功")
    void testRegisterSuccess() {
        // Arrange
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";

        when(userMapper.selectCount(any())).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("token123");

        // Act
        Map<String, Object> result = authService.register(username, email, password, null);

        // Assert
        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertEquals(username, result.get("username"));
        verify(userMapper, times(1)).insert(any(User.class));
    }

    @Test
    @DisplayName("TC-AUTH-002: 用户名已存在则注册失败")
    void testRegisterUsernameExists() {
        // Arrange
        String username = "existinguser";
        String email = "test@example.com";
        String password = "password123";

        when(userMapper.selectCount(any())).thenReturn(1L);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.register(username, email, password, null)
        );
    }

    @Test
    @DisplayName("TC-AUTH-003: 邮箱已存在则注册失败")
    void testRegisterEmailExists() {
        // Arrange
        String username = "newuser";
        String email = "existing@example.com";
        String password = "password123";

        when(userMapper.selectCount(any())).thenReturn(1L);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.register(username, email, password, null)
        );
    }

    @Test
    @DisplayName("TC-AUTH-004: 用户登录成功")
    void testLoginSuccess() {
        // Arrange
        String loginName = "testuser";
        String password = "password123";

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("token123");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        Map<String, Object> result = authService.login(loginName, password);

        // Assert
        assertNotNull(result);
        assertNotNull(result.get("token"));
        assertEquals("testuser", result.get("username"));
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    @DisplayName("TC-AUTH-005: 用户名不存在则登录失败")
    void testLoginUserNotFound() {
        // Arrange
        String loginName = "notfound";
        String password = "password123";

        when(userMapper.selectOne(any())).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.login(loginName, password)
        );
    }

    @Test
    @DisplayName("TC-AUTH-006: 密码错误则登录失败")
    void testLoginWrongPassword() {
        // Arrange
        String loginName = "testuser";
        String wrongPassword = "wrongpassword";

        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.login(loginName, wrongPassword)
        );
    }

    @Test
    @DisplayName("TC-AUTH-007: 账户被禁用则登录失败")
    void testLoginAccountDisabled() {
        // Arrange
        String loginName = "testuser";
        String password = "password123";

        testUser.setStatus(0); // 禁用

        when(userMapper.selectOne(any())).thenReturn(testUser);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.login(loginName, password)
        );
    }

    @Test
    @DisplayName("TC-AUTH-008: 邮箱登录成功")
    void testLoginWithEmail() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("token123");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        Map<String, Object> result = authService.login(email, password);

        // Assert
        assertNotNull(result);
        assertNotNull(result.get("token"));
    }

    @Test
    @DisplayName("TC-AUTH-009: 手机号登录成功")
    void testLoginWithPhone() {
        // Arrange
        String phone = "13800138000";
        String password = "password123";
        testUser.setPhone(phone);

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("token123");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        Map<String, Object> result = authService.login(phone, password);

        // Assert
        assertNotNull(result);
        assertNotNull(result.get("token"));
    }

    @Test
    @DisplayName("TC-AUTH-010: Token刷新成功")
    void testRefreshToken() {
        // Arrange
        String oldToken = "old_token";
        String newToken = "new_token";

        when(jwtUtils.getUserId(oldToken)).thenReturn(1L);
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn(newToken);

        // Act
        Map<String, Object> result = authService.refreshToken(oldToken);

        // Assert
        assertNotNull(result);
        assertEquals(newToken, result.get("token"));
    }

    @Test
    @DisplayName("TC-AUTH-011: Token刷新用户不存在")
    void testRefreshTokenUserNotFound() {
        // Arrange
        String oldToken = "old_token";

        when(jwtUtils.getUserId(oldToken)).thenReturn(999L);
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(Exception.class, () ->
            authService.refreshToken(oldToken)
        );
    }

    @Test
    @DisplayName("TC-AUTH-012: 登录成功登录计数增加")
    void testLoginIncrementsLoginCount() {
        // Arrange
        String loginName = "testuser";
        String password = "password123";

        testUser.setLoginCount(5);

        when(userMapper.selectOne(any())).thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), any())).thenReturn("token123");
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // Act
        authService.login(loginName, password);

        // Assert
        assertEquals(6, testUser.getLoginCount());
    }
}