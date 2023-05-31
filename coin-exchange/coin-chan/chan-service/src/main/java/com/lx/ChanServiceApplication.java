package com.lx;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.tio.core.Tio;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.starter.EnableTioWebSocketServer;
import org.tio.websocket.starter.TioWebSocketServerBootstrap;

import java.util.Date;

@SpringBootApplication
// 开启tio的websocket
@EnableTioWebSocketServer
public class ChanServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(ChanServiceApplication.class,args);
    }

}
