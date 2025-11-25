package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VeVeloPayOrderService {

    public static String querySellOrder(String merchantOrderId, String apiUrl) throws Exception {
        URL url = new URL(apiUrl + "/sellOrder/query/" + merchantOrderId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        int responseCode = conn.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) response.append(inputLine);
        in.close();

        if (responseCode == 200) {
            return response.toString(); // 回傳 JSON 字串
        } else {
            throw new RuntimeException("Query failed, responseCode=" + responseCode);
        }
    }

    public static void main(String[] args) throws Exception {
        String result = querySellOrder("2220328152313", "https://YOUR_API_URL");
        System.out.println(result);
    }
}
