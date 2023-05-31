package com.lx.rocket;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface Sink {

    /**
     * 监听交易记录的输入
     * @return
     */
    @Input("exchange_trades_in")
    MessageChannel exchangeTradesIn() ;

}
