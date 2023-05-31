package com.lx.service;

import com.lx.model.LoginResult;

/*
*  登录的接口
* */
public interface SysLoginService {
    LoginResult login(String username,String password);
}
