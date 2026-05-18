package com.vibecoding.order.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
public class PayPalGateway implements PaymentGateway {

    @Override
    public String getName() {
        return "PayPal";
    }

    @Override
    public String createPayment(Long orderId, String orderNo, BigDecimal amount, String currency) {
        String paymentId = "PP-" + UUID.randomUUID().toString().substring(0, 8);
        log.info("Created PayPal payment: {} for order: {}", paymentId, orderNo);
        return paymentId;
    }

    @Override
    public Map<String, Object> queryPayment(String paymentNo) {
        log.info("Query PayPal payment: {}", paymentNo);
        return Map.of("status", "pending");
    }

    @Override
    public boolean verifyCallback(Map<String, String> params) {
        return true;
    }
}
