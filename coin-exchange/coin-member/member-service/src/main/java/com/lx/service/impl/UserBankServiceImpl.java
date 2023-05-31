package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.User;
import com.lx.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.UserBankMapper;
import com.lx.domain.UserBank;
import com.lx.service.UserBankService;
import org.springframework.util.StringUtils;

@Service
public class UserBankServiceImpl extends ServiceImpl<UserBankMapper, UserBank> implements UserBankService{

    @Autowired
    private UserService userService;

    /*
    *  查询用户的银行卡信息
    * */
    @Override
    public Page<UserBank> findByPage(Page<UserBank> page, Long userId) {
        return page(page,new LambdaQueryWrapper<UserBank>().eq(userId != null,UserBank::getUserId,userId));
    }

    /*
    *  通过用户的id 查询用户的银行卡
    * */
    @Override
    public UserBank getCurrentUserBank(Long userId) {
        UserBank userBank = getOne(
                new LambdaQueryWrapper<UserBank>()
                        .eq(UserBank::getUserId, userId)
                        .eq(UserBank::getStatus, 1));
        return userBank;
    }

    /*
     *  给用户绑定银行卡
     * */
    @Override
    public boolean bindBank(Long userId, UserBank userBank) {
        // 支付密码的判断
        String payPassword = userBank.getPayPassword();
        User user = userService.getById(userId);
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        if (!bCryptPasswordEncoder.matches(payPassword,user.getPaypassword())){
            throw new IllegalArgumentException("用户的支付密码错误");
        }
        Long id = userBank.getId();
        // 有Id 代表是修改操作
        if (id!=0){
            UserBank userBankDb = getById(id);
            if (userBankDb==null){
                throw new IllegalArgumentException("用户的银行卡的ID输入错误");
            }
            // 修改值
            return updateById(userBank);
        }
        // 若银行卡的id为null，则需要新建一个
        userBank.setUserId(userId);
        userBank.setStatus((byte) 1);
        return save(userBank);
    }
}
