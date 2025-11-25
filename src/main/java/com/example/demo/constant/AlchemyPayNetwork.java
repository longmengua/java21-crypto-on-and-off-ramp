package com.example.demo.constant;

import lombok.Getter;

/**
 * 支付網路枚舉（Crypto acquiring network）
 */
@Getter
public enum AlchemyPayNetwork {

    ETH("ETH", "以太坊"),
    BSC("BSC", "幣安智能鏈"),
    TRON("TRX", "Tron 網路"),
    SOLANA("SOL", "Solana 網路"),
    POLYGON("MATIC", "Polygon 網路"),
    // 可根據官方支持補充更多
    ;

    private final String code;
    private final String name;

    AlchemyPayNetwork(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
