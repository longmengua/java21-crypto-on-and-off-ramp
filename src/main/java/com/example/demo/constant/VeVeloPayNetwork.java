package com.example.demo.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * VeVeloPay 支援的加密貨幣網絡 (network)
 * 用於 Widget / API 請求中指定區塊鏈
 */
@AllArgsConstructor
@Getter
public enum VeVeloPayNetwork {

    // 公鏈
    BITCOIN("Bitcoin", "BTC 主網"),
    ETHEREUM("Ethereum", "ETH 主網"),
    ALGORAND("Algorand", "ALGO 主網"),
    TRON("Tron", "TRX 主網"),
    SOLANA("Solana", "SOL 主網"),
    BNB("Binance Smart Chain", "BNB 主網"),
    HEDERA("Hedera Hashgraph", "HBAR 主網"),
    KLAYTN("Klaytn", "KLAY 主網"),
    FILECOIN("Filecoin", "FIL 主網"),
    MIOTA("MIOTA", "IOTA 主網"),
    NEAR("NEAR Protocol", "NEAR 主網"),
    OKEXCHAIN("OKExChain", "OKT 主網"),
    QTUM("Qtum", "QTUM 主網"),
    ELROND("Elrond eGold", "EGLD 主網"),
    WAVAX("WAVAX", "Avalanche WAVAX"),
    POLKADOT("Polkadot", "DOT 主網"),
    NEM("NEM", "XEM 主網"),
    CELO("Celo", "CELO 主網"),
    STELLAR("Stellar", "XLM 主網"),
    CARDANO("Cardano", "ADA 主網"),
    TEZOS("Tezos", "XTZ 主網"),
    TERRA("TeTerra", "Terra 主網"),
    APTOS("Aptos", "APT 主網"),
    COSMOS("Cosmos", "ATOM 主網"),
    ARBITRUM("Arbitrum", "ETH Layer2"),
    AVALANCHE_CCHAIN("Avalanche C-Chain", "AVAX EVM 兼容鏈"),
    FANTOM("Fantom", "FTM EVM 兼容鏈"),
    EOS("EOS", "EOS 主網"),
    HARMONY("Harmony", "ONE 主網"),
    LITECOIN("Litecoin", "LTC 主網"),
    OPTIMISM("Optimism", "ETH Layer2"),
    DASH("Dash", "DASH 主網"),
    CELESTIA("Celestia", "分片鏈 / Layer1"),
    BITCOIN_CASH("Bitcoin Cash", "BCH 主網"),
    TON("Ton", "TON 主網"),
    RIPPLE("Ripple", "XRP 主網"),
    BASE("Base", "ETH Layer2"),
    ZILLIQA("Zilliqa", "ZIL 主網"),
    LINEA("Linea", "ETH Layer2"),
    POLYGON("Polygon", "ETH Layer2 / Sidechain"),
    ZKSYNC("ZKSYNC", "ETH Layer2"),
    ETHEREUM_CLASSIC("Ethereum Classic", "ETC 主網");

    private final String networkName;
    private final String description;
}
