package com.vibecoding.user.controller;

import com.vibecoding.common.result.R;
import com.vibecoding.user.entity.User;
import com.vibecoding.user.entity.UserAddress;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final com.vibecoding.user.service.UserService userService;

    @GetMapping("/profile")
    public R<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return R.ok(userService.getById(userId));
    }

    @PutMapping("/profile")
    public R<Void> updateProfile(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        userService.updateById(user);
        return R.ok();
    }

    @GetMapping("/addresses")
    public R<List<UserAddress>> listAddresses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return R.ok(userService.listAddresses(userId));
    }

    @PostMapping("/addresses")
    public R<UserAddress> addAddress(@RequestBody UserAddress address, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        address.setUserId(userId);
        userService.saveAddress(address);
        return R.ok(address);
    }

    @PutMapping("/addresses/{id}")
    public R<Void> updateAddress(@PathVariable Long id, @RequestBody UserAddress address) {
        address.setId(id);
        userService.updateAddress(address);
        return R.ok();
    }

    @DeleteMapping("/addresses/{id}")
    public R<Void> deleteAddress(@PathVariable Long id) {
        userService.deleteAddress(id);
        return R.ok();
    }
}