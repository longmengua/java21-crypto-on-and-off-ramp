package com.example.demo.controller;

import com.example.demo.config.AlchemyPayConfig;
import com.example.demo.util.AchSignUtil;
import com.example.demo.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

/**
 * 提供給前端下拉選單用的選項（法幣、虛擬貨幣）
 *
 * GET /alchemypay/crypto
 * GET /alchemypay/fiat
 * POST /alchemypay/getToken
 */
@RestController
@RequestMapping("/alchemypay")
public class AlchemyPayController {

    @Autowired
    private AlchemyPayConfig alchemyPayConfig;

    // 共享 HttpClient
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * 取得虛擬貨幣清單（供下拉選單）
     */
    @GetMapping("/crypto")
    public ResponseEntity<String> cryptoList() {
        try {
            String path = "/open/api/v4/merchant/crypto/list";
            String resp = callAlchemyGet(path);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonUtil.toJson(Map.of("error", e.getMessage())));
        }
    }

    /**
     * 取得法幣清單（供下拉選單）
     */
    @GetMapping("/fiat")
    public ResponseEntity<String> fiatList() {
        try {
            String path = "/open/api/v4/merchant/fiat/list";
            String resp = callAlchemyGet(path);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonUtil.toJson(Map.of("error", e.getMessage())));
        }
    }

    /**
     * 新增：呼叫 Alchemy Pay 的 getToken API（支援以 email 或 uid 取得 token）
     * POST /alchemypay/getToken
     * Body 範例：{ "email": "test@gmail.com" } 或 { "uid": "1234-..." }
     */
    @PostMapping(value = "/getToken", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> getToken(@RequestBody Map<String, Object> body) {
        try {
            // 必須傳入 email 或 uid 其中一個
            boolean hasEmail = body.containsKey("email") && body.get("email") != null && !String.valueOf(body.get("email")).isBlank();
            boolean hasUid = body.containsKey("uid") && body.get("uid") != null && !String.valueOf(body.get("uid")).isBlank();

            if (!hasEmail && !hasUid) {
                // 回傳 API 所需的錯誤碼與訊息結構（3108: Missing Parameter）
                return ResponseEntity.badRequest().body(JsonUtil.toJson(Map.of(
                        "success", false,
                        "returnCode", "3108",
                        "returnMsg", "Missing Parameter: must provide email or uid"
                )));
            }

            String requestPath = "/open/api/v4/merchant/getToken";
            String timestamp = String.valueOf(System.currentTimeMillis());

            // 這裡把 body map 傳給簽名函式（假設 AchSign.apiSign 會根據 method 與傳入的 map 處理 POST body 的簽名）
            String sign = AchSignUtil.apiSign(timestamp, "POST", requestPath, body, alchemyPayConfig.getAppSecret());

            String url = alchemyPayConfig.getApiBase() + requestPath;
            String bodyJson = JsonUtil.toJson(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(bodyJson))
                    .header("Content-Type", "application/json")
                    .header("appId", alchemyPayConfig.getAppId())
                    .header("timestamp", timestamp)
                    .header("sign", sign)
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            return ResponseEntity.status(response.statusCode()).body(response.body());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(JsonUtil.toJson(Map.of("error", e.getMessage())));
        }
    }

    /**
     * 通用呼叫 Alchemy Pay GET API 的 helper
     *
     * @param requestPath ex: /open/api/v4/merchant/crypto/list
     * @return Alchemy Pay 回傳的原始 JSON 字串
     */
    private String callAlchemyGet(String requestPath) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());

        // 生成簽名：GET 的 body 為 null 或空 string
        String sign = AchSignUtil.apiSign(timestamp, "GET", requestPath, null, alchemyPayConfig.getAppSecret());

        String url = alchemyPayConfig.getApiBase() + requestPath;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .header("appId", alchemyPayConfig.getAppId())
//                .header("access-token", alchemyPayConfig.getAccessToken())
                .header("timestamp", timestamp)
                .header("sign", sign)
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
