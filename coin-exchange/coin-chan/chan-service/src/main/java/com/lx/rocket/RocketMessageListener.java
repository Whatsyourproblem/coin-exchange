package com.lx.rocket;

import com.alibaba.fastjson.JSON;
import com.lx.model.MessagePayload;
import com.lx.vo.ResponseEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tio.core.Tio;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.starter.TioWebSocketServerBootstrap;

@Component
@Slf4j
public class RocketMessageListener {

    @Autowired
    private TioWebSocketServerBootstrap bootstrap;

    @StreamListener("tio_group")
    public void handlerMessage(MessagePayload message){
        log.info("接收到rocketmq的消息=========>{}", JSON.toJSONString(message));
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setSubbed(message.getChannel());
        responseEntity.put("result",message.getBody());
        // 推送给某一用户就可以了
        if(StringUtils.hasText(message.getUserId())){

            Tio.sendToUser(bootstrap.getServerTioConfig(),message.getUserId(),responseEntity.build());
            return;
        }
        // 推送给消息组
        @NonNull String group = message.getChannel();
        Tio.sendToGroup(bootstrap.getServerTioConfig(),group,responseEntity.build());
    }
}
