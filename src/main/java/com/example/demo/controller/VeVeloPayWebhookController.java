package com.example.demo.controller;

import com.example.demo.util.VeVeloPaySignUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/vevelopay")
public class VeVeloPayWebhookController {

    private final String secretKey = "yourSecretKey"; // 與 VeVeloPay 共享

    @PostMapping("/webhook")
    public String handleWebhook(@RequestBody Map<String, Object> payload, HttpServletResponse response) {
        try {
            // 簽名驗證
            String receivedSign = (String) payload.get("sign");
            String calculatedSign = VeVeloPaySignUtil.HMACSHA256(
                    VeVeloPaySignUtil.mapSort2Str(payload), secretKey
            );

            if (!calculatedSign.equals(receivedSign)) {
                response.setStatus(400);
                return "sign error";
            }

            // 處理訂單
            String merchantOrderId = (String) payload.get("merchantOrderId");
            Integer status = (Integer) payload.get("status");
            String detailStatus = (String) payload.get("detailStatus");

            // TODO: 更新你系統的訂單狀態
            System.out.println("訂單 " + merchantOrderId + " 更新為 " + status + "/" + detailStatus);

            return "ok"; // 回覆 ok 給 VeVeloPay
        } catch (Exception e) {
            response.setStatus(500);
            return "error";
        }
    }
}

