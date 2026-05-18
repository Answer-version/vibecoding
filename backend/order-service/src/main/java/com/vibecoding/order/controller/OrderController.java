package com.vibecoding.order.controller;

import com.vibecoding.common.result.PageResult;
import com.vibecoding.common.result.R;
import com.vibecoding.order.entity.Order;
import com.vibecoding.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public R<Map<String, Object>> checkout(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return R.ok(orderService.create(userId, params));
    }

    @GetMapping
    public R<PageResult<Order>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return R.ok(orderService.pageList(userId, page, pageSize, status));
    }

    @GetMapping("/{id}")
    public R<Order> get(@PathVariable Long id) {
        return R.ok(orderService.getById(id));
    }

    @PostMapping("/{id}/cancel")
    public R<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancel(id, userId);
        return R.ok();
    }
}