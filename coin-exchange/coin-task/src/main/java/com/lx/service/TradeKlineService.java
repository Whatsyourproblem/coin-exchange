package com.lx.service;


import com.lx.constant.Constants;
import com.lx.domain.Line;
import com.lx.dto.CreateKLineDto;
import com.lx.enums.KlineType;
import com.lx.util.KlineTimeUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 *  K线的生成
 * */
@Component
public class TradeKlineService implements CommandLineRunner {

    /**
     *  当我们的交易完成(撮合)之后,就会触发k 线的生成
     *  使用阻塞队列
     * */
    public static BlockingQueue<CreateKLineDto> queue = new LinkedBlockingDeque<>();

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     *  该方法在boot启动时,就会执行
     * */
    @Override
    public void run(String... args) throws Exception {

        /**
         *  从里面获取一个数据,若没有数据,则会阻塞
         * */
        while (true) {
            CreateKLineDto createKLineDto = queue.poll();
            if (createKLineDto != null) {
                for (KlineType klineType : KlineType.values()) {
                    // 把所有类型的K线都生成一遍
                    this.generateKLine(createKLineDto, klineType);
                }
            }
        }

    }

    /**
     * 为当前的交易数据生成K 线
     *
     * @param klineData
     * @param klineType
     */
    private void generateKLine(CreateKLineDto klineData, KlineType klineType) {
        // 1 获取之前该K 线的数据
        String redisKey = new StringBuilder(Constants.REDIS_KEY_TRADE_KLINE)
                .append(klineData.getSymbol().toLowerCase())
                .append(":")
                .append(klineType.getValue().toLowerCase()).toString();
        Long size = redisTemplate.opsForList().size(redisKey);
        DateTime dateTime = KlineTimeUtil.getKLineTime(klineType);
        // 2 之前没有该K线的数据,没有K线数据就进行初始化
        if (size == 0) {
            Line line = new Line(dateTime, klineData.getPrice(), klineData.getVolume());
            // 放在Redis 里面
            redisTemplate.opsForList().leftPush(redisKey, line.toKline());
            return;
        }

        // 3 之前有数据 ,获取最近的一个数据
        String historyKlineData = redisTemplate.opsForList().range(redisKey, 0, 1).get(0);
        Line historyKline = new Line(historyKlineData);
        // 4 若当前的时间: 是否还在上一个时间的区间内,如果还在,则将其划入当前时间段K线
        if (dateTime.compareTo(historyKline.getTime()) == 1) {

            // redis的容量是否超
            if (size > Constants.REDIS_MAX_CACHE_KLINE_SIZE) {
                redisTemplate.opsForList().rightPop(redisKey);
            }

            Line line = new Line();
            line.setTime(dateTime);
            // 如果我们当前的交易量为 0,设置为收盘价
            if (klineData.getVolume().compareTo(BigDecimal.ZERO) == 0) {
                line.setHigh(historyKline.getClose());
                line.setLow(historyKline.getClose());
                line.setOpen(historyKline.getClose());
                line.setClose(historyKline.getClose());
                // 设置交易量为0
                line.setVolume(BigDecimal.ZERO) ;
                // 放在Redis 里面
                redisTemplate.opsForList().leftPush(redisKey, line.toKline());
                return;
            }
            // 设置开盘价和收盘价
            line.setOpen(klineData.getPrice());
            line.setClose(klineData.getPrice());

            // 设置最高价 ,最低价
            if (klineData.getPrice().compareTo(historyKline.getHigh()) == 1) {
                line.setHigh(klineData.getPrice());
                line.setLow(historyKline.getClose());
            }
            if (klineData.getPrice().compareTo(historyKline.getLow()) == 1) {
                line.setLow(klineData.getPrice());
                line.setHigh(historyKline.getClose());
            }

            // 新增的数据会对历史数据造成影响
            historyKline.setClose(klineData.getPrice());
            redisTemplate.opsForList().set(redisKey, 0, historyKline.toKline());
            // 放最新的进入
            // 放在Redis 里面
            redisTemplate.opsForList().leftPush(redisKey, line.toKline());
            return;
        }

        if (klineData.getVolume().compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        // 历史价格的收盘价
        historyKline.setClose(klineData.getPrice());

        // 更新历史价格的最高价和最低价
        if (klineData.getPrice().compareTo(historyKline.getHigh()) == 1) {
            historyKline.setHigh(klineData.getPrice());
        }

        if (klineData.getPrice().compareTo(historyKline.getLow()) == 1) {
            historyKline.setLow(klineData.getPrice());
        }
        // 设置历史K线数据数量(也就是让历史数据动态变化)
        historyKline.setVolume(historyKline.getVolume().add(klineData.getVolume()));
        redisTemplate.opsForList().set(redisKey, 0, historyKline.toKline());
    }


}
