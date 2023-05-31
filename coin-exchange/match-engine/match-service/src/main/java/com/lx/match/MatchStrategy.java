package com.lx.match;

/*
*  撮合策略,分为限价交易和市场交易
*  如果后续需要拓展市场交易,只需要在ServiceFactory里面进行添加,然后在eventHandler处理该类型即可
* */
public enum  MatchStrategy {

    /**
     * 限价交易
     */
    LIMIT_PRICE ,

    /**
     * 市场交易
     */
    MARKER_PRICE ;
}
