package com.lx.config.rocket;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding(value = {Source.class,Sink.class})
public class RocketMQConfig {
}
