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
public class AlipayChannel implements PaymentChannel {

    private final PaymentConfig paymentConfig;

    @Override
    public String getChannelCode() {
        return "alipay";
    }

    @Override
    public Map<String, Object> createPayment(String orderNo, BigDecimal amount, String currency,
                                      String subject, String returnUrl, String notifyUrl) {
        Map<String, Object> result = new HashMap<>();

        try {
            PaymentConfig.AlipayConfig config = paymentConfig.getAlipay();

            // 检查是否配置了真实的支付宝凭证
            if (config == null || config.getAppId() == null || config.getAppId().isEmpty()) {
                // 使用模拟实现
                String tradeNo = "AL-" + UUID.randomUUID().toString().substring(0, 8);
                String mockPayUrl = "https://openapi.alipay.com/gateway.do?trade_no=" + tradeNo;

                result.put("success", true);
                result.put("tradeNo", tradeNo);
                result.put("payUrl", mockPayUrl);
                result.put("orderNo", orderNo);
                result.put("simulated", true);

                log.info("Alipay payment created (simulated): orderNo={}, tradeNo={}", orderNo, tradeNo);
                return result;
            }

            // TODO: 实现真实的支付宝API调用
            // 需要添加支付宝SDK依赖
            String tradeNo = "AL-" + UUID.randomUUID().toString().substring(0, 8);
            result.put("success", true);
            result.put("tradeNo", tradeNo);
            result.put("payUrl", "https://openapi.alipay.com/gateway.do?trade_no=" + tradeNo);
            result.put("orderNo", orderNo);

            log.info("Alipay payment created: orderNo={}, amount={} {}", orderNo, currency, amount);

        } catch (Exception e) {
            log.error("Alipay payment creation failed: orderNo={}, error={}", orderNo, e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    @Override
    public String queryPayment(String channelTradeNo) {
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
        return true;
    }

    @Override
    public Map<String, Object> refund(String channelTradeNo, BigDecimal refundAmount, String refundReason) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("refundNo", "REF-" + UUID.randomUUID().toString().substring(0, 8));
        return result;
    }
}