package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.dto.UserDto;
import com.lx.model.*;

import java.util.List;
import java.util.Map;

public interface UserService extends IService<User>{


    /**
     *
     * 条件分页查询会员的列表
     * @param page 分页参数
     * @param mobile    手机号
     * @param userId    会员ID
     * @param userName  会员的名称
     * @param realName  会员的真实名称
     * @param status    会员的状态
     * @param reviewsStatus 会员的审核状态
     * @return
     */
    Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status,Integer reviewsStatus);

    /**
     * 通过用户的id，查询该用户邀请的人员
     * @param page
     * @param userId
     * @return
     */
    Page<User> findDirectInvitePage(Page<User> page, Long userId);

    /*
    *  修改用户的审核状态
    *  remark 审核被拒绝的原因
    * */
    void updateUserAuthStatus(Long id, Byte authStatus, Long authCode,String remark);

    /**
     * 用户的实名认证
     * @param id 用户的id
     * @param userAuthForm 认证的表单数据
     * @return
     */
    boolean identifyVerfiy(Long id, UserAuthForm userAuthForm);

    /*
    *  用户的高级认证
    *   id 当前用户的id
    *   imgs 用户的图片地址
    * */
    void authUser(Long id, List<String> imgs);

    /*
    *  修改用户的手机号
    * */
    boolean updatePhone(Long userId,UpdatePhoneParam updatePhoneParam);

    /*
    *  检验新的手机号是否可用，若可用，则给新的手机号发送一个验证码
    * */
    boolean checkNewPhone(String mobile, String countryCode);

    /*
    *  修改用户的登录密码
    * */
    boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam);

    /*
    *  修改用户的交易密码
    * */
    boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam);

    /*
    *  重置用户的交易密码
    * */
    boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam);

    /*
    *  获取该用户邀请的用户列表
    * */
    List<User> getUserInvites(Long userId);

    /*
    *  通过用户的Id 批量查询用户的基础信息
    * */
    //List<UserDto> getBasicUsers(List<Long> ids);
    Map<Long,UserDto> getBasicUsers(List<Long> ids,String userName,String mobile);

    /*
    *  用户的注册
    * */
    boolean register(RegisterParam registerParam);

    /*
    *  重置登录密码
    * */
    boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam);
}
