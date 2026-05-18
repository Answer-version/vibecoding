package com.vibecoding.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.common.security.JwtUtils;
import com.vibecoding.user.entity.CustomerGroup;
import com.vibecoding.user.entity.User;
import com.vibecoding.user.mapper.UserMapper;
import com.vibecoding.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtils jwtUtils;

    @Override
    public Map<String, Object> register(String username, String email, String password, String phone) {
        // 检查用户名是否存在
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .or()
                .eq(User::getEmail, email)) ;

        if (count > 0) {
            throw new BusinessException("Username or email already exists");
        }

        // 创建用户
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPhone(phone);
        user.setPassword(BCrypt.hashpw(password));
        user.setUserType(1);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), "USER", null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", username);

        return result;
    }

    @Override
    public Map<String, Object> login(String loginName, String password) {
        // 查询用户
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginName)
                .or()
                .eq(User::getEmail, loginName)
                .or()
                .eq(User::getPhone, loginName));

        if (user == null) {
            throw new BusinessException("Invalid credentials");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new BusinessException("Invalid credentials");
        }

        if (user.getStatus() == 0) {
            throw new BusinessException("Account is disabled");
        }

        // 更新登录信息
        user.setLastLoginTime(LocalDateTime.now());
        user.setLoginCount(user.getLoginCount() + 1);
        userMapper.updateById(user);

        // 生成 Token
        String token = jwtUtils.generateToken(user.getId(), "USER", null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("nickname", user.getNickname());
        result.put("userType", user.getUserType());

        return result;
    }

    @Override
    public Map<String, Object> refreshToken(String token) {
        Long userId = jwtUtils.getUserId(token);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("User not found");
        }

        String newToken = jwtUtils.generateToken(user.getId(), "USER", null);

        Map<String, Object> result = new HashMap<>();
        result.put("token", newToken);

        return result;
    }
}