package com.lx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/*
*  解决跨域问题
* */
@Configuration
public class CorsConfig {


    /*
    *  如果是zuul网关，则使用CorsFilter
    * */
    @Bean
    public CorsWebFilter corsWebFilter(){

        // 跨域配置
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 允许请求头
        corsConfiguration.addAllowedHeader("*");
        // 允许请求域
        corsConfiguration.addAllowedOrigin("*");
        // 允许方法
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}
