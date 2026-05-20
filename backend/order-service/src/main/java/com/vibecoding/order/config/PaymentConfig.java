package com.vibecoding.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentConfig {

    private PayPalConfig paypal = new PayPalConfig();
    private AlipayConfig alipay = new AlipayConfig();
    private WechatConfig wechat = new WechatConfig();

    @Data
    public static class PayPalConfig {
        private String clientId;
        private String secret;
        private String mode; // sandbox/live
        private String baseUrl;
    }

    @Data
    public static class AlipayConfig {
        private String appId;
        private String privateKey;
        private String alipayPublicKey;
        private String gateway;
        private String format = "JSON";
        private String charset = "UTF-8";
        private String signType = "RSA2";
    }

    @Data
    public static class WechatConfig {
        private String appId;
        private String mchId;
        private String apiKey;
        private String notifyUrl;
        private String partnerId;
    }
}