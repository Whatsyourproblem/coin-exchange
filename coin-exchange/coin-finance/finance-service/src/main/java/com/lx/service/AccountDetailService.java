package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.AccountDetail;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AccountDetailService extends IService<AccountDetail>{


    /*
    *  资金流水的分页查询
    * */
    Page<AccountDetail> findByPage(Page<AccountDetail> page, Long coinId, Long accountId, Long userId, String userName, String mobile, String amountStart, String amountEnd, String startTime, String endTime);
}
