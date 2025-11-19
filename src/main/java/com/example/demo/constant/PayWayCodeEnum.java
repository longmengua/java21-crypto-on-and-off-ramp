package com.example.demo.constant;

import lombok.Getter;

/**
 * 支付方式枚舉對照 AlchemyPay payWayCode
 */
@Getter
public enum PayWayCodeEnum {

    VISA("10001", "Visa/Master Card"),
    ALIPAY("501", "Apple Pay"),
    BANK_TRANSFER("701", "Google Pay"),
    ;

    private final String code;
    private final String name;

    PayWayCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
