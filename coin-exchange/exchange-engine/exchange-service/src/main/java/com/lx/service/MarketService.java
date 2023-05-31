package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Market;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.dto.MarketDto;

import java.util.List;

public interface MarketService extends IService<Market>{


    /*
    *  分页查询市场的配置
    * */
    Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status);

    /*
    *  使用交易区域的id查询该区域下的市场
    * */
    List<Market> getMarketsByTradeAreaId(Long id);

    /*
    *  使用交易对，查询市场
    * */
    Market getMarketBySymbol(String symbol);

    /*
    *  使用报价货币和基础货币查询市场
    * */
    MarketDto findByCoinId(Long buyCoinId, Long sellCoinId);

    /**
     * 查询所有的市场数据
     * @return
     */
    List<MarketDto> queryAllMarkets();

    /**
     * 使用交易区域查询市场
     * @param id
     * @return
     */
    List<Market> queryByAreaId(Long id);
}
