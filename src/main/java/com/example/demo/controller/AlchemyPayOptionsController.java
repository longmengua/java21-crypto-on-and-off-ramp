package com.example.demo.controller;

import com.example.demo.config.AlchemyPayConfig;
import com.example.demo.util.AchSign;
import com.example.demo.util.JsonUtils;
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
 * GET /alchemypay/options/crypto
 * GET /alchemypay/options/fiat
 */
@RestController
@RequestMapping("/alchemypay/options")
public class AlchemyPayOptionsController {

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
            String resp = callAlchemyGet(path, null);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * 取得法幣清單（供下拉選單）
     */
    @GetMapping("/fiat")
    public ResponseEntity<String> fiatList() {
        try {
            String path = "/open/api/v4/merchant/fiat/list";
            String resp = callAlchemyGet(path, null);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /**
     * 通用呼叫 Alchemy Pay GET API 的 helper
     *
     * @param requestPath ex: /open/api/v4/merchant/crypto/list
     * @param queryParams 如果有 query 參數，可傳 Map（會自行排序拼接），否則傳 null
     * @return Alchemy Pay 回傳的原始 JSON 字串
     */
    private String callAlchemyGet(String requestPath, Map<String, Object> queryParams) throws Exception {
        String method = "GET";
        String timestamp = String.valueOf(System.currentTimeMillis());

        // 生成簽名：GET 的 body 為 null 或空 string
        String sign = AchSign.apiSign(timestamp, method, requestPath, queryParams, alchemyPayConfig.getAppSecret());

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

        // 若你想解析並只回傳 data field，可以用 JsonUtils.parse，再 return data
        // 例如：
        // Map<String,Object> parsed = JsonUtils.toMap(response.body());
        // return JsonUtils.toJson(parsed.get("data"));

        return response.body();
    }
}
