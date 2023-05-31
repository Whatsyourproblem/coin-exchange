package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.UserAuthInfo;
import com.lx.mapper.UserAuthInfoMapper;
import com.lx.service.UserAuthInfoService;
@Service
public class UserAuthInfoServiceImpl extends ServiceImpl<UserAuthInfoMapper, UserAuthInfo> implements UserAuthInfoService{

    /*
     *  通过认证的Code 来查询用户的认证详情
     * */
    @Override
    public List<UserAuthInfo> getUserAuthInfoByCode(Long authCode) {
        return list(new LambdaQueryWrapper<UserAuthInfo>()
                    .eq(UserAuthInfo::getAuthCode,authCode) //通过认证的唯一Code 来查询用户的认证信息
        );
    }

    /*
     *  用户未被认证，我们需要通过用户的id 查询用户的认证列表
     * */
    @Override
    public List<UserAuthInfo> getUserAuthInfoByUserId(Long id) {
        List<UserAuthInfo> list = list(new LambdaQueryWrapper<UserAuthInfo>().eq(UserAuthInfo::getUserId, id));
        return  list == null ? Collections.emptyList() : list; //处理null
    }
}
