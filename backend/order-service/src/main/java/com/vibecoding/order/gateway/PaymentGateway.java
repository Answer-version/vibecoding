package com.vibecoding.order.gateway;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentGateway {
    String getChannel();
    String getName();
    String createPayment(Long orderId, String orderNo, BigDecimal amount, String currency, String returnUrl);
    Map<String, Object> queryPayment(String paymentNo);
    boolean verifyCallback(Map<String, Object> params);
}
