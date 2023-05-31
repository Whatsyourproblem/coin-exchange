package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CoinWithdraw;
import com.lx.domain.UserAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface UserAddressService extends IService<UserAddress>{



    /*
    *  通过用户的Id 分页查询用户的钱包地址
    * */
    Page<UserAddress> findByPage(Page<UserAddress> page, Long userId);

    /*
    *  使用用户的id和币种的id查询用户的充币地址
    * */
    UserAddress getUserAddressByUserIdAndCoinId(Long coinId,Long userId);

    /*
    *  用户对指定货币进行提现
    * */
    boolean withdrawCoin(Long userId, CoinWithdraw coinWithdraw);
}
