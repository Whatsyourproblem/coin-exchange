package com.lx.config.rocket;

import com.lx.domain.ExchangeTrade;
import com.lx.service.EntrustOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/*
*  交易数据的监听
* */
@Component
@Slf4j
public class ExchangeTradeListener {

    @Autowired
    private EntrustOrderService entrustOrderService;

    @Transactional
    @StreamListener("exchange_trade_in")
    public void receiveExchangeTrade(List<ExchangeTrade> exchangeTrades){
        if (CollectionUtils.isEmpty(exchangeTrades)){
            return;
        }
        // 更新委托单的账户信息
        for (ExchangeTrade exchangeTrade : exchangeTrades) {
            if (exchangeTrade != null){
                // 交易完成之后，更新数据库
                entrustOrderService.doMatch(exchangeTrade);
            }
        }
    }

    @StreamListener("cancel_order_in")
    public void receiveCancelOrder(String orderId){
        entrustOrderService.cancleEntrustOrderToDb(orderId);
    }
}
