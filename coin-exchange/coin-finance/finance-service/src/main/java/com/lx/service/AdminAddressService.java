package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.AdminAddress;
import com.baomidou.mybatisplus.extension.service.IService;
public interface AdminAddressService extends IService<AdminAddress>{


    /*
    *  条件分页查询归集地址
    * */
    Page<AdminAddress> findByPage(Page<AdminAddress> page, Long coinId);
}
