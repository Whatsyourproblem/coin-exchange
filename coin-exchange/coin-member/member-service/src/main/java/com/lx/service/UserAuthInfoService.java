package com.lx.service;

import com.lx.domain.UserAuthInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserAuthInfoService extends IService<UserAuthInfo>{

    /*
    *  通过认证的Code 来查询用户的认证详情
    * */
    List<UserAuthInfo> getUserAuthInfoByCode(Long authCode);

    /*
    *  用户未被认证，我们需要通过用户的id 查询用户的认证列表
    * */
    List<UserAuthInfo> getUserAuthInfoByUserId(Long id);
}
