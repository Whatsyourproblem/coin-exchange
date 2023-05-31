package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.AdminBank;
import com.lx.dto.AdminBankDto;

import java.util.List;

public interface AdminBankService extends IService<AdminBank>{


    /**
     * 条件查询公司银行卡
     * @param page 分页参数
     * @param bankCard  公司的银行卡
     **/
    Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard);

    /*
    *  查询所有的银行卡的信息
    * */
    List<AdminBankDto> getAllAdminBanks();
}
