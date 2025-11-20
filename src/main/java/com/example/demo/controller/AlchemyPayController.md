
## 取得虛擬貨幣清單

```bash
curl -X GET "http://localhost:8080/alchemypay/crypto" \
     -H "Content-Type: application/json"
```

## 取得法幣清單

```bash
curl -X GET "http://localhost:8080/alchemypay/fiat" \
     -H "Content-Type: application/json"
```

## 取得token

### 使用 Email

```bash
curl -X POST "http://localhost:8080/alchemypay/getToken" \
  -d '{
    "email": "waltor.h@intteam.net"
  }'
```

### 使用 UID

```bash
curl -X POST "http://localhost:8080/alchemypay/getToken" \
  -H "Content-Type: application/json" \
  -H "appId: YOUR_APP_ID" \
  -H "timestamp: 1699999999999" \
  -H "sign: YOUR_GENERATED_SIGN" \
  -d '{
    "uid": "1234567xxxxx"
  }'
```