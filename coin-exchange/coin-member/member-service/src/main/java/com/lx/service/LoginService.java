package com.lx.service;

import com.lx.model.LoginForm;
import com.lx.model.LoginUser;

public interface LoginService {

    /*
    *  会员的登录
    * */
    LoginUser login(LoginForm loginForm);
}
