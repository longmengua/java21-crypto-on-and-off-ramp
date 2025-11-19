package com.example.demo.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * AchSign 工具類
 * 用於生成 API 請求簽名（HMAC-SHA256 + Base64）
 * 並對參數進行去空、排序、JSON 序列化處理
 */
public class AchSign {

    // ================= API 簽名 =================

    /**
     * 生成 API 簽名
     *
     * @param timestamp 時間戳
     * @param method HTTP 方法，例如 POST、GET
     * @param path API 路徑
     * @param paramMap 請求參數
     * @param secretKey 用於 HMAC 的密鑰
     * @return Base64 編碼的簽名
     */
    public static String apiSign(
            String timestamp,
            String method,
            String path,
            Map<String, Object> paramMap,
            String secretKey
    )
            throws NoSuchAlgorithmException, InvalidKeyException {

        // 拼接簽名原文：timestamp + method + path + JSON 化的參數
        String content = timestamp + method.toUpperCase() + path + toSortedJson(paramMap);

        // 計算 HMAC-SHA256
        byte[] hash = hmacSha256(content.getBytes(StandardCharsets.UTF_8),
                secretKey.getBytes(StandardCharsets.UTF_8));

        // Base64 編碼
        return Base64.getEncoder().encodeToString(hash);
    }

    /**
     * 計算 HMAC-SHA256
     *
     * @param message 原文
     * @param secret 密鑰
     * @return HMAC-SHA256 bytes
     */
    private static byte[] hmacSha256(byte[] message, byte[] secret)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSha256");
        mac.init(new SecretKeySpec(secret, "HmacSha256"));
        return mac.doFinal(message);
    }

    // ================= JSON 處理 =================

    /**
     * 將 Map 參數去空、排序後轉成 JSON 字串
     */
    private static String toSortedJson(Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        // 去掉空值參數
        Map<String, Object> filtered = removeEmptyKeys(params);

        // 排序 Map
        Map<String, Object> sorted = sortMap(filtered);

        // JSON 序列化（假設你有 JsonUtils 工具）
        return JsonUtils.toJson(sorted);
    }

    /**
     * 去掉 Map 中值為 null 或空字串的鍵值對
     */
    private static Map<String, Object> removeEmptyKeys(Map<String, ?> map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * 遞歸排序 Map（key 升序）
     * 如果 value 是 Map 或 List，也會遞歸排序
     */
    private static Map<String, Object> sortMap(Map<String, Object> map) {
        if (map.isEmpty()) return Collections.emptyMap();

        SortedMap<String, Object> sortedMap = new TreeMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Map) {
                value = sortMap((Map<String, Object>) value); // 遞歸 Map
            } else if (value instanceof List) {
                value = sortList((List<Object>) value);       // 遞歸 List
            }

            sortedMap.put(entry.getKey(), value);
        }
        return sortedMap;
    }

    /**
     * 遞歸排序 List
     * 對不同類型做分組排序：Integer / BigDecimal / String / 嵌套集合
     */
    private static List<Object> sortList(List<Object> list) {
        if (list.isEmpty()) return Collections.emptyList();

        // 分組存放不同類型
        List<Integer> intList = new ArrayList<>();
        List<BigDecimal> floatList = new ArrayList<>();
        List<String> stringList = new ArrayList<>();
        List<Object> nestedList = new ArrayList<>();

        for (Object obj : list) {
            if (obj instanceof Map || obj instanceof List) {
                nestedList.add(obj); // 嵌套集合
            } else if (obj instanceof Integer) {
                intList.add((Integer) obj);
            } else if (obj instanceof BigDecimal) {
                floatList.add((BigDecimal) obj);
            } else if (obj instanceof String) {
                stringList.add((String) obj);
            } else {
                // 其他類型嘗試轉成 Integer
                intList.add(Integer.parseInt(obj.toString()));
            }
        }

        // 各類型排序
        Collections.sort(intList);
        Collections.sort(floatList, Comparator.comparing(BigDecimal::doubleValue));
        Collections.sort(stringList);

        // 合併排序後的集合
        List<Object> sorted = new ArrayList<>();
        sorted.addAll(intList);
        sorted.addAll(floatList);
        sorted.addAll(stringList);
        sorted.addAll(nestedList);

        // 遞歸排序嵌套集合
        List<Object> result = new ArrayList<>();
        for (Object obj : sorted) {
            if (obj instanceof Map) {
                result.add(sortMap((Map<String, Object>) obj));
            } else if (obj instanceof List) {
                result.add(sortList((List<Object>) obj));
            } else {
                result.add(obj);
            }
        }
        return result;
    }

    // ================= Main =================
    public static void main(String[] args) throws Exception {
        String secretKey = "你的 appSecret"; // 替換成實際測試用 appSecret

        // ---------------- POST 範例：Create Order ----------------
        String timestampPost = "1699261493465";
        String methodPost = "POST";
        String pathPost = "/open/api/v4/merchant/trade/create";

        Map<String, Object> bodyPost = getStringStringMap();

        // 使用 sortMap + toSortedJson 生成排序後 JSON
        String sortedJsonPost = toSortedJson(bodyPost);
        String signPost = apiSign(timestampPost, methodPost, pathPost, bodyPost, secretKey);

        System.out.println("=== POST Create Order ===");
        System.out.println("timestamp: " + timestampPost);
        System.out.println("sorted JSON: " + sortedJsonPost);
        System.out.println("sign: " + signPost);

        // ---------------- GET 範例：Query Order ----------------
        String timestampGet = "1699261493465";
        String methodGet = "GET";
        String pathGet = "/open/api/v4/merchant/query/trade";

        Map<String, Object> queryGet = new HashMap<>();
        queryGet.put("orderNo", "1028577684629876736");
        queryGet.put("side", "BUY");
        queryGet.put("email", "abc@gamial.com");

        // 使用 sortMap 排序 query 參數
        Map<String, Object> sortedQuery = sortMap(queryGet);

        // 拼接 query string
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedQuery.entrySet()) {
            if (!sb.isEmpty()) sb.append("&");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        String fullPathGet = pathGet + "?" + sb.toString();

        // GET 請求 bodyString 為空
        String signGet = apiSign(timestampGet, methodGet, fullPathGet, null, secretKey);

        System.out.println("=== GET Query Order ===");
        System.out.println("timestamp: " + timestampGet);
        System.out.println("query string: " + sb.toString());
        System.out.println("sign: " + signGet);
    }

    private static Map<String, Object> getStringStringMap() {
        Map<String, Object> bodyPost = new HashMap<>();
        bodyPost.put("side", "BUY");
        bodyPost.put("cryptoCurrency", "USDT");
        bodyPost.put("address", "TSx82tWNWe5Ns6t3w94Ye3Gt6E5KeHSoP8");
        bodyPost.put("network", "TRX");
        bodyPost.put("fiatCurrency", "USD");
        bodyPost.put("amount", "100");
        bodyPost.put("depositType", 2);
        bodyPost.put("payWayCode", "10001");
        bodyPost.put("alpha2", "US");
        bodyPost.put("redirectUrl", "");
        bodyPost.put("callbackUrl", "http://payment.jyoumoney.com/alchemyRamp/pay/callback?tradeNo=DZ02207091800356504");
        return bodyPost;
    }
}
