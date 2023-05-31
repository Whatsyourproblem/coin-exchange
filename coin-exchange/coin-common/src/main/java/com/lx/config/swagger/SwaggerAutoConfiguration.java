package com.lx.config.swagger;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    private SwaggerProperties swaggerProperties;

    /*
    *  使用构造器进行注入ioc
    * */
    public SwaggerAutoConfiguration(SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
    }

    @Bean
    public Docket docket() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2) //文档类型
                .apiInfo(apiInfo()) //api 信息
                .select()
                //给这个路径生成文档(给哪些接口生成文档 )
                 .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getBasePackage()))
//                .apis(RequestHandlerSelectors.basePackage("com.lx.controller"))
                .paths(PathSelectors.any())
                .build();
        // 安全的配置
        docket.securitySchemes(securitySchemes()) // 安全规则
                .securityContexts(securityContexts());          // 安全配置的上下文
        return docket;
    }

    /**
     * <h2>安全的上下文</h2>
     * @param
     **/
    private List<SecurityContext> securityContexts() {
        return Arrays.asList(new SecurityContext(
                Arrays.asList(new SecurityReference("Authorization", new AuthorizationScope[]{new AuthorizationScope("global", "accessResource")})),
                PathSelectors.any()
        ));
    }

    /**
     * <h2>安全的规则配置</h2>
     * @param
     **/
    private List<SecurityScheme> securitySchemes() {
        return Arrays.asList(new ApiKey("Authorization", "Authorization", "Authorization"));
    }

    /**
     * <h2>api 信息的简介</h2>
     *
     * @param
     **/
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().contact(
                new Contact(
                        swaggerProperties.getName(),
                        swaggerProperties.getUrl(),
                        swaggerProperties.getEmail())
        )
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .version(swaggerProperties.getVersion())
                .termsOfServiceUrl(swaggerProperties.getTermsOfServiceUrl())
                .build();
/*        return new ApiInfoBuilder().contact(
                new Contact("","","")
                            )
                            .title("title")
                            .description("")
                            .version("")
                            .termsOfServiceUrl("")
                            .build();*/
    }
}
