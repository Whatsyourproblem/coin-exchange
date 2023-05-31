package com.lx.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.dto.CoinDto;
import com.lx.dto.MarketDto;
import com.lx.feign.CoinServiceFeign;
import com.lx.mappers.MarketDtoMappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.domain.Market;
import com.lx.mapper.MarketMapper;
import com.lx.service.MarketService;
import org.springframework.util.CollectionUtils;

@Service
@Slf4j
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService{

    @Autowired
    private CoinServiceFeign coinServiceFeign;

    /*
     *  分页查询市场的配置
     * */
    @Override
    public Page<Market> findByPage(Page<Market> page, Long tradeAreaId, Byte status) {
        return page(page,new LambdaQueryWrapper<Market>()
                .eq(tradeAreaId!=null,Market::getTradeAreaId,tradeAreaId)
                .eq(status!=null,Market::getStatus,status));
    }

    /*
    *  重写save方法
    * */
    @Override
    public boolean save(Market market){
        log.info("开始新增市场数据{}", JSON.toJSONString(market));
        Long sellCoinId = market.getSellCoinId(); // 报价货币
        Long buyCoinId = market.getBuyCoinId(); //基础货币
        List<CoinDto> coins = coinServiceFeign.findCoins(Arrays.asList(sellCoinId, buyCoinId));
        if (CollectionUtils.isEmpty(coins) || coins.size() != 2){
            throw new IllegalArgumentException("货币输入错误");
        }
        CoinDto coinDto = coins.get(0);
        CoinDto sellCoin = null;
        CoinDto buyCoin = null;
        if (coinDto.getId().equals(sellCoinId)){
            sellCoin = coinDto;
            buyCoin = coins.get(1);
        }else {
            sellCoin = coins.get(1);
            buyCoin = coinDto;
        }

        market.setName(sellCoin.getName() + "/" + buyCoin.getName()); //交易市场的名称 -> 报价货币/基础货币
        market.setTitle(sellCoin.getTitle() + "/" + buyCoin.getTitle()); //交易市场的标题  -> 报价货币/基础货币
        market.setSymbol(sellCoin.getName() + buyCoin.getName() ); //交易市场的标题-> 报价货币基础货币
        market.setImg(sellCoin.getImg());  // 交易市场的标题
        return super.save(market);
    }

    /*
     *  使用交易区域的id查询该区域下的市场
     * */
    @Override
    public List<Market> getMarketsByTradeAreaId(Long id) {
        return list(new LambdaQueryWrapper<Market>()
                .eq(Market::getTradeAreaId,id)
                .eq(Market::getStatus,1)
                .orderByAsc(Market::getSort));
    }

    /*
     *  使用交易对，查询市场
     * */
    @Override
    public Market getMarketBySymbol(String symbol) {
        return getOne(new LambdaQueryWrapper<Market>()
                .eq(Market::getSymbol,symbol));
    }

    /*
     *  使用报价货币和基础货币查询市场
     * */
    @Override
    public MarketDto findByCoinId(Long buyCoinId, Long sellCoinId) {
        LambdaQueryWrapper<Market> queryWrapper = new LambdaQueryWrapper<Market>()
                .eq(Market::getBuyCoinId, buyCoinId)
                .eq(Market::getSellCoinId, sellCoinId)
                .eq(Market::getStatus, 1);
        Market one = getOne(queryWrapper);
        if (one == null){
            return null;
        }
        MarketDto marketDto = MarketDtoMappers.INSTANCE.toConvertDto(one);
        return marketDto;
    }

    /**
     * 查询所有的市场数据
     */
    @Override
    public List<MarketDto> queryAllMarkets() {
        List<Market> list = list(new LambdaQueryWrapper<Market>().eq(Market::getStatus, 1));
        return MarketDtoMappers.INSTANCE.toConvertDto(list);
    }

    /**
     * 使用交易区域查询市场
     * @param id
     * @return
     */
    @Override
    public List<Market> queryByAreaId(Long id) {
        List<Market> markets = list(new LambdaQueryWrapper<Market>().eq(Market::getTradeAreaId, id));
        return markets;
    }
}
