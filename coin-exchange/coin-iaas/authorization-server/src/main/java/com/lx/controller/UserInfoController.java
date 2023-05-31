package com.lx.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.plugin.liveconnect.SecurityContextHelper;

import java.security.Principal;

@RestController
public class UserInfoController {


    /*
    *  当前登录的用户对象
    * */
    @GetMapping("/user/info")
    public Principal userInfo(Principal principal){
        // 使用ThreadLocal来实现的
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return principal;
    }

}
