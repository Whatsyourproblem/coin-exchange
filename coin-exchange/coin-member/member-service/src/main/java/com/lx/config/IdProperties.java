package com.lx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "identify")
@Data
// 阿里云身份验证服务
public class IdProperties {

    // 身份认证的URL地址
    // http://naidcard.market.alicloudapi.com/nidCard
    // http://naidcard.market.alicloudapi.com/nidCard?idCard=%s&name=%s
    private String url;

    /*
    *
    *   你购买的appKey
    * */
    private String appKey;

    /*
    *  你购买的appSecret
    * */
    private String appSecret;

    /*
    *  你购买的appCode
    * */
    private String appCode;



}
