package com.vibecoding.order.service;

import com.vibecoding.common.base.BaseService;
import com.vibecoding.common.result.PageResult;
import com.vibecoding.order.entity.Order;
import com.vibecoding.order.mapper.OrderMapper;

import java.util.Map;

public interface OrderService extends BaseService<OrderMapper, Order> {

    PageResult<Order> pageList(Long userId, int pageNum, int pageSize, String status);

    Map<String, Object> create(Long userId, Map<String, Object> params);

    void cancel(Long orderId, Long userId);
}