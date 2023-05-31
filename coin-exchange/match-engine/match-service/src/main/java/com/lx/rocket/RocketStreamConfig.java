package com.lx.rocket;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

/*
*  开启我们的Stream的开发
* */
@Configuration
@EnableBinding({Sink.class,Source.class}) //绑定到Sink(消费者),Source(生产者)
public class RocketStreamConfig {
}
