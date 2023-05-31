package com.lx.config.feign;


import com.lx.constant.Constants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class OAuth2FeignConfig implements RequestInterceptor {
    /*
    *  当我们发起每一次的远程调用时，都会调用这个方法
    * */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1.我们可以从request的上下文环境中获取token
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String header = null;
        if (requestAttributes == null){
            log.info("没有请求的上下文，故无法进行token的传递");
            header = "bearer " + Constants.INSIDE_TOKEN;
        }else{
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            // 获取我们请求上下文的里面的请求头
            header = request.getHeader(HttpHeaders.AUTHORIZATION);
        }

        if (!StringUtils.isEmpty(header)){
            requestTemplate.header(HttpHeaders.AUTHORIZATION,header);
            log.info("本次token传递成功,token的值为:{}",header);
        }
    }
}
