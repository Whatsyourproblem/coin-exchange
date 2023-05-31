package com.lx.service.impl;

import com.alibaba.fastjson.JSON;
import com.lx.feign.JwtToken;
import com.lx.feign.OAuth2FeignClient;
import com.lx.geetest.GeetestLib;
import com.lx.geetest.GeetestLibResult;
import com.lx.model.LoginForm;
import com.lx.model.LoginUser;
import com.lx.service.LoginService;
import com.lx.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private StringRedisTemplate strRedisTemplate;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private GeetestLib geetestLib;

    @Override
    public LoginUser login(LoginForm loginForm) {

        log.info("用户{}开始登录",loginForm.getUsername());
        checkFormData(loginForm);
        LoginUser loginUser = null;

        // 登录就是使用用户名和密码换一个token
        // 需要远程调用-> authorization-server
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", loginForm.getUsername(), loginForm.getPassword(), "member_type", basicToken);
        if (tokenResponseEntity.getStatusCode() == HttpStatus.OK){
            JwtToken jwtToken = tokenResponseEntity.getBody();
            log.info("远程调用成功，结果为", JSON.toJSONString(jwtToken,true));
            // token 必须包含bearer
            loginUser = new LoginUser(loginForm.getUsername(),jwtToken.getExpiresIn(),jwtToken.getTokenType()+ " " +jwtToken.getAccessToken(),jwtToken.getRefreshToken());
            // 使用网关解决登出问题
            // token是直接存储的
            strRedisTemplate.opsForValue().set(jwtToken.getAccessToken(),"",jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        }
        return loginUser;
    }

    /*
    *  校验数据
    *  极验的数据校验
    * */
    private void checkFormData(LoginForm loginForm) {
        // 这里由于 loginForm继承了GeetestForm,所以会存在GeetestForm中的check()方法,该方法用于极验的校验
        loginForm.check(geetestLib,redisTemplate);
    }
}
