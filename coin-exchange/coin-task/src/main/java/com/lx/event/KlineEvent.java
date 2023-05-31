package com.lx.event;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lx.enums.KlineType;
import com.lx.model.MessagePayload;
import com.lx.rocket.Source;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;

import java.util.List;

/**
 * K 线推送事件
 */

@Data
@Slf4j
public class KlineEvent implements Runnable,Event {

    /**
     * 交易对标识符
     */
    private String symbol;

    /**
     * 通道
     */
    private String channel;

    /**
     * redis key 前缀
     */
    private String keyPrefix;

    /**
     *  1%s: 交易对  2%s: k 线类型
     * */
    private static final String KLINE_GROUP = "market.%s.kline.%s" ;

    private StringRedisTemplate redisTemplate ;

    private Source source ;

    public KlineEvent() {
    }

    public KlineEvent(String symbol, String channel, String keyPrefix) {
        this.symbol = symbol;
        this.channel = channel;
        this.keyPrefix = keyPrefix;
    }

    /**
     * 事件触发处理机制
     */
    @Override
    public void handle() {

        /**
        *  把所有K线数据逐个放入到Redis的List数据结构中,直到1000个为止
         *  如果List满了则弹出K线数据,这样保证每次的K线数据都是最新的
        * */

        // 1 循环所有的K 线类型
        //for (KlineType klineType : KlineType.values()) { // 计算机性能差 ,我们只能推一种
            // 2 获取特定的K 线类型 keyPrefix:etcgcn:1min klineType.getValue().toLowerCase()
            String key = new StringBuilder(keyPrefix).append(symbol.toLowerCase()).append(":").append("1min").toString() ;
            // 取出redis List里面的第一个数据
            List<String> lines = redisTemplate.opsForList().range(key, 0, 1);
            if(!CollectionUtils.isEmpty(lines)){
                String lineData = lines.get(0);
                MessagePayload messagePayload = new MessagePayload();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tick", JSON.parseArray(lineData).toString()) ;
                // market.%s.kline.%s klineType.getValue().toLowerCase()
                // messagePayload.setChannel(String.format(KLINE_GROUP,symbol,klineType.getValue().toLowerCase()));
                // 性能原因 只推一种
                messagePayload.setChannel(String.format(KLINE_GROUP,symbol,"1min"));
                messagePayload.setBody(jsonObject.toString());
                // 发送K 线
                source.subscribeGroupOutput()
                        .send(
                                MessageBuilder
                                        .withPayload(messagePayload)
                                        .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build()
                        );
            }

        }

   // }

    /**
     * 让线程池调度
     */
    @Override
    public void run() {
        handle();
    }
}