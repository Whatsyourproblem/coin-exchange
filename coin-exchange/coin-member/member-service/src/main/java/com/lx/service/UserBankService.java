package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.UserBank;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserBankService extends IService<UserBank>{

    /*
    *  查询用户的银行卡信息
    * */
    Page<UserBank> findByPage(Page<UserBank> page, Long userId);

    /*
    *  通过用户的id，查询用户的银行卡
    * */
    UserBank getCurrentUserBank(Long userId);

    /*
    *  给用户绑定银行卡
    * */
    boolean bindBank(Long userId, UserBank userBank);
}
