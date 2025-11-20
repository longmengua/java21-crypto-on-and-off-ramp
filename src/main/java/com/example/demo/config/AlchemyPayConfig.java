package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * AlchemyPay 配置
 * 使用 @Value 注入，並提供默認值
 */
@Configuration
@Getter
@Setter
public class AlchemyPayConfig {

    /**
     * App ID，用於標識應用
     */
    @Value("${alchemypay.appId:f83Is2y7L425rxl8}")
    private String appId;

    /**
     * App Secret，用於生成簽名，必須保密
     */
    @Value("${alchemypay.appSecret:5Zp9SmtLWQ4Fh2a1}")
    private String appSecret;

    /**
     * Access Token，可放固定值或由後端配置
     */
    @Value("${alchemypay.accessToken:ACH7084714072@ACH@c+G1UnaozACJuboWub8nug==@PAY@YuyyGJ/PGErpmIG27wXbzLZXM5pE/a6o987wGZz1abw=@IO@czAJ1TXnlD8kCY1MdGLXJYuinFE4SB+TcVkrS8IUsWC9UOVId+wxNpZrM18sKlIoNmHp5OQFtfLf14+zzURrHA==}")
    private String accessToken;

    /**
     * 回調域名
     */
    @Value("${alchemypay.callbackDomain:https://lakiesha-condemnable-unvauntingly.ngrok-free.dev}")
    private String callbackDomain;

    /**
     * API 基本地址
     * Domains
     * Prod: https://openapi.alchemypay.org/
     * Test: https://openapi-test.alchemypay.org/
     */
    @Value("${alchemypay.apiBase:https://openapi-test.alchemypay.org}")
    private String apiBase;
}
