package com.vibecoding.order.service;

import java.util.Map;

public interface PaymentService {

    Map<String, Object> createPayment(Long orderId, String payMethod, String returnUrl);

    void handleCallback(String payMethod, Map<String, Object> params);

    Map<String, Object> getPaymentInfo(Long orderId);
}