package com.lx.rocket;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * 使用Source 发送数据
 */
public interface Source {

    /**
     *  往前端订阅的group_message_out的topic里面发送数据
     * */
    @Output("group_message_out")
    MessageChannel subscribeGroupOutput() ;
}
