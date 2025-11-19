package com.example.demo;

import java.util.*;

public class AlchemyPayOrder {
    public static String createOrder() throws Exception {
        long ts = System.currentTimeMillis();
        String tsStr = String.valueOf(ts);

        String sign = APSignUtil.sha1Hex(APConfig.APP_ID + APConfig.APP_SECRET + tsStr);

        Map<String,Object> order = new LinkedHashMap<>();
        order.put("merchantOrderNo","ORDER" + ts);
        order.put("name","Test Item");
        order.put("amount",1000);
        order.put("fiat","USD");
        order.put("targetFiat","USDT");
        order.put("payWayCode","PAYCODE_EXAMPLE");
        order.put("callbackUrl","https://your.callback/notify");
        order.put("redirectUrl","https://your.store/success");
        order.put("failRedirectUrl","https://your.store/fail");

        String bodyJson = JsonUtils.toJson(order);
        String bodyMd5 = APSignUtil.md5Hex(bodyJson + APConfig.APP_SECRET);

        String url = APConfig.API_BASE + "/nft/openapi/trade/order";
        return APIClient.post(url, bodyJson, APConfig.APP_ID, sign, APConfig.ACCESS_TOKEN, tsStr, bodyMd5);
    }
}
