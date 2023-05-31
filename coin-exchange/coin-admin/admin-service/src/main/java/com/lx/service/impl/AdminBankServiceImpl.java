package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.AdminBank;
import com.lx.dto.AdminBankDto;
import com.lx.mappers.AdminBankDtoMappers;
import com.lx.service.AdminBankService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.AdminBankMapper;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
public class AdminBankServiceImpl extends ServiceImpl<AdminBankMapper, AdminBank> implements AdminBankService {

    /**
     * <h2>条件查询公司银行卡</h2>
     * @param page 分页参数
     * @param bankCard  公司的银行卡
     **/
    @Override
    public Page<AdminBank> findByPage(Page<AdminBank> page, String bankCard) {
        return page(page, new LambdaQueryWrapper<AdminBank>()
                .like(!StringUtils.isEmpty(bankCard), AdminBank::getBankCard, bankCard)
        );
    }

    /*
     *  查询所有的银行卡的信息
     * */
    @Override
    public List<AdminBankDto> getAllAdminBanks() {
        List<AdminBank> adminBanks = list(new LambdaQueryWrapper<AdminBank>().eq(AdminBank::getStatus, 1));
        if (CollectionUtils.isEmpty(adminBanks)){
            return Collections.emptyList();
        }
        // adminBank -> adminBankDto 实体转换
        List<AdminBankDto> adminBankDtos = AdminBankDtoMappers.INSTANCE.toConvertDto(adminBanks);
        return adminBankDtos;
    }
}
