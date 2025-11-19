package com.example.demo.controller;

import com.example.demo.config.AlchemyPayConfig;
import com.example.demo.constant.CryptoNetwork;
import com.example.demo.constant.PayWayCodeEnum;
import com.example.demo.constant.TradeSide;
import com.example.demo.service.AlchemyPayOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class AlchemyPayOrderController {
    @Autowired
    private AlchemyPayOrderService alchemyPayOrderService;
    @Autowired
    private AlchemyPayConfig alchemyPayConfig;

    /**
     * 建立新訂單
     */
    @PostMapping("/create")
    public String createOrder() {
        String timestamp = String.valueOf(System.currentTimeMillis());

        try {
            Map<String, Object> params = new HashMap<>();

            // 必填
            params.put("side", "BUY"); // 固定 BUY
            params.put("cryptoCurrency", "USDT"); // 加密貨幣
            params.put("address", "0x632b659467e8f0e16c8df49fadf4b620286b43f6"); // 收款地址
            params.put("network", "BSC"); // 網路
            params.put("fiatCurrency", "USD"); // 法幣
            params.put("merchantOrderNo", "ORD" + timestamp + UUID.randomUUID().toString().substring(0,8)); // 商戶自定義的訂單號，需要保證它在系統裡是唯一的
            params.put("amount", 100.00); // 法幣金額
            params.put("depositType", 2); // 固定值 2
            params.put("payWayCode", "10001"); // 支付方式
//            params.put("timestamp", timestamp);

            // 非必填
//            params.put("redirectUrl", alchemyPayConfig.getCallbackDomain() + "/alchemypay/success"); // 前端成功跳轉頁面
//            params.put("callbackUrl", alchemyPayConfig.getCallbackDomain() + "/alchemypay/callback"); // 後端回傳頁面
//        bodyMap.put("memo", "memo"); // 備註 (沙盒模式不支援)
//            params.put("failRedirectUrl", alchemyPayConfig.getCallbackDomain() + "/alchemypay/fail"); // 前端失敗跳轉頁面

            return alchemyPayOrderService.createOrder(params, timestamp);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"msg\":\"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 查詢訂單狀態
     */
    @GetMapping("/query")
    public String queryOrder(@RequestParam String merchantOrderNo) {
        try {
            return alchemyPayOrderService.queryOrder(merchantOrderNo);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\":false,\"msg\":\"" + e.getMessage() + "\"}";
        }
    }
}
