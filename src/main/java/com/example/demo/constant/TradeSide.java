package com.example.demo.constant;

import lombok.Getter;

/**
 * 交易方向枚舉
 */
@Getter
public enum TradeSide {

    BUY("BUY", "買入"),
    SELL("SELL", "賣出");

    private final String code;
    private final String name;

    TradeSide(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
