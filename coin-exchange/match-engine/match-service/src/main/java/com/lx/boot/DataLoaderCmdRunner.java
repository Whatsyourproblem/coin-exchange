package com.lx.boot;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.disruptor.DisruptorTemplate;
import com.lx.domain.EntrustOrder;
import com.lx.mapper.EntrustOrderMapper;
import com.lx.model.Order;
import com.lx.util.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;

//@Component
public class DataLoaderCmdRunner implements CommandLineRunner {

    @Autowired
    private EntrustOrderMapper entrustOrderMapper;

    @Autowired
    private DisruptorTemplate disruptorTemplate;

    /*
    *  项目一启动就会执行该方法
    * */
    @Override
    public void run(String... args) throws Exception {
        // 在项目启动后，对委托单的数据进行加载(数据库读取)
        List<EntrustOrder> entrustOrders = entrustOrderMapper.selectList(
                new LambdaQueryWrapper<EntrustOrder>()
                        .eq(EntrustOrder::getStatus, 0)
                        .orderByAsc(EntrustOrder::getCreated)
        );
        if (CollectionUtils.isEmpty(entrustOrders)){
            return;
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 对委托单数据进行操作
        for (EntrustOrder entrustOrder : entrustOrders) {
            disruptorTemplate.onData(BeanUtils.entrustOrder2Order(entrustOrder));
        }
        stopWatch.stop();
        long lastTaskTimeMillis = stopWatch.getLastTaskTimeMillis();
        System.out.println("总条数:" + entrustOrders.size() + ",总共耗时:" + lastTaskTimeMillis + "ms");
    }

}
