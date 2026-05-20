package com.vibecoding.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.common.base.BaseService;
import com.vibecoding.user.entity.User;
import com.vibecoding.user.entity.UserAddress;
import com.vibecoding.user.mapper.UserAddressMapper;
import com.vibecoding.user.mapper.UserMapper;
import com.vibecoding.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends BaseService<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final UserAddressMapper userAddressMapper;

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }

    @Override
    public boolean updateById(User user) {
        return userMapper.updateById(user) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    @Override
    public List<UserAddress> listAddresses(Long userId) {
        return userAddressMapper.selectList(new LambdaQueryWrapper<UserAddress>()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getDeleted, 0)
                .orderByDesc(UserAddress::getIsDefault));
    }

    @Override
    @Transactional
    public void saveAddress(UserAddress address) {
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }
        userAddressMapper.insert(address);
    }

    @Override
    @Transactional
    public void updateAddress(UserAddress address) {
        if (address.getIsDefault() == 1) {
            clearDefaultAddress(address.getUserId());
        }
        userAddressMapper.updateById(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        userAddressMapper.deleteById(id);
    }

    private void clearDefaultAddress(Long userId) {
        List<UserAddress> addresses = listAddresses(userId);
        for (UserAddress a : addresses) {
            if (a.getIsDefault() == 1) {
                a.setIsDefault(0);
                userAddressMapper.updateById(a);
            }
        }
    }
}