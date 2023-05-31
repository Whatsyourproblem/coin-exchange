package com.lx.rocket;


import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

/*
*  接收RocketMQ里面的订单消息
* */
public interface Sink {

    /*
    *  输入通道,接收的消息将通过该通道进入应用程序
    *  监听某一个RocketMQ队列,如果该RocketMQ中有消息,信息会被监听
    *  消费者
    * */
    @Input("order_in")
    public MessageChannel messageChannel();
}
