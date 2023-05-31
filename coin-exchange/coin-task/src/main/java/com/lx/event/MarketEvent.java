package com.lx.event;

import com.alibaba.fastjson.JSONObject;

import com.lx.dto.MarketDto;
import com.lx.dto.TradeAreaDto;
import com.lx.dto.TradeMarketDto;
import com.lx.feign.MarketServiceFeign;
import com.lx.feign.TradingAreaServiceClient;
import com.lx.model.MessagePayload;
import com.lx.rocket.Source;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MimeTypeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 行情市场的K 线
 */
@Component
@Slf4j
public class MarketEvent implements Event {

    @Autowired
    private Source source;

    @Autowired
    private TradingAreaServiceClient tradingAreaServiceClient;


    @Autowired
    private MarketServiceFeign marketServiceFeign;

    private static final String MARKET_GROUP = "market.%s.ticker"; // %s代表交易区域

    private static final String MARKET_DETAIL_GROUP = "markets.%s.detail" ; // 交易区域的详情交易数据

    @Override
    public void handle() {
        // 1 交易区域的查询
        List<TradeAreaDto> tradeAreaDtoList = tradingAreaServiceClient.tradeAreas();
        if (CollectionUtils.isEmpty(tradeAreaDtoList)) {
            return;
        }
        for (TradeAreaDto tradeAreaDto : tradeAreaDtoList) {
            // 2 差该交易区域下的交易数据 , 使用的是交易区域里面市场的id("id1,id2")
            String marketIds = tradeAreaDto.getMarketIds();
            String[] split = marketIds.split(",");
            List<TradeMarketDto> tradeMarketDtos = null;
            if (split == null){
                tradeMarketDtos = marketServiceFeign.queryMarkesByIds(Arrays.asList(tradeAreaDto.getId()));
            }else {
                List<Long> list = Arrays.stream(split).map(s -> Long.valueOf(s)).collect(Collectors.toList());
                tradeMarketDtos = marketServiceFeign.queryMarkesByIds(list);
            }
            if (CollectionUtils.isEmpty(tradeMarketDtos)) {
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("markets", tradeMarketDtos);
            MessagePayload messagePayload = new MessagePayload();
            messagePayload.setChannel(String.format(MARKET_GROUP, tradeAreaDto.getCode().toLowerCase()));
            messagePayload.setBody(jsonObject.toJSONString());
            source.subscribeGroupOutput()
                    .send(
                            MessageBuilder
                                    .withPayload(messagePayload)
                                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build()
                    );
        }


        // 获取所有的交易市场
        List<MarketDto> marketDtos = marketServiceFeign.tradeMarkets();
        if(CollectionUtils.isEmpty(marketDtos)){
            return;
        }
        for (MarketDto marketDto : marketDtos) {
            List<TradeMarketDto> tradeMarketDtos = marketServiceFeign.queryMarkesByIds(Arrays.asList(marketDto.getId()));

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tick",tradeMarketDtos) ;

            MessagePayload messagePayload = new MessagePayload();
            messagePayload.setChannel(String.format(MARKET_DETAIL_GROUP,marketDto.getSymbol().toLowerCase()));
            messagePayload.setBody(jsonObject.toJSONString());
            source.subscribeGroupOutput()
                    .send(
                            MessageBuilder
                                    .withPayload(messagePayload)
                                    .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON).build()
                    );
        }

    }
}