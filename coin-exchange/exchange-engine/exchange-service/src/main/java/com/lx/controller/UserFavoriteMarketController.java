package com.lx.controller;

import cn.hutool.core.lang.Snowflake;
import com.lx.domain.Market;
import com.lx.domain.UserFavoriteMarket;
import com.lx.model.R;
import com.lx.service.MarketService;
import com.lx.service.UserFavoriteMarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userFavorites")
@Api(tags = "用户收藏市场")
public class UserFavoriteMarketController {

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private UserFavoriteMarketService userFavoriteMarketService;

    @Autowired
    private MarketService marketService;

    @PostMapping("/addFavorite")
    @ApiOperation(value = "用户收藏某个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "market",value = "market的交易对和类型")
    })
    public R addFavorite(@RequestBody Market market){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        UserFavoriteMarket userFavoriteMarket = new UserFavoriteMarket();
        String symbol = market.getSymbol(); // 市场的symbol
        userFavoriteMarket.setId(snowflake.nextId());// 使用雪花算法生成Id
        userFavoriteMarket.setUserId(userId);


        Market marketDb =  marketService.getMarketBySymbol(symbol);
        userFavoriteMarket.setMarketId(marketDb.getId());
        userFavoriteMarket.setType(market.getType().intValue()); // 该字段暂未使用
        boolean save = userFavoriteMarketService.save(userFavoriteMarket);
        if (save){
            return R.ok("收藏成功");
        }
        return R.fail("收藏失败");
    }

    @DeleteMapping("/{symbol}")
    @ApiOperation(value = "用户取消收藏某个市场")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol",value = "symbol交易对")
    })
    public R deleteUserFavoriteMarket(@PathVariable("symbol") String symbol){
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Market marketBySymbol = marketService.getMarketBySymbol(symbol);
        boolean isOk = userFavoriteMarketService.deleteUserFavoriteMarket(marketBySymbol.getId(),userId);
        if (isOk){
            return R.ok();
        }
        return R.fail("取消收藏失败");
    }

}
