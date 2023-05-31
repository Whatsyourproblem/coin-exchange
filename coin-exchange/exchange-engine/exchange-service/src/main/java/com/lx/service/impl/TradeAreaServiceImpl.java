package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.Market;
import com.lx.domain.UserFavoriteMarket;
import com.lx.dto.CoinDto;
import com.lx.dto.TradeAreaDto;
import com.lx.feign.CoinServiceFeign;
import com.lx.mappers.TradeAreaDtoMappers;
import com.lx.service.MarketService;
import com.lx.service.UserFavoriteMarketService;
import com.lx.vo.MergeDeptVo;
import com.lx.vo.TradeAreaMarketVo;
import com.lx.vo.TradeMarketVo;
import org.checkerframework.checker.units.qual.min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.TradeAreaMapper;
import com.lx.domain.TradeArea;
import com.lx.service.TradeAreaService;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;

@Service
public class TradeAreaServiceImpl extends ServiceImpl<TradeAreaMapper, TradeArea> implements TradeAreaService{

    @Autowired
    private MarketService marketService;

    @Autowired
    private CoinServiceFeign coinServiceFeign;

    @Autowired
    private UserFavoriteMarketService userFavoriteMarketService;

    /*
     *  分页查询交易区域
     * */
    @Override
    public Page<TradeArea> findByPage(Page<TradeArea> page, String name, Byte status) {
        return page(page,new LambdaQueryWrapper<TradeArea>()
                .eq(status!=null,TradeArea::getStatus,status)
                .like(!StringUtils.isEmpty(name),TradeArea::getName,name));
    }

    /*
     *  使用交易区域的状态查询列表
     * */
    @Override
    public List<TradeArea> findAll(Byte status) {
        return list(new LambdaQueryWrapper<TradeArea>().eq(status!=null,TradeArea::getStatus,status));
    }

    /*
     * 查询交易区域，以及区域下面的市场
     * */
    @Override
    public List<TradeAreaMarketVo> findTradeAreaMarket() {
        // 1.查询所有的交易区域
        List<TradeArea> tradeAreas = list(new LambdaQueryWrapper<TradeArea>().eq(TradeArea::getStatus, 1)
                .orderByAsc(TradeArea::getSort));
        if (CollectionUtils.isEmpty(tradeAreas)){
            return Collections.emptyList();
        }
        ArrayList<TradeAreaMarketVo> tradeAreaMarketVos = new ArrayList<>();
        for (TradeArea tradeArea : tradeAreas) {
            // 2.查询我们交易区域里面包含的市场
            List<Market> markets = marketService.getMarketsByTradeAreaId(tradeArea.getId());
            if (!CollectionUtils.isEmpty(markets)){
                TradeAreaMarketVo tradeAreaMarketVo = new TradeAreaMarketVo();
                tradeAreaMarketVo.setAreaName(tradeArea.getName());
                tradeAreaMarketVo.setMarkets(markets2marketsVos(markets));
                tradeAreaMarketVos.add(tradeAreaMarketVo);
            }
        }
        return tradeAreaMarketVos;
    }

    /*
    *  将markets转换成 marketVos
    * */
    private List<TradeMarketVo> markets2marketsVos(List<Market> markets) {
        return markets.stream().map(market -> {
            return toConvertVo(market);
        }).collect(Collectors.toList());
    }

    /*
    * 将一个market转换为marketVo
    * */
    private TradeMarketVo toConvertVo(Market market) {
        TradeMarketVo tradeMarketVo = new TradeMarketVo();
        tradeMarketVo.setImage(market.getImg()); //报价货币的图片
        tradeMarketVo.setName(market.getName());
        tradeMarketVo.setSymbol(market.getSymbol());

        // 价格的设置
        tradeMarketVo.setHigh(market.getOpenPrice()); //openPrice 开盘价
        tradeMarketVo.setLow(market.getOpenPrice()); // 获取K线数据
        tradeMarketVo.setPrice(market.getOpenPrice()); // 获取K线数据
        tradeMarketVo.setCnyPrice(market.getOpenPrice()); // 计算获得
        tradeMarketVo.setCoinCnyPrice(market.getOpenPrice()); // 计算获得
        // 设置价格单位(报价货币的名称)
        // 获取报价货币的名称
        Long buyCoinId = market.getBuyCoinId();
        List<CoinDto> coins = coinServiceFeign.findCoins(Arrays.asList(buyCoinId));
        if (CollectionUtils.isEmpty(coins) || coins.size() > 1){
            throw new IllegalArgumentException("报价货币错误");
        }
        CoinDto coinDto = coins.get(0);
        tradeMarketVo.setPriceUnit(coinDto.getName());

        // 设置交易量
        tradeMarketVo.setVolume(BigDecimal.ZERO);  // 日的总交易量
        tradeMarketVo.setAmount(BigDecimal.ZERO);  //总的交易量

        // 交易额度
        tradeMarketVo.setTradeMin(market.getTradeMin());
        tradeMarketVo.setTradeMax(market.getTradeMax());

        // 下班的数量限制
        tradeMarketVo.setNumMin(market.getNumMin());
        tradeMarketVo.setNumMax(market.getNumMax());

        // 手续费的设置
        tradeMarketVo.setSellFeeRate(market.getFeeSell());
        tradeMarketVo.setBuyFeeRate(market.getFeeBuy());

        // 价格的小数位数
        tradeMarketVo.setNumScale(market.getNumScale());

        // 设置合并的深度
        tradeMarketVo.setMergeDepth(getMergeDepths(market.getMergeDepth()));

        // 排序
        tradeMarketVo.setSort(market.getSort());

        // 设置涨幅
        tradeMarketVo.setChange(0.00);
        return tradeMarketVo;
    }

    /*
    *  获取合并的深度
    * */
    private List<MergeDeptVo> getMergeDepths(String mergeDepth) {
        String[] split = mergeDepth.split(",");
        if (split.length!=3){
            throw new IllegalArgumentException("合并深度不合法");
        }

        // 6(1/1000000), 5(1/100000),4(1/10000)
        //最小深度
        MergeDeptVo minMergeDeptVo = new MergeDeptVo();
        minMergeDeptVo.setMergeType("MIN"); // 默认,高,低
        minMergeDeptVo.setValue(getDeptValue(Integer.valueOf(split[0]))); //

        // 默认深度
        MergeDeptVo defaultMergeDeptVo = new MergeDeptVo();
        defaultMergeDeptVo.setMergeType("DEFAULT"); // 默认,高,低
        defaultMergeDeptVo.setValue(getDeptValue(Integer.valueOf(split[1]))); //

        MergeDeptVo maxMergeDeptVo = new MergeDeptVo();
        maxMergeDeptVo.setMergeType("MAX"); // 默认,高,低
        maxMergeDeptVo.setValue(getDeptValue(Integer.valueOf(split[2]))); //

        List<MergeDeptVo> mergeDeptVos = new ArrayList<>();
        mergeDeptVos.add(minMergeDeptVo);
        mergeDeptVos.add(defaultMergeDeptVo);
        mergeDeptVos.add(maxMergeDeptVo);

        return mergeDeptVos;
    }

    // 6 -> 1/1000000
    private BigDecimal getDeptValue(Integer scale) {
        // 指数操作
        BigDecimal bigDecimal = new BigDecimal(Math.pow(10, scale));
        // 1 / 10的n次方
        return BigDecimal.ONE.divide(bigDecimal).setScale(scale, RoundingMode.HALF_UP);
    }

    /*
     *  查询用户收藏的交易市场
     * */
    @Override
    public List<TradeAreaMarketVo> getUserFavoriteMarkets(Long userId) {
        List<UserFavoriteMarket> userFavoriteMarkets = userFavoriteMarketService.list(new LambdaQueryWrapper<UserFavoriteMarket>()
                .eq(UserFavoriteMarket::getUserId, userId));
        if (CollectionUtils.isEmpty(userFavoriteMarkets)){
            return Collections.emptyList();
        }
        List<Long> marketIds = userFavoriteMarkets.stream().map(UserFavoriteMarket::getMarketId).collect(Collectors.toList());
        // 创建一个TradeAreaMarketVo
        TradeAreaMarketVo tradeAreaMarketVo = new TradeAreaMarketVo();
        tradeAreaMarketVo.setAreaName("自选");
        // 通过marketIds来查询
        List<Market> markets = marketService.listByIds(marketIds);
        List<TradeMarketVo> tradeMarketVos = markets2marketsVos(markets);
        tradeAreaMarketVo.setMarkets(tradeMarketVos);


        return Arrays.asList(tradeAreaMarketVo);
    }

    /**
     * 查询所有的交易区域和交易区域下的市场
     * @return
     */
    @Override
    public List<TradeAreaDto> findAllTradeAreaAndMarket() {
        List<TradeArea> tradeAreas = findAll((byte) 1);
        List<TradeAreaDto> tradeAreaDtoList = TradeAreaDtoMappers.INSTANCE.toConvertDto(tradeAreas);
        if(CollectionUtils.isEmpty(tradeAreaDtoList)){
            for (TradeAreaDto tradeAreaDto : tradeAreaDtoList) {
                List<Market> markets = marketService.queryByAreaId(tradeAreaDto.getId()) ;
                if(!CollectionUtils.isEmpty(markets)){
                    String marketIds = markets.stream().map(market -> market.getId().toString()).collect(Collectors.joining(","));
                    tradeAreaDto.setMarketIds(marketIds);
                }

            }
        }
        return tradeAreaDtoList;
    }


}
