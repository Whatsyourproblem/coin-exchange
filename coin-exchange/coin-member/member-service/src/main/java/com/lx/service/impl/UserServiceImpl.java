package com.lx.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.util.Md5Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.config.IdAutoConfiguration;
import com.lx.domain.Sms;
import com.lx.domain.UserAuthAuditRecord;
import com.lx.domain.UserAuthInfo;
import com.lx.dto.UserDto;
import com.lx.geetest.GeetestLib;
import com.lx.mapper.UserAuthAuditRecordMapper;
import com.lx.mappers.UserDtoMapper;
import com.lx.model.*;
import com.lx.service.SmsService;
import com.lx.service.UserAuthAuditRecordService;
import com.lx.service.UserAuthInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.UserMapper;
import com.lx.domain.User;
import com.lx.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import sun.security.provider.MD5;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private UserAuthAuditRecordService userAuthAuditRecordService;

    @Autowired
    private UserAuthInfoService userAuthInfoService;

    @Autowired
    private GeetestLib geetestLib;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SmsService smsService;

    /*
    *  条件分页查询会员的列表
    * */
    @Override
    public Page<User> findByPage(Page<User> page, String mobile, Long userId, String userName, String realName, Integer status,Integer reviewsStatus) {
        return page(page,
                new LambdaQueryWrapper<User>()
                        .like(!StringUtils.isEmpty(mobile),User::getMobile,mobile)
                        .like(!StringUtils.isEmpty(userName),User::getUsername,userName)
                        .like(!StringUtils.isEmpty(realName),User::getRealName,realName)
                        .eq(userId!=null,User::getId,userId)
                        .eq(status!=null,User::getStatus,status)
                        .eq(reviewsStatus!=null,User::getReviewsStatus,reviewsStatus)

        );
    }



    /**
     * 通过用户的id，查询该用户邀请的人员
     * @param page
     * @param userId
     * @return
     */
    @Override
    public Page<User> findDirectInvitePage(Page<User> page, Long userId) {
        return page(page,new LambdaQueryWrapper<User>().eq(User::getDirectInviteid,userId));
    }

    /*
    *  修改用户的审核状态
    * */
    @Override
    @Transactional
    public void updateUserAuthStatus(Long id, Byte authStatus, Long authCode,String remark) {
        log.info("开始修改用户的审核状态，当前用户{}，用户的审核状态{}，图片的唯一code{}",id,authStatus,authCode);
        User user = getById(id);
        if (user!= null){
            //user.setAuthStatus(authStatus); // 认证的状态
            user.setReviewsStatus(authStatus.intValue()); //审核的状态
            updateById(user); // 修改用户的状态
        }
        UserAuthAuditRecord userAuthAuditRecord = new UserAuthAuditRecord();
        userAuthAuditRecord.setUserId(id);
        userAuthAuditRecord.setStatus(authStatus);
        userAuthAuditRecord.setAuthCode(authCode);

        String usrStr = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

        userAuthAuditRecord.setAuditUserId(Long.valueOf(usrStr));   // 审核人的ID
        userAuthAuditRecord.setAuditUserName("--------------");   // 审核人的名称 -> 远程调用admin-service，没有书屋
        userAuthAuditRecord.setRemark(remark);   // 审核人的名称

        userAuthAuditRecordService.save(userAuthAuditRecord);
    }

    /*
    *  用户的实名认证
    * */
    @Override
    public boolean identifyVerfiy(Long id, UserAuthForm userAuthForm) {
        User user = getById(id);
        Assert.notNull(user,"认证的用户不存在");
        Byte authStatus = user.getAuthStatus();
        if (!authStatus.equals((byte) 0)){
            throw new IllegalArgumentException("该用户已经认证成功了");
        }
        // 执行认证
        checkForm(userAuthForm); // 极验
        // 实名认证
        boolean check = IdAutoConfiguration.check(userAuthForm.getRealName(), userAuthForm.getIdCard());
        if (!check){
            throw new IllegalArgumentException("该用户信息错误,请检查");
        }

        // 设置用户的认证属性
        user.setAuthtime(new Date());
        user.setAuthStatus((byte)1);
        user.setRealName(userAuthForm.getRealName());
        user.setIdCard(userAuthForm.getIdCard());
        user.setIdCardType(userAuthForm.getIdCardType());
        return updateById(user);
    }



    private void checkForm(UserAuthForm userAuthForm) {
        // 同理 这里的UserAuthForm继承了GeetestForm,所以用GeetestForm的check()方法,直接使用即可
        userAuthForm.check(geetestLib,redisTemplate);
    }

    @Override
    public User getById(Serializable id) {
        User user = super.getById(id);
        if (user == null){
            throw new IllegalArgumentException("请输入正确的用户Id");
        }
        // 用户的高级认证状态
        Byte seniorAuthStatus = null;
        // 用户的高级认证未通过,原因
        String seniorAuthDesc = "";
        // 用户被审核的状态 0-待审核 1-通过 2-拒绝
        Integer reviewsStatus = user.getReviewsStatus();
        if (reviewsStatus == null){
            // 为null时,代表用户的资料没有上传
            seniorAuthStatus = 3;
            seniorAuthDesc = "资料未填写";
        }else {
            switch (reviewsStatus){
                case 1:
                    seniorAuthStatus = 1;
                    seniorAuthDesc = "审核通过";
                    break;
                case 2:
                    seniorAuthStatus = 2;
                    // 查询被拒绝的原因--->审核记录里面的
                    // 按照时间排序的审核记录
                    List<UserAuthAuditRecord> userAuthAuditRecordList = userAuthAuditRecordService.getUserAuthAuditRecordList(user.getId());
                    if (!CollectionUtils.isEmpty(userAuthAuditRecordList)){
                        UserAuthAuditRecord userAuthAuditRecord = userAuthAuditRecordList.get(0);
                        seniorAuthDesc = userAuthAuditRecord.getRemark();
                    }
                    //seniorAuthDesc = "原因未知";bug所在
                    break;
                case 0:
                    seniorAuthStatus = 0;
                    seniorAuthDesc = "等待审核";
                    break;
            }
        }
        user.setSeniorAuthStatus(seniorAuthStatus);
        user.setSeniorAuthDesc(seniorAuthDesc);
        return user;
    }

    /*
     *   用户的高级认证
     * */
    @Override
    @Transactional
    public void authUser(Long id, List<String> imgs) {
        if (CollectionUtils.isEmpty(imgs)){
            throw new IllegalArgumentException("用户的身份证信息为null");
        }
        User user = getById(id);
        if (user == null){
            throw new IllegalArgumentException("请输入正确的userId");
        }
        long authCode = snowflake.nextId(); // 使用时间戳(有重复) => 雪花算法
        List<UserAuthInfo> userAuthInfoList = new ArrayList<>(imgs.size());
        for (int i = 0; i < imgs.size(); i++) {
            String s =  imgs.get(i);
            UserAuthInfo userAuthInfo = new UserAuthInfo();
            userAuthInfo.setImageUrl(imgs.get(i));
            userAuthInfo.setUserId(id);
            userAuthInfo.setSerialno(i+1);  // 设置图片序号 1.正面 2.反面 3.手持
            userAuthInfo.setAuthCode(authCode); // 是一组身份信息的标识 3个图片为一组
            userAuthInfoList.add(userAuthInfo);
        }
        userAuthInfoService.saveBatch(userAuthInfoList); // 批量操作


        user.setReviewsStatus(0); // 等待审核
        updateById(user);   //更新用户的状态

    }

    /*
    *  修改用户的手机号
    * */
    @Override
    public boolean updatePhone(Long userId,UpdatePhoneParam updatePhoneParam) {
        // 1.使用userId 查询用户
        User user = getById(userId);
        // 获取旧的手机号码 --> 验证旧手机号码的验证码
        // 2.验证旧手机
        String oldMobile = user.getMobile();
        String oldMobileCode = stringRedisTemplate.opsForValue().get("SMS:VERIFY_OLD_PHONE:" + oldMobile);
        if (!updatePhoneParam.getOldValidateCode().equals(oldMobileCode)){
            throw new IllegalArgumentException("旧手机验证码错误");
        }
        // 3.验证新手机
        String newPhoneCode = stringRedisTemplate.opsForValue().get("SMS:CHANGE_PHONE_VERIFY:" + updatePhoneParam.getNewMobilePhone());
        if (!updatePhoneParam.getValidateCode().equals(newPhoneCode)){
            throw new IllegalArgumentException("新手机验证码错误");
        }
        // 4.修改手机号
        user.setMobile(updatePhoneParam.getNewMobilePhone());

        return updateById(user);
    }

    /*
     *  检验新的手机号是否可用，若可用，则给新的手机号发送一个验证码
     * */
    @Override
    public boolean checkNewPhone(String mobile, String countryCode) {

        // 1.新的手机号，没有旧的用户使用
        int count = count(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile).eq(User::getCountryCode, countryCode));
        if (count > 0){
            // 有用户占用这个手机号
            throw new IllegalArgumentException("改手机号已经被占用");
        }
        // 2.向新的手机发送短信
        Sms sms = new Sms();
        sms.setMobile(mobile);
        sms.setCountryCode(countryCode);
        // 模板代码 --> 校验手机号
        sms.setTemplateCode("CHANGE_PHONE_VERIFY");
        return  smsService.sendSms(sms);
    }

    /*
    *  修改用户的登录密码
    * */
    @Override
    public boolean updateUserLoginPwd(Long userId, UpdateLoginParam updateLoginParam) {

        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的Id错误");
        }
        String oldpassword = updateLoginParam.getOldpassword();
        // 1.校验之前的密码 数据库的密码都是我们加密后的密码
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 密码匹配
        boolean matches = bCryptPasswordEncoder.matches(updateLoginParam.getOldpassword(), user.getPassword());
        if (!matches){
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        // 2.校验手机验证码
        String validateCode = updateLoginParam.getValidateCode();
        String phoneValidateCode =  stringRedisTemplate.opsForValue().get("SMS:CHANGE_LOGIN_PWD_VERIFY:" + user.getMobile());// "SMS:CHANGE_LOGIN_PWD_VERIFY:1523xx"
        if (!validateCode.equals(phoneValidateCode)){
            throw new IllegalArgumentException("手机验证码错误");
        }
        user.setPassword(bCryptPasswordEncoder.encode(updateLoginParam.getNewpassword()));
        return updateById(user);
    }

    /*
    *  修改用户的交易密码
    * */
    @Override
    public boolean updateUserPayPwd(Long userId, UpdateLoginParam updateLoginParam) {
        // 1.查询之前的用户
        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的Id错误");
        }
        String oldpassword = updateLoginParam.getOldpassword();
        // 1.校验之前的密码 数据库的密码都是我们加密后的密码
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 密码匹配
        boolean matches = bCryptPasswordEncoder.matches(updateLoginParam.getOldpassword(), user.getPaypassword());
        if (!matches){
            throw new IllegalArgumentException("用户的原始密码输入错误");
        }
        // 2.校验手机验证码
        String validateCode = updateLoginParam.getValidateCode();
        String phoneValidateCode =  stringRedisTemplate.opsForValue().get("SMS:CHANGE_PAY_PWD_VERIFY:" + user.getMobile());// "SMS:CHANGE_LOGIN_PWD_VERIFY:1523xx"
        if (!validateCode.equals(phoneValidateCode)){
            throw new IllegalArgumentException("手机验证码错误");
        }
        user.setPaypassword(updateLoginParam.getNewpassword());
        return updateById(user);
    }

    /*
    *  重置用户的交易密码
    * */
    @Override
    public boolean unsetPayPassword(Long userId, UnsetPayPasswordParam unsetPayPasswordParam) {
        User user = getById(userId);
        if (user == null){
            throw new IllegalArgumentException("用户的Id错误");
        }
        String validateCode = unsetPayPasswordParam.getValidateCode();
        String phoneValidate = stringRedisTemplate.opsForValue().get("SMS:FORGOT_PAY_PWD_VERIFY:" + user.getMobile());
        if (!validateCode.equals(phoneValidate)){
            throw new IllegalArgumentException("用户的验证码错误");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPaypassword(bCryptPasswordEncoder.encode(unsetPayPasswordParam.getPayPassword()));
        return updateById(user);
    }

    /*
    *  获取该用户邀请的用户列表
    * */
    @Override
    public List<User> getUserInvites(Long userId) {
        List<User> list = list(new LambdaQueryWrapper<User>().eq(User::getDirectInviteid, userId));
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        list.forEach(user -> {
            user.setPaypassword("***********");
            user.setPassword("**************");
            user.setAccessKeyId("**************");
            user.setAccessKeySecret("***************");
        });
        return list;
    }

    /*
     *  通过用户的Id 批量查询用户的基础信息
     * */
/*    @Override
    public List<UserDto> getBasicUsers(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)){
            return Collections.emptyList();
        }
        List<User> list = list(new LambdaQueryWrapper<User>().in(User::getId, ids));
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyList();
        }
        // 经过批量in查询后，进行 entity->Dto的映射 User->UserDto
        List<UserDto> userDtos = UserDtoMapper.INSTANCE.convert2Dto(list);
        return userDtos;
    }*/

    /*
     *  通过用户的Id 批量查询用户的基础信息
     * */
    @Override
    public Map<Long,UserDto> getBasicUsers(List<Long> ids,String userName,String mobile) {
        // 使用ids进行用户的批量查询，用在我们给别人远程调用时批量获取用户的数据
        // 使用用户名、手机号查询一系列用户的集合
        if (CollectionUtils.isEmpty(ids)&&StringUtils.isEmpty(userName)&&StringUtils.isEmpty(mobile)){
            return Collections.emptyMap();
        }
        List<User> list = list(new LambdaQueryWrapper<User>()
                .in(!CollectionUtils.isEmpty(ids),User::getId, ids)
                .like(!StringUtils.isEmpty(userName),User::getUsername,userName)
                .like(!StringUtils.isEmpty(mobile),User::getMobile,mobile))
                ;
        if (CollectionUtils.isEmpty(list)){
            return Collections.emptyMap();
        }
        // 经过批量in查询后，进行 entity->Dto的映射 User->UserDto
        List<UserDto> userDtos = UserDtoMapper.INSTANCE.convert2Dto(list);
        Map<Long, UserDto> userDtoIdMappings = userDtos.stream().collect(Collectors.toMap(UserDto::getId, userDto -> userDto));
        return userDtoIdMappings;
    }


    /*
     *  用户的注册
     * */
    @Override
    public boolean register(RegisterParam registerParam) {
        log.info("用户开始注册{}", JSON.toJSONString(registerParam,true));
        String mobile = registerParam.getMobile();
        String email = registerParam.getEmail();
        // 这里判断 mobile和email是否重复
        // 1.简单校验
        if (StringUtils.isEmpty(email)&&StringUtils.isEmpty(mobile)){
            throw new IllegalArgumentException("手机号或邮箱不能同时为空");
        }
        // 2.查询校验
        int count = count(new LambdaQueryWrapper<User>()
                .eq(!StringUtils.isEmpty(email), User::getEmail, email)
                .eq(!StringUtils.isEmpty(mobile), User::getMobile, mobile)
        );
        if (count > 0){
            throw new IllegalArgumentException("手机号或邮箱已经被注册");
        }

        // 进行极验的校验
        registerParam.check(geetestLib,redisTemplate);
        // 构建一个新的用户
        User user = getUser(registerParam);

        return save(user);
    }

    private User getUser(RegisterParam registerParam) {
        User user = new User();

        user.setEmail(registerParam.getEmail());
        user.setMobile(registerParam.getMobile());

        String encodePwd = new BCryptPasswordEncoder().encode(registerParam.getPassword());
        user.setPassword(encodePwd);
        user.setPaypassSetting(false);
        user.setStatus((byte) 1);
        user.setType((byte) 1);
        user.setAuthStatus((byte) 0);
        user.setLogins(0);
        // 用户的邀请码
        user.setInviteCode(RandomUtil.randomString(6));

        if (!StringUtils.isEmpty(registerParam.getInvitionCode())){
            User userPre = getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, registerParam.getInvitionCode()));
            if (userPre != null){
                // 邀请人的id，需要查询
                user.setDirectInviteid(String.valueOf(userPre.getId()));
                // 邀请关系
                user.setInviteRelation(String.valueOf(userPre.getId()));
            }
        }

        return user;
    }

    /*
     *  重置登录密码
     * */
    @Override
    public boolean unsetLoginPwd(UnSetPasswordParam unSetPasswordParam) {
        log.info("开始重置密码{}",JSON.toJSONString(unSetPasswordParam,true));
        // 1.极验校验
        unSetPasswordParam.check(geetestLib,redisTemplate);
        // 2.手机号码校验
        String phoneValidateCode = stringRedisTemplate.opsForValue().get("SMS:FORGOT_VERIFY:" + unSetPasswordParam.getMobile());
        if (!unSetPasswordParam.getValidateCode().equals(phoneValidateCode)){
            throw new IllegalArgumentException("手机验证码错误");
        }
        // 3.数据库用户的校验
        String mobile = unSetPasswordParam.getMobile();
        User user = getOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user == null){
            throw new IllegalArgumentException("该用户不存在");
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(unSetPasswordParam.getPassword()));
        return updateById(user);
    }

}
