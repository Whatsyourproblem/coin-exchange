package com.lx.rocket;

import com.lx.disruptor.DisruptorTemplate;
import com.lx.domain.EntrustOrder;
import com.lx.model.Order;
import com.lx.util.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageConsumerListener {

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    /*
    *  通过Sink与某一个队列进行绑定,绑定的队列中的消息会被该方法发送到ringBuffer中
    *  然后 供我们的消费者进行消费
    *  RocketMQ中一旦有消息,该方法会监听到,并将消息发送给ringBuffer
     * */
    @StreamListener("order_in")
    public void handleMessage(EntrustOrder entrustOrder){
        Order order = null;
        // 该单需要取消 status = 2 说明为取消订单操作的委托单
        if (entrustOrder.getStatus() == 2){
            order = new Order();
            order.setOrderId(entrustOrder.getId().toString());
            order.setCancelOrder(true);
        }else{
            order = BeanUtils.entrustOrder2Order(entrustOrder);
        }
        log.info("接收到了委托单:{}",order);
        disruptorTemplate.onData(order);
    }
}
