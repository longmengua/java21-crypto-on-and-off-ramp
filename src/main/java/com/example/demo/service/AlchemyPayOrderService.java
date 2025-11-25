package com.example.demo.service;

import com.example.demo.config.AlchemyPayConfig;
import com.example.demo.util.AchSignUtil;
import com.example.demo.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * AlchemyPay 訂單服務
 * 支援建立訂單與查詢訂單
 */
@Service
public class AlchemyPayOrderService {
    // 建立單例 HttpClient 避免每次都創建
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Autowired
    private AlchemyPayConfig alchemyPayConfig;

    /**
     * 建立訂單
     * @return API 原始回應 JSON
     *
     * | payWayCode | 支付方式                    |
     * | ---------- | ----------------------- |
     * | 501        | 信用卡 Visa/MasterCard（示例） |
     * | 502        | 支付寶                     |
     * | 503        | 銀行轉帳                    |
     * | …          | …                       |
     */
    public String createOrder(Map<String, Object> params, String timestamp) throws Exception {
        // --------- API 基本信息 ----------
        String path = "/open/api/v4/merchant/trade/create";
        String method = "POST";

        // --------- 生成簽名 ----------
        String sign = AchSignUtil.apiSign(timestamp, method, path, params, alchemyPayConfig.getAppSecret());

        // --------- 發送 POST 請求 ----------
        String jsonBody = JsonUtil.toJson(params);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(alchemyPayConfig.getApiBase() + path))
                .header("Content-Type", "application/json")
                .header("appId", alchemyPayConfig.getAppId())
                .header("access-token", alchemyPayConfig.getAccessToken())
                .header("timestamp", timestamp)
                .header("sign", sign)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    /**
     * 查詢訂單狀態
     * @param merchantOrderNo 商戶訂單號
     * @return API 原始回應 JSON
     */
    public String queryOrder(String merchantOrderNo) throws Exception {
        String path = "/open/api/v4/merchant/trade/query";
        String method = "POST"; // API 說明為 POST
        String timestamp = String.valueOf(System.currentTimeMillis());

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("merchantOrderNo", merchantOrderNo);

        // 生成簽名
        String sign = AchSignUtil.apiSign(timestamp, method, path, bodyMap, alchemyPayConfig.getAppSecret());

        // 發送 POST 請求
        String jsonBody = JsonUtil.toJson(bodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(alchemyPayConfig.getApiBase() + path))
                .header("Content-Type", "application/json")
                .header("appId", alchemyPayConfig.getAppId())
                .header("access-token", alchemyPayConfig.getAccessToken())
                .header("timestamp", timestamp)
                .header("sign", sign)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
