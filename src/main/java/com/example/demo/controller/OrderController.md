
## 建立訂單

```bash
curl -X POST "http://localhost:8080/order/alchemypay/create" \
     -H "Content-Type: application/json"
```

## 查詢訂單

```bash
curl -X GET "http://localhost:8080/order/alchemypay/query?merchantOrderNo=ORDER1699999999999"
```
