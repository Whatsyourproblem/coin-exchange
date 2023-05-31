package com.lx.controller;

import com.lmax.disruptor.EventHandler;
import com.lx.disruptor.OrderEvent;
import com.lx.disruptor.OrderEventHandler;
import com.lx.domain.DepthItemVo;
import com.lx.enums.OrderDirection;
import com.lx.feign.OrderBooksFeignClient;
import com.lx.model.MergeOrder;
import com.lx.model.OrderBooks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class MatchController implements OrderBooksFeignClient {

    @Autowired
    private EventHandler<OrderEvent>[] eventHandlers;

    @GetMapping("/match/order")
    public TreeMap<BigDecimal, MergeOrder> getTradeData(@RequestParam(required = true) String symbol,@RequestParam(required = true) Integer orderDirection){
        for (EventHandler<OrderEvent> eventHandler : eventHandlers) {
            OrderEventHandler orderEventHandler = (OrderEventHandler) eventHandler;
            if (orderEventHandler.getSymbol().equals(symbol)) {
                OrderBooks orderBooks = orderEventHandler.getOrderBooks();
                return orderBooks.getCurrentLimitPrices(OrderDirection.getOrderDirection(orderDirection));
            }
        }
        return null;
    }

    /*
    *  远程调用提供者
    * */
    @Override
    public Map<String, List<DepthItemVo>> querySymbolDepth(String symbol) {
        for (EventHandler<OrderEvent> eventHandler : eventHandlers) {
            OrderEventHandler orderEventHandler = (OrderEventHandler) eventHandler;
            if(orderEventHandler.getSymbol().equals(symbol)){
                HashMap<String, List<DepthItemVo>> deptMap = new HashMap<>();
                deptMap.put("asks",orderEventHandler.getOrderBooks().getSellTradePlate().getItems());
                deptMap.put("bids",orderEventHandler.getOrderBooks().getBuyTradePlate().getItems());
                return deptMap;
            }
        }
        return null;
    }
}
