package com.vibecoding.order.pay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 支付工厂 - 管理所有支付渠道
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFactory {

    private final List<PaymentChannel> channels;

    private PaymentChannel getChannel(String code) {
        return channels.stream()
            .filter(c -> c.getChannelCode().equalsIgnoreCase(code))
            .findFirst()
            .orElse(null);
    }

    /**
     * 创建支付订单
     */
    public Map<String, Object> createPayment(String channelCode, String orderNo,
                                           java.math.BigDecimal amount, String currency,
                                           String subject, String returnUrl, String notifyUrl) {
        PaymentChannel channel = getChannel(channelCode);
        if (channel == null) {
            log.warn("Payment channel not found: {}", channelCode);
            return Map.of("success", false, "error", "Payment channel not supported");
        }

        return channel.createPayment(orderNo, amount, currency, subject, returnUrl, notifyUrl);
    }

    /**
     * 查询支付状态
     */
    public String queryPayment(String channelCode, String channelTradeNo) {
        PaymentChannel channel = getChannel(channelCode);
        if (channel == null) {
            return "UNKNOWN";
        }
        return channel.queryPayment(channelTradeNo);
    }

    /**
     * 处理异步通知
     */
    public Map<String, Object> handleNotify(String channelCode, String notifyData) {
        PaymentChannel channel = getChannel(channelCode);
        if (channel == null) {
            return Map.of("verified", false, "error", "Channel not found");
        }
        return channel.handleNotify(notifyData);
    }

    /**
     * 退款
     */
    public Map<String, Object> refund(String channelCode, String channelTradeNo,
                                     java.math.BigDecimal amount, String reason) {
        PaymentChannel channel = getChannel(channelCode);
        if (channel == null) {
            return Map.of("success", false, "error", "Channel not found");
        }
        return channel.refund(channelTradeNo, amount, reason);
    }

    /**
     * 获取支持的支付渠道列表
     */
    public Map<String, String> getSupportedChannels() {
        Map<String, String> result = new HashMap<>();
        for (PaymentChannel channel : channels) {
            result.put(channel.getChannelCode(), channel.getChannelCode().toUpperCase());
        }
        return result;
    }
}