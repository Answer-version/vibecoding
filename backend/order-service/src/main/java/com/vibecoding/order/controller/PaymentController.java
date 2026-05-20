package com.vibecoding.order.controller;

import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.common.result.R;
import com.vibecoding.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public R<Map<String, Object>> create(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String auth) {

        Long orderId = Long.parseLong(params.get("orderId").toString());
        String payMethod = params.get("payMethod").toString();
        String returnUrl = (String) params.get("returnUrl");

        return R.ok(paymentService.createPayment(orderId, payMethod, returnUrl));
    }

    /**
     * 支付回调通知
     */
    @PostMapping("/notify/{payMethod}")
    public String notify(
            @PathVariable String payMethod,
            @RequestBody Map<String, Object> params) {

        paymentService.handleCallback(payMethod, params);
        return "success";
    }

    /**
     * 获取支付信息
     */
    @GetMapping("/{orderId}")
    public R<Map<String, Object>> getPaymentInfo(@PathVariable Long orderId) {
        return R.ok(paymentService.getPaymentInfo(orderId));
    }

    /**
     * 获取支持的支付方式列表
     */
    @GetMapping("/methods")
    public R<Map<String, String>> getPaymentMethods() {
        return R.ok(Map.of(
            "paypal", "PayPal",
            "alipay", "Alipay",
            "wechat", "WeChat Pay"
        ));
    }
}