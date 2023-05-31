package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.User;
import com.lx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.UserWallet;
import com.lx.mapper.UserWalletMapper;
import com.lx.service.UserWalletService;
import org.springframework.util.StringUtils;

@Service
public class UserWalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements UserWalletService{

    @Autowired
    private UserService userService;

    /*
     *  分页查询用户的提币地址
     * */
    @Override
    public Page<UserWallet> findByPage(Page<UserWallet> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserWallet>().eq(UserWallet::getUserId,userId));
    }

    /*
     *  查询指定币种id的钱包地址
     * */
    @Override
    public List<UserWallet> findUserWallets(Long userId, Long coinId) {
        return list(new LambdaQueryWrapper<UserWallet>()
                .eq(UserWallet::getUserId,userId)
                .eq(UserWallet::getCoinId,coinId));
    }

    /*
     *  新增指定币种的提现地址
     * */
    @Override
    public boolean add(Long userId, UserWallet userWallet) {
        User user = userService.getById(userId);
        if (user == null){
            throw new IllegalArgumentException("该用户不存在");
        }
        // 校验交易密码
        String paypassword = user.getPaypassword();
        if (StringUtils.isEmpty(paypassword) || !(new BCryptPasswordEncoder().matches(userWallet.getPayPassword(),paypassword))){
            throw new IllegalArgumentException("交易密码错误");
        }
        userWallet.setUserId(userId);
        return save(userWallet);
    }

    /*
     * 删除用户指定币种的提现地址
     * */
    @Override
    public boolean delete(Long addressId, String payPassword) {
        UserWallet userWallet = getById(addressId);
        if (userWallet == null){
            throw new IllegalArgumentException("提现地址错误");
        }
        Long userId = userWallet.getUserId();
        User user = userService.getById(userId);
        if (user == null){
            throw new IllegalArgumentException("该用户不存在");
        }
        String paypassword = user.getPaypassword();
        if (StringUtils.isEmpty(paypassword) || !(new BCryptPasswordEncoder().matches(payPassword,paypassword))){
            throw new IllegalArgumentException("交易密码错误");
        }
        return super.removeById(addressId);
    }
}
