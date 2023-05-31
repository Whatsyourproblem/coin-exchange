package com.lx.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lx.match.MatchServiceFactory;
import com.lx.match.MatchStrategy;
import com.lx.model.Order;
import com.lx.model.OrderBooks;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;


/*
*  该对象有多个: 数量与Symbol的数量对应
*   针对某一个OrderEventHandler,只会同一时间有一个线程来执行它
*  ringBuffer绑定的消费者
* */
@Data
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {

    private OrderBooks orderBooks;

    private String symbol ;

    public OrderEventHandler(OrderBooks orderBooks){
        this.orderBooks = orderBooks;
        this.symbol = this.orderBooks.getSymbol();
    }

    /*
    *  接收到ringBuffer里面的消息后,如何处理
    * */
    @Override
    public void onEvent(OrderEvent event, long sequence, boolean endOfBatch) throws Exception {
        // 从ringBuffer 里面接收了某个数据
        Order order = (Order)event.getSource();
        if(!order.getSymbol().equals(symbol)){ // 我们接收到了一个不属于我们处理的数据,我们不处理
            return;
        }
        log.info("开始接收订单事件=========>{}",event);
        // 处理逻辑是啥
        // 对我们应该处理的消息进行处理
        MatchServiceFactory.getMatchService(MatchStrategy.LIMIT_PRICE).match(orderBooks ,order);
        log.info("处理完成我们的订单事件========================>{}",event);
    }


}
