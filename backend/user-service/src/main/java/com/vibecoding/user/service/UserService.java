package com.vibecoding.user.service;

import com.vibecoding.common.base.BaseService;
import com.vibecoding.user.entity.User;
import com.vibecoding.user.entity.UserAddress;
import com.vibecoding.user.mapper.UserMapper;

import java.util.List;

public interface UserService extends BaseService<UserMapper, User> {

    List<UserAddress> listAddresses(Long userId);

    void saveAddress(UserAddress address);

    void updateAddress(UserAddress address);

    void deleteAddress(Long id);
}