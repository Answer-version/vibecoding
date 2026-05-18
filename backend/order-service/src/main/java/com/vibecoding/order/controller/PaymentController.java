package com.vibecoding.order.controller;

import com.vibecoding.common.exception.BusinessException;
import com.vibecoding.common.result.R;
import com.vibecoding.order.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public R<Map<String, Object>> create(
            @RequestBody Map<String, Object> params,
            @RequestHeader(value = "Authorization", required = false) String auth) {

        Long orderId = Long.parseLong(params.get("orderId").toString());
        String payMethod = params.get("payMethod").toString();
        String returnUrl = (String) params.get("returnUrl");

        return R.ok(paymentService.createPayment(orderId, payMethod, returnUrl));
    }

    @PostMapping("/notify/{payMethod}")
    public String notify(
            @PathVariable String payMethod,
            @RequestBody Map<String, Object> params) {

        paymentService.handleCallback(payMethod, params);
        return "success";
    }

    @GetMapping("/{orderId}")
    public R<Map<String, Object>> getPaymentInfo(@PathVariable Long orderId) {
        return R.ok(paymentService.getPaymentInfo(orderId));
    }
}