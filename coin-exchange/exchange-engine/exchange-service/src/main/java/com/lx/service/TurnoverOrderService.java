package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.TurnoverOrder;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface TurnoverOrderService extends IService<TurnoverOrder>{


    /*
    *  查询分页数据
    * */
    Page<TurnoverOrder> findByPage(Page<TurnoverOrder> page, Long userId, String symbol, Integer type);

    /*
    *  查询成交记录
    * */
    List<TurnoverOrder> findBySymbol(String symbol);

    /**
     * 获取买入的订单的成功的记录
     */
    List<TurnoverOrder> getBuyTurnoverOrder(Long orderId, Long userId);

    /**
     * 获取卖出订单的成交记录
     */
    List<TurnoverOrder> getSellTurnoverOrder(Long orderId, Long userId);
}
