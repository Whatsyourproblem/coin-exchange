package com.lx.service;

import com.lx.domain.Account;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.vo.SymbolAssetVo;
import com.lx.vo.UserTotalAccountVo;

import java.math.BigDecimal;

public interface AccountService extends IService<Account>{

    /*
     *  用户资金的划转
     * */
    //Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum, String remark, String recharge_into, byte b);


    /*
    *  用户资金的划转
    * */
    Boolean transferAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum,String remark,String businessType,Byte direction);

    /*
    *  给用户扣钱
    * */
    Boolean decreaseAccountAmount(Long adminId, Long userId, Long coinId, BigDecimal num, BigDecimal fee, Long orderNum, String remark, String businessType, byte direction);

    /*
    *  查询某个用户的货币资产
    * */
    Account findByUserAndCoin(Long userId, String coinName);

    /*
    *  暂时锁定用户的资产
    * */
    void lockUserAmount(Long userId, Long coinId, BigDecimal mum, String type, Long orderId, BigDecimal fee);

    /*
    *  计算用户的总的资产
    * */
    UserTotalAccountVo getUserTotalAccount(Long userId);

    /*
    *  交易货币的资产
    * */
    SymbolAssetVo getSymbolAssert(String symbol, Long userId);

    /*
    *  划转买入的账户余额
    * */
    void transferBuyAmount(Long fromUserId, Long toUserId, Long coinId, BigDecimal amount, String businessType, Long orderId);

    /*
    *  划转出售的成功的账户余额
    * */
    void transferSellAmount(Long fromUserId, Long toUserId, Long coinId, BigDecimal amount, String businessType, Long orderId);
}
