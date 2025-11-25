package com.example.demo.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class VeVeloPaySignUtil {

    // HMAC-SHA256 簽名
    public static String HMACSHA256(String data, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString(b & 0xFF | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMACSHA256", e);
        }
    }

    // Map 排序轉字串 key1=value1&key2=value2
    public static String mapSort2Str(Map<String, Object> params) {
        Map<String, Object> treeMap = new TreeMap<>();
        if (params != null && !params.isEmpty()) {
            treeMap.putAll(params);
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : treeMap.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        if (sb.length() > 0) sb.setLength(sb.length()-1);
        return sb.toString();
    }

    public static void main(String[] args) {
        // 範例參數
        Map<String, Object> data = new TreeMap<>();
        data.put("widgetId", "yourWidgetId");
        data.put("timestamp", System.currentTimeMillis());
        data.put("tradeType", "buy");
        data.put("fiatCoin", "USD");
        data.put("cryptoCoin", "BTC");
        data.put("walletAddress", "TDD8xpRpYQxhURoqf3cXwtgnyq6JKP3sr1");

        String sortedStr = mapSort2Str(data);
        String signature = HMACSHA256(sortedStr, "yourSecretKey");
        System.out.println("簽名: " + signature);
    }
}
