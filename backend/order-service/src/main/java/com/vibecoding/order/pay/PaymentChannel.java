package com.vibecoding.order.pay;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付渠道接口
 */
public interface PaymentChannel {

    /**
     * 获取渠道标识
     */
    String getChannelCode();

    /**
     * 创建支付订单
     * @param orderNo 平台订单号
     * @param amount 支付金额
     * @param currency 币种
     * @param subject 商品标题
     * @param returnUrl 支付返回地址
     * @param notifyUrl 异步通知地址
     * @return 支付信息，包含支付链接等
     */
    Map<String, Object> createPayment(String orderNo, BigDecimal amount, String currency,
                                      String subject, String returnUrl, String notifyUrl);

    /**
     * 查询支付状态
     * @param channelTradeNo 渠道交易号
     * @return 支付状态
     */
    String queryPayment(String channelTradeNo);

    /**
     * 处理异步通知
     * @param notifyData 通知数据
     * @return 验证结果和解析出的信息
     */
    Map<String, Object> handleNotify(String notifyData);

    /**
     * 验证签名
     * @param params 参数
     * @return 是否有效
     */
    boolean verifySignature(Map<String, String> params);

    /**
     * 退款
     * @param channelTradeNo 渠道交易号
     * @param refundAmount 退款金额
     * @param refundReason 退款原因
     * @return 退款结果
     */
    Map<String, Object> refund(String channelTradeNo, BigDecimal refundAmount, String refundReason);
}