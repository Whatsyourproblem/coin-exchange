package com.lx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.EntrustOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.domain.ExchangeTrade;
import com.lx.param.OrderParam;
import com.lx.vo.TradeEntrustOrderVo;

public interface EntrustOrderService extends IService<EntrustOrder>{


    /*
    *  分页查询委托单
    * */
    Page<EntrustOrder> findByPage(Page<EntrustOrder> page, Long userId, String symbol, Integer type);

    /*
    *  查询历史的委托单记录
    * */
    Page<TradeEntrustOrderVo> getHistoryEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    /*
    *  查询未的委托单记录
    * */
    Page<TradeEntrustOrderVo> getEntrustOrder(Page<EntrustOrder> page, String symbol, Long userId);

    /*
    *  创建一个新的委托单
    * */
    Boolean createEntrustOrder(Long userId, OrderParam orderParam);


    /*
    *  更新委托单数据
    * */
    void doMatch(ExchangeTrade exchangeTrade);


    /*
    *  取消委托单
    * */
    void cancleEntrustOrder(Long orderId);

    /*
    *  数据库里面委托单的取消
    * */
    void cancleEntrustOrderToDb(String orderId);
}
