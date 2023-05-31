package com.lx.match;

import java.util.HashMap;
import java.util.Map;

/*
*  如何处理ringBuffer里面的消息(交易对,order)
*  消息处理策略工厂
* */
public class MatchServiceFactory {

    private static Map<MatchStrategy,MatchService> matchServiceMap = new HashMap<>() ;


    /**
     * 给我们的策略工厂里面添加一个交易的实现类型
     * @param matchStrategy
     * @param matchService
     */
    public static  void addMatchService(MatchStrategy matchStrategy,MatchService matchService){
        matchServiceMap.put(matchStrategy ,matchService ) ;
    }


    /**
     * 使用策略的名称获取具体的实现类
     * @param matchStrategy
     * @return
     */
    public static MatchService getMatchService(MatchStrategy matchStrategy){
        return matchServiceMap.get(matchStrategy) ;
    }
}
