package com.lx.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.RandomUtil;
import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.constant.Constants;
import com.lx.domain.CashRechargeAuditRecord;
import com.lx.domain.Coin;
import com.lx.domain.Config;
import com.lx.dto.AdminBankDto;
import com.lx.dto.UserDto;
import com.lx.feign.AdminBankServiceFeign;
import com.lx.feign.UserServiceFeign;
import com.lx.mapper.CashRechargeAuditRecordMapper;
import com.lx.mapper.CoinMapper;
import com.lx.model.CashParam;
import com.lx.service.AccountService;
import com.lx.service.ConfigService;
import com.lx.vo.CashTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.CashRecharge;
import com.lx.mapper.CashRechargeMapper;
import com.lx.service.CashRechargeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class CashRechargeServiceImpl extends ServiceImpl<CashRechargeMapper, CashRecharge> implements CashRechargeService{

    @Autowired
    private UserServiceFeign userServiceFeign;

    @Autowired
    private CashRechargeAuditRecordMapper auditRecordMapper;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ConfigService configService;

    @Autowired
    private CoinMapper coinMapper;

    @Autowired
    private AdminBankServiceFeign adminBankServiceFeign;

    /*
    *  雪花算法
    * */
    @Autowired
    private Snowflake snowflake;

    /*
    *  创建分布式锁
    * */
    @CreateCache(name = "CASH_RECHARGE_LOCK:", expire = 100, timeUnit = TimeUnit.SECONDS, cacheType = CacheType.BOTH)
    private Cache<String, String> lock;

    /*
     *  分页条件查询充值记录 -- 最多进行一次远程调用
     * */
    @Override
    public Page<CashRecharge>  findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime) {
        LambdaQueryWrapper<CashRecharge> cashRechargeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 1.若用户本次的查询中,带了用户的信息userId,userName,mobile ---> 本质就是要把用户的id放在我们的查询条件里面
        Map<Long, UserDto> basicUsers = null;
        if (userId!=null || !StringUtils.isEmpty(userName) || !StringUtils.isEmpty(mobile)){
            // 使用用户的信息查询
            // 需要远程调用查询用户的信息
            // 这远程调用接口不仅仅通过ids进行批量查询，还通过userName和mobile进行查询UserDto的值
            basicUsers = userServiceFeign.getBasicUsers(userId == null ? null : Arrays.asList(userId), userName, mobile);
            if (CollectionUtils.isEmpty(basicUsers)){
                // 找不到这样的用户
                return page;
            }
            Set<Long> userIds = basicUsers.keySet();
            cashRechargeLambdaQueryWrapper.in(!CollectionUtils.isEmpty(userIds), CashRecharge::getUserId,userIds);
        }
        // 2.若用户本次的查询中，没有带用户的信息
        cashRechargeLambdaQueryWrapper.eq(coinId!=null,CashRecharge::getCoinId,coinId)
                                        .eq(status!=null,CashRecharge::getStatus,status)
                                        .between(
                                                !(StringUtils.isEmpty(numMin)||StringUtils.isEmpty(numMax)),
                                                CashRecharge::getNum,
                                                new BigDecimal(StringUtils.isEmpty(numMin) ? "0":numMin),
                                                new BigDecimal(StringUtils.isEmpty(numMax) ? "0":numMax)
                                        )
                                        .between(
                                                !(StringUtils.isEmpty(startTime)||StringUtils.isEmpty(endTime)),
                                                CashRecharge::getCreated,
                                                startTime,
                                                endTime + " 23:59:59"
                                        );
        Page<CashRecharge> cashRechargePage = page(page, cashRechargeLambdaQueryWrapper);
        List<CashRecharge> records = cashRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            List<Long> userIds = records.stream().map(CashRecharge::getUserId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(basicUsers)){
                basicUsers = userServiceFeign.getBasicUsers(userIds,null,null);
            }
            Map<Long, UserDto> finalBasicUsers = basicUsers;
            records.forEach(cashRecharge -> {
                // 需要远程调用查询用户的信息
                UserDto userDto = finalBasicUsers.get(cashRecharge.getUserId());
                if (userDto != null){
                    cashRecharge.setUsername(userDto.getUsername());
                    cashRecharge.setRealName(userDto.getRealName());
                }
            });
        }
        return cashRechargePage;
    }

    /*
     *  充值审核
     * */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public boolean cashRechargeAudit(Long valueOf, CashRechargeAuditRecord auditRecord) {
        // 同一时刻，只允许一名员工审核,加锁
        // CASH_RECHARGE_LOCK:1231123
        boolean tryLockAndRun = lock.tryLockAndRun(auditRecord.getId() + "", 300, TimeUnit.SECONDS, () -> {
            // 判断该记录是否被审核
            Long rechargeId = auditRecord.getId();
            CashRecharge cashRecharge = getById(rechargeId);
            if (cashRecharge == null) {
                throw new IllegalArgumentException("现金充值记录不存在");
            }
            Byte status = cashRecharge.getStatus();
            if (status == 1) {
                throw new IllegalArgumentException("充值记录审核已经通过");
            }
            // 没有审核 开始审核
            CashRechargeAuditRecord auditRecordDb = new CashRechargeAuditRecord();
            Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            auditRecordDb.setAuditUserId(userId);
            auditRecordDb.setStatus(auditRecord.getStatus());
            auditRecordDb.setRemark(auditRecord.getRemark());
            byte step = (byte) (cashRecharge.getStep() + 1);
            auditRecordDb.setStep(step);

            // 审核成功 保存审核记录
            int insert = auditRecordMapper.insert(auditRecordDb);
            if (insert == 0) {
                throw new IllegalArgumentException("审核记录保存失败");
            }
            // 这里分为通过审核和没有通过审核
            // 管理员通过审核
            cashRecharge.setStatus(auditRecord.getStatus());
            cashRecharge.setAuditRemark(auditRecord.getRemark());
            cashRecharge.setStep(step);
            // 审核未通过
            if (auditRecord.getStatus() == 2) {
                boolean update = updateById(cashRecharge);
                if (!update) {
                    throw new IllegalArgumentException("更新充值记录失败");
                }
            } else {
                // 审核通过，用户账户加钱
                Boolean isOk = accountService.transferAccountAmount(userId,cashRecharge.getUserId(),
                        cashRecharge.getCoinId(), cashRecharge.getNum(), cashRecharge.getFee(),
                        cashRecharge.getId(), "充值", "recharge_into", (byte) 1);
                /*Boolean isOk = accountService.transferAccountAmount(userId,cashRecharge.getUserId(),
                        cashRecharge.getCoinId(), cashRecharge.getNum(), cashRecharge.getFee(),
                        cashRecharge.getId());*/
                if (isOk){
                    cashRecharge.setLastTime(new Date());// 设置完成时间
                    boolean update = updateById(cashRecharge);
                    if (!update) {
                        throw new IllegalArgumentException("更新充值记录失败");
                    }
                }

            }
        });
        return tryLockAndRun;
    }

    /*
     *  查询当前用户的充值记录
     * */
    @Override
    public Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long userId, Byte status) {
        page.addOrder(OrderItem.desc("last_update_time"));
        return page(page, new LambdaQueryWrapper<CashRecharge>()
                .eq(CashRecharge::getUserId, userId)
                .eq(status != null, CashRecharge::getStatus, status)
        );
    }

    /*
     *  GCN的充值(买入)操作
     * */
    @Override
    public CashTradeVo buy(Long userId, CashParam cashParam) {

        // 1.校验现金参数
        checkCashParam(cashParam);
        // 2.查询我们公司的银行卡
        // 仅仅需要一张银行卡
        List<AdminBankDto> adminBanks = adminBankServiceFeign.getAllAdminBanks();
        AdminBankDto adminBankDto = loadBalancer(adminBanks);
        // 3.生成订单号、参考号
        String orderNo = String.valueOf(snowflake.nextId());
        String remark = RandomUtil.randomNumbers(6);
        // 4.在数据库里面插入一条充值记录
        CashRecharge cashRecharge = new CashRecharge();
        cashRecharge.setUserId(userId);

        // 设置银行卡信息
        cashRecharge.setName(adminBankDto.getName());
        cashRecharge.setBankCard(adminBankDto.getBankCard());
        cashRecharge.setBankName(adminBankDto.getBankName());

        // 设置币种信息
        cashRecharge.setTradeno(orderNo);
        cashRecharge.setCoinId(cashParam.getCoinId());
        // 查询币种来获取币种名称
        Coin coin = coinMapper.selectById(cashParam.getCoinId());
        if (coin == null) {
            throw new IllegalArgumentException("币种不存在");
        }
        // cashParam.getMum() 这是前端给我们的金额,前端可能因为浏览器缓存导致价格不准确,因此,我们需要在后端进行计算
        Config buyGCNRate = configService.getConfigByCode(Constants.BUY_GCN_RATE);
        BigDecimal realNum = cashParam.getMum().multiply(new BigDecimal(buyGCNRate.getValue())).setScale(2, RoundingMode.HALF_UP);
        cashRecharge.setCoinName(coin.getName());

        cashRecharge.setNum(cashParam.getNum());
        cashRecharge.setMum(realNum);
        cashRecharge.setFee(BigDecimal.ZERO);

        // 其他信息
        cashRecharge.setType("linepay");
        cashRecharge.setStatus((byte) 0);// 待审核
        cashRecharge.setStep((byte) 1); // 还未被审核
        cashRecharge.setRemark(remark);
        // 更新数据库
        boolean save = save(cashRecharge);
        if (save){
            // 5.返回结果
            CashTradeVo cashTradeVo = new CashTradeVo();
            cashTradeVo.setName(adminBankDto.getName());
            cashTradeVo.setBankName(adminBankDto.getBankName());
            cashTradeVo.setBankCard(adminBankDto.getBankCard());
            cashTradeVo.setRemark(remark);
            cashTradeVo.setAmount(realNum);
            cashTradeVo.setStatus((byte) 0);

            return cashTradeVo;
        }
        return null;
    }

    /**
     * 银行卡中随机选择一个银行卡
     * @param adminBanks    银行卡集合
     * @return              随机银行卡
     */
    private AdminBankDto loadBalancer(List<AdminBankDto> adminBanks) {
        if (CollectionUtils.isEmpty(adminBanks)) {
            throw new IllegalArgumentException("没有可用银行卡");
        }
        int size = adminBanks.size();
        if (size == 1){
            return adminBanks.get(0);
        }
        //Random random = new Random();
        return adminBanks.get(RandomUtil.randomInt(size));
    }

    /**
     * 校验充值参数
     * @param cashParam 充值参数
     */
    private void checkCashParam(CashParam cashParam) {
        // 校验最小充值数量
        BigDecimal num = cashParam.getNum(); // 现金充值数量
        Config withDrawConfig = configService.getConfigByCode(Constants.RECHARGE_MIN_AMOUNT);
        BigDecimal minRecharge = new BigDecimal(withDrawConfig.getValue());
        // 现金充值数量需要大于 100

        if (num.compareTo(minRecharge) < 0) {
            throw new IllegalArgumentException("不能小于最小充值数量:" + minRecharge);
        }

    }
}
