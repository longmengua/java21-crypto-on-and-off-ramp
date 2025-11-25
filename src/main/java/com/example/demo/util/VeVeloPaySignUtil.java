package com.example.demo.util;

import com.example.demo.constant.VeVeloPayNetwork;
import com.example.demo.constant.VeveloPayFiatCoin;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Map;
import java.util.TreeMap;

public class VeVeloPaySignUtil {

    private static final String WIDGET_ID = "N2tDRWhwUjg";
    private static final String SECRET_KEY = "ODZjNzk0NTk3MDA2YjBlYWU4ZmQyYTQ2YzkwOTVhOWY0YzFiZDkwZg";

    // 固定法幣、虛擬幣、網絡
    private static final String FIXED_FIAT = VeveloPayFiatCoin.USD.getCode();
    private static final String FIXED_CRYPTO = "USDT";
    private static final String FIXED_NETWORK = VeVeloPayNetwork.BNB.getNetworkName();

    /**
     * Build iframe URL for VeVeloPay Widget
     *
     * @param walletAddress user wallet address
     * @param tradeType     "buy" or "sell"
     * @return full URL with signature
     */
    public static String buildWidgetUrl(String walletAddress, String tradeType) {
        long timestamp = System.currentTimeMillis();

        Map<String, Object> data = new TreeMap<>();
        data.put("widgetId", WIDGET_ID);
        data.put("timestamp", timestamp);
        data.put("tradeType", tradeType);
        data.put("fiatCoin", FIXED_FIAT);
        data.put("cryptoCoin", FIXED_CRYPTO);
        data.put("network", FIXED_NETWORK);
        data.put("walletAddress", walletAddress);

        String sortedStr = mapSort2Str(data);
        String signature = apiSign(sortedStr, SECRET_KEY);

        // Build final URL
        return String.format(
                "https://sandbox-env.openc.pro/widget-page/?widgetId=%s&fixCryptoCoin=true&&timestamp=%d&tradeType=%s&fiatCoin=%s&cryptoCoin=%s&walletAddress=%s&network=%s&sign=%s",
                WIDGET_ID,
                timestamp,
                tradeType,
                FIXED_FIAT,
                FIXED_CRYPTO,
                walletAddress,
                FIXED_NETWORK,
                signature
        );
    }

    public static String mapSort2Str(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : new TreeMap<>(params).entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String apiSign(String data, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(Integer.toHexString(b & 0xFF | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMACSHA256", e);
        }
    }

    // Example usage
    public static void main(String[] args) {
        String walletAddress = "0x632b659467e8f0e16c8df49fadf4b620286b43f6";
        String tradeType = "buy";

        String url = buildWidgetUrl(walletAddress, tradeType);
        System.out.println("Widget URL: " + url);
    }
}
