package com.vibecoding.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.order.entity.Order;
import com.vibecoding.order.entity.Payment;
import com.vibecoding.order.mapper.PaymentMapper;
import com.vibecoding.order.gateway.AlipayGateway;
import com.vibecoding.order.gateway.PayPalGateway;
import com.vibecoding.order.gateway.PaymentGateway;
import com.vibecoding.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentMapper paymentMapper;
    private final AlipayGateway alipayGateway;
    private final PayPalGateway payPalGateway;

    private static final Map<String, String> CHANNEL_MAP = Map.of(
            "ALIPAY", "alipay",
            "PAYPAL", "paypal",
            "CREDIT_CARD", "stripe"
    );

    @Override
    @Transactional
    public Map<String, Object> createPayment(Long orderId, String payMethod, String returnUrl) {
        // 查找订单
        Order order = new Order(); // TODO: OrderMapper 查询
        if (order == null) {
            throw new BusinessException("Order not found");
        }

        if (!"PENDING".equals(order.getOrderStatus())) {
            throw new BusinessException("Order cannot be paid");
        }

        // 选择支付网关
        PaymentGateway gateway = getGateway(payMethod);
        if (gateway == null) {
            throw new BusinessException("Unsupported payment method");
        }

        // 创建支付记录
        String paymentNo = "PAY" + System.currentTimeMillis();
        Payment payment = new Payment();
        payment.setPaymentNo(paymentNo);
        payment.setOrderId(orderId);
        payment.setOrderNo(order.getOrderNo());
        payment.setUserId(order.getUserId());
        payment.setPayMethod(payMethod);
        payment.setPayChannel(gateway.getChannel());
        payment.setAmount(order.getTotalAmount());
        payment.setCurrency(order.getCurrency());
        payment.setPayStatus("PENDING");
        payment.setCreateTime(LocalDateTime.now());
        payment.setUpdateTime(LocalDateTime.now());

        paymentMapper.insert(payment);

        // 调用支付网关创建支付
        Map<String, Object> gatewayPayment = gateway.createPayment(
                orderId,
                order.getOrderNo(),
                order.getTotalAmount(),
                order.getCurrency(),
                returnUrl
        );

        // 更新支付记录
        payment.setChannelOrderNo((String) gatewayPayment.get("paymentNo"));
        payment.setPayUrl((String) gatewayPayment.get("payUrl"));
        paymentMapper.updateById(payment);

        Map<String, Object> result = new HashMap<>();
        result.put("paymentNo", paymentNo);
        result.put("payUrl", gatewayPayment.get("payUrl"));
        result.put("amount", order.getTotalAmount());

        return result;
    }

    @Override
    @Transactional
    public void handleCallback(String payMethod, Map<String, Object> params) {
        PaymentGateway gateway = getGateway(payMethod);
        if (gateway == null || !gateway.verifyCallback(params)) {
            log.warn("Invalid callback: {}", params);
            return;
        }

        String paymentNo = (String) params.get("out_trade_no");
        if (paymentNo == null) {
            paymentNo = (String) params.get("orderId");
        }

        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getPaymentNo, paymentNo));

        if (payment == null) {
            log.error("Payment not found: {}", paymentNo);
            return;
        }

        if ("PENDING".equals(payment.getPayStatus())) {
            payment.setPayStatus("SUCCESS");
            payment.setChannelTradeNo((String) params.get("trade_no"));
            payment.setNotifyData(params.toString());
            payment.setUpdateTime(LocalDateTime.now());
            paymentMapper.updateById(payment);

            // 更新订单状态
            Order order = new Order(); // TODO: 查询
            if (order != null) {
                order.setPayStatus("PAID");
                order.setPayTime(LocalDateTime.now());
                order.setOrderStatus("PAID");
                // orderMapper.updateById(order);
            }
        }
    }

    @Override
    public Map<String, Object> getPaymentInfo(Long orderId) {
        Payment payment = paymentMapper.selectOne(new LambdaQueryWrapper<Payment>()
                .eq(Payment::getOrderId, orderId)
                .orderByDesc(Payment::getCreateTime)
                .last("LIMIT 1"));

        if (payment == null) {
            return null;
        }

        Map<String, Object> info = new HashMap<>();
        info.put("paymentNo", payment.getPaymentNo());
        info.put("amount", payment.getAmount());
        info.put("currency", payment.getCurrency());
        info.put("status", payment.getPayStatus());
        info.put("payMethod", payment.getPayMethod());

        return info;
    }

    private PaymentGateway getGateway(String payMethod) {
        return switch (payMethod.toUpperCase()) {
            case "ALIPAY" -> alipayGateway;
            case "PAYPAL" -> payPalGateway;
            default -> null;
        };
    }
}