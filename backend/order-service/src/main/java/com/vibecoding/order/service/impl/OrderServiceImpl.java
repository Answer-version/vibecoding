package com.vibecoding.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vibecoding.common.base.BaseService;
import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.common.result.PageResult;
import com.vibecoding.order.entity.Order;
import com.vibecoding.order.entity.OrderItem;
import com.vibecoding.order.mapper.OrderMapper;
import com.vibecoding.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseService<OrderMapper, Order> implements OrderService {

    @Override
    public PageResult<Order> pageList(Long userId, int pageNum, int pageSize, String status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId);
        wrapper.eq(Order::getDeleted, 0);

        if (status != null && !status.isBlank()) {
            wrapper.eq(Order::getOrderStatus, status);
        }

        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> pageResult = page(new Page<>(pageNum, pageSize), wrapper);

        return PageResult.of(pageResult.getRecords(), pageResult.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional
    public Map<String, Object> create(Long userId, Map<String, Object> params) {
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setOrderStatus("PENDING");
        order.setPayStatus("PENDING");
        order.setShipStatus("PENDING");

        // 从 params 提取收货地址等信息
        order.setShipName((String) params.get("shipName"));
        order.setShipPhone((String) params.get("shipPhone"));
        order.setShipCountryCode((String) params.get("countryCode"));
        order.setShipAddress1((String) params.get("address"));

        // 金额
        BigDecimal amount = new BigDecimal(params.getOrDefault("amount", "0").toString());
        order.setTotalAmount(amount);
        order.setUsdAmount(amount);
        order.setCurrency("USD");

        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        baseMapper.insert(order);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());

        return result;
    }

    @Override
    @Transactional
    public void cancel(Long orderId, Long userId) {
        Order order = baseMapper.selectById(orderId);

        if (order == null) {
            throw new BusinessException("Order not found");
        }

        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("No permission");
        }

        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException("Cannot cancel");
        }

        order.setOrderStatus("CANCELLED");
        order.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(order);
    }

    private String generateOrderNo() {
        return "VC" + LocalDateTime.now().getYear()
                + String.format("%02d", LocalDateTime.now().getMonthValue())
                + String.format("%02d", LocalDateTime.now().getDayOfMonth())
                + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
    }
}