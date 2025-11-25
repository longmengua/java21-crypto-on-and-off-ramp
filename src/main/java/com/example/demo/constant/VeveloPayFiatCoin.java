package com.example.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VeVeloPay 支援的法幣
 */
@Getter
@AllArgsConstructor
public enum VeveloPayFiatCoin {
    USD("USD", "美元"),
    EUR("EUR", "歐元"),
    TWD("TWD", "新台幣"),
    JPY("JPY", "日圓"),
    CNY("CNY", "人民幣"),
    GBP("GBP", "英鎊");

    private final String code;
    private final String description;
}
