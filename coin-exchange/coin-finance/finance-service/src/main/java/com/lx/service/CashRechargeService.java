package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CashRecharge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.CashRechargeAuditRecord;
import com.lx.model.CashParam;
import com.lx.vo.CashTradeVo;

public interface CashRechargeService extends IService<CashRecharge>{


    /*
    *  分页条件查询充值记录
    * */
    Page<CashRecharge> findByPage(Page<CashRecharge> page, Long coinId, Long userId, String userName, String mobile, Byte status, String numMin, String numMax, String startTime, String endTime);


    /*
    *  现金的充值审核
    * */
    boolean cashRechargeAudit(Long userId, CashRechargeAuditRecord auditRecord);


    /*
    *  查询当前用户的充值记录
    * */
    Page<CashRecharge> findUserCashRecharge(Page<CashRecharge> page, Long userId, Byte status);

    /*
    *  GCN的充值(购买)操作
    * */
    CashTradeVo buy(Long valueOf, CashParam cashParam);
}
