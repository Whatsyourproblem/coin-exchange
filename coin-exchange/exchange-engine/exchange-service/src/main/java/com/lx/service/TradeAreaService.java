package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.TradeArea;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.dto.TradeAreaDto;
import com.lx.vo.TradeAreaMarketVo;

import java.util.List;

public interface TradeAreaService extends IService<TradeArea>{


    /*
    *  分页查询交易区域
    * */
    Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status);

    /*
    *  使用交易区域的状态查询列表
    * */
    List<TradeArea> findAll(Byte status);

    /*
    * 查询交易区域，以及区域下面的市场
    * */
    List<TradeAreaMarketVo> findTradeAreaMarket();

    /*
    *  查询用户收藏的交易市场
    * */
    List<TradeAreaMarketVo> getUserFavoriteMarkets(Long userId);

    /**
     * 查询所有的交易区域和交易区域下的市场
     * @return
     */
    List<TradeAreaDto> findAllTradeAreaAndMarket();
}
