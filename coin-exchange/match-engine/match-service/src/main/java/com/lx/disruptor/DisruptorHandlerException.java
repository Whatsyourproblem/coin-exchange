package com.lx.disruptor;


import com.lmax.disruptor.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/*
* DisruptorHandlerException的异常处理
* */
@Slf4j
public class DisruptorHandlerException implements ExceptionHandler {

    @Override
    public void handleEventException(Throwable throwable, long sequence, Object event) {
        log.info("handleEvent Exception===>{},sequence==>{},event===>{}",throwable.getMessage(),sequence,event);
    }

    /*
    *  开始时处理的异常
    * */
    @Override
    public void handleOnStartException(Throwable throwable) {
        log.info("OnStartHandler Exception===>{}",throwable.getMessage());
    }

    /*
    *  结束时处理的异常
    * */
    @Override
    public void handleOnShutdownException(Throwable throwable) {
        log.info("OnShutdownHandler Exception===>{}",throwable.getMessage());
    }
}
