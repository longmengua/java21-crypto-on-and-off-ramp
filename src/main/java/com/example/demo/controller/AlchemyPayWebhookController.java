package com.example.demo.controller;

import com.example.demo.config.AlchemyPayConfig;
import com.example.demo.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.util.Map;

/**
 * AlchemyPay 支付回調 Controller
 */
@RestController
@RequestMapping("/alchemypay")
@Slf4j
public class AlchemyPayWebhookController {
    @Autowired
    private AlchemyPayConfig alchemyPayConfig;

    /**
     * 支付回調接口
     * AlchemyPay 將支付結果 POST 到此接口
     */
    @PostMapping("/callback")
    public String alchemyPayCallback(HttpServletRequest request) {
        try {
            // --------- 1. 讀取請求 body ----------
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            String body = sb.toString();
            log.info("AlchemyPay callback body: {}", body);

            // --------- 2. 讀取 header ----------
            String timestamp = request.getHeader("timestamp");
            String sign = request.getHeader("sign");

            if (timestamp == null || sign == null) {
                log.warn("Missing timestamp or sign header");
                return JsonUtil.toJson(Map.of(
                        "success", false,
                        "returnCode", "400",
                        "returnMsg", "Missing timestamp or sign"
                ));
            }

            // todo: 待確認
            // --------- 3. 驗證簽名 ----------
            // 回調簽名驗證公式：timestamp + body
//            boolean validSign = validateSign(timestamp, body, sign);
//            if (!validSign) {
//                log.warn("Invalid callback signature");
//                return JsonUtils.toJson(Map.of(
//                        "success", false,
//                        "returnCode", "401",
//                        "returnMsg", "Invalid signature"
//                ));
//            }

            // --------- 4. 處理回調邏輯 ----------
            Map<String, Object> callbackData = (Map<String, Object>) JsonUtil.parse(body);
            // TODO: 根據業務邏輯更新訂單狀態
            String merchantOrderNo = (String) callbackData.get("merchantOrderNo");
            String orderStatus = (String) callbackData.get("status");
            log.info("Order {} callback status: {}", merchantOrderNo, orderStatus);

            // --------- 5. 返回成功 ----------
            return JsonUtil.toJson(Map.of(
                    "success", true,
                    "returnCode", "0000",
                    "returnMsg", "SUCCESS"
            ));

        } catch (Exception e) {
            log.error("Error handling AlchemyPay callback", e);
            return JsonUtil.toJson(Map.of(
                    "success", false,
                    "returnCode", "500",
                    "returnMsg", "Internal error"
            ));
        }
    }

    /**
     * 驗證回調簽名
     * todo: 待確認
     */
//    private boolean validateSign(String timestamp, String body, String callbackSign) {
//        try {
//            String content = timestamp + body;
//            String expectedSign = AchSign.apiSignForCallback(content, alchemyPayConfig.getAppSecret());
//            return expectedSign.equals(callbackSign);
//        } catch (Exception e) {
//            log.error("Failed to validate callback sign", e);
//            return false;
//        }
//    }

    @GetMapping("/success")
    public String success(HttpServletRequest request) {
        return "success";
    }

    @GetMapping("/fail")
    public String fail(HttpServletRequest request) {
        return "fail";
    }
}
