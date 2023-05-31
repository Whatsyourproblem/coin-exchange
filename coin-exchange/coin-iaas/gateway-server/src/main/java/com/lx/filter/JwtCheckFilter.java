package com.lx.filter;

import com.alibaba.fastjson.JSONObject;
import com.google.common.net.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
public class JwtCheckFilter implements GlobalFilter, Ordered {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${no.require.urls:/admin/login,/user/gt/register,/user/login,/user/users/register,/user/sms/sendTo,/user/users/setPassword,/exchange/markets/kline}")
    private Set<String> noRequireTokenUris;

    /*
    *  过滤器拦截到用户的请求后做啥
    * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.接口是否需要token才能访问
        if(!isRequireToken(exchange)){
            // 不需要token 直接放行
            return chain.filter(exchange);
        }
        // 2.取出用户的token
        String token = getUserToken(exchange);
        // 3.用户的token是否有效
        if (StringUtils.isEmpty(token)){
            return buildNoAuthorizationResult(exchange);
        }
        Boolean hasKey = redisTemplate.hasKey(token);
        if (hasKey!=null && hasKey){
            // token有效 直接放行
            return chain.filter(exchange);
        }
        return buildNoAuthorizationResult(exchange);
    }

    /*
    *  给用户响应一个没有token的错误
    * */
    private Mono<Void> buildNoAuthorizationResult(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set("Content-Type","application/json");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error","NoAuthorization");
        jsonObject.put("errorMsg","Token is Null or Error");
        DataBuffer wrap = response.bufferFactory().wrap(jsonObject.toJSONString().getBytes());
        return response.writeWith(Flux.just(wrap));
    }

    /*
    *  从请求头获取用户token
    * */
    private String getUserToken(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        return token == null ? null : token.replace("bearer ","");
    }

    /*
    *  判断这个接口是否需要token
    * */
    private boolean isRequireToken(ServerWebExchange exchange) {
        String path = exchange.getRequest().getURI().getPath();
        if (noRequireTokenUris.contains(path)){
            return false;// 不需要token
        }
        if (path.contains("/kline/")){
            return false;
        }
        return Boolean.TRUE;
    }

    /*
    *  拦截器的顺序
    * */
    @Override
    public int getOrder() {
        return 0;
    }
}
