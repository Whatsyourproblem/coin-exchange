package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Coin;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.dto.CoinDto;

import java.util.List;

public interface CoinService extends IService<Coin>{


    /*
    *  数字货币的条件分页查询
    * */
    Page<Coin> findByPage(String name, String type, Byte status, String title, String walletType, Page<Coin> page);

    /*
    *  使用币种的状态查询所有的币种信息
    * */
    List<Coin> getCoinsByStatus(Byte status);

    /*
    *  使用货币的名称来查询货币
    *  货币的名称是唯一的
    * */
    Coin getCoinByCoinName(String coinName);

    /*
    *  使用coinId的id集合,查询我们的币种
    * */
    List<CoinDto> findList(List<Long> coinIds);
}
