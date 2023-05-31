package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CoinWithdraw;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.UserAddressMapper;
import com.lx.domain.UserAddress;
import com.lx.service.UserAddressService;
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService{

    @Override
    public Page<UserAddress> findByPage(Page<UserAddress> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,userId));
    }

    /*
     *  使用用户的id和币种的id查询用户的充币地址
     * */
    @Override
    public UserAddress getUserAddressByUserIdAndCoinId(Long coinId, Long userId) {
        LambdaQueryWrapper<UserAddress> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAddress::getCoinId,coinId).eq(UserAddress::getUserId,userId);
        return getOne(queryWrapper);
    }

    /*
     *  用户对指定货币进行提现
     * */
    @Override
    public boolean withdrawCoin(Long userId, CoinWithdraw coinWithdraw) {

        // 1.根据userId查询出用户信息

        // 2.验证短信验证码

        // 3.验证支付密码

        // 4.判断 提现货币的数量余量是否充足

        // 5.提现货币进行扣减 基础货币按照 数量*汇率进行增加

        // 6.保存

        return false;
    }
}
