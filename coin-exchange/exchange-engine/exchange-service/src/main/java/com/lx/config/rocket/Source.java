package com.lx.config.rocket;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import sun.plugin2.message.Message;

public interface Source {

    @Output("order_out")
    MessageChannel outputMessage();

}
