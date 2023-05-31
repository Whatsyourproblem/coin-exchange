package com.lx.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.lx.domain.SysMenu;
import com.lx.feign.JwtToken;
import com.lx.feign.OAuth2FeignClient;
import com.lx.model.LoginResult;
import com.lx.service.SysLoginService;
import com.lx.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}开始登录",username);
        // 1.获取token 需要远程调用authorization-server服务
        System.out.println("2222222222222222222222222");
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", username, password, "admin_type",basicToken);
        System.out.println("1111111111111111111");
        System.out.println(tokenResponseEntity);
        if (tokenResponseEntity.getStatusCode() != HttpStatus.OK){
            throw new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken jwtToken = tokenResponseEntity.getBody();
        log.info("远程调用授权服务器成功，获取的token为{}", JSON.toJSONString(jwtToken,true));
        String token = jwtToken.getAccessToken();
        // 2.查询我们的菜单数据
        Jwt jwt = JwtHelper.decode(token);
        String jwtJson = jwt.getClaims();
        JSONObject jsonObject = JSON.parseObject(jwtJson);
        Long userId = Long.valueOf(jsonObject.getString("user_name"));
        List<SysMenu> menus = sysMenuService.getMenusByUserId(userId);
        System.out.println(menus);
        // 3.权限数据怎么查询 --不需要查询，因为jwt里面已经携带的有权限数据
        JSONArray authoritiesJsonArray = jsonObject.getJSONArray("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesJsonArray.stream() //组装我们的权限数据
                .map(authorityJson->new SimpleGrantedAuthority(authorityJson.toString()))
                .collect(Collectors.toList());

        //将token 存储redis里面， 配置网关做jwt验证的操作
        redisTemplate.opsForValue().set(token, "", jwtToken.getExpiresIn(), TimeUnit.SECONDS);
        return new LoginResult(jwtToken.getTokenType()+ " " + token,menus,authorities);
    }
}
