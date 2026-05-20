package com.vibecoding.order.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class WechatPayGateway implements PaymentGateway {

    @Override
    public String getChannel() {
        return "wechat";
    }

    @Override
    public String getName() {
        return "WeChat Pay";
    }

    @Override
    public String createPayment(Long orderId, String orderNo, BigDecimal amount, String currency, String returnUrl) {
        String paymentId = "WX-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Created WeChat Pay payment: {} for order: {}", paymentId, orderNo);
        return paymentId;
    }

    @Override
    public Map<String, Object> queryPayment(String paymentNo) {
        log.info("Query WeChat Pay payment: {}", paymentNo);
        return Map.of("status", "pending");
    }

    @Override
    public boolean verifyCallback(Map<String, Object> params) {
        // 验证签名
        return true;
    }
}