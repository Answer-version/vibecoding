package com.vibecoding.user.service;

import com.vibecoding.user.entity.User;
import com.vibecoding.user.entity.UserAddress;

import java.util.List;

public interface UserService {

    User getById(Long id);

    boolean save(User user);

    boolean updateById(User user);

    boolean removeById(Long id);

    List<UserAddress> listAddresses(Long userId);

    void saveAddress(UserAddress address);

    void updateAddress(UserAddress address);

    void deleteAddress(Long id);
}