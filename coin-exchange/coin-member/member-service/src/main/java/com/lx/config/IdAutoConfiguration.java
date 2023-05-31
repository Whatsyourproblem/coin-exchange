package com.lx.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(IdProperties.class)
public class IdAutoConfiguration {

    private static IdProperties idProperties;

    /*
    *  发送请求的工具
    * */
    private static RestTemplate restTemplate = new RestTemplate();

    public IdAutoConfiguration(IdProperties idProperties){
        IdAutoConfiguration.idProperties = idProperties;
    }

    /*
    *  用户信息的实名认证
    *   用户的真实姓名和身份证号
    * */
    public static boolean check(String realName,String cardNum){

        /*
        *  本次请求我们是AppCode的形式验证
        * */

        /*
        * 请求头
        *   Header: {"Authorization":"APPCODE 9931ed84bd8942538d4f4407719a7f4b","X-Ca-Timestamp":"1674372849264","gateway_channel":"http","X-Ca-Key":"204148835","x-ca-nonce":"dcf0202d-d22d-492e-b6d1-f8288e054b12","X-Ca-Request-Mode":"DEBUG","X-Ca-Stage":"RELEASE","X-Ca-Supervisor-Token":"eyJhbGciOiJSUzI1NiJ9.eyJpYXQiOjE2NzQzNzI4NDksIm5iZiI6MTY3NDM3Mjc4OSwiaXNzIjoiQWxpeXVuQXBpR2F0ZXdheSIsInJvbGUiOiJ1c2VyIiwiYXVkIjoiYXBpLXNoYXJlZC12cGMtMDAxIiwiZXhwIjoxNjc0MzczNzQ5LCJqdGkiOiJhMmMwNWIyOGQ1YTM0ZTY5OTEzZTQ1NjVhZDZlN2ZlYyIsImFjdGlvbiI6IkRFQlVHIiwidWlkIjoiMTcxNDk2NTUwMTg5MzYwOSJ9.UU9XGhIeQZHZbPl5jKg1eMPLDaj-Kh_Eie5M9elX5-fc03aRZ00bH3u9MroFLA2FW3HN63kON-usLCzhcVsnJdv9XXnVqd7e3fn1I1dMobm0rExJxZEKtPLMhvbcwbHqe8opv-WQWOF7SZq3LrvNok4_ooPLcrx4nEGM37VaC_ON76eBAExtdTwuk1SdB0UxMzyjY1CktyAjGdn0QAb7oPCtjo1fyUkEueItf3TsqunXNdHeigk6Eo6K1mVV3eCB19Xwi32PqIMxEUswzrtL0OkJ0LcSPemSJUqPcHWGy20FyERC8T0EdV2k7PvmyR24YhdyXT_br-mbZ-2ntAHhhA","Host":"naidcard.market.alicloudapi.com","Content-Type":"text/html; charset=utf-8"}
        * */

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization","APPCODE " + idProperties.getAppCode());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                //%s 是变量
                String.format(idProperties.getUrl(), cardNum, realName),
                HttpMethod.GET,
                new HttpEntity<>(null, httpHeaders),
                String.class
        );
        // 详细教程参照文档
        if (responseEntity.getStatusCode() == HttpStatus.OK){
            String body = responseEntity.getBody();
            JSONObject jsonObject = JSON.parseObject(body);
            String status = jsonObject.getString("status");
            if ("01".equals(status)){ //验证成功
                return true;
            }
        }
        return false;
    }
}
