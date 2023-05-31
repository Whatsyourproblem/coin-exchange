package com.lx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lx.domain.EntrustOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.mapper.TurnoverOrderMapper;
import com.lx.domain.TurnoverOrder;
import com.lx.service.TurnoverOrderService;
import org.springframework.util.StringUtils;

@Service
public class TurnoverOrderServiceImpl extends ServiceImpl<TurnoverOrderMapper, TurnoverOrder> implements TurnoverOrderService{


    /*
     *  查询分页数据
     * */
    @Override
    public Page<TurnoverOrder> findByPage(Page<TurnoverOrder> page, Long userId, String symbol, Integer type) {

        LambdaQueryWrapper<TurnoverOrder> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TurnoverOrder::getBuyUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type)
                .or(wrapper -> wrapper.eq(TurnoverOrder::getSellUserId,userId)
                        .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                        .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type));
/*        lambdaQueryWrapper.eq(TurnoverOrder::getBuyUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type);*/
/*        lambdaQueryWrapper.eq(TurnoverOrder::getSellUserId,userId)
                .eq(!StringUtils.isEmpty(symbol),TurnoverOrder::getSymbol,symbol)
                .eq(type!=null && type!=0, TurnoverOrder::getTradeType,type);*/
        return page(page,lambdaQueryWrapper);
    }

    /*
     *  查询成交记录
     * */
    @Override
    public List<TurnoverOrder> findBySymbol(String symbol) {
        List<TurnoverOrder> turnoverOrders = list(
                new LambdaQueryWrapper<TurnoverOrder>()
                        .eq(TurnoverOrder::getSymbol, symbol)
                        .orderByDesc(TurnoverOrder::getCreated)
                        .eq(TurnoverOrder::getStatus,1)
                        .last("limit 60")
        );
        return turnoverOrders;
    }

    /**
     * 获取买入的订单的成功的记录
     */
    @Override
    public List<TurnoverOrder> getBuyTurnoverOrder(Long orderId, Long userId) {
        return list(new LambdaQueryWrapper<TurnoverOrder>().eq(TurnoverOrder::getOrderId, orderId)
                .eq(TurnoverOrder::getBuyUserId, userId)
        );
    }

    /**
     * 获取卖出订单的成交记录
     */
    @Override
    public List<TurnoverOrder> getSellTurnoverOrder(Long orderId, Long userId) {
        return list(new LambdaQueryWrapper<TurnoverOrder>().eq(TurnoverOrder::getOrderId, orderId)
                .eq(TurnoverOrder::getSellUserId, userId)
        );
    }
}
