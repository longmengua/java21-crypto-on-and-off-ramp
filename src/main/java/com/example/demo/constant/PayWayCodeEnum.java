package com.example.demo.constant;

/**
 * 支付方式枚舉對照 AlchemyPay payWayCode
 */
public enum PayWayCode {

    VISA("501", "信用卡 Visa/MasterCard"),
    ALIPAY("502", "支付寶"),
    BANK_TRANSFER("503", "銀行轉帳"),
    PAYPAL("504", "PayPal"),
    // 可根據官方文件補充更多
    ;

    private final String code;
    private final String name;

    PayWayCode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
