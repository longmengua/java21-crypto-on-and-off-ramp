package com.example.demo.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.*;
import java.util.*;

public class AchSign {


    public static String apiSign(String timestamp, String method, String path, Map<String, String> paramMap, String secretkey) throws NoSuchAlgorithmException, InvalidKeyException {
        String content = timestamp + method.toUpperCase() + path + getJsonBody(paramMap);
        Base64.Encoder base = Base64.getEncoder();
        String signVal = base.encodeToString(sha256(content.getBytes(StandardCharsets.UTF_8), secretkey.getBytes(StandardCharsets.UTF_8)));
        return signVal;
    }

    public static byte[] sha256(byte[] message, byte[] secret) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSha256");
        SecretKeySpec secretKey = new SecretKeySpec(secret, "HmacSha256");
        sha256_HMAC.init(secretKey);
        return sha256_HMAC.doFinal(message);
    }


    private static String getJsonBody(Map<String,String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }
        parameters = removeEmptyKeys(parameters);
        parameters = (Map) sortObject(parameters);
        return JsonUtils.toJson(parameters);
    }


    private static Map removeEmptyKeys(Map map) {
        if (map.isEmpty()) {
            return map;
        }
        Map retMap = new HashMap();
        Iterator<Map.Entry> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            if (entry.getValue() != null && !entry.getValue().equals("")) {
                retMap.put(entry.getKey(), entry.getValue());
            }
        }
        return retMap;
    }

    private static Object sortObject(Object obj) {
        if (obj instanceof Map) {
            return sortMap((Map) obj);
        } else if (obj instanceof List) {
            sortList((List) obj);
            return obj;
        }
        return null;
    }

    private static Map sortMap(Map map) {
        if (map.isEmpty()) {
            return null;
        }
        SortedMap<String, Object> sortedMap = new TreeMap<>(removeEmptyKeys(map));
        for (String sortKey : sortedMap.keySet()) {
            if (sortedMap.get(sortKey) instanceof Map) {
                sortedMap.put(sortKey, sortMap((Map) sortedMap.get(sortKey)));
            } else if (sortedMap.get(sortKey) instanceof List) {
                sortedMap.put(sortKey, sortList((List) sortedMap.get(sortKey)));
            }
        }
        return sortedMap;
    }

    private static List sortList(List list) {
        if (list.isEmpty()) {
            return null;
        }
        List objectList = new ArrayList();

        List intList = new ArrayList();
        List floatList = new ArrayList();
        List stringList = new ArrayList();

        List jsonArray = new ArrayList();
        for (Object obj : list) {
            if (obj instanceof Map || obj instanceof List) {
                jsonArray.add(obj);
            } else if (obj instanceof Integer) {
                intList.add(obj);
            } else if (obj instanceof BigDecimal) {
                floatList.add(obj);
            } else if (obj instanceof String) {
                stringList.add(obj);
            } else {
                intList.add(obj);
            }
        }

        Collections.sort(intList);
        Collections.sort(floatList);
        Collections.sort(stringList);

        objectList.addAll(intList);
        objectList.addAll(floatList);
        objectList.addAll(stringList);
        objectList.addAll(jsonArray);

        list.clear();
        list.addAll(objectList);


        List retList = new ArrayList();

        for (Object obj : list) {
            if (obj instanceof Map) {
                retList.add(sortMap((Map) obj));
            } else if (obj instanceof List) {
                retList.add(sortList((List) obj));
            } else {
                retList.add(obj);
            }
        }
        return retList;
    }

    public static void main(String[] args) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String method = "";
        String path = "";
        Map map = new HashMap();
        String secretkey = "";
        String sign = apiSign(timestamp, method, path, map, secretkey);
        System.out.println(timestamp);
        System.out.println(sign);
        System.out.println(URLEncoder.encode(sign));
    }
}
