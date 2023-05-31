package com.lx.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "authorization-server")
public interface OAuth2FeignClient {

    @PostMapping("/oauth/token")
    ResponseEntity<JwtToken> getToken(
            @RequestParam("grant_type") String granType, //授权类型
            @RequestParam("username") String username,  // 用户名
            @RequestParam("password") String password,  // 密码
            @RequestParam("login_type") String loginType, // 登录的类型
            @RequestHeader(name = "Authorization",required = true) String basicToken //第三方客户端加密
    );
}
