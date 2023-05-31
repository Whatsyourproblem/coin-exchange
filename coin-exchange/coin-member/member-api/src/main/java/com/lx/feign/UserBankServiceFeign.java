package com.lx.feign;

import com.lx.config.feign.OAuth2FeignConfig;
import com.lx.dto.UserBankDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/*
*  若FeignClient里面的name相同时,spring创建对象就会报错,它认为它们两个对象是一样的
* */
@FeignClient(name = "member-service",contextId = "userBankServiceFeign",configuration = OAuth2FeignConfig.class,path = "/userBanks")
public interface UserBankServiceFeign {

    @GetMapping("/{userId}/info")
    UserBankDto getUserBankInfo(@PathVariable(value = "userId") Long userId);
}
