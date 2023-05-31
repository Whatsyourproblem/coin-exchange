package com.lx.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.constant.Constants;
import com.lx.domain.*;
import com.lx.dto.UserBankDto;
import com.lx.dto.UserDto;
import com.lx.feign.UserBankServiceFeign;
import com.lx.feign.UserServiceFeign;
import com.lx.mapper.CashWithdrawAuditRecordMapper;
import com.lx.model.CashSellParam;
import com.lx.service.AccountService;
import com.lx.service.CashWithdrawAuditRecordService;
import com.lx.service.ConfigService;
import io.netty.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.CashWithdrawalsMapper;
import com.lx.service.CashWithdrawalsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CashWithdrawalsServiceImpl extends ServiceImpl<CashWithdrawalsMapper, CashWithdrawals> implements CashWithdrawalsService{

    @Autowired
    private UserServiceFeign userServiceFeign;

    @Autowired
    private ConfigService configService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private AccountService accountService;

    @CreateCache(name = "CASH_WITHDRAWALS_LOCK:",expire = 100,timeUnit = TimeUnit.SECONDS,cacheType = CacheType.BOTH)
    private Cache<String,String> lock;

    @Autowired
    private CashWithdrawAuditRecordMapper cashWithdrawAuditRecordMapper;

    @Autowired
    private UserBankServiceFeign userBankServiceFeign;

    /*
     *  提现记录的查询
     * */
    @Override
    public Page<CashWithdrawals> findByPage(Page<CashWithdrawals> page, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {

        LambdaQueryWrapper<CashWithdrawals> cashWithdrawalsLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 有用户的信息时
        Map<Long, UserDto> basicUsers = null;
        if (userId!=null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)){
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)){
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            cashWithdrawalsLambdaQueryWrapper.in(CashWithdrawals::getUserId,userIds);
        }
        // 其他的查询信息
        cashWithdrawalsLambdaQueryWrapper.eq(status!=null,CashWithdrawals::getStatus,status)
                                        .between(
                                                !(StringUtils.isEmpty(numMin) || StringUtils.isEmpty(numMax)),
                                                CashWithdrawals::getNum,
                                                new BigDecimal(StringUtils.isEmpty(numMin)?"0":numMin),
                                                new BigDecimal(StringUtils.isEmpty(numMax)?"0":numMax)
                                        )
                                        .between(
                                                !(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)),
                                                CashWithdrawals::getCreated,
                                                startTime, endTime + " 23:59:59"
                                        );
        Page<CashWithdrawals> pageData = page(page, cashWithdrawalsLambdaQueryWrapper);
        List<CashWithdrawals> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            List<Long> userIds = records.stream().map(CashWithdrawals::getUserId).collect(Collectors.toList());
            if (basicUsers == null){
                basicUsers = userServiceFeign.getBasicUsers(userIds,null,null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashWithdrawals -> {
                UserDto userDto = finalBasicUsers.get(cashWithdrawals.getUserId());
                if (userDto!=null){
                    cashWithdrawals.setUsername(userDto.getUsername());
                    cashWithdrawals.setRealName(userDto.getRealName());
                }
            });
        }
        return pageData;
    }

    /*
     *  审核提现记录
     * */
    @Override
    public boolean updateWithdrawalsStatus(Long userId, CashWithdrawAuditRecord cashWithdrawAuditRecord) {
        // 涉及金额类型的管理 必须加锁
        // 1.使用锁锁住
        boolean isOk = lock.tryLockAndRun(cashWithdrawAuditRecord.getId() + "",300,TimeUnit.SECONDS,()->{
            CashWithdrawals cashWithdrawals = getById(cashWithdrawAuditRecord.getId());
            if (cashWithdrawals == null){
                throw new IllegalArgumentException("现金的审核记录不存在");
            }
            // 2.添加一个审核的记录
            CashWithdrawAuditRecord cashWithdrawAuditRecordNew = new CashWithdrawAuditRecord();
            cashWithdrawAuditRecordNew.setAuditUserId(userId);
            cashWithdrawAuditRecordNew.setRemark(cashWithdrawAuditRecord.getRemark());
            cashWithdrawAuditRecordNew.setCreated(new Date());
            cashWithdrawAuditRecordNew.setStatus(cashWithdrawAuditRecord.getStatus());
            Integer step = cashWithdrawals.getStep() + 1;
            cashWithdrawAuditRecordNew.setStep(step.byteValue());
            cashWithdrawAuditRecordNew.setOrderId(cashWithdrawals.getId());


            cashWithdrawals.setStatus(cashWithdrawAuditRecord.getStatus());
            cashWithdrawals.setRemark(cashWithdrawAuditRecord.getRemark());
            cashWithdrawals.setLastTime(new Date());
            cashWithdrawals.setAccountId(userId); //审核的用户的id
            cashWithdrawals.setStep(step.byteValue());

            // 插入记录
            int count = cashWithdrawAuditRecordMapper.insert(cashWithdrawAuditRecordNew);
            if (cashWithdrawAuditRecord.getStatus() == 2){
                boolean update = updateById(cashWithdrawals);
                if (!update) {
                    throw new IllegalArgumentException("更新提现记录失败");
                }
            }else{// 审核通过
                    Boolean isPass = accountService.decreaseAccountAmount(userId,
                            cashWithdrawals.getUserId(),cashWithdrawals.getCoinId(),cashWithdrawals.getNum(),
                            cashWithdrawals.getFee(),cashWithdrawals.getId(),cashWithdrawals.getRemark(),"withdrawals_out",(byte)2);
                    if (isPass){
                        cashWithdrawals.setLastTime(new Date());
                        boolean update = updateById(cashWithdrawals);
                        if (!update){
                            throw new IllegalArgumentException("更新提现记录失败");
                        }
                    }
            }
        });
        return isOk;
    }

    /*
     *  查询用户的提现记录
     * */
    @Override
    public Page<CashWithdrawals> findUserCashWithdrawals(Page<CashWithdrawals> page, Long userId, Byte status) {
        return page(page,new LambdaQueryWrapper<CashWithdrawals>()
                .eq(CashWithdrawals::getUserId,userId)
                .eq(status != null,CashWithdrawals::getStatus,status));
    }

    /*
     *  GCN的卖出操作
     * */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean sell(Long userId, CashSellParam cashSellParam) {
        // 1 校验参数
        checkCashSellParam(cashSellParam);
        Map<Long, UserDto> basicUsers = userServiceFeign.getBasicUsers(Arrays.asList(userId), null, null);
        if (CollectionUtils.isEmpty(basicUsers)){
            throw new IllegalArgumentException("用户的id错误");
        }
        UserDto userDto = basicUsers.get(userId);
        // 1.2 手机验证码
        //validatePhoneCode(userDto.getMobile(),cashSellParam.getValidateCode());
        // 1.3 支付密码
        checkUserPayPassword(userDto.getPaypassword(),cashSellParam.getPayPassword());
        // 2 订单创建
        // 远程调用用户的银行卡信息
        UserBankDto bankDto = userBankServiceFeign.getUserBankInfo(userId);
        if (bankDto == null){
            throw new IllegalArgumentException("该用户暂未绑定银行卡信息");
        }
        CashWithdrawals cashWithdrawals = new CashWithdrawals();
        // 用户信息
        cashWithdrawals.setUserId(userId);
        // 查询用户的账号并设置
        Account account = accountService.findByUserAndCoin(userId, "GCN");
        cashWithdrawals.setAccountId(account.getId());
        cashWithdrawals.setStatus((byte) 0);
        cashWithdrawals.setStep((byte) 1);



        // 金额信息
        // 获取本次交易的金额
        BigDecimal amount = getCashWithdrawalsAmount(cashSellParam.getNum());
        // 获取本次的手续费
        BigDecimal fee = getCashWithdrawalsFee(amount);
        // 设置本次的实际金额  交易金额 - 手续费
        cashWithdrawals.setMum(amount.subtract(fee));
        cashWithdrawals.setFee(fee);
        cashWithdrawals.setNum(cashSellParam.getNum());

        //银行卡信息
        cashWithdrawals.setBank(bankDto.getBank());
        cashWithdrawals.setBankAddr(bankDto.getBankAddr());
        cashWithdrawals.setBankCard(bankDto.getBankCard());
        cashWithdrawals.setBankProv(bankDto.getBankProv());
        cashWithdrawals.setBankCity(bankDto.getBankCity());
        cashWithdrawals.setRealName(bankDto.getRealName());
        cashWithdrawals.setRemark(RandomUtil.randomNumbers(6));
        cashWithdrawals.setCoinId(cashSellParam.getCoinId());
        cashWithdrawals.setTruename(userDto.getRealName());


        boolean save = save(cashWithdrawals);
        if (save){
            //扣减总资产 --account-->accountDetail
            accountService.lockUserAmount(userId,cashWithdrawals.getCoinId(),
                    cashWithdrawals.getMum(),"withdrawals_out",cashWithdrawals.getId(),cashWithdrawals.getFee());
            return true;
        }
        return false;
    }

    /*
    *  计算本次的手续费
    * */
    private BigDecimal getCashWithdrawalsFee(BigDecimal amount) {
        // 1.通过总金额*费率 = 手续费
        // 2.若金额较小-->最小的提现的手续费
        // 最小的提现费用
        Config withdrawMinPoundage = configService.getConfigByCode(Constants.WITHDRAW_MIN_POUNDAGE);
        BigDecimal withdrawMinPoundageFee = new BigDecimal(withdrawMinPoundage.getValue());

        // 提现费率
        Config withdrawPoundageRate = configService.getConfigByCode(Constants.WITHDRAW_POUNDAGE_RATE);

        // 通过费率算出的手续费
        BigDecimal poundageFee = amount.multiply(new BigDecimal(withdrawPoundageRate.getValue()).setScale(2,RoundingMode.HALF_UP));

        // 比较两者谁大 越大越好
        return poundageFee.min(withdrawMinPoundageFee).equals(poundageFee) ? withdrawMinPoundageFee : poundageFee;
    }

    /*
    *  通过数量计算金额
    * */
    private BigDecimal getCashWithdrawalsAmount(BigDecimal num) {
        // 金额信息
        Config rateConfig = configService.getConfigByCode(Constants.SELL_GCN_RATE);
        return num.multiply(new BigDecimal(rateConfig.getValue()).setScale(2,RoundingMode.HALF_UP));
    }

    /*
    *  支付密码的校验
    * */
    private void checkUserPayPassword(String payDBPassword, String payPassword) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(payPassword, payDBPassword);
        if (!matches){
            throw new IllegalArgumentException("支付密码错误");
        }
    }

    /*
    *   手机验证码校验
    * */
    private void validatePhoneCode(String mobile, String validateCode) {

        // 验证  SMS:CASH_WITHDRAWS:mobile
        String code = redisTemplate.opsForValue().get("SMS:CASH_WITHDRAWS:" + mobile);
        if (!validateCode.equals(code)){
            throw new IllegalArgumentException("验证码错误");
        }
    }

    /*
    *
    *  参数校验
    *
    * */
    private void checkCashSellParam(CashSellParam cashSellParam) {
        // 1.提现状态
        Config cashWithdrawalsStatus = configService.getConfigByCode(Constants.WITHDRAW_STATUS);
        if (Integer.valueOf(cashWithdrawalsStatus.getValue())!=1){
            // 无法提现
            throw new IllegalArgumentException("提现暂未开启");
        }
        // 2.提现金额
        // 2.1 最小的提现额度
        @NotNull BigDecimal cashSellParamNum = cashSellParam.getNum();
        Config cashWithdrawalsConfigMin = configService.getConfigByCode(Constants.WITHDRAW_MIN_AMOUNT);
        if (cashSellParamNum.compareTo(new BigDecimal(cashWithdrawalsConfigMin.getValue())) < 0){
            throw new IllegalArgumentException("检查提现的金额");
        }
        // 2.2 最大的提现额度
        Config cashWithdrawalsConfigMax = configService.getConfigByCode(Constants.WITHDRAW_MAX_AMOUNT);
        if (cashSellParamNum.compareTo(new BigDecimal(cashWithdrawalsConfigMax.getValue())) >= 0){
            throw new IllegalArgumentException("检查提现的金额");
        }

    }


}
