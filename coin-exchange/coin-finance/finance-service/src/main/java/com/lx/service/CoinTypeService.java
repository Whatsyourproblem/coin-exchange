package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.CoinType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CoinTypeService extends IService<CoinType>{


    /*
    *  条件分页查询货币类型
    * */
    Page<CoinType> findByPage(Page<CoinType> page, String code);

    /*
    *  使用币种的状态查询所有的币种类型值
    * */
    List<CoinType> listByStatus(Byte status);
}
