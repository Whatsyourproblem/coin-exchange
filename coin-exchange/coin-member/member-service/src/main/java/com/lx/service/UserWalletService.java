package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.UserWallet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserWalletService extends IService<UserWallet>{


    /*
    *  分页查询用户的提币地址
    * */
    Page<UserWallet> findByPage(Page<UserWallet> page, Long userId);

    /*
    *  查询指定币种id的钱包地址
    * */
    List<UserWallet> findUserWallets(Long userId, Long coinId);

    /*
    *  新增指定币种的提现地址
    * */
    boolean add(Long userId, UserWallet userWallet);

    /*
    * 删除用户指定币种的提现地址
    * */
    boolean delete(Long addressId, String payPassword);
}
