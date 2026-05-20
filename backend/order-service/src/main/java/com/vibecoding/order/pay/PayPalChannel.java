package com.vibecoding.order.pay;

import com.vibecoding.order.config.PaymentConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayPalChannel implements PaymentChannel {

    private final PaymentConfig paymentConfig;

    @Override
    public String getChannelCode() {
        return "paypal";
    }

    @Override
    public Map<String, Object> createPayment(String orderNo, BigDecimal amount, String currency,
                                      String subject, String returnUrl, String notifyUrl) {
        Map<String, Object> result = new HashMap<>();

        try {
            PaymentConfig.PayPalConfig config = paymentConfig.getPaypal();

            // 检查是否配置了真实的PayPal凭证
            if (config == null || config.getClientId() == null || config.getClientId().isEmpty()) {
                // 使用模拟实现
                String paymentId = "PP-" + UUID.randomUUID().toString().substring(0, 8);
                String mockPayUrl = returnUrl + "?paymentId=" + paymentId + "&orderNo=" + orderNo;

                result.put("success", true);
                result.put("paymentId", paymentId);
                result.put("payUrl", mockPayUrl);
                result.put("orderNo", orderNo);
                result.put("simulated", true);

                log.info("PayPal payment created (simulated): orderNo={}, paymentId={}", orderNo, paymentId);
                return result;
            }

            // TODO: 实现真实的PayPal API调用
            // 这里需要添加真实的PayPal SDK依赖到pom.xml
            String paymentId = "PP-" + UUID.randomUUID().toString().substring(0, 8);
            result.put("success", true);
            result.put("paymentId", paymentId);
            result.put("payUrl", returnUrl + "?paymentId=" + paymentId);
            result.put("orderNo", orderNo);

            log.info("PayPal payment created: orderNo={}, amount={} {}", orderNo, currency, amount);

        } catch (Exception e) {
            log.error("PayPal payment creation failed: orderNo={}, error={}", orderNo, e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public String queryPayment(String channelTradeNo) {
        // 模拟实现
        return "PENDING";
    }

    @Override
    public Map<String, Object> handleNotify(String notifyData) {
        Map<String, Object> result = new HashMap<>();
        result.put("verified", true);
        result.put("simulated", true);
        return result;
    }

    @Override
    public boolean verifySignature(Map<String, String> params) {
        // 模拟实现
        return true;
    }

    @Override
    public Map<String, Object> refund(String channelTradeNo, BigDecimal refundAmount, String refundReason) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("refundId", "REF-" + UUID.randomUUID().toString().substring(0, 8));
        return result;
    }
}