package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VeVeloPayOrderService {

    /**
     * 查詢 Sell Order 狀態
     * @param merchantOrderId 你的訂單 ID
     * @param apiUrl VeVeloPay API URL，例如：https://sandbox-api.openc.pro
     * @return JSON 字串
     * @throws Exception
     */
    public static String querySellOrder(String merchantOrderId, String apiUrl) throws Exception {
        String requestUrl = apiUrl + "/sellOrder/query/" + merchantOrderId;
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(5000); // 5 秒連線 timeout
        conn.setReadTimeout(5000);    // 5 秒讀取 timeout

        int responseCode = conn.getResponseCode();

        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(responseCode == 200 ? conn.getInputStream() : conn.getErrorStream())
        )) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            if (responseCode == 200) {
                return response.toString(); // 回傳 JSON 字串
            } else {
                throw new RuntimeException("Query failed, responseCode=" + responseCode + ", body=" + response.toString());
            }
        } finally {
            conn.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        String apiUrl = "https://sandbox-api.openc.pro"; // 測試用 API
        String merchantOrderId = "2220328152313";

        String result = querySellOrder(merchantOrderId, apiUrl);
        System.out.println("Sell Order Result: " + result);
    }
}
