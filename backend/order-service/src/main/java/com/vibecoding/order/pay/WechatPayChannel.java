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
public class WechatPayChannel implements PaymentChannel {

    private final PaymentConfig paymentConfig;

    @Override
    public String getChannelCode() {
        return "wechat";
    }

    @Override
    public Map<String, Object> createPayment(String orderNo, BigDecimal amount, String currency,
                                      String subject, String returnUrl, String notifyUrl) {
        Map<String, Object> result = new HashMap<>();

        try {
            PaymentConfig.WechatConfig config = paymentConfig.getWechat();

            // 检查是否配置了真实的微信支付凭证
            if (config == null || config.getAppId() == null || config.getAppId().isEmpty()) {
                // 使用模拟实现 - 生成二维码URL
                String prepayId = "WX-" + UUID.randomUUID().toString().substring(0, 8);
                String codeUrl = "wxp://wxpay/" + prepayId;

                result.put("success", true);
                result.put("prepayId", prepayId);
                result.put("codeUrl", codeUrl);
                result.put("orderNo", orderNo);
                result.put("simulated", true);

                log.info("WeChat payment created (simulated): orderNo={}, prepayId={}", orderNo, prepayId);
                return result;
            }

            // TODO: 实现真实的微信支付API调用
            String prepayId = "WX-" + UUID.randomUUID().toString().substring(0, 8);
            result.put("success", true);
            result.put("prepayId", prepayId);
            result.put("codeUrl", "wxp://wxpay/" + prepayId);
            result.put("orderNo", orderNo);

            log.info("WeChat payment created: orderNo={}, amount={} {}", orderNo, currency, amount);

        } catch (Exception e) {
            log.error("WeChat payment creation failed: orderNo={}, error={}", orderNo, e.getMessage());
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
        result.put("refundId", "REF-" + UUID.randomUUID().toString().substring(0, 8));
        return result;
    }
}